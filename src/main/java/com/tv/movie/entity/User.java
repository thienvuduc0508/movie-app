package com.tv.movie.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tv.movie.enums.Role;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private boolean emailVerified = false;

    @Column(unique = true)
    private String verificationToken = UUID.randomUUID().toString();

    @Column
    private Instant verificationTokenExpiry = Instant.now().plusSeconds(86400);

    @Column
    private String passwordResetToken;

    @Column
    private Instant passwordResetTokenExpiry;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column
    private Instant updatedAt;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_watchlist",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "video_id")
    )
    private Set<Video> watchlist = new HashSet<>();

    public void addToWatchlist(Video video) {
        watchlist.add(video);
    }

    public void removeFromWatchlist(Video video) {
        watchlist.remove(video);
    }
}
