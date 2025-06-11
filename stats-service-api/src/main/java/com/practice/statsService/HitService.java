package com.practice.statsService;

import java.time.LocalDateTime;
import java.util.List;

public interface HitService {

     void save(Hit hit);

     List<ViewStats> getStats(LocalDateTime start,LocalDateTime end,List<String> uris,boolean unique);

     boolean existsByIpAndUri(String ip,String uri);

}
