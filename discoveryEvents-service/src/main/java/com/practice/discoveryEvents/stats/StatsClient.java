package com.practice.discoveryEvents.stats;


import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class StatsClient {

    private final RestTemplate restTemplate;

    public StatsClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendHit(String ip, String uri) {
        EndpointHit hit = new EndpointHit();
        hit.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        hit.setIp(ip);
        hit.setUri(uri);
        hit.setApp("afisha-app");

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EndpointHit> request = new HttpEntity<>(hit, headers);

        restTemplate.postForEntity("http://stats-service-server:9090/hit", request, Void.class);
    }

    public boolean getHit(String ip, String uri) {

        String url = "http://stats-service-server:9090/hit?ip=" + ip + "&uri=" + uri;

        ResponseEntity<Boolean> response = restTemplate.getForEntity(url, Boolean.class);

        return response.getBody();

    }
}
