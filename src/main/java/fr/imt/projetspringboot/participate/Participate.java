package fr.imt.projetspringboot.participate;

import fr.imt.projetspringboot.team.Team;
import fr.imt.projetspringboot.tournament.Tournament;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "participate")
public class Participate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @Column
    private int favorite;

    public void setFavorite(int favorite) throws Exception {
        if(favorite < 0 || favorite >5){
            throw new Exception("Favorite need be between 0 and 5");
        }
        this.favorite = favorite;
    }

    public Participate() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participate that = (Participate) o;
        return favorite == that.favorite && Objects.equals(team, that.team) && Objects.equals(tournament, that.tournament);
    }

    @Override
    public int hashCode() {
        return Objects.hash(team, tournament, favorite);
    }
}