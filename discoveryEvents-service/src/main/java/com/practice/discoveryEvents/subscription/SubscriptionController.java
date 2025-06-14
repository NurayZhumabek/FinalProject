package com.practice.discoveryEvents.subscription;


import com.practice.discoveryEvents.categories.CategoryDTO;
import com.practice.discoveryEvents.events.Event;
import com.practice.discoveryEvents.events.EventShortDTO;
import com.practice.discoveryEvents.stats.StatsClient;
import com.practice.discoveryEvents.users.UserShortDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/subscriptions")
@Validated
public class SubscriptionController {

    private final SubsService subsService;
    private final StatsClient statsClient;

    public SubscriptionController(SubsService subsService, StatsClient statsClient) {
        this.subsService = subsService;
        this.statsClient = statsClient;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriptionDto subscribe(@RequestParam("followerId") @Min(1) Integer followerId,
                                     @RequestParam("followingId") @Min(1) Integer followingId,
                                     HttpServletRequest request) {
        statsClient.sendHit(request.getRemoteAddr(), request.getRequestURI());

        Subscription subscription = subsService.createSubscription(followerId, followingId);

        return toDto(subscription);
    }

    @GetMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDTO> getEvents(@RequestParam("followerId") @Min(1) Integer followerId,
                                         @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                         @RequestParam(defaultValue = "10") @Min(1) Integer size,
                                         HttpServletRequest request) {

        statsClient.sendHit(request.getRemoteAddr(), request.getRequestURI());

        return subsService.getEventsFromSubscriptions(followerId, from, size)
                .stream()
                .map(event -> toEventShortDto(event))
                .collect(Collectors.toList());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping
    public void unfollow(@RequestParam("followerId") @Min(1) Integer followerId,
                         @RequestParam("followingId") @Min(1) Integer followingId,
                         HttpServletRequest request) {

        statsClient.sendHit(request.getRemoteAddr(), request.getRequestURI());

        subsService.unfollow(followerId, followingId);
    }

    private SubscriptionDto toDto(Subscription sub) {
        SubscriptionDto dto = new SubscriptionDto();
        dto.setId(sub.getId());
        dto.setFollower(sub.getFollower().getId());
        dto.setFollowing(sub.getFollowing().getId());
        dto.setCreatedAt(sub.getCreatedAt());
        return dto;
    }

    public EventShortDTO toEventShortDto(Event event) {
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


}
