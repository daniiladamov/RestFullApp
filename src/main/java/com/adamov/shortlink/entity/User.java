package com.adamov.shortlink.entity;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    public User() {
    }

    public Long getId() {

        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    private String secretKey;


@ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
@CollectionTable(name="hashs", joinColumns = @JoinColumn(name = "id"))
    private Set<String> hashsSet=new HashSet();

    public void addHash(String hash) {
        hashsSet.add(hash);
    }

    public boolean haveHash(String hash){
        if (hashsSet.contains(hash)) return true;
        else return false;
    }

    public boolean deleteHash(String hash){
        return hashsSet.remove(hash);
    }
}
