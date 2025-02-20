package com.security.security.auth;

import com.security.security.DTO.AuthenticationRequest;
import com.security.security.DTO.AuthenticationResponse;
import com.security.security.DTO.RegisterRequest;
import com.security.security.config.JwtService;
import com.security.security.domain.User;
import com.security.security.repository.UserProvidersRepository;
import io.jsonwebtoken.Claims;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService authenticationService;
  private final JwtService jwtService;
  private final UserProvidersRepository userProvider;



  @PostMapping("/login")
  public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request){
    AuthenticationResponse auth = authenticationService.login(request);
    if (auth == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    return new ResponseEntity<>(auth, HttpStatus.OK);

  }

  @PostMapping("/register")
  public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {

    AuthenticationResponse auth = authenticationService.register(request);

    if (auth == null) throw new ResponseStatusException(HttpStatus.CONFLICT);

    return new ResponseEntity<>(auth, HttpStatus.OK);
  }

  @GetMapping("/userinfo")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<User> getUserInfo(@RequestHeader("Authorization") String token) {
    token = token.substring(7);

    Claims claims = jwtService.extractAllClaims(token);

    User user = new User();
    user.setId(claims.get("id", UUID.class));
    user.setEmail(claims.get("email", String.class));

    user.setName(claims.get("name", String.class));

    return ResponseEntity.ok(user);
  }

  @GetMapping("/secured")
  @PreAuthorize("isAuthenticated()")
  public Map<String, String> securedEndpoint() {
    Map<String, String> response = new HashMap<>();
    response.put("message", "Accès autorisé : Utilisateur authentifié avec succès !");
    response.put("status", "success");
    return response;
  }


  }
