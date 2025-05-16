package com.practice.discoveryEvents.requests;


import com.practice.discoveryEvents.events.Event;
import com.practice.discoveryEvents.users.User;
import com.practice.discoveryEvents.util.RequestStatus;
import com.practice.discoveryEvents.util.Status;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "requests")
@Entity
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    Event event;

    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    User requester;


    @Column(name = "created_at", nullable = false)
    LocalDateTime created;


    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    RequestStatus status;


}
