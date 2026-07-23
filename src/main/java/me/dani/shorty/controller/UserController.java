package me.dani.shorty.controller;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import me.dani.shorty.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserController {
    private final AuthService service;

    public record AuthRequest(String username, String password) {}

    public record AuthResponse(String message, @Nullable String token) {}


    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest request){
        if(service.userExists(request.username())){
            return ResponseEntity.badRequest()
                    .body(new AuthResponse("User already exists", null));
        }

        service.createUser(request.username(), request.password());
        return ResponseEntity.ok(new AuthResponse("User created successfully", null));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginAttempt(@RequestBody AuthRequest request){
        boolean result = service.loginAttempt(request.username(), request.password());

        if(!result)
            return ResponseEntity.badRequest().body(new AuthResponse("Invalid username or password", null));

        String jwt = service.generateToken(request.username);

        return ResponseEntity.ok(new AuthResponse("Login successful", jwt));
    }
}
