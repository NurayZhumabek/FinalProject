package com.practice.discoveryEvents.subscription;


import com.practice.discoveryEvents.events.Event;
import com.practice.discoveryEvents.events.EventService;
import com.practice.discoveryEvents.users.User;
import com.practice.discoveryEvents.users.UserService;
import com.practice.discoveryEvents.util.ConflictException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubsServiceImpl implements SubsService {

    private final SubscriptionRepo subscriptionRepo;
    private final EventService eventService;
    private final UserService userService;

    public SubsServiceImpl(SubscriptionRepo subscriptionRepo, EventService eventService, UserService userService) {
        this.subscriptionRepo = subscriptionRepo;
        this.eventService = eventService;
        this.userService = userService;
    }

    @Override
    public Subscription createSubscription(Integer followerUserId, Integer followingUserId) {

        User follower = userService.getUserById(followerUserId);
        User following = userService.getUserById(followingUserId);

        if (followerUserId.equals(followingUserId)) {
            throw new ConflictException("Follower and Following are the same");
        }
        if (subscriptionRepo.findByFollowerIdAndFollowingId(followerUserId, followingUserId) != null) {
            throw new ConflictException("You have already subscribed to this user");
        }

        Subscription subscription = new Subscription();
        subscription.setCreatedAt(LocalDateTime.now());
        subscription.setFollower(follower);
        subscription.setFollowing(following);

        return subscriptionRepo.save(subscription);
    }

    @Override
    public List<Event> getEventsFromSubscriptions(Integer followerUserId, Integer from, Integer size) {

        List<Integer> followingUsers = subscriptionRepo.findFollowingUserIdsByFollowerId(followerUserId);

        return eventService.getActualPublishedEvents(followingUsers, from, size);

    }

    @Override
    public void unfollow(Integer followerUserId, Integer followingUserId ) {

        if (followerUserId.equals(followingUserId)) {
            throw new ConflictException("Follower and Following are the same");
        }
        if (subscriptionRepo.findByFollowerIdAndFollowingId(followerUserId, followingUserId) == null) {
            throw new ConflictException("You do not  subscribe to this user");
        }

        subscriptionRepo.deleteByFollowerIdAndFollowingId(followerUserId, followingUserId);
    }
}
