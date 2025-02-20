package com.security.security.repository;

import com.security.security.domain.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
  List<Availability> findByUserProviderId(UUID userProviderId);
}
