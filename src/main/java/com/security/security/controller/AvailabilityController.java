package com.security.security.controller;

import com.google.api.client.util.DateTime;
import com.security.security.domain.Availability;
import com.security.security.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/availability")
@RequiredArgsConstructor
public class AvailabilityController {

  private final AvailabilityService availabilityService;

  @GetMapping("/{userId}")
  @PreAuthorize("isAuthenticated()")
  public List<Availability> getUserAvailability(@PathVariable UUID userId) {
    return availabilityService.findByUserProviderId(userId);
  }

  @PostMapping("/add")
  @PreAuthorize("isAuthenticated()")
  public Availability addManualAvailability(@RequestBody Availability availability) {
    return availabilityService.createManualAvailability(availability);
  }

  @PatchMapping("/update")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Availability> updateAvailability(@RequestBody Availability availability) {
    Optional<Availability> updatedAvailability = availabilityService.updateAvailability(availability);
    return updatedAvailability.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @DeleteMapping("/delete/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<String> deleteAvailability(@PathVariable Long id) {
    boolean deleted = availabilityService.deleteAvailability(id);
    if (deleted) {
      return ResponseEntity.ok("Disponibilité supprimée avec succès");
    } else {
      return ResponseEntity.notFound().build();
    }
  }
}
