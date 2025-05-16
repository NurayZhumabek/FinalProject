package com.practice.discoveryEvents.compilations;


import com.practice.discoveryEvents.events.Event;
import com.practice.discoveryEvents.events.EventRepository;
import com.practice.discoveryEvents.events.EventService;
import com.practice.discoveryEvents.util.NotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventService eventService;

    public CompilationServiceImpl(CompilationRepository compilationRepository, EventService eventService) {
        this.compilationRepository = compilationRepository;
        this.eventService = eventService;
    }

    @Override
    public List<Compilation> getCompilations(Boolean pinned, Integer from, Integer size) {

        Pageable pageable = PageRequest.of((from / size), size);

        if (pinned != null)
            return compilationRepository.getCompilationsByPinned(pinned, pageable);
        else
            return compilationRepository.findAll(pageable).getContent();

    }

    @Override
    public Compilation getCompilationById(Integer id) {
        return compilationRepository.findById(id).orElseThrow(() -> new NotFoundException("Compilation with id=" + id + " not found."));
    }

    @Override
    public Compilation createCompilation(NewCompilationDTO newCompilationDTO) {
        List<Integer> eventIds = new ArrayList<>(newCompilationDTO.getEventsIds());

        List<Event> events = checkEvents(eventIds);

        Compilation compilation = new Compilation();

        compilation.setEvents(events);
        compilation.setPinned(newCompilationDTO.getPinned());
        compilation.setTitle(newCompilationDTO.getTitle());
        return compilationRepository.save(compilation);
    }

    @Override
    public void deleteCompilationById(Integer id) {
        if (!compilationRepository.existsById(id)) {
            throw new NotFoundException("Compilation with id=" + id + " not found.");
        }
        compilationRepository.deleteById(id);
    }

    @Override
    public Compilation updateCompilationById(Integer id, UpdateCompilationRequestDTO updateCompilationDTO) {

        Compilation compilation = getCompilationById(id);

        List<Integer> eventIds = new ArrayList<>(updateCompilationDTO.getEventIds());
        List<Event> events = checkEvents(eventIds);

        if (!events.isEmpty()) {
            compilation.getEvents().clear();
            compilation.setEvents(events);
        }
        if (updateCompilationDTO.getTitle() != null && !updateCompilationDTO.getTitle().isBlank())
            compilation.setTitle(updateCompilationDTO.getTitle());
        if (updateCompilationDTO.getPinned() != null) compilation.setPinned(updateCompilationDTO.getPinned());


        return compilationRepository.save(compilation);
    }

    private List<Event> checkEvents(List<Integer> ids) {
        List<Event> events = new ArrayList<>();
        if (!ids.isEmpty()) {
            events.addAll(ids.stream().distinct().map(eventService::getPublicEventById).collect(Collectors.toList()));

        }
        return events;
    }
}
