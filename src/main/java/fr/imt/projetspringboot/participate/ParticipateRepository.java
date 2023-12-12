package fr.imt.projetspringboot.participate;

import fr.imt.projetspringboot.team.Team;
import fr.imt.projetspringboot.tournament.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipateRepository extends JpaRepository<Participate, Long> {
    @Query("select p from Participate p where p.team = ?1")
    List<Participate> findByTeam(Team team);

    @Query("select p from Participate p where p.tournament.id = ?1 and p.favorite >= ?2")
    List<Participate> findByTournament_IdAndFavoriteGreaterThanEqual(Long id, int favorite);

    Participate findByTeamAndTournament(Team team, Tournament tournament);
}

