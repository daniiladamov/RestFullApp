package com.adamov.shortlink.repositories;

import com.adamov.shortlink.entity.Link;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LinkRepository extends JpaRepository<Link,Long> {
    Link findByShortLink(String shortLink);
    Link findByFullLink(String fullLink);

}
