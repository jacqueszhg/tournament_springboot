package fr.imt.projetspringboot.tournament;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {

    Tournament findByTitleAndTournamentYear(String title, int tournamentYear);


}
