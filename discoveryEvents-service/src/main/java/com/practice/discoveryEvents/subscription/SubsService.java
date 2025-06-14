package com.practice.discoveryEvents.subscription;


import com.practice.discoveryEvents.events.Event;

import java.util.List;

public interface SubsService {

    Subscription createSubscription(Integer followerUserId, Integer followingUserId);

    List<Event> getEventsFromSubscriptions(Integer followerUserId, Integer from, Integer size);

    void unfollow(Integer followerUserId, Integer followingUserId);


}
