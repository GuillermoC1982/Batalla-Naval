package com.mindhubweb.salvo.Repositories;


import com.mindhubweb.salvo.Models.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource
public interface PlayerRepository extends JpaRepository<Player, Long> {
   Player findByUserName(String userName);

}

