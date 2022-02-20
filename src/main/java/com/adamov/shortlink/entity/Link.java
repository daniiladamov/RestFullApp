package com.adamov.shortlink.entity;

import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "links")
public class Link {
    public String getFullLink() {
        return fullLink;
    }

    public void setFullLink(String fullLink) {
        this.fullLink = fullLink;
    }

    public String getShortLink() {
        return shortLink;
    }

    public void setShortLink(String shortLink) {
        this.shortLink = shortLink;
    }

    public Link() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY )
    private Long id;
    @Column(name="full_link")
    private String fullLink;
    @Column(name="short_link")
    private String shortLink;
    @Column(name="hash")
    private String hash;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }


}
