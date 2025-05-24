package com.practice.discoveryEvents.compilations;


import com.practice.discoveryEvents.events.Event;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "compilations")
@Entity
public class Compilation {

    @ManyToMany
    @JoinTable(
            name = "compilation_event",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    List<Event> events = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id;

    @Column(name = "pinned", nullable = false)
    Boolean pinned;

    @Column(name = "title")
    @Size(min = 1, max = 50, message = "Title must be between 1 and 50 characters")
    String title;

}
