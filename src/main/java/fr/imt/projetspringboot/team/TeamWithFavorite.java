package fr.imt.projetspringboot.team;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamWithFavorite extends Team {
    private int favorite;

    public TeamWithFavorite(){
        super();
    }

    public TeamWithFavorite(String name, String country, int favorite) {
        super(name,country);
        this.favorite = favorite;
    }

    public TeamWithFavorite(Long id, String name, String country, int favorite) {
        super(id, name, country);
        this.favorite = favorite;
    }

    @Override
    public String toString() {
        return "TeamWithFavorite{" +
                "name='" + super.getName() + '\'' +
                ", country='" + super.getCountry() + '\'' +
                ", favorite=" + favorite +
                '}';
    }
}
