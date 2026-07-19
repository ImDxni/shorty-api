package me.dani.shorty.services;

import lombok.RequiredArgsConstructor;
import me.dani.shorty.entities.URLEntity;
import me.dani.shorty.repositories.UrlRepository;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final static int LEFT_LIMIT = 48; // numeral '0'
    private final static int RIGHT_LIMIT = 122; // letter 'z'
    private final static ThreadLocalRandom random = ThreadLocalRandom.current();

    private final UrlRepository repository;

    public String generateShortUrl(String originalUrl){
        if(!validateURL(originalUrl)){
            throw new IllegalArgumentException("Invalid URL");
        }

        String code;
        do {
            code = generateShortCode(6);
        } while (repository.existsByCode(code));

        URLEntity entity = new URLEntity();
        entity.setOriginalUrl(originalUrl);
        entity.setCode(code);
        entity.setExpiresAt(LocalDateTime.now().plusDays(30));

        repository.save(entity);

        return code;
    }

    public String getOriginalUrl(String code){
        return repository.findByCode(code)
                .map(URLEntity::getOriginalUrl)
                .orElseThrow(() -> new IllegalArgumentException("Short URL not found"));
    }


    public boolean validateURL(String url){
        try {
            new URI(url).toURL();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String generateShortCode(int length){
        return random.ints(LEFT_LIMIT, RIGHT_LIMIT + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
