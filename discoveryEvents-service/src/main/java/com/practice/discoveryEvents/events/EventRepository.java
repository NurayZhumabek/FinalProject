package com.practice.discoveryEvents.events;

import com.practice.discoveryEvents.util.State;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Integer>, JpaSpecificationExecutor<Event> {

    Optional<Event> findEventByIdAndInitiatorId(int eventId, int userId);



    List<Event> findEventsByCategoryId(int categoryId);

    Optional<Event> findEventByIdAndStatus(int eventId, State status);

    Boolean existsByCategoryId(int categoryId);





}
