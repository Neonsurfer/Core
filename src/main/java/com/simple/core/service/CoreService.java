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

    public Boolean validateUserTokenAndCardId(String userToken, String cardId) {
        return userRepository.validateUserTokenAndCardId(userToken, cardId);
    }

    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(TokenExpiredException::new);
    }

    public List<User> listAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public Long reserveSeatAndPay(Long eventId, String seatId, String cardId) throws JsonProcessingException {
        SimpleEventDto eventDto = getEventById(eventId);
        UserBankCard card = userBankCardRepository.findById(cardId).orElseThrow(UserBankCardMismatchException::new);
        SeatDto currentSeat = eventDto.getSeats().stream().filter(t -> t.getId().equals(seatId)).findFirst().orElseThrow(SeatNotFoundException::new);
        ExtendedEventDto extendedEventDto = getEvents();
        EventDto currentEvent = extendedEventDto.getData().stream().filter(t -> t.getEventId().equals(eventId)).findFirst().orElseThrow(EventNotFoundException::new);
        log.info("Bank card and event info queried, validating");
        if (currentSeat.getReserved()) {
            throw new SeatNotFoundException();
        }
        if (card.getAmount().compareTo(currentSeat.getPrice()) < 0) {
            throw new CardBalanceLowException();
        }
        if (Instant.ofEpochSecond(Long.parseLong(currentEvent.getStartTimeStamp())).isAfter(Instant.now())) {
            throw new EventAlreadyStartedException();
        }

        userBankCardRepository.updateBalance(cardId, card.getAmount().subtract(currentSeat.getPrice()));
        return reserveByEventAndSeat(eventId, seatId).getReserverId();
    }

    private SimpleEventDto getEventById(Long eventId) throws JsonProcessingException {
        try {
            return webClient.get()
                    .uri("/ticket/getEvent/{eventId}", eventId)
                    .retrieve()
                    .bodyToMono(SimpleEventDto.class).blockOptional().orElseThrow();
        } catch (WebClientResponseException e) {
            ErrorResponse response = objectMapper.readValue(e.getResponseBodyAsString(), ErrorResponse.class);
            throw new BusinessException(response.getErrorMessage(), response.getErrorCode());
        }

    }

    private ExtendedEventDto getEvents() throws JsonProcessingException {
        try {
            return webClient.get()
                    .uri("/ticket/getEvents")
                    .retrieve()
                    .bodyToMono(ExtendedEventDto.class).blockOptional().orElseThrow();
        } catch (WebClientResponseException e) {
            ErrorResponse response = objectMapper.readValue(e.getResponseBodyAsString(), ErrorResponse.class);
            throw new BusinessException(response.getErrorMessage(), response.getErrorCode());
        }
    }

    private ReserveDto reserveByEventAndSeat(Long eventId, String seatId) throws JsonProcessingException {
        try {
            return webClient.post()
                    .uri("/ticket/reserve/{eventId}/{seatId}", eventId, seatId)
                    .retrieve()
                    .bodyToMono(ReserveDto.class).blockOptional().orElseThrow();
        } catch (WebClientResponseException e) {
            ErrorResponse response = objectMapper.readValue(e.getResponseBodyAsString(), ErrorResponse.class);
            throw new BusinessException(response.getErrorMessage(), response.getErrorCode());
        }
    }

}
