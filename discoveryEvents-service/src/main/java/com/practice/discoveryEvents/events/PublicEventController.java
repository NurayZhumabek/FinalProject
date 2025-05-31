package com.practice.discoveryEvents.events;


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

    public PublicEventController(EventService eventService, ModelMapper mapper) {
        this.eventService = eventService;
        this.mapper = mapper;
    }

    @GetMapping
    public List<EventShortDTO> getPublicEvents(@ModelAttribute @Valid EventFilterParams eventFilterParams) {

        return eventService.getPublishedEvents(eventFilterParams)
                .stream()
                .map(e->mapper.map(e,EventShortDTO.class))
                .collect(Collectors.toList());
    }


    @GetMapping("/{id}")
    public EventFullDTO getPublicEventsById(@PathVariable int id) {
        return mapper.map(eventService.getEventById(id),EventFullDTO.class);
    }



}
