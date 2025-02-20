package com.security.security.service;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class GoogleCalendarService {

  private final OAuth2AuthorizedClientService authorizedClientService;

  private static final String APPLICATION_NAME = "Hapiklan";
  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
  private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

  /**
   * Récupérer les événements de l'utilisateur Google
   */
  public List<Event> getEvents(OAuth2User oauthUser, String calendarId) throws IOException, GeneralSecurityException {
    if (!(oauthUser instanceof OidcUser oidcUser)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, " Utilisateur non oidcUser");
    }

    OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
        "google",
        oidcUser.getName()
    );

    String accessToken = client.getAccessToken().getTokenValue();

    Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, null)
        .setApplicationName(APPLICATION_NAME)
        .setHttpRequestInitializer(request -> request.getHeaders().setAuthorization("Bearer " + accessToken))
        .build();

    Events events = service.events().list(calendarId)
        .setMaxResults(10)
        .setOrderBy("startTime")
        .setSingleEvents(true)
        .setFields("items(id,summary,description,start,end,location,creator(email),organizer(email),attendees,status,visibility,reminders,hangoutLink,colorId,recurrence)")
        .execute();



    return events.getItems();
  }

}
