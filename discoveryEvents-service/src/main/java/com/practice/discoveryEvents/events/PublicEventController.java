package com.practice.discoveryEvents.events;


import com.practice.discoveryEvents.stats.EndpointHit;
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
    private final ModelMapper modelMapper;

    public PublicEventController(EventService eventService, ModelMapper mapper, StatsClient statsClient, ModelMapper modelMapper) {
        this.eventService = eventService;
        this.mapper = mapper;
        this.statsClient = statsClient;
        this.modelMapper = modelMapper;
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
    public EventFullDTO getPublicEventsById(@PathVariable int id, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();

        boolean isExists = statsClient.getHit(ip, uri);

        Event event = eventService.getEventById(id, isExists);

        if (!isExists) {
            statsClient.sendHit(ip, uri);
        }

        return modelMapper.map(event, EventFullDTO.class);
    }

}
