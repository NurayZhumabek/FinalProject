package com.practice.discoveryEvents.users;


import com.practice.discoveryEvents.categories.CategoryDTO;
import com.practice.discoveryEvents.events.*;
import com.practice.discoveryEvents.requests.*;
import com.practice.discoveryEvents.stats.StatsClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
    private final StatsClient statsClient;

    public UserController(EventService eventService, ModelMapper modelMapper, RequestService requestService,
                          StatsClient statsClient) {
        this.eventService = eventService;
        this.modelMapper = modelMapper;
        this.requestService = requestService;
        this.statsClient = statsClient;
    }

    @GetMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDTO> getEventsByUser(@PathVariable Integer userId,
                                               @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                               @RequestParam(defaultValue = "10") @Min(1) Integer size,
                                               HttpServletRequest request) {
        statsClient.sendHit(request.getRemoteAddr(), request.getRequestURI());

        return eventService.getEventsByUser(userId, from, size)
                .stream()
                .map(this::toShortDto) // ← вот тут заменили
                .collect(Collectors.toList());
    }

    public EventShortDTO toShortDto(Event event) {
        EventShortDTO dto = new EventShortDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setAnnotation(event.getAnnotation());
        dto.setEventDate(event.getEventDate());
        dto.setPaid(event.getPaid());
        dto.setConfirmedRequests(event.getConfirmedRequests());
        dto.setViews(event.getViews());

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(event.getCategory().getId());
        categoryDTO.setName(event.getCategory().getName());
        dto.setCategory(categoryDTO);

        UserShortDTO userDTO = new UserShortDTO();
        userDTO.setId(event.getInitiator().getId());
        userDTO.setName(event.getInitiator().getName());
        dto.setInitiator(userDTO);

        return dto;
    }


    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDTO createEvent(@PathVariable Integer userId,
                                    @RequestBody @Valid NewEventDTO eventDTO,
                                    HttpServletRequest request) {
        statsClient.sendHit(request.getRemoteAddr(), request.getRequestURI());

        Event createdEvent = eventService.createEvent(eventDTO, userId);
        return modelMapper.map(createdEvent, EventFullDTO.class);

    }

    @GetMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDTO getEvent(@PathVariable Integer userId, @PathVariable Integer eventId,
                                 HttpServletRequest request) {
        statsClient.sendHit(request.getRemoteAddr(), request.getRequestURI());

        return modelMapper.map(eventService.getEventByUserById(eventId, userId), EventFullDTO.class);
    }

    @PatchMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDTO updateEvent(@PathVariable Integer userId, @PathVariable Integer eventId,
                                    @RequestBody @Valid UpdateEventUserRequest eventDTO,
                                    HttpServletRequest request) {
        statsClient.sendHit(request.getRemoteAddr(), request.getRequestURI());

        return modelMapper.map(eventService.updateEventByUser(eventId, eventDTO, userId), EventFullDTO.class);
    }


    @GetMapping("/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDTO> getRequestsByUser(@PathVariable Integer userId,
                                                           HttpServletRequest request) {
        statsClient.sendHit(request.getRemoteAddr(), request.getRequestURI());

        return requestService.getRequestsByRequesterId(userId)
                .stream()
                .map(req -> toParticipationRequestDTO(req))
                .collect(Collectors.toList());
    }


    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDTO createRequest(@PathVariable Integer userId, @RequestParam Integer eventId,
                                                 HttpServletRequest request) {
        statsClient.sendHit(request.getRemoteAddr(), request.getRequestURI());

        Request createdRequest = requestService.createRequest(userId, eventId);
        return toParticipationRequestDTO(createdRequest);

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

    @PatchMapping("/requests/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDTO cancelRequest(@PathVariable Integer userId, @PathVariable Integer requestId,
                                                 HttpServletRequest request) {
        statsClient.sendHit(request.getRemoteAddr(), request.getRequestURI());

        Request cancelledRequest = requestService.cancelRequest(requestId, userId);
        return toParticipationRequestDTO(cancelledRequest);
    }

    @GetMapping("/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDTO> getRequestsByEventAndInitiator(@PathVariable Integer eventId,
                                                                        @PathVariable Integer userId,
                                                                        HttpServletRequest request) {
        statsClient.sendHit(request.getRemoteAddr(), request.getRequestURI());

        return requestService.getRequestsByInitiator(eventId, userId)
                .stream()
                .map(req -> toParticipationRequestDTO(req))
                .collect(Collectors.toList());
    }

    @PatchMapping("/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateResult(@PathVariable Integer userId, @PathVariable Integer eventId,
                                                       @RequestBody @Valid EventRequestStatusUpdateRequestDTO dto,
                                                       HttpServletRequest request) {
        statsClient.sendHit(request.getRemoteAddr(), request.getRequestURI());

        return requestService.updateRequestsStatus(eventId, userId, dto);
    }


}
