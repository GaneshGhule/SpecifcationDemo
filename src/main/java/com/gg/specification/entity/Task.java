package com.gg.specification.entity;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.time.Instant;


@JsonIdentityInfo(
		  generator = ObjectIdGenerators.PropertyGenerator.class, 
		  property = "id")
@Entity
@Table(catalog = "specificationDemo")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private Instant completedOn;

    @Column
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @JoinColumn(name = "userId")
    @ManyToOne(cascade = CascadeType.ALL)
    private User user;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getCompletedOn() {
        return completedOn;
    }

    public void setCompletedOn(Instant completedOn) {
        this.completedOn = completedOn;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
