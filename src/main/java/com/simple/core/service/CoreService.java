package com.simple.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simple.core.entity.User;
import com.simple.core.entity.UserBankCard;
import com.simple.core.error.*;
import com.simple.core.repository.UserBankCardRepository;
import com.simple.core.repository.UserRepository;
import com.simple.simpleLib.dto.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class CoreService {
    private final WebClient webClient;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserBankCardRepository userBankCardRepository;

    public CoreService(WebClient.Builder builder,
                       @Value("${ticket.module.host}") String host,
                       @Value("${ticket.module.port}") String port) {
        this.webClient = builder.baseUrl("http://" + host + ":" + port).build();
    }

    /**
     * Validates that the user token is connected to give card
     *
     * @param userToken user token to be validated
     * @param cardId    card id subject to validation
     * @return result of validation
     */
    public Boolean validateUserTokenAndCardId(String userToken, String cardId) {
        log.info("Validating userToken {} with cardId {}", userToken, cardId);
        return userRepository.validateUserTokenAndCardId(userToken, cardId);
    }

    /**
     * Queries single user by id
     *
     * @param userId id of requested user
     * @return User if found, exception otherwise
     */
    public User findUserById(Long userId) {
        log.info("Querying for user with id {}", userId);
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    /**
     * Lists all users
     *
     * @return List of all users in the database
     */
    public List<User> listAllUsers() {
        log.info("Querying for all users");
        return userRepository.findAll();
    }

    /**
     * Tries to reserve a seat for given event.
     * Validates card has enough balance to cover reservation
     *
     * @param eventId id of event subject to reservation
     * @param seatId  id of seat to be reserved
     * @param cardId  user's card id
     * @return if successful, reserves reservation id. Otherwise, returns exception
     * @throws JsonProcessingException if one of the requests returns erroneous response
     */
    @Transactional
    public Long reserveSeatAndPay(Long eventId, String seatId, String cardId) throws JsonProcessingException {
        log.info("Requesting event data by id {}", eventId);
        SimpleEventDto eventDto = getEventById(eventId);
        log.info("Querying bank card data");
        UserBankCard card = userBankCardRepository.findById(cardId).orElseThrow(UserBankCardMismatchException::new);
        log.info("Searching for seat with id {}", seatId);
        SeatDto currentSeat = eventDto.getSeats().stream().filter(t -> t.getId().equals(seatId)).findFirst().orElseThrow(SeatNotFoundException::new);
        ExtendedEventDto extendedEventDto = getEvents();
        EventDto currentEvent = extendedEventDto.getData().stream().filter(t -> t.getEventId().equals(eventId)).findFirst().orElseThrow(EventNotFoundException::new);
        log.info("Bank card and event info queried, validating");
        if (currentSeat.getReserved()) {
            log.error("Seat was already reserved!");
            throw new SeatNotFoundException();
        }
        if (card.getAmount().compareTo(currentSeat.getPrice()) < 0) {
            log.error("Card balance was too low!");
            throw new CardBalanceLowException();
        }
        if (Instant.ofEpochSecond(Long.parseLong(currentEvent.getStartTimeStamp())).isAfter(Instant.now())) {
            log.error("Event has already started!");
            throw new EventAlreadyStartedException();
        }

        userBankCardRepository.updateBalance(cardId, card.getAmount().subtract(currentSeat.getPrice()));
        return reserveByEventAndSeat(eventId, seatId).getReserverId();
    }

    /**
     * Requests event by id from Ticket microservice
     *
     * @param eventId id of event to be requested
     * @return dto of event or exception if not found
     * @throws JsonProcessingException if response is erroneous
     */
    private SimpleEventDto getEventById(Long eventId) throws JsonProcessingException {
        try {
            log.info("Getting event by id {} from ticket service", eventId);
            return webClient.get()
                    .uri("/ticket/getEvent/{eventId}", eventId)
                    .retrieve()
                    .bodyToMono(SimpleEventDto.class)
                    .block();
        } catch (WebClientResponseException e) {
            ErrorResponse response = objectMapper.readValue(e.getResponseBodyAsString(), ErrorResponse.class);
            throw new BusinessException(response.getErrorMessage(), response.getErrorCode());
        }

    }

    /**
     * Requests events from Ticket microservice
     *
     * @return returns a list of events
     * @throws JsonProcessingException if response is erroneous
     */
    private ExtendedEventDto getEvents() throws JsonProcessingException {
        try {
            log.info("Getting event list from ticket service");
            return webClient.get()
                    .uri("/ticket/getEvents")
                    .retrieve()
                    .bodyToMono(ExtendedEventDto.class)
                    .block();
        } catch (WebClientResponseException e) {
            ErrorResponse response = objectMapper.readValue(e.getResponseBodyAsString(), ErrorResponse.class);
            throw new BusinessException(response.getErrorMessage(), response.getErrorCode());
        }
    }

    /**
     * Tries to reserve a seat on an event
     *
     * @param eventId id of event for reservation
     * @param seatId  id of seat to be reserved
     * @return success, and if so, reservationId. Otherwise exception
     * @throws JsonProcessingException if response is erroneous
     */
    private ReserveDto reserveByEventAndSeat(Long eventId, String seatId) throws JsonProcessingException {
        try {
            log.info("Trying to reserve seat with id {} on event {}", seatId, eventId);
            return webClient.post()
                    .uri("/ticket/reserve/{eventId}/{seatId}", eventId, seatId)
                    .retrieve()
                    .bodyToMono(ReserveDto.class)
                    .block();
        } catch (WebClientResponseException e) {
            ErrorResponse response = objectMapper.readValue(e.getResponseBodyAsString(), ErrorResponse.class);
            throw new BusinessException(response.getErrorMessage(), response.getErrorCode());
        }
    }

}
