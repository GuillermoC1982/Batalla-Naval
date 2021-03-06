package com.mindhubweb.salvo.Repositories;

import com.mindhubweb.salvo.Models.GamePlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;



@RepositoryRestResource
public interface GamePlayerRepository extends JpaRepository <GamePlayer, Long> {
}
