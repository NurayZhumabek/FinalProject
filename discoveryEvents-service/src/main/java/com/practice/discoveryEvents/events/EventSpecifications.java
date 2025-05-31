package com.practice.discoveryEvents.events;

import com.practice.discoveryEvents.util.State;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

public class EventSpecifications {

    public static Specification<Event> hasText(String text) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), "%" + text.toLowerCase() + "%"),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + text.toLowerCase() + "%")
        );
    }

    // for public and admin
    public static Specification<Event> hasCategoryIds(List<Integer> ids) {
        return (root, query, criteriaBuilder) -> root.get("category").get("id").in(ids);
    }

    public static Specification<Event> isPaid(Boolean isPaid) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("paid"), isPaid);
    }

    public static Specification<Event> hasState(State state) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("state"), state);
    }

    // for public and admin
    public static Specification<Event> isBetween(LocalDateTime start, LocalDateTime end) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("eventDate"), start, end);
    }

    // for public and admin
    public static Specification<Event> isAfterStart(LocalDateTime start) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), start);
    }

    // for public and admin
    public static Specification<Event> isBeforeEnd(LocalDateTime end) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), end);
    }

    public static Specification<Event> isAvailable() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get("participantLimit"), root.get("confirmedRequests"));
    }


    //for method getEventByAdmin

    public static Specification<Event> hasUserIds(List<Integer> userIds) {
        return (root, query, criteriabuilder) -> root.get("initiator").get("id").in(userIds);
    }

    public static Specification<Event> hasStates(List<State> statuses) {
        return ((root, query, criteriaBuilder) -> root.get("state").in(statuses));
    }

    public static Specification<Event> hasUserId(Integer userId) {
        return (root, query, criteriabuilder) -> root.get("initiator").get("id").in(userId);
    }




}
