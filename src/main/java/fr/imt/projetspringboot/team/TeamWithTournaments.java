package fr.imt.projetspringboot.team;

import fr.imt.projetspringboot.tournament.Tournament;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class TeamWithTournaments extends Team {
    private List<Tournament> listTournaments;

    public TeamWithTournaments() {
        super();
    }

    public TeamWithTournaments(String name, String country, List<Tournament> listTournaments) {
        super(name, country);
        this.listTournaments = listTournaments;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TeamWithTournaments that = (TeamWithTournaments) o;
        return Objects.equals(listTournaments, that.listTournaments) && super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), listTournaments);
    }
}
