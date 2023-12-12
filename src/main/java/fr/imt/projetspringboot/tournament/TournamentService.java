package fr.imt.projetspringboot.tournament;

import fr.imt.projetspringboot.team.TeamWithFavorite;
import fr.imt.projetspringboot.participate.Participate;
import fr.imt.projetspringboot.participate.ParticipateRepository;
import fr.imt.projetspringboot.team.Team;
import fr.imt.projetspringboot.team.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


@Service
@Transactional(rollbackFor = Exception.class)
public class TournamentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TournamentService.class);
    private final TournamentRepository tournamentRepository;

    private final TeamRepository teamRepository;

    private final ParticipateRepository participateRepository;

    @Autowired
    public TournamentService(TournamentRepository tournamentRepository, TeamRepository teamRepository, ParticipateRepository participateRepository) {
        this.tournamentRepository = tournamentRepository;
        this.teamRepository = teamRepository;
        this.participateRepository = participateRepository;
    }

    public Optional<Tournament> addTournament(Tournament tournament) throws Exception {
        LOGGER.info("begin - save tournament");
        try{
            Tournament t = tournamentRepository.save(tournament);
            LOGGER.info("end - save tournament");
            return Optional.of(t);
        }catch (Exception e){
            throw new Exception("The tournament already exist");
        }
    }


    public Optional<List<Tournament>> findAll(){
        return Optional.of(tournamentRepository.findAll());
    }

    public Optional<TournamentWithTeamRequest> insertTournamentAndTeam(TournamentWithTeamRequest tournamentWithTeam) throws Exception {
        LOGGER.info("begin - insertTournamentAndTeam");
        // Create tournament and add in database
        Tournament tournament = new Tournament();
        tournament.setTournamentYear(tournamentWithTeam.getTournamentYear());
        tournament.setTitle(tournamentWithTeam.getTitle());
        try {
            tournament = tournamentRepository.save(tournament);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        List<TeamWithFavorite> listTeams = new LinkedList<>();
        for (TeamWithFavorite teamWithFavorite : tournamentWithTeam.getListTeam()) {
            // Create team and add team in database
            Team team = new Team();
            team.setName(teamWithFavorite.getName());
            team.setCountry(teamWithFavorite.getCountry());

            // Check if team exist for no recreate
            if (teamRepository.findByCountry(team.getCountry()) == null) {
                try {
                    team = teamRepository.save(team);
                } catch (Exception e) {
                    throw new Exception(e.getMessage());
                }
            } else {
                team = teamRepository.findByCountry(team.getCountry());
            }

            listTeams.add(new TeamWithFavorite(team.getId(),team.getName(),team.getCountry(),teamWithFavorite.getFavorite()));

            // Create participate and add in database
            Participate participate = new Participate();
            participate.setTournament(tournament);
            participate.setTeam(team);
            try {
                participate.setFavorite(teamWithFavorite.getFavorite());
            }catch (Exception e){
                throw new Exception(e);
            }

            try {
                participateRepository.save(participate);
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        }

        LOGGER.info("end - insertTournamentAndTeam");
        tournamentWithTeam.setId(tournament.getId());
        tournamentWithTeam.setListTeam(listTeams);
        return Optional.of(tournamentWithTeam);
    }
}
