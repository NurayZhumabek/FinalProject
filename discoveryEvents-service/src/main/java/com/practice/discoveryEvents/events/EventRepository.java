package com.practice.discoveryEvents.events;

import com.practice.discoveryEvents.util.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Integer>, JpaSpecificationExecutor<Event> {

    Optional<Event> findEventByIdAndInitiatorId(int eventId, int userId);


    Page<Event> findEventsByInitiatorId(int userId, Pageable pageable);

    List<Event> findEventsByCategoryId(int categoryId);

    Optional<Event> findEventByIdAndStatus(int eventId, Status status);





}
