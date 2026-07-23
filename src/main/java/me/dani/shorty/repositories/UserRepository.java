package me.dani.shorty.repositories;

import me.dani.shorty.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity,String> {
}
