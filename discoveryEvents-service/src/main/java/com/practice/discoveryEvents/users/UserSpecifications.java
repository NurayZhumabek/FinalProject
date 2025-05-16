package com.practice.discoveryEvents.users;

import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class UserSpecifications {

    public static Specification<User> byIds(List<Integer> ids) {
        return (root, query, criteriaBuilder) -> root.get("id").in(ids);
    }
}
