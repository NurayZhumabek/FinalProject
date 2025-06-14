package com.practice.discoveryEvents.events;

import java.util.List;

public interface EventService {



    List<Event> getEventsByUser(Integer userId,Integer from, Integer size);

    Event createEvent(NewEventDTO event, Integer userId);
    Event getEventByUserById(Integer eventId,Integer userId);
    Event updateEventByUser(Integer eventId,UpdateEventUserRequest event,Integer userId);


    Event getPublicEventById(Integer eventId);

    Event getEventById(Integer eventId,boolean  isExists);

    List<Event> getPublishedEvents(EventFilterParams eventFilter);

    List<Event> searchEventsByAdmin(EventAdminFilterParams eventFiler);

    Event updateEventByAdmin(Integer eventId, UpdateEventAdminRequestDTO updated);
    Event findEventById(Integer eventId);

    List<Event> getActualPublishedEvents(List<Integer> userIds,Integer from, Integer size);
}
