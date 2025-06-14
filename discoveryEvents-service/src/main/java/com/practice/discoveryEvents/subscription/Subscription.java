package com.practice.discoveryEvents.subscription;


import com.practice.discoveryEvents.users.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id;

    @ManyToOne
    User follower;


    @ManyToOne
    User following;


    @Column(name = "created_at")
    LocalDateTime createdAt;

}
