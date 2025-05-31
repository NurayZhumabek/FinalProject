package com.practice.discoveryEvents.compilations;

import com.practice.discoveryEvents.events.EventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CompilationMapper {

    private final EventMapper eventMapper;

    public CompilationDTO toCompilationDTO(Compilation compilation) {
        CompilationDTO dto = new CompilationDTO();
        dto.setId(compilation.getId());
        dto.setTitle(compilation.getTitle());
        dto.setPinned(compilation.getPinned());
        dto.setEvents(compilation.getEvents().stream()
                .map(eventMapper::toEventShortDTO)
                .collect(Collectors.toList()));
        return dto;
    }
}

