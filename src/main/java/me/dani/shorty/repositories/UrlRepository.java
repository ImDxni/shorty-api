package me.dani.shorty.repositories;

import me.dani.shorty.entities.URLEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<URLEntity,Long>  {

    Optional<URLEntity> findByCode(String code);

    boolean existsByCode(String code);
}
