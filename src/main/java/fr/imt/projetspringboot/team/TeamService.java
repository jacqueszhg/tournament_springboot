package fr.imt.projetspringboot.team;

import fr.imt.projetspringboot.participate.Participate;
import fr.imt.projetspringboot.participate.ParticipateRepository;
import fr.imt.projetspringboot.tournament.Tournament;
import fr.imt.projetspringboot.tournament.TournamentRepository;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;

import java.util.LinkedList;
import java.util.Optional;

@Service
@Transactional(rollbackFor = Exception.class)
public class TeamService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TeamService.class);
    private final ParticipateRepository participateRepository;

    private final TeamRepository teamRepository;

    private final TournamentRepository tournamentRepository;

    @Autowired
    public TeamService(
            ParticipateRepository participateRepository,
            TeamRepository teamRepository,
            TournamentRepository tournamentRepository) {
        this.participateRepository = participateRepository;
        this.teamRepository = teamRepository;
        this.tournamentRepository = tournamentRepository;
    }

    public Optional<Team> addTeam(Team team) throws Exception {
        LOGGER.info("begin - save team");
        try{
            Team t = teamRepository.save(team);
            LOGGER.info("end - save team");
            return Optional.of(t);
        }catch (Exception e){
            throw new Exception(e);
        }
    }

    public Optional<TeamWithTournaments> findTeamParticipateTournament(String teamCountry) throws Exception {
        LOGGER.info("begin - find findTeamParticipateTournament");
        if(teamCountry.isBlank())
            throw new Exception("Need a country name for find the team");

        Team team = teamRepository.findByCountry(teamCountry);
        if(team == null)
            throw new Exception("Team not find");


        TeamWithTournaments teamWithTournaments = new TeamWithTournaments();
        teamWithTournaments.setName(team.getName());
        teamWithTournaments.setCountry(team.getCountry());
        teamWithTournaments.setId(team.getId());

        List<Participate> listParticipates = participateRepository.findByTeam(team);
        List<Tournament> listTournaments = new LinkedList<>();
        for(Participate participate : listParticipates){
            listTournaments.add(participate.getTournament());
        }
        teamWithTournaments.setListTournaments(listTournaments);

        LOGGER.info("end - findTeamParticipateTournament");
        return Optional.of(teamWithTournaments);
    }

    public Optional<List<TeamWithFavorite>> getFavoriteTournament(String tournamentTitle, int tournamentYear, int score) throws Exception {
        LOGGER.info("BEGIN - getfavoriteTournament");

        Tournament tournament = tournamentRepository.findByTitleAndTournamentYear(tournamentTitle, tournamentYear);

        if (tournament == null) throw new Exception("Tournament not exist");
        // If the tournament exist
        try {
            List<TeamWithFavorite>res = new ArrayList<>();

            List<Participate> listeParticipate = participateRepository.findByTournament_IdAndFavoriteGreaterThanEqual(tournament.getId(), score);

            if (listeParticipate == null) throw new Exception("Error participate");

            for(Participate elt :listeParticipate){
                TeamWithFavorite team = new TeamWithFavorite();
                team.setName(elt.getTeam().getName());
                team.setCountry(elt.getTeam().getCountry());
                team.setFavorite(elt.getFavorite());
                team.setId(elt.getId());

                res.add(team);

                if (team.getFavorite() == 0 || team.getCountry() == null || team.getName() ==null) throw new Exception("Error participate");
            }

            LOGGER.info("END - getfavoriteTournament");

            return Optional.of(res);
        }catch (Exception e){
            throw new Exception(e);
        }
    }



}
