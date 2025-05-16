package com.practice.discoveryEvents.events;

import com.practice.discoveryEvents.categories.Category;
import com.practice.discoveryEvents.categories.CategoryService;
import com.practice.discoveryEvents.users.User;
import com.practice.discoveryEvents.users.UserService;
import com.practice.discoveryEvents.util.AccessDeniedException;
import com.practice.discoveryEvents.util.Location;
import com.practice.discoveryEvents.util.NotFoundException;
import com.practice.discoveryEvents.util.Status;
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
    public Event updateEventByUser(int eventId, UpdateEventUserRequest updated, int userId) {

        User initiator = userService.getUserById(userId);
        Event current = getEventByUserById(eventId, initiator.getId());

        if (updated.getStateAction() != null) {
            switch (updated.getStateAction()) {
                case SEND_TO_REVIEW -> {
                    if (current.getStatus() == Status.PUBLISHED) {
                        throw new AccessDeniedException("PUBLISHED status cannot be changed!");
                    }

                    if (updated.getEventDate() != null && !updated.getEventDate().isAfter(LocalDateTime.now().plusHours(2))) {
                        throw new AccessDeniedException("Event start time must be after at least 2 hour from now!");
                    }
                    current.setStatus(Status.PENDING);
                }
                case CANCEL_REVIEW -> {
                    if (current.getStatus() == Status.PUBLISHED) {
                        throw new AccessDeniedException("PUBLISHED status cannot be changed!");
                    }
                    current.setStatus(Status.CANCELED);
                }
            }
        }
        // ТУТ ГДЕ ЕСТЬ АННОТАЦИИ И ЗАЧЕМ ПРОВЕРЯТЬ НО null МНЕ КАЖЕТСЯ ЛОГИЧНО ПРОВЕРЯТЬ ТОЛЬКО СУЩЕСТВУЕТ ЛИ ИМЕННО КАТЕГОРИЯ

        if (updated.getAnnotation() != null) current.setAnnotation(updated.getAnnotation());

        if (updated.getCategoryId() != null) {
            Category category = categoryService.getCategoryById(updated.getCategoryId());
            current.setCategory(category);
        }

        if (updated.getDescription() != null) current.setDescription(updated.getDescription());
        if (updated.getEventDate() != null) current.setEventDate(updated.getEventDate());

        if (updated.getLocation() != null) {
            Location location = new Location();
            location.setLatitude(updated.getLocation().getLatitude());
            location.setLongitude(updated.getLocation().getLongitude());
            current.setLocation(location);
        }

        if (updated.getPaid() != null) current.setPaid(updated.getPaid());
        if (updated.getParticipantLimit() != null) current.setParticipantLimit(updated.getParticipantLimit());
        if (updated.getRequestModeration() != null) current.setRequestModeration(updated.getRequestModeration());
        if (updated.getTitle() != null) current.setTitle(updated.getTitle());

        return eventRepository.save(current);
    }


    @Override
    public Event getEventByUserById(int eventId, int userId) {
        userService.getUserById(userId);
        return eventRepository.findEventByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
    }

    @Override
    public List<Event> getEventsByUser(int userId, int from, int size) {
        userService.getUserById(userId);

        Pageable pageable = PageRequest.of((from / size), size);

        return eventRepository.findEventsByInitiatorId(userId, pageable).getContent();
    }

    @Override
    public Event createEvent(NewEventDTO newEventDTO, int userId) {

        User initiator = userService.getUserById(userId);
        Event event = new Event();

        LocalDateTime eventTime = newEventDTO.getEventDate();
        if (!eventTime.isAfter(LocalDateTime.now().plusHours(2))) {
            throw new AccessDeniedException("Event date must be created at least 2 hours from now!");
        }
        event.setEventDate(eventTime);
        event.setInitiator(initiator);

        if (newEventDTO.getCategoryId() != null) {
            Category category = categoryService.getCategoryById(newEventDTO.getCategoryId());
            event.setCategory(category);
        }

        event.setDescription(newEventDTO.getDescription());

        if (newEventDTO.getLocation() != null) {
            Location location = new Location();
            location.setLatitude(newEventDTO.getLocation().getLatitude());
            location.setLongitude(newEventDTO.getLocation().getLongitude());
            event.setLocation(location);
        }

        event.setPaid(newEventDTO.getPaid());
        event.setParticipantLimit(newEventDTO.getParticipantLimit());
        event.setRequestModeration(newEventDTO.getRequestModeration());

        event.setTitle(newEventDTO.getTitle());

        event.setStatus(Status.PENDING);
        event.setCreatedOn(LocalDateTime.now());
        return eventRepository.save(event);

    }


    @Override
    public Event getPublicEventById(int eventId) {
        return eventRepository.findEventByIdAndStatus(eventId, Status.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Event not found or not published"));
    }


    /*
   GET/events
        Получение событий с возможностью фильтрации */
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
    public Event updateEventByAdmin(int eventId, UpdateEventAdminRequestDTO updated) {

        Event current = getPublicEventById(eventId);

        if (updated.getStateAction() != null) {
            switch (updated.getStateAction()) {
                case PUBLISH_EVENT -> {
                    if (current.getStatus() != Status.PENDING) {
                        throw new AccessDeniedException("Only PENDING status events can be updated!");
                    }
                    if (updated.getEventDate() != null && !updated.getEventDate().isAfter(LocalDateTime.now().plusHours(1))) {
                        throw new AccessDeniedException("Event start time must be after at least 1 hour from now!");
                    }
                    current.setStatus(Status.PUBLISHED);
                }

                case REJECT_EVENT -> {
                    if (current.getStatus() == Status.PUBLISHED) {
                        throw new AccessDeniedException("PUBLISHED status cannot be rejected!");
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
            location.setLatitude(updated.getLocation().getLatitude());
            location.setLongitude(updated.getLocation().getLongitude());
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
        }
        return spec;
    }

}
