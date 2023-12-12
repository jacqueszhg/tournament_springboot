package fr.imt.projetspringboot.team;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    // You can add custom methods here if needed
    @Query("SELECT t FROM Team t WHERE t.country = :country")
    Team findByCountry(@Param("country") String country);
}

