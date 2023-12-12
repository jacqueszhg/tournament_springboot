package fr.imt.projetspringboot.participate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import fr.imt.projetspringboot.team.TeamRepository;
import fr.imt.projetspringboot.team.TeamWithFavorite;
import fr.imt.projetspringboot.team.Team;
import fr.imt.projetspringboot.team.TeamService;
import fr.imt.projetspringboot.tournament.Tournament;
import fr.imt.projetspringboot.tournament.TournamentRepository;
import fr.imt.projetspringboot.tournament.TournamentService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class ParticipateControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TournamentService tournamentService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private ParticipateService participateService;

    @Autowired
    private ParticipateRepository participateRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private TournamentRepository tournamentRepository;

    @BeforeEach
    void setUp(TestInfo testInfo, TestReporter testReporter) throws Exception {
        if (testInfo.getTags().contains("insertTeamInTournament")) {

            Tournament tournament = new Tournament();
            tournament.setTournamentYear(2000);
            tournament.setTitle("competition");
            tournamentService.addTournament(tournament);

            Team team = new Team();
            team.setCountry("country21");
            team.setName("name1");
            teamService.addTeam(team);

            Team team2 = new Team();
            team2.setCountry("country221");
            team2.setName("name21");
            teamService.addTeam(team2);

            // Add team2 in participate
            Participate participate = new Participate();
            participate.setTeam(team2);
            participate.setTournament(tournament);
            participate.setFavorite(4);
            participateService.addParticipate(participate);

            testReporter.publishEntry("Setup", "Performing setup steps for insertTeamInTournament");
        }
    }

    @ParameterizedTest
    @Tag("insertTeamInTournament")
    @MethodSource("inputValues_insertTeamInTournament")
    void insertTeamInTournament(
            String tournamentTitle,
            int tournamentYear,
            ResultMatcher expectedCode,
            int expectedSize,
            TeamWithFavorite teamWithFavorite
    ) throws Exception {


        // ACT
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(teamWithFavorite);


        // ASSERT
        ResultActions result = mockMvc.perform(put("/api/participate/insert/" +
                        tournamentTitle + "/" +
                        tournamentYear)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(expectedCode);
        Gson gson = new Gson();
        Participate participateRes = gson.fromJson(result.andReturn().getResponse().getContentAsString(), Participate.class);

        if(participateRes != null){
            Participate participateEqual = new Participate();
            Team team = teamRepository.findByCountry(teamWithFavorite.getCountry());
            Tournament tournament = tournamentRepository.findByTitleAndTournamentYear(tournamentTitle,tournamentYear);

            participateEqual.setTeam(team);
            participateEqual.setTournament(tournament);
            participateEqual.setFavorite(teamWithFavorite.getFavorite());


            assertThat(participateRes).isEqualTo(participateEqual);
            assertThat(participateRepository.findAll().contains(participateRes)).isTrue();
        }else{
            assertThat(participateRepository.findAll().contains(participateRes)).isFalse();
        }
        assertThat(participateRepository.findAll().size()).isEqualTo(expectedSize);
    }

    static Stream<Arguments> inputValues_insertTeamInTournament(){
        return Stream.of(
                // [code 200] insert in an existing tournament a team (team doesn't exist)
                Arguments.of("competition",2000,status().isOk(),2,
                        new TeamWithFavorite("name1","country1",4)
                ),
                // [code 200] insert in an existing tournament in an existing team
                Arguments.of("competition",2000,status().isOk(),2,
                        new TeamWithFavorite("name1","country21",4)
                ),
                // [code 400] insert in a tournament a team (team already participate)
                Arguments.of("competition",2000,status().isBadRequest(),1,
                        new TeamWithFavorite("name21","country221",4)
                ),
                // [code 400] insert in a tournament a team (tournament not created)
                Arguments.of("competition3",2000,status().isBadRequest(),1,
                        new TeamWithFavorite("name1","country31",4)
                ),
                // [code 400] insert in an existing tournament a team (team is null)
                Arguments.of("competition3",2000,status().isBadRequest(),1,null),
                // [code 400] insert in an existing tournament a team (team name is empty)
                Arguments.of("competition3",2000,status().isBadRequest(),1,
                        new TeamWithFavorite("","country31",4)
                ),
                // [code 400] insert in an existing tournament a team (team name is null)
                Arguments.of("competition3",2000,status().isBadRequest(),1,
                        new TeamWithFavorite(null,"country31",4)
                ),
                // [code 400] insert in an existing tournament a team (team country is empty)
                Arguments.of("competition3",2000,status().isBadRequest(),1,
                        new TeamWithFavorite("name1","",4)
                ),
                // [code 400] insert in an existing tournament a team (team country is null)
                Arguments.of("competition3",2000,status().isBadRequest(),1,
                        new TeamWithFavorite("name1",null,4)
                ),
                // [code 400] insert in an existing tournament a team (tournament name is null)
                Arguments.of(null,2000,status().isBadRequest(),1,
                        new TeamWithFavorite("","country31",4)
                ),
                // [code 400] insert in an existing tournament a team (tournament year is negative)
                Arguments.of("competition",-50,status().isBadRequest(),1,
                        new TeamWithFavorite("","country31",4)
                ),
                // [code 400] insert in an existing tournament a team (favorite is negative)
                Arguments.of("competition",-50,status().isBadRequest(),1,
                        new TeamWithFavorite("","country31",-4)
                ),
                // [code 400] insert in an existing tournament a team (favorite > 5)
                Arguments.of("competition",2000,status().isBadRequest(),1,
                        new TeamWithFavorite("","country31",6)
                )
        );
    }
}