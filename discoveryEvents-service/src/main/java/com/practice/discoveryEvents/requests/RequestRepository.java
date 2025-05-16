package com.practice.discoveryEvents.requests;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Integer> {

    List<Request> findByRequesterId(Integer requesterId);
    Request findByRequesterIdAndEventId(Integer requesterId, Integer eventId);
    List<Request> getRequestsByEventId(Integer eventId);

}
