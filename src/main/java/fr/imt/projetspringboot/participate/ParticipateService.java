package fr.imt.projetspringboot.participate;

import fr.imt.projetspringboot.team.Team;
import fr.imt.projetspringboot.team.TeamRepository;
import fr.imt.projetspringboot.tournament.Tournament;
import fr.imt.projetspringboot.tournament.TournamentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(rollbackFor = Exception.class)
public class ParticipateService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParticipateService.class);
    private ParticipateRepository participateRepository;

    private TeamRepository teamRepository;

    private TournamentRepository tournamentRepository;

    @Autowired
    public ParticipateService(ParticipateRepository participateRepository, TeamRepository teamRepository, TournamentRepository tournamentRepository) {
        this.participateRepository = participateRepository;
        this.teamRepository = teamRepository;
        this.tournamentRepository = tournamentRepository;
    }

    public Optional<Participate> addParticipate(Participate participate) throws Exception {
        LOGGER.info("begin - save participate");
        if(participate.getFavorite() < 0 || participate.getFavorite() > 5){
            throw new Exception("Favorite need to be between 0 and 5");
        }

        Tournament tournament = participate.getTournament();
        tournament = tournamentRepository.findByTitleAndTournamentYear(
                tournament.getTitle(),
                tournament.getTournamentYear()
        );
        if(tournament == null){
            throw new Exception("Tournament not exist");
        }

        Team team = participate.getTeam();
        if(teamRepository.findByCountry(team.getCountry()) == null){
            try {
                 team = teamRepository.save(team);
            } catch (Exception e) {
                throw new Exception(e);
            }
        }else{
            team = teamRepository.findByCountry(team.getCountry());
        }


        participate.setTeam(team);
        participate.setTournament(tournament);
        if(participateRepository.findByTeamAndTournament(team,tournament) != null){
            throw new Exception("Team already participate");
        }
        try{
            Participate p = participateRepository.save(participate);
            LOGGER.info("end - save participate");
            return Optional.of(p);
        }catch (Exception e){
            throw new Exception(e);
        }
    }

}
