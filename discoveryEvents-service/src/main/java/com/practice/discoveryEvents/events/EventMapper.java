package com.practice.discoveryEvents.events;

import com.practice.discoveryEvents.categories.Category;
import com.practice.discoveryEvents.categories.CategoryDTO;
import com.practice.discoveryEvents.users.User;
import com.practice.discoveryEvents.users.UserShortDTO;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public EventShortDTO toEventShortDTO(Event event) {
        if (event == null) return null;

        EventShortDTO dto = new EventShortDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setAnnotation(event.getAnnotation());
        dto.setPaid(event.getPaid());
        dto.setEventDate(event.getEventDate());
        dto.setConfirmedRequests(event.getConfirmedRequests());
        dto.setViews(event.getViews());

        dto.setInitiator(toUserShortDTO(event.getInitiator()));
        dto.setCategory(toCategoryDTO(event.getCategory()));

        return dto;
    }

    private UserShortDTO toUserShortDTO(User user) {
        if (user == null) return null;
        UserShortDTO dto = new UserShortDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        return dto;
    }

    private CategoryDTO toCategoryDTO(Category category) {
        if (category == null) return null;
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }
}
