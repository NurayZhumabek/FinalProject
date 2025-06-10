package com.practice.discoveryEvents.events;


import com.practice.discoveryEvents.stats.StatsClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/events")
@Validated
public class PublicEventController {

    private final EventService eventService;
    private final ModelMapper mapper;
    private final StatsClient statsClient;

    public PublicEventController(EventService eventService, ModelMapper mapper, StatsClient statsClient) {
        this.eventService = eventService;
        this.mapper = mapper;
        this.statsClient = statsClient;
    }

    @GetMapping
    public List<EventShortDTO> getPublicEvents(@ModelAttribute @Valid EventFilterParams eventFilterParams,
                                               HttpServletRequest request) {

        statsClient.sendHit(request.getRemoteAddr(), request.getRequestURI());

        return eventService.getPublishedEvents(eventFilterParams)
                .stream()
                .map(e -> mapper.map(e, EventShortDTO.class))
                .collect(Collectors.toList());
    }


    @GetMapping("/{id}")
    public EventFullDTO getPublicEventsById(@PathVariable int id,
                                            HttpServletRequest request) {
        statsClient.sendHit(request.getRemoteAddr(), request.getRequestURI());

        return mapper.map(eventService.getEventById(id,request.getRemoteAddr(),request.getRequestURI()), EventFullDTO.class);
    }


}
