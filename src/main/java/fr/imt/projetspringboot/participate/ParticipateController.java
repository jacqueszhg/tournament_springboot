package fr.imt.projetspringboot.participate;

import fr.imt.projetspringboot.team.TeamWithFavorite;
import fr.imt.projetspringboot.team.Team;
import fr.imt.projetspringboot.tournament.Tournament;
import fr.imt.projetspringboot.tournament.TournamentController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/participate")
@Transactional
public class ParticipateController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TournamentController.class);
    private final ParticipateService participateService;

    @Autowired
    public ParticipateController(ParticipateService participateService) {
        this.participateService = participateService;
    }

    @PutMapping("/insert/{tournamentTitle}/{tournamentYear}")
    public ResponseEntity<Participate> addParticipate(
            @RequestBody TeamWithFavorite teamWithFavorite,
            @PathVariable("tournamentTitle") String tournamentTitle,
            @PathVariable("tournamentYear") int tournamentYear
    ) {
        if (tournamentYear < 0){
            LOGGER.error("Year can't be negative");
            return ResponseEntity.badRequest().build();
        }

        if(teamWithFavorite.getName() == null ||
                (teamWithFavorite.getName() != null &&
                        teamWithFavorite.getName().isBlank())
        ){
            LOGGER.error("Team name can't be empty");
            return ResponseEntity.badRequest().build();
        }

        if(teamWithFavorite.getCountry() == null ||
                (teamWithFavorite.getCountry() != null &&
                        teamWithFavorite.getCountry().isBlank())
        ){
            LOGGER.error("Team country can't be empty");
            return ResponseEntity.badRequest().build();
        }

        Tournament tournament = new Tournament();
        tournament.setTournamentYear(tournamentYear);
        tournament.setTitle(tournamentTitle);

        Team team = new Team();
        team.setName(teamWithFavorite.getName());
        team.setCountry(teamWithFavorite.getCountry());

        Participate participate = new Participate();
        participate.setTournament(tournament);
        participate.setTeam(team);
        try {
            participate.setFavorite(teamWithFavorite.getFavorite());
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }

        try {
            Optional<Participate> res = participateService.addParticipate(participate);
            if (res.isPresent()){
                return ResponseEntity.ok(res.get());
            }else{
                LOGGER.error("Not add in participate");
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}