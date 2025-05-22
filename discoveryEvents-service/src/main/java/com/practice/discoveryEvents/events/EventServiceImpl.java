package com.practice.discoveryEvents.events;

import com.practice.discoveryEvents.categories.Category;
import com.practice.discoveryEvents.categories.CategoryService;
import com.practice.discoveryEvents.users.User;
import com.practice.discoveryEvents.users.UserService;
import com.practice.discoveryEvents.util.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserService userService;
    private final CategoryService categoryService;

    public EventServiceImpl(EventRepository eventRepository, UserService userService, CategoryService categoryService) {
        this.eventRepository = eventRepository;
        this.userService = userService;
        this.categoryService = categoryService;
    }

    @Override
    public Event updateEventByUser(Integer eventId, UpdateEventUserRequest updated, Integer userId) {

        User initiator = userService.getUserById(userId);
        Event current = getEventByUserById(eventId, initiator.getId());

        if (current.getStatus() != Status.PENDING && current.getStatus() != Status.CANCELED) {
            throw new ConflictException("Only events in PENDING or CANCELED state can be updated");
        }

        if (updated.getEventDate() != null && !updated.getEventDate().isAfter(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Event start time must be at least 2 hours from now!");
        }

        if (updated.getStateAction() != null) {
            switch (updated.getStateAction()) {
                case SEND_TO_REVIEW -> current.setStatus(Status.PENDING);

                case CANCEL_REVIEW -> current.setStatus(Status.CANCELED);

            }
        }

        if (updated.getAnnotation() != null) current.setAnnotation(updated.getAnnotation());

        if (updated.getCategory() != null) {
            Category category = categoryService.getCategoryById(updated.getCategory());
            current.setCategory(category);
        }

        if (updated.getDescription() != null) current.setDescription(updated.getDescription());
        if (updated.getEventDate() != null) current.setEventDate(updated.getEventDate());

        if (updated.getLocation() != null) {
            Location location = new Location();
            location.setLat(updated.getLocation().getLat());
            location.setLon(updated.getLocation().getLon());
            current.setLocation(location);
        }

        if (updated.getPaid() != null) current.setPaid(updated.getPaid());
        if (updated.getParticipantLimit() != null) current.setParticipantLimit(updated.getParticipantLimit());
        if (updated.getRequestModeration() != null) current.setRequestModeration(updated.getRequestModeration());
        if (updated.getTitle() != null) current.setTitle(updated.getTitle());

        return eventRepository.save(current);
    }


    @Override
    public Event getEventByUserById(Integer eventId, Integer userId) {
        userService.getUserById(userId);
        return eventRepository.findEventByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
    }

    @Override
    public List<Event> getEventsByUser(Integer userId, Integer from, Integer size) {
        userService.getUserById(userId);

        Specification<Event> spec = EventSpecifications.hasUserId(userId);

        Pageable pageable = PageRequest.of((from / size), size);

        return eventRepository.findAll(spec, pageable).getContent();
    }

    @Override
    public Event createEvent(NewEventDTO newEventDTO, Integer userId) {

        User initiator = userService.getUserById(userId);
        Event event = new Event();

        LocalDateTime eventTime = newEventDTO.getEventDate();
        if (!eventTime.isAfter(LocalDateTime.now().plusHours(2))) {
            throw new AccessDeniedException("Event date must be created at least 2 hours from now!");
        }
        event.setEventDate(eventTime);
        event.setInitiator(initiator);

        if (newEventDTO.getCategory() != null) {
            Category category = categoryService.getCategoryById(newEventDTO.getCategory());
            event.setCategory(category);
        }

        event.setDescription(newEventDTO.getDescription());

        if (newEventDTO.getLocation() != null) {
            Location location = new Location();
            location.setLat(newEventDTO.getLocation().getLat());
            location.setLon(newEventDTO.getLocation().getLon());
            event.setLocation(location);
        }
        if (event.getConfirmedRequests() == null) {
            event.setConfirmedRequests(0);
        }

        event.setAnnotation(newEventDTO.getAnnotation());
        event.setPaid(newEventDTO.getPaid());
        event.setParticipantLimit(newEventDTO.getParticipantLimit());
        event.setRequestModeration(newEventDTO.getRequestModeration());

        event.setTitle(newEventDTO.getTitle());

        event.setStatus(Status.PENDING);
        event.setCreatedOn(LocalDateTime.now());
        return eventRepository.save(event);

    }


    @Override
    public Event getPublicEventById(Integer eventId) {
        return eventRepository.findEventByIdAndStatus(eventId, Status.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Event not found or not published"));
    }


    @Override
    public List<Event> getPublishedEvents(EventFilterParams eventFilter) {

        Specification<Event> spec = Specification.where(null);

        spec = spec.and(EventSpecifications.hasStatus(Status.PUBLISHED));

        if (eventFilter.getText() != null && !eventFilter.getText().isEmpty()) {
            spec = spec.and(EventSpecifications.hasText(eventFilter.getText()));
        }

        if (eventFilter.getCategoryIds() != null && !eventFilter.getCategoryIds().isEmpty()) {
            spec = spec.and(EventSpecifications.hasCategoryIds(eventFilter.getCategoryIds()));
        }

        if (eventFilter.getPaid() != null) {
            spec = spec.and(EventSpecifications.isPaid(eventFilter.getPaid()));
        }

        spec = getCommonEventSpecification(spec, eventFilter.getRangeStart(), eventFilter.getRangeEnd());

        if (Boolean.TRUE.equals(eventFilter.getOnlyAvailable())) {
            spec = spec.and(EventSpecifications.isAvailable());
        }

        int from = eventFilter.getFrom();
        int size = eventFilter.getSize();

        Sort sort = Sort.unsorted();

        if (eventFilter.getSortBy() != null) {
            switch (eventFilter.getSortBy()) {
                case VIEWS -> sort = Sort.by(Sort.Direction.DESC, "views");
                case EVENT_DATE -> sort = Sort.by(Sort.Direction.DESC, "eventDate");
            }
        }

        Pageable pageable = PageRequest.of((from / size), size, sort);

        return eventRepository.findAll(spec, pageable).getContent();
    }

    @Override
    public List<Event> searchEventsByAdmin(EventAdminFilterParams eventFiler) {

        Specification<Event> spec = Specification.where(null);

        if (eventFiler.getUserIds() != null && !eventFiler.getUserIds().isEmpty()) {
            spec = spec.and(EventSpecifications.hasUserIds(eventFiler.getUserIds()));
        }

        if (eventFiler.getStatuses() != null && !eventFiler.getStatuses().isEmpty()) {
            spec = spec.and(EventSpecifications.hasState(eventFiler.getStatuses()));
        }

        if (eventFiler.getCategoryIds() != null && !eventFiler.getCategoryIds().isEmpty()) {
            spec = spec.and(EventSpecifications.hasCategoryIds(eventFiler.getCategoryIds()));
        }

        spec = getCommonEventSpecification(spec, eventFiler.getRangeStart(), eventFiler.getRangeEnd());

        int from = eventFiler.getFrom();
        int size = eventFiler.getSize();

        Pageable pageable = PageRequest.of((from / size), size);

        return eventRepository.findAll(spec, pageable).getContent();
    }

    @Override
    public Event updateEventByAdmin(Integer eventId, UpdateEventAdminRequestDTO updated) {

        Event current = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " not found"));

        if (updated.getStateAction() != null) {
            switch (updated.getStateAction()) {
                case PUBLISH_EVENT -> {
                    if (current.getStatus() != Status.PENDING) {
                        throw new ConflictException("Only PENDING status events can be updated!");
                    }
                    if (updated.getEventDate() != null && !updated.getEventDate().isAfter(LocalDateTime.now().plusHours(1))) {
                        throw new ConflictException("Event start time must be after at least 1 hour from now!");
                    }
                    current.setStatus(Status.PUBLISHED);
                    current.setPublishedOn(LocalDateTime.now());
                }

                case REJECT_EVENT -> {
                    if (current.getStatus() == Status.PUBLISHED) {
                        throw new ConflictException("PUBLISHED status cannot be rejected!");
                    }
                    current.setStatus(Status.CANCELED);
                }
            }
        }

        if (updated.getAnnotation() != null) current.setAnnotation(updated.getAnnotation());
        if (updated.getDescription() != null) current.setDescription(updated.getDescription());
        if (updated.getTitle() != null) current.setTitle(updated.getTitle());
        if (updated.getEventDate() != null) current.setEventDate(updated.getEventDate());
        if (updated.getParticipantLimit() != null) current.setParticipantLimit(updated.getParticipantLimit());
        if (updated.getRequestModeration() != null) current.setRequestModeration(updated.getRequestModeration());
        if (updated.getLocation() != null) {
            Location location = new Location();
            location.setLat(updated.getLocation().getLat());
            location.setLon(updated.getLocation().getLon());
            current.setLocation(location);
        }
        if (updated.getCategoryId() != null) {
            Category category = categoryService.getCategoryById(updated.getCategoryId());
            current.setCategory(category);
        }

        return eventRepository.save(current);
    }


    private Specification<Event> getCommonEventSpecification(Specification<Event> spec, LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeStart != null && rangeEnd != null) {
            spec = spec.and(EventSpecifications.isBetween(rangeStart, rangeEnd));
        } else if (rangeStart != null) {
            spec = spec.and(EventSpecifications.isAfterStart(rangeStart));
        } else if (rangeEnd != null) {
            spec = spec.and(EventSpecifications.isBeforeEnd(rangeEnd));
        } else {
            spec = spec.and(EventSpecifications.isAfterStart(LocalDateTime.now()));
        }
        return spec;
    }

}
