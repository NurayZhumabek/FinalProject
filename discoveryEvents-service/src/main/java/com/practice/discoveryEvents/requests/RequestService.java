package com.practice.discoveryEvents.requests;

import java.util.List;

public interface RequestService {

    List<Request> getRequestsByRequesterId(Integer requesterId);
    Request createRequest(Integer userId, Integer eventId);
    Request cancelRequest(Integer requestId, Integer requesterId);
    List<Request> getRequestsByInitiator(Integer eventId,Integer initiatorId);
    EventRequestStatusUpdateResult updateRequestsStatus(Integer eventId,Integer initiatorId, EventRequestStatusUpdateRequestDTO requests);
}
