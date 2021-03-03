package com.mindhubweb.salvo.Repositories;

import com.mindhubweb.salvo.Models.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource
public interface GameRepository extends JpaRepository<Game, Long> {
    Optional<Game> findById(long id);

}
