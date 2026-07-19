package me.dani.shorty.controller;

import lombok.RequiredArgsConstructor;
import me.dani.shorty.services.UrlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    public record UrlRequest(String originalUrl) {}
    public record UrlResponse(String shortUrl) {}

    @PostMapping("/shorten")
    public ResponseEntity<UrlResponse> shortenLink(@RequestBody UrlRequest request){

        String shortCode;
        try {
            shortCode = urlService.generateShortUrl(request.originalUrl());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(new UrlResponse(shortCode));
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectToOriginal(@PathVariable String shortCode){
        String originalUrl;

        try{
            originalUrl = urlService.getOriginalUrl(shortCode);
        } catch(IllegalArgumentException ex){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT)
                .location(URI.create(originalUrl))
                .build();
    }
}
