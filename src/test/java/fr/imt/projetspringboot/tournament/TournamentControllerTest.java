package fr.imt.projetspringboot.tournament;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import fr.imt.projetspringboot.participate.Participate;
import fr.imt.projetspringboot.participate.ParticipateRepository;
import fr.imt.projetspringboot.team.Team;
import fr.imt.projetspringboot.team.TeamRepository;
import fr.imt.projetspringboot.team.TeamWithFavorite;
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
import  org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TournamentControllerTest {

    @Autowired
    private TournamentService tournamentService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TournamentRepository tournamentRepository;
    @Autowired
    private ParticipateRepository participateRepository;
    @Autowired
    private TeamRepository teamRepository;


    @BeforeEach
    void setUp(TestInfo testInfo, TestReporter testReporter) throws Exception {
        if (testInfo.getTags().contains("insertTournamentAndTeam")) {
            // Get the input values for the test method
            if (testInfo.getDisplayName().contains("2003")) {
                Tournament tournament = new Tournament();
                tournament.setTitle("competition3");
                tournament.setTournamentYear(2003);
                tournamentService.addTournament(tournament);
                testReporter.publishEntry("Setup", "Performing setup steps for insertTournamentAndTeam with tournamentYear = 2003");
            }

        }
    }

    @ParameterizedTest
    @Tag("insertTournamentAndTeam")
    @MethodSource("inputValues_insertTournamentAndTeam")
    void insertTournamentAndTeam(int tournamentYear,
                                 String tournamentTitle,
                                 ResultMatcher expected,
                                 List<TeamWithFavorite> listTeams) throws Exception {
        // ARRANGE
        TournamentWithTeamRequest tournamentWithTeamRequest = new TournamentWithTeamRequest();
        tournamentWithTeamRequest.setTournamentYear(tournamentYear);
        tournamentWithTeamRequest.setTitle(tournamentTitle);

        tournamentWithTeamRequest.setListTeam(listTeams);

        // ACT
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(tournamentWithTeamRequest);

        // ASSERT
        ResultActions result = mockMvc.perform(put("/api/tournament/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(expected);
        Gson gson = new Gson();
        TournamentWithTeamRequest tournamentWithTeamRequestResult = gson.fromJson(result.andReturn().getResponse().getContentAsString(), TournamentWithTeamRequest.class);
        if(tournamentWithTeamRequestResult != null){
            Tournament tournament = tournamentRepository.findByTitleAndTournamentYear(tournamentTitle,tournamentYear);
            List<TeamWithFavorite> listTeamCreate = new LinkedList<>();
            for(TeamWithFavorite team : listTeams){
                Team teamTmp = teamRepository.findByCountry(team.getCountry());
                listTeamCreate.add(
                        new TeamWithFavorite(teamTmp.getId(),teamTmp.getName(),teamTmp.getCountry(),team.getFavorite())
                );
            }
            TournamentWithTeamRequest tournamentWithTeamRequestEqual = new TournamentWithTeamRequest();
            tournamentWithTeamRequestEqual.setListTeam(listTeamCreate);
            tournamentWithTeamRequestEqual.setTitle(tournament.getTitle());
            tournamentWithTeamRequestEqual.setTournamentYear(tournament.getTournamentYear());
            tournamentWithTeamRequestEqual.setId(tournament.getId());

            assertThat(tournamentWithTeamRequestResult).isEqualTo(tournamentWithTeamRequestEqual);
            for(TeamWithFavorite team : listTeamCreate){
                Participate p = participateRepository.findByTeamAndTournament(
                        new Team(team.getId(),team.getName(),team.getCountry()),
                        tournament
                );
                assertThat(p).isNotNull();
            }
        }
    }

    static Stream<Arguments> inputValues_insertTournamentAndTeam(){
        return Stream.of(
                // [code 200] create tournament with its teams
                Arguments.of(2000, "competition", status().isOk(),
                        List.of(
                                new TeamWithFavorite("name1","country1",4),
                                new TeamWithFavorite("name1","country2",3),
                                new TeamWithFavorite("name1","country3",1)
                        )
                ),
                // [code 400] create tournament with its teams (teams list empty)
                Arguments.of(2002, "competition2", status().isBadRequest(), new LinkedList<>()),
                // [code 400] create tournament with its teams (tournament exist && teams doesn't exist)
                Arguments.of(2003, "competition3", status().isBadRequest(),
                        List.of(
                                new TeamWithFavorite("name1","country31",4),
                                new TeamWithFavorite("name1","country32",3),
                                new TeamWithFavorite("name1","country33",1)
                        )
                ),
                // [code 200] create tournament with its teams (one team exist && tournament doesn't exist)
                Arguments.of(2004, "competition4", status().isOk(),
                        List.of(
                                new TeamWithFavorite("name1","country1",4),
                                new TeamWithFavorite("name1","country42",3),
                                new TeamWithFavorite("name1","country43",1)
                        )
                ),
                //[code 400] create tournament with tournamentTitle empty
                Arguments.of(2004, "", status().isBadRequest(),
                        List.of(
                                new TeamWithFavorite("name1","country1",4),
                                new TeamWithFavorite("name1","country42",3),
                                new TeamWithFavorite("name1","country43",1)
                        )
                ),
                //[code 400] create tournament with tournamentTitle null
                Arguments.of(2004, null, status().isBadRequest(),
                        List.of(
                                new TeamWithFavorite("name1","country1",4),
                                new TeamWithFavorite("name1","country42",3),
                                new TeamWithFavorite("name1","country43",1)
                        )
                ),
                //[code 400] create tournament with a negative value of tournamentYear
                Arguments.of(-50, "competition", status().isBadRequest(),
                        List.of(
                                new TeamWithFavorite("name1","country1",4),
                                new TeamWithFavorite("name1","country42",3),
                                new TeamWithFavorite("name1","country43",1)
                        )
                ),
                //[code 400] create tournament with favorite value < 0
                Arguments.of(2010, "competition10", status().isBadRequest(),
                        List.of(
                                new TeamWithFavorite("name1","country1",-5),
                                new TeamWithFavorite("name1","country42",3),
                                new TeamWithFavorite("name1","country43",1)
                        )
                ),
                //[code 400] create tournament with favorite value > 5
                Arguments.of(2010, "competition10", status().isBadRequest(),
                        List.of(
                                new TeamWithFavorite("name1","country1",6),
                                new TeamWithFavorite("name1","country42",3),
                                new TeamWithFavorite("name1","country43",1)
                        )
                )
        );
    }


    @Test
    public void test_findAllTournament() throws Exception {

        //[code 200] any tournament in DB
        mockMvc.perform(get("/api/tournament/findAll")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Tournament tournament1 = new Tournament();
        tournament1.setTournamentYear(2000);
        tournament1.setTitle("tournois");

        Tournament tournament2 = new Tournament();
        tournament1.setTournamentYear(2010);
        tournament1.setTitle("Coupe du monde");

        tournamentService.addTournament(tournament1);
        tournamentService.addTournament(tournament2);

        //[code 200] returns all tournaments
        mockMvc.perform(get("/api/tournament/findAll")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        List<Tournament> list = tournamentRepository.findAll();
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.contains(tournament1)).isTrue();
        assertThat(list.contains(tournament2)).isTrue();
    }
}