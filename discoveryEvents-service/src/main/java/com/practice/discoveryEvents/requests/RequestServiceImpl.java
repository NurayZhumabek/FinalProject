package com.practice.discoveryEvents.requests;

import com.practice.discoveryEvents.events.Event;
import com.practice.discoveryEvents.events.EventRepository;
import com.practice.discoveryEvents.events.EventService;
import com.practice.discoveryEvents.users.User;
import com.practice.discoveryEvents.users.UserService;
import com.practice.discoveryEvents.util.*;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final EventService eventService;
    private final UserService userService;
    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;

    public RequestServiceImpl(RequestRepository requestRepository, EventService eventService, UserService userService, EventRepository eventRepository, ModelMapper modelMapper) {
        this.requestRepository = requestRepository;
        this.eventService = eventService;
        this.userService = userService;
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<Request> getRequestsByRequesterId(Integer requesterId) {
        User user = userService.getUserById(requesterId);
        return requestRepository.findByRequesterId(requesterId);
    }

    @Override
    public Request createRequest(Integer userId, Integer eventId) {

        if (checkRequest(userId, eventId) != null) {
            throw new ConflictException("Request is already created by " + userId + " for event " + eventId);
        }

        Event event =  eventService.getPublicEventById(eventId);
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Initiator cannot send a request");
        }

        Request request = new Request();

        User requester = userService.getUserById(userId);

        request.setEvent(event);
        request.setRequester(requester);
        request.setCreated(LocalDateTime.now());

        if (event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        } else if (event.getConfirmedRequests() < event.getParticipantLimit()) {
            if (Boolean.FALSE.equals(event.getRequestModeration())) {
                request.setStatus(RequestStatus.CONFIRMED);
                event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            } else {
                request.setStatus(RequestStatus.PENDING);
            }
        } else {
            throw new ConflictException("Participant limit exceeded for this event " + eventId);
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
        Event event =eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " not found"));

        if (!event.getInitiator().getId().equals(initiatorId)) {
            throw new AccessDeniedException("You are not owner of this event");
        }

        return requestRepository.getRequestsByEventId(eventId);
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestsStatus(Integer eventId, Integer initiatorId, EventRequestStatusUpdateRequestDTO requests) {
        User initiator = userService.getUserById(initiatorId);
        Event event = eventRepository.findEventByIdAndState(eventId, State.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Event not found or not published"));

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        List<Request> confirmedList = new ArrayList<>();
        List<Request> rejectedList = new ArrayList<>();

        if (!event.getInitiator().getId().equals(initiator.getId())) {
            throw new AccessDeniedException("You are not owner of this event");
        }

        RequestStatus newStatusFromRequest = requests.getStatus();

        for (Integer requestId : requests.getRequestIds()) {
            Request request = requestRepository.findById(requestId)
                    .orElseThrow(() -> new NotFoundException("Request with id " + requestId + " not found"));

            if (request.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Request with id " + requestId + " status is not PENDING. Only PENDING requests can be updated.");
            }

            if (newStatusFromRequest == RequestStatus.CONFIRMED) {

                if (event.getConfirmedRequests() >= event.getParticipantLimit() && event.getParticipantLimit() != 0) {

                    request.setStatus(RequestStatus.REJECTED);
                    rejectedList.add(request);
                } else {
                    request.setStatus(RequestStatus.CONFIRMED);
                    event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                    confirmedList.add(request);
                }
            } else if (newStatusFromRequest == RequestStatus.REJECTED) {

                request.setStatus(RequestStatus.REJECTED);
                rejectedList.add(request);
            }
            requestRepository.save(request);
        }

        eventRepository.save(event);

        if (newStatusFromRequest == RequestStatus.CONFIRMED && event.getConfirmedRequests() >= event.getParticipantLimit()){
            List<Request> pending = requestRepository.getRequestsByEventId(eventId)
                    .stream()
                    .filter(req -> req.getStatus().equals(RequestStatus.PENDING))
                    .collect(Collectors.toList());

            for (Request request : pending) {
                request.setStatus(RequestStatus.REJECTED);
                requestRepository.save(request);
                rejectedList.add(request);
            }
        }


        result.setConfirmedRequests(
                confirmedList.stream()
                        .map(this::toParticipationRequestDTO)
                        .collect(Collectors.toList())
        );
        result.setRejectedRequests(
                rejectedList.stream()
                        .map(this::toParticipationRequestDTO)
                        .collect(Collectors.toList())
        );

        return result;
    }

    public ParticipationRequestDTO toParticipationRequestDTO(Request request) {
        ParticipationRequestDTO dto = new ParticipationRequestDTO();
        dto.setId(request.getId());
        dto.setCreated(request.getCreated());
        dto.setStatus(request.getStatus());

        if (request.getEvent() != null) {
            dto.setEvent(request.getEvent().getId());
        }

        if (request.getRequester() != null) {
            dto.setRequester(request.getRequester().getId());
        }

        return dto;
    }
}

