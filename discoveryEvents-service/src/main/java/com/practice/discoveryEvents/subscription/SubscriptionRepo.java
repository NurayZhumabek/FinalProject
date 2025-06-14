package com.practice.discoveryEvents.subscription;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface SubscriptionRepo extends JpaRepository<Subscription, Integer> {

    Subscription findByFollowerIdAndFollowingId(Integer followerId, Integer followingId);

    @Query(value = "SELECT  s.following.id FROM Subscription  s  WHERE s.follower.id =:followerId")
    List<Integer> findFollowingUserIdsByFollowerId(@Param("followerId") Integer followerId);

    void deleteByFollowerIdAndFollowingId(Integer followerId, Integer followingId);

}
