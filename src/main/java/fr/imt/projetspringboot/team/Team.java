package fr.imt.projetspringboot.team;

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
@Table(name = "team", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "country"})
})
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @Column(unique = true)
    private String country;

    @JsonIgnore
    @OneToMany(mappedBy = "team")
    private Set<Participate> participates;

    public Team() {
    }

    public Team(String name, String country) {
        this.name = name;
        this.country = country;
    }

    public Team(Long id, String name, String country) {
        this.id = id;
        this.name = name;
        this.country = country;
    }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return Objects.equals(name, team.name) && Objects.equals(country, team.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, country);
    }
}
