package fr.imt.projetspringboot.team;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.Optional;

@RestController
@RequestMapping("/api/team")
public class TeamController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TeamController.class);

    private final TeamService teamService;

    @Autowired
    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping("/tournaments")
    public ResponseEntity<TeamWithTournaments> findTeamParticipateTournament(
            @RequestParam("teamCountry") String teamCountry
    ){
        try {
            Optional<TeamWithTournaments> list = teamService.findTeamParticipateTournament(teamCountry);
            if(list.isPresent()){
                return ResponseEntity.ok(list.get());
            }else{
                LOGGER.error("Error in find team participate tournaments");
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/favorites/{tournamentTitle}/{tournamentYear}")
    public ResponseEntity<List<TeamWithFavorite>> getFavoriteTournament(
            @PathVariable("tournamentTitle") String tournamentTitle,
            @PathVariable("tournamentYear") int tournamentYear,
            @RequestParam("score") int score) {

        if(tournamentTitle.isBlank()){
            LOGGER.error("The tournamentTitle can't be empty");
            return ResponseEntity.badRequest().build();
        }
        if (tournamentYear < 0){
            LOGGER.error("The tournamentYear can't be empty");
            return ResponseEntity.badRequest().build();
        }
        if (score < 0 || score > 5) {
            LOGGER.error("getFavoriteTournament error: the score not between [0-5]");
            return ResponseEntity.badRequest().build();
        }

        try {
            Optional<List<TeamWithFavorite>> teamsFavorites = teamService.getFavoriteTournament(tournamentTitle, tournamentYear, score);
            if(teamsFavorites.isPresent()){
                return ResponseEntity.ok(teamsFavorites.get());
            }else{
                LOGGER.error("Error in find teams of a tournament who has favorite greater than the favorite specified");
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            LOGGER.error("\nerror : " + e.getMessage() + "\n" + "cause : " + e.getCause());
            return ResponseEntity.badRequest().build();
        }


    }
}
