package com.practice.discoveryEvents.compilations;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Integer> {


    List<Compilation> getCompilationsByPinned(Boolean pinned,Pageable pageable);
}
