package com.practice.discoveryEvents.users;


import com.practice.discoveryEvents.events.*;
import com.practice.discoveryEvents.requests.ParticipationRequestDTO;
import com.practice.discoveryEvents.requests.Request;
import com.practice.discoveryEvents.requests.RequestService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users/{userId}")
public class UserController {

    private final EventService eventService;
    private final ModelMapper modelMapper;
    private final RequestService requestService;

    public UserController(EventService eventService, ModelMapper modelMapper, RequestService requestService) {
        this.eventService = eventService;
        this.modelMapper = modelMapper;
        this.requestService = requestService;
    }

    @GetMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDTO> getEventsByUser(@PathVariable Integer userId,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size) {
        return eventService.getEventsByUser(userId, from, size)
                .stream()
                .map(e -> modelMapper.map(e, EventShortDTO.class))
                .collect(Collectors.toList());
    }

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDTO createEvent(@PathVariable Integer userId,
                                    @RequestBody @Valid NewEventDTO eventDTO) {
        Event createdEvent = eventService.createEvent(eventDTO, userId);
        return modelMapper.map(createdEvent, EventFullDTO.class);

    }

    @GetMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDTO getEvent(@PathVariable Integer userId, @PathVariable Integer eventId) {
        return modelMapper.map(eventService.getEventByUserById(eventId, userId), EventFullDTO.class);
    }

    @PatchMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDTO updateEvent(@PathVariable Integer userId, @PathVariable Integer eventId, @RequestBody @Valid UpdateEventUserRequest eventDTO) {
        return modelMapper.map(eventService.updateEventByUser(eventId, eventDTO, userId), EventFullDTO.class);
    }


    @GetMapping("/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDTO> getRequestsByUser(@PathVariable Integer userId) {
        return requestService.getRequestsByRequesterId(userId)
                .stream()
                .map(request -> modelMapper.map(request, ParticipationRequestDTO.class))
                .collect(Collectors.toList());
    }


    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDTO createRequest(@PathVariable Integer userId, @RequestParam Integer eventId) {

        Request createdRequest = requestService.createRequest(userId, eventId);
        return modelMapper.map(createdRequest, ParticipationRequestDTO.class);

    }

    @PatchMapping("/requests/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDTO cancelRequest(@PathVariable Integer userId, @PathVariable Integer requestId) {
        Request cancelledRequest = requestService.cancelRequest( requestId,userId);
        return modelMapper.map(cancelledRequest, ParticipationRequestDTO.class);
    }

    @GetMapping("/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDTO> getRequestsByEventAndInitiator(@PathVariable Integer eventId, @PathVariable Integer userId) {
        return requestService.getRequestsByInitiator(eventId,userId)
                .stream()
                .map(request -> modelMapper.map(request,ParticipationRequestDTO.class))
                .collect(Collectors.toList());
    }





}
