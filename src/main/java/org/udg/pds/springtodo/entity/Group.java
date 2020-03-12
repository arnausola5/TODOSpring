package org.udg.pds.springtodo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.Fetch;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Entity(name = "usergroup")
public class Group implements Serializable {
    private static final long serialVersionUID = 1L;

    public Group() {}

    public Group(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_owner")
    private User owner;

    @ManyToMany(cascade = CascadeType.ALL)
    private Collection<User> members = new ArrayList<>();

    @Column(name = "fk_owner", insertable = false, updatable = false)
    private Long ownerId;

    @JsonView(Views.Private.class)
    public Long getId() { return id; }

    @JsonIgnore
    public User getOwner() { return owner; }

    public void setOwner(User owner) { this.owner = owner; }

    @JsonIgnore
    public Collection<User> getMembers() { return members; }

    public void addMember(User member) { this.members.add(member); }
}
