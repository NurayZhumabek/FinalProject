package com.practice.discoveryEvents.requests;

import com.practice.discoveryEvents.events.Event;
import com.practice.discoveryEvents.events.EventRepository;
import com.practice.discoveryEvents.events.EventService;
import com.practice.discoveryEvents.users.User;
import com.practice.discoveryEvents.users.UserService;
import com.practice.discoveryEvents.util.AccessDeniedException;
import com.practice.discoveryEvents.util.AlreadyExistsException;
import com.practice.discoveryEvents.util.NotFoundException;
import com.practice.discoveryEvents.util.RequestStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final EventService eventService;
    private final UserService userService;
    private final EventRepository eventRepository;

    public RequestServiceImpl(RequestRepository requestRepository, EventService eventService, UserService userService, EventRepository eventRepository) {
        this.requestRepository = requestRepository;
        this.eventService = eventService;
        this.userService = userService;
        this.eventRepository = eventRepository;
    }

    @Override
    public List<Request> getRequestsByRequesterId(Integer requesterId) {
        return requestRepository.findByRequesterId(requesterId);
    }

    @Override
    public Request createRequest(Integer userId, Integer eventId) {

        if (checkRequest(userId, eventId) != null) {
            throw new AlreadyExistsException("Request is already created by " + userId + " for event " + eventId);
        }

        Event event = eventService.getPublicEventById(eventId);

        if (event.getInitiator().getId().equals(userId)) {
            throw new AccessDeniedException("Initiator cannot send a request");
        }

        if (event.getParticipantLimit() != 0 &&
                event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new AccessDeniedException("Participant limit exceeded for this event " + eventId);
        }

        Request request = new Request();

        User requester = userService.getUserById(userId);

        request.setEvent(event);
        request.setRequester(requester);
        request.setCreated(LocalDateTime.now());

        if (Boolean.FALSE.equals(event.getRequestModeration()) && event.getParticipantLimit() == 0 ) {
            request.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }

        eventRepository.save(event);

        return requestRepository.save(request);
    }


    private Request checkRequest(Integer userId, Integer eventId) {
        return requestRepository.findByRequesterIdAndEventId(userId, eventId);
    }


    @Override
    public Request cancelRequest(Integer requestId, Integer requesterId) {

        Request request = requestRepository
                .findById(requestId).orElseThrow(() -> new NotFoundException("Request with id " + requestId + " not found"));

        if (!request.getRequester().getId().equals(requesterId)) {
            throw new AccessDeniedException("You are not owner of this request");
        }

        if (request.getStatus() == RequestStatus.CANCELED) {
            throw new AccessDeniedException("Request is already canceled");
        }

        if (request.getStatus() == RequestStatus.CONFIRMED) {
            Event event = request.getEvent();
            event.setConfirmedRequests(event.getConfirmedRequests() - 1);
            eventRepository.save(event);
        }

        request.setStatus(RequestStatus.CANCELED);
        return requestRepository.save(request);
    }

    @Override
    public List<Request> getRequestsByInitiator(Integer eventId, Integer initiatorId) {
        User user = userService.getUserById(initiatorId);
        Event event = eventService.getPublicEventById(eventId);

        if (!event.getInitiator().getId().equals(initiatorId)) {
            throw new AccessDeniedException("You are not owner of this event");
        }

        return requestRepository.getRequestsByEventId(eventId);
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestsStatus(Integer eventId, Integer initiatorId, EventRequestStatusUpdateRequestDTO requests) {
        User initiator = userService.getUserById(initiatorId);
        Event event = eventService.getPublicEventById(eventId);

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        List<Request> confirmedList = new ArrayList<>();
        List<Request> rejectedList = new ArrayList<>();

        if (!event.getInitiator().getId().equals(initiator.getId())) {
            throw new AccessDeniedException("You are not owner of this event");
        }

        for (Integer requestId : requests.getRequestIds()) {

            Request request = requestRepository.findById(requestId)
                    .orElseThrow(() -> new NotFoundException("Request with id " + requestId + " not found"));

            if (request.getStatus() != RequestStatus.PENDING) {
                throw new AccessDeniedException("Request  with id " + requestId + " status is not PENDING");
            }


            if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
                request.setStatus(RequestStatus.REJECTED);
                requestRepository.save(request);
                rejectedList.add(request);
            } else {
                request.setStatus(RequestStatus.CONFIRMED);
                requestRepository.save(request);
                event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                eventRepository.save(event);
                confirmedList.add(request);
            }

        }

        result.setConfirmedRequests(confirmedList);
        result.setRejectedRequests(rejectedList);

        return result;
    }


}

