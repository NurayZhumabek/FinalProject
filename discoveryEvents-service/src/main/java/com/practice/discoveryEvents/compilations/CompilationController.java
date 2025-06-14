package com.practice.discoveryEvents.compilations;


import com.practice.discoveryEvents.stats.StatsClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/compilations")
public class CompilationController {

    private final CompilationService compilationService;
    private final ModelMapper mapper;
    private final StatsClient statsClient;

    public CompilationController(CompilationService compilationService, ModelMapper mapper, StatsClient statsClient) {
        this.compilationService = compilationService;
        this.mapper = mapper;
        this.statsClient = statsClient;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationDTO> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                @RequestParam(defaultValue = "10") @Min(1) Integer size,
                                                HttpServletRequest request) {
        statsClient.sendHit(request.getRemoteAddr(), request.getRequestURI());

        return compilationService.getCompilations(pinned, from, size)
                .stream()
                .map(c -> mapper.map(c, CompilationDTO.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDTO getCompilationById(@PathVariable Integer compId,
                                             HttpServletRequest request) {
        statsClient.sendHit(request.getRemoteAddr(), request.getRequestURI());

        return mapper.map(compilationService.getCompilationById(compId), CompilationDTO.class);
    }
}
