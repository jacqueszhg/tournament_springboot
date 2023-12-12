package fr.imt.projetspringboot.tournament;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tournament")
public class TournamentController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TournamentController.class);
    private final TournamentService tournamentService;

    @Autowired
    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @PutMapping("/insert")
    public ResponseEntity<TournamentWithTeamRequest> insertTournamentAndTeam(
            @RequestBody TournamentWithTeamRequest tournamentWithTeam) {
        if(tournamentWithTeam.getTitle() == null ||
                ( tournamentWithTeam.getTitle() != null &&
                        tournamentWithTeam.getTitle().isBlank()
                )
        ){
            LOGGER.error("Missing the tournament title");
            return ResponseEntity.badRequest().build();
        }

        if (tournamentWithTeam.getTournamentYear() < 0){
            LOGGER.error("Year can't be negative");
            return ResponseEntity.badRequest().build();
        }

        if (tournamentWithTeam.getListTeam() == null ||
                (tournamentWithTeam.getListTeam() != null &&
                        tournamentWithTeam.getListTeam().isEmpty()
                )
        ){
            LOGGER.error("Tournament teams are empty");
            return ResponseEntity.badRequest().build();
        }
        try {
            Optional<TournamentWithTeamRequest> res =  tournamentService.insertTournamentAndTeam(tournamentWithTeam);
            if (res.isPresent()){
                return ResponseEntity.ok(res.get());
            }else{
                LOGGER.error("Error in add");
                return ResponseEntity.badRequest().build();
            }
        }catch (Exception e){
            LOGGER.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<Tournament>> findAll(){
        try{
            Optional<List<Tournament>> t = tournamentService.findAll();
            if (t.isPresent()){
                return ResponseEntity.ok(t.get());
            }else{
                LOGGER.error("Error in findall tournament");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }catch(Exception e){
            LOGGER.error("\nerror : " + e.getMessage() + "\n" + "cause : " + e.getCause());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

}
