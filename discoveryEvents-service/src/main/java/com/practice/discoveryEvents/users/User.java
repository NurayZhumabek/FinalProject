package com.practice.discoveryEvents.users;


import com.practice.discoveryEvents.events.Event;
import com.practice.discoveryEvents.requests.Request;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id;

    @Column(name = "name")
    String name;

    @Column(name = "email")
    String email;

    @OneToMany(mappedBy = "initiator")
    List<Event> events;

    @OneToMany(mappedBy = "requester")
    List<Request> requests;


}
