package com.practice.discoveryEvents.compilations;


import com.practice.discoveryEvents.categories.Category;
import com.practice.discoveryEvents.events.Event;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    int id;

    @Column(name = "title")
    String title;


    @Column(name = "pinned", nullable = false)
    Boolean pinned;


    @ManyToMany
    @JoinTable(
            name = "compilation_event",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    List<Event> events = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    Category category;
}
