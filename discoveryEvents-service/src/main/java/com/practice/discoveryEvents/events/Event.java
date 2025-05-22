package com.practice.discoveryEvents.events;


import com.practice.discoveryEvents.categories.Category;
import com.practice.discoveryEvents.compilations.Compilation;
import com.practice.discoveryEvents.requests.Request;
import com.practice.discoveryEvents.users.User;
import com.practice.discoveryEvents.util.Location;
import com.practice.discoveryEvents.util.Status;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "events")
@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id;

    @Column(name = "title")
    String title;

    @Column(name = "annotation")
    String annotation;

    @Column(name = "description")
    String description;

    @Column(name = "paid", nullable = false)
    Boolean paid;

    @Column(name = "eventDate", nullable = false)
    LocalDateTime eventDate;

    @Embedded
    Location location;

    @Column(name = "participantLimit")
    Integer participantLimit;


    @Column(name = "requestModeration", nullable = false)
    Boolean requestModeration;


    @Column(name = "confirmedRequests")
    Integer confirmedRequests;


    @Column(name = "views")
    Integer views;

    @ManyToOne
    @JoinColumn(name = "initiator_id", nullable = false)
    User initiator;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    Category category;

    @OneToMany(mappedBy = "event")
    List<Request> requests;

    @ManyToMany(mappedBy = "events")
    List<Compilation> compilations = new ArrayList<>();


    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    Status status;


    @Column(name = "created_on", nullable = false)
    LocalDateTime createdOn;

    @Column(name = "published_on")
    LocalDateTime publishedOn;
}
