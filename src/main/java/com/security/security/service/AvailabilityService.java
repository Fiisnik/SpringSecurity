package com.security.security.service;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Event;
import com.security.security.domain.Availability;
import com.security.security.domain.UserProvider;
import com.security.security.repository.AvailabilityRepository;
import com.security.security.repository.UserProvidersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

  private final AvailabilityRepository availabilityRepository;
  private final UserProvidersRepository userProviderRepository;

  public List<Availability> findByUserProviderId(UUID userProviderId) {
    return availabilityRepository.findByUserProviderId(userProviderId);
  }

  public Availability createManualAvailability(Availability availability) {
    Optional<UserProvider> userProviderOpt = userProviderRepository.findById(availability.getUserProvider()
        .getId());
    if (userProviderOpt.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Utilisateur non trouvé");
    }

     availability = Availability.builder()
        .userProvider(userProviderOpt.get())
        .startTime(availability.getStartTime() != null ? availability.getStartTime().toString() : null)
        .endTime(availability.getEndTime() != null ? availability.getEndTime().toString() : null)
        .source("MANUAL")
        .build();

    return availabilityRepository.save(availability);
  }

  public void saveGoogleEvents(UserProvider user, List<Event> events) {

    for (Event event : events) {

      if (event.getSummary() != null && event.getSummary().equals("Happy birthday!")) continue;

      String startTime = getFormattedDateTime(event.getStart());
      String endTime = getFormattedDateTime(event.getEnd());

      Availability availability = Availability.builder()
          .userProvider(user)
          .summary(event.getSummary())
          .startTime(startTime)
          .endTime(endTime)
          .source("GOOGLE_CALENDAR")
          .build();

      availabilityRepository.save(availability);
    }

  }

  public Optional<Availability> updateAvailability(Availability availability) {
    Optional<Availability> availabilityOpt = availabilityRepository.findById(availability.getId());

    if (availabilityOpt.isPresent()) {
      Availability newAvailability = availabilityOpt.get();

      if (newAvailability.getStartTime() != null) newAvailability.setStartTime(availability.getStartTime());
      if (newAvailability.getEndTime() != null) newAvailability.setEndTime(availability.getEndTime());

      availabilityRepository.save(newAvailability);
    }
    return availabilityOpt;
  }

  public boolean deleteAvailability(Long id) {
    Optional<Availability> availabilityOpt = availabilityRepository.findById(id);
    if (availabilityOpt.isPresent()) {
      availabilityRepository.deleteById(id);
      return true;
    }
    return false;
  }

  /**
   *  Méthode pour récupérer `dateTime` ou `date` en String
   */
  private String getFormattedDateTime(EventDateTime eventDateTime) {
    if (eventDateTime == null) return null;

    DateTime dateTime = eventDateTime.getDateTime();
    if (dateTime != null) {
      return dateTime.toString();
    }

    DateTime date = eventDateTime.getDate();
    return date != null ? date.toString() : null;
  }
}
