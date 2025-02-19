package com.security.security.repository;

import com.security.security.domain.UserProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserProvidersRepository extends JpaRepository<UserProvider, UUID> {
    Optional<UserProvider> findByEmail(String username);

}
