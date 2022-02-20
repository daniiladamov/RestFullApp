package com.adamov.shortlink.repositories;

import com.adamov.shortlink.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
