package fr.imt.projetspringboot.team;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fr.imt.projetspringboot.participate.Participate;
import fr.imt.projetspringboot.participate.ParticipateRepository;
import fr.imt.projetspringboot.participate.ParticipateService;
import fr.imt.projetspringboot.tournament.Tournament;
import fr.imt.projetspringboot.tournament.TournamentService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class TeamControllerTest {

    @Autowired
    private ParticipateService participateService;
    @Autowired
    private TournamentService tournamentService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ParticipateRepository participateRepository;

    @BeforeEach
    void setUp(TestInfo testInfo, TestReporter testReporter) throws Exception {
        if (testInfo.getTags().contains("findTeamParticipateTournament") || testInfo.getTags().contains("getFavoriteTournament")) {
            // Get the input values for the test method
            Tournament tournament = new Tournament();
            tournament.setTournamentYear(2000);
            tournament.setTitle("competition");
            tournamentService.addTournament(tournament);

            Tournament tournament2 = new Tournament();
            tournament2.setTournamentYear(2002);
            tournament2.setTitle("competition2");
            tournamentService.addTournament(tournament2);

            Team team = new Team();
            team.setCountry("country61");
            team.setName("name61");
            teamService.addTeam(team);

            Team team2 = new Team();
            team2.setCountry("country221");
            team2.setName("name21");
            teamService.addTeam(team2);

            Participate participate = new Participate();
            participate.setTeam(team);
            participate.setTournament(tournament);
            participate.setFavorite(4);
            participateService.addParticipate(participate);

            Participate participate1 = new Participate();
            participate1.setTeam(team);
            participate1.setTournament(tournament2);
            participate1.setFavorite(2);
            participateService.addParticipate(participate1);

            Participate participate3 = new Participate();
            participate3.setTeam(team2);
            participate3.setTournament(tournament);
            participate3.setFavorite(2);
            participateService.addParticipate(participate3);


            testReporter.publishEntry("Setup", "Performing setup steps for findTeamParticipateTournament");
        }
    }

    @ParameterizedTest
    @Tag("findTeamParticipateTournament")
    @MethodSource("inputValues_findTeamParticipateTournament")
    void findTeamParticipateTournament(
            String teamCountry,
            ResultMatcher expectedCode,
            TeamWithTournaments expectedResult
    ) throws Exception {
        // ASSERT
        ResultActions result = mockMvc.perform(get("/api/team/tournaments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("teamCountry",teamCountry))
                .andExpect(expectedCode);
        Gson gson = new Gson();
        TeamWithTournaments teamWithTournamentsRes = gson.fromJson(result.andReturn().getResponse().getContentAsString(), TeamWithTournaments.class);
        assertThat(teamWithTournamentsRes).isEqualTo(expectedResult);
    }

    static Stream<Arguments> inputValues_findTeamParticipateTournament(){
        return Stream.of(
                // [code 200] Get team with its tournament
                Arguments.of("country61",status().isOk(),
                        new TeamWithTournaments("name61","country61",
                                new LinkedList<>(Arrays.asList(
                                        new Tournament("competition",2000),
                                        new Tournament("competition2",2002)
                                ))
                        )
                ),
                // [code 400] Get team with its tournament (country name not exist)
                Arguments.of("cozuntry61",status().isBadRequest(),null),
                // [code 400] Get team with its tournament (country name is empty)
                Arguments.of("",status().isBadRequest(),null)
        );
    }

    @ParameterizedTest
    @Tag("getFavoriteTournament")
    @MethodSource("inputValues_getFavoriteTournament")
    void getFavoriteTournament(
            String tournamentTitle,
            int tournamentYear,
            int score,
            ResultMatcher expected,
            List<TeamWithFavorite> expectedList
    ) throws Exception {


        // ASSERT
        ResultActions result =  mockMvc.perform(get("/api/team/favorites/" +
                        tournamentTitle + "/" +
                        tournamentYear)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("score", String.valueOf(score)))
                .andExpect(expected);
        Gson gson = new Gson();
        Type listType = new TypeToken<List<TeamWithFavorite>>(){}.getType();
        List<TeamWithFavorite> res =  gson.fromJson(result.andReturn().getResponse().getContentAsString(), listType);
        assertThat(res).isEqualTo(expectedList);
    }

    static Stream<Arguments> inputValues_getFavoriteTournament(){
        return Stream.of(
                // [code 200] returns favorite teams with an existing tournament
                Arguments.of("competition",2000,2,status().isOk(),
                        new LinkedList<>(Arrays.asList(
                                new TeamWithFavorite("name61","country61",4),
                                new TeamWithFavorite("name21","country221",2)
                        ))
                ),

                // [code 400] returns favorite teams with unexistant competition
                Arguments.of("tournamentNotExist",2010,5,status().isBadRequest(),null),

                // [code 400] returns favorite teams without tournament name
                Arguments.of(null,2000,5,status().isBadRequest(),null),

                // [code 400] returns favorite teams without tournament year
                Arguments.of("competition",0,5,status().isBadRequest(),null),

                // [code 400] returns favorite teams without favorite score out of range
                Arguments.of("competition",2000,-1,status().isBadRequest(),null),

                // [code 400] returns favorite teams without favorite score out of range
                Arguments.of("competition",2000,7,status().isBadRequest(),null)
        );
    }
}