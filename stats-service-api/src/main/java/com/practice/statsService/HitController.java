package com.practice.statsService;


import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
public class HitController {

    private final HitService hitService;
    private final ModelMapper modelMapper;

    public HitController(HitService hitService, ModelMapper modelMapper) {
        this.hitService = hitService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/hit")
    public ResponseEntity<Void> saveHit(@RequestBody EndpointHit endpointHit) {
        Hit hit = modelMapper.map(endpointHit, Hit.class);
        hitService.save(hit);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/response")
    public String response() {
        return "hit response";
    }


    @GetMapping("/stats")
    public List<ViewStats> getStats(@RequestParam String start,
                                    @RequestParam String end,
                                    @RequestParam(required = false) List<String> uris,
                                    @RequestParam(defaultValue = "false") boolean unique) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDate = LocalDateTime.parse(start, formatter);
        LocalDateTime endDate = LocalDateTime.parse(end, formatter);

        return hitService.getStats(startDate, endDate, uris, unique);
    }
}
