package com.practice.statsService;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface  HitRepository extends JpaRepository<Hit, Integer> {
    @Query("SELECT new com.practice.statsService.ViewStats (h.app, h.uri, COUNT(h.ip)) " +
            "FROM Hit h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC")
    List<ViewStats> getAll(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new com.practice.statsService.ViewStats(h.app, h.uri, COUNT(DISTINCT h.ip)) " +
            "FROM Hit h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC")
    List<ViewStats> getAllUnique(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new com.practice.statsService.ViewStats(h.app, h.uri, COUNT(h.ip)) " +
            "FROM Hit h " +
            "WHERE h.timestamp BETWEEN :start AND :end AND h.uri IN :uris " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC")
    List<ViewStats> getByUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new com.practice.statsService.ViewStats(h.app, h.uri, COUNT(DISTINCT h.ip)) " +
            "FROM Hit h " +
            "WHERE h.timestamp BETWEEN :start AND :end AND h.uri IN :uris " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC")
    List<ViewStats> getByUrisUnique(LocalDateTime start, LocalDateTime end, List<String> uris);


    Optional<Hit> findByIpAndUri(String ip, String uri);
}
