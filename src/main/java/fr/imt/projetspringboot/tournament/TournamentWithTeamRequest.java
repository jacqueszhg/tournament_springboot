package fr.imt.projetspringboot.tournament;

import fr.imt.projetspringboot.team.TeamWithFavorite;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class TournamentWithTeamRequest extends Tournament{
    private List<TeamWithFavorite> listTeam;

    public TournamentWithTeamRequest(){

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TournamentWithTeamRequest that = (TournamentWithTeamRequest) o;
        return Objects.equals(listTeam, that.listTeam) && super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), listTeam);
    }
}
