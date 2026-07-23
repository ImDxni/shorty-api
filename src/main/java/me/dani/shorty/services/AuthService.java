package me.dani.shorty.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Jwks;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import me.dani.shorty.entities.UserEntity;
import me.dani.shorty.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expirationDays}")
    private int expirationDays;


    private final UserRepository repository;
    private final PasswordEncoder encoder;

    public void createUser(String username, String password){
        String hashedPassword = encoder.encode(password);

        UserEntity entity = new UserEntity();

        entity.setUsername(username);
        entity.setPassword(hashedPassword);

        repository.save(entity);
    }

    //The endpoint is supposed to receive the clear password, not too safe, but it's just a personal project lol
    public boolean loginAttempt(String username, String password) {
        return repository.findById(username).map(user -> encoder.matches(password, user.getPassword()))
                .orElse(false);
    }

    public boolean userExists(String username) {
        return repository.existsById(username);
    }

    public String generateToken(String identifier) {
        return Jwts.builder()
                .subject(identifier)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + (expirationDays * 24 * 60 * 60 * 1000L)))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }
}
