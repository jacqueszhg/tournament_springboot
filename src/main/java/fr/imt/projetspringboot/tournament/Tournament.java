package fr.imt.projetspringboot.tournament;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.imt.projetspringboot.participate.Participate;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "tournament", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"title", "tournament_year"})
})
public class Tournament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column(name = "tournament_year")
    private int tournamentYear;


    @JsonIgnore
    @OneToMany(mappedBy = "tournament")
    private Set<Participate> participates;


    public Tournament(String title, int tournamentYear) {
        this.title = title;
        this.tournamentYear = tournamentYear;
    }

    public Tournament() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tournament that = (Tournament) o;
        return tournamentYear == that.tournamentYear && Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, tournamentYear);
    }
}