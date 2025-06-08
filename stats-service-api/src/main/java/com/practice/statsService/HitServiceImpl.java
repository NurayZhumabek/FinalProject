package com.practice.statsService;


import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HitServiceImpl implements HitService {

    private final HitRepository hitRepository;

    public HitServiceImpl(HitRepository hitRepository) {
        this.hitRepository = hitRepository;
    }

    @Override
    public void save(Hit hit) {
        hitRepository.save(hit);
    }

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (unique) {
            return uris == null ? hitRepository.getAllUnique(start, end)
                    : hitRepository.getByUrisUnique(start, end, uris);
        } else {
            return uris == null ? hitRepository.getAll(start, end)
                    : hitRepository.getByUris(start, end, uris);
        }
    }
}
