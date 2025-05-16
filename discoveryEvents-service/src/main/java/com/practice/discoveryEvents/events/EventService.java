package com.practice.discoveryEvents.events;

import java.util.List;

public interface EventService {


    // пока неизвестно но надо ли вызывать на контролллере Users
    // так как путь GET///users/{userId}/events
    List<Event> getEventsByUser(int userId, int from, int size);

    Event createEvent(NewEventDTO event, int userId);
    Event getEventByUserById(int eventId,int userId);
    Event updateEventByUser(int eventId,UpdateEventUserRequest event,int userId);


    // public GET events/{id}
    Event getPublicEventById(int eventId);

    /*
           GET/events
                Получение событий с возможностью фильтрации */
    List<Event> getPublishedEvents(EventFilterParams eventFilter);

    List<Event> searchEventsByAdmin(EventAdminFilterParams eventFiler);

    Event updateEventByAdmin(int eventId, UpdateEventAdminRequestDTO updated);
}
