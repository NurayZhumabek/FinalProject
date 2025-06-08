package com.practice.statsService;


import org.modelmapper.ModelMapper;
import org.springframework.format.annotation.DateTimeFormat;
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
    public List<ViewStats> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime  end,
                                    @RequestParam(required = false) List<String> uris,
                                    @RequestParam(defaultValue = "false") boolean unique) {

        return hitService.getStats(start, end, uris, unique);
    }
}
