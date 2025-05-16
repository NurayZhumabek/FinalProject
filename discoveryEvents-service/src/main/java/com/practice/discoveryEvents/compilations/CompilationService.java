package com.practice.discoveryEvents.compilations;

import java.util.List;

public interface CompilationService {

    List<Compilation> getCompilations(Boolean pinned, Integer from ,Integer size);
    Compilation getCompilationById(Integer id);
    Compilation createCompilation(NewCompilationDTO newCompilationDTO);
    void deleteCompilationById(Integer id);
    Compilation updateCompilationById(Integer id, UpdateCompilationRequestDTO updateCompilationDTO);
}
