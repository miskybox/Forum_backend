package com.forumviajeros.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.forumviajeros.backend.model.VisitedPlace;
import com.forumviajeros.backend.model.VisitedPlace.PlaceStatus;

/**
 * Repositorio para la entidad VisitedPlace
 */
@Repository
public interface VisitedPlaceRepository extends JpaRepository<VisitedPlace, Long> {

    /**
     * Obtiene todos los lugares de un usuario
     */
    List<VisitedPlace> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Obtiene lugares de un usuario con paginación
     */
    Page<VisitedPlace> findByUserId(Long userId, Pageable pageable);

    /**
     * Obtiene lugares de un usuario por estado
     */
    List<VisitedPlace> findByUserIdAndStatusOrderByVisitDateDesc(Long userId, PlaceStatus status);

    /**
     * Obtiene lugares favoritos de un usuario
     */
    List<VisitedPlace> findByUserIdAndFavoriteTrueOrderByVisitDateDesc(Long userId);

    /**
     * Busca un lugar específico de un usuario
     */
    Optional<VisitedPlace> findByUserIdAndCountryIdAndCityName(Long userId, Long countryId, String cityName);

    /**
     * Busca un país visitado por un usuario (sin ciudad específica)
     */
    Optional<VisitedPlace> findByUserIdAndCountryIdAndCityNameIsNull(Long userId, Long countryId);

    /**
     * Verifica si un usuario ha visitado un país
     */
    boolean existsByUserIdAndCountryIdAndStatus(Long userId, Long countryId, PlaceStatus status);

    /**
     * Cuenta países visitados por un usuario
     */
    @Query("SELECT COUNT(DISTINCT vp.country.id) FROM VisitedPlace vp WHERE vp.user.id = :userId AND vp.status = :status")
    long countDistinctCountriesByUserIdAndStatus(@Param("userId") Long userId, @Param("status") PlaceStatus status);

    /**
     * Cuenta ciudades visitadas por un usuario
     */
    @Query("SELECT COUNT(vp) FROM VisitedPlace vp WHERE vp.user.id = :userId AND vp.cityName IS NOT NULL AND vp.status = :status")
    long countCitiesByUserIdAndStatus(@Param("userId") Long userId, @Param("status") PlaceStatus status);

    /**
     * Suma el área total de países visitados por un usuario
     */
    @Query("SELECT COALESCE(SUM(DISTINCT c.areaSqKm), 0) FROM VisitedPlace vp " +
           "JOIN vp.country c WHERE vp.user.id = :userId AND vp.status = 'VISITED'")
    Double sumVisitedAreaByUserId(@Param("userId") Long userId);

    /**
     * Obtiene los continentes visitados por un usuario
     */
    @Query("SELECT DISTINCT c.continent FROM VisitedPlace vp " +
           "JOIN vp.country c WHERE vp.user.id = :userId AND vp.status = 'VISITED'")
    List<String> findVisitedContinentsByUserId(@Param("userId") Long userId);

    /**
     * Cuenta países por continente para un usuario
     */
    @Query("SELECT c.continent, COUNT(DISTINCT c.id) FROM VisitedPlace vp " +
           "JOIN vp.country c WHERE vp.user.id = :userId AND vp.status = 'VISITED' " +
           "GROUP BY c.continent")
    List<Object[]> countCountriesByContinent(@Param("userId") Long userId);

    /**
     * Obtiene el país más visitado por un usuario
     */
    @Query("SELECT vp.country.name FROM VisitedPlace vp WHERE vp.user.id = :userId " +
           "GROUP BY vp.country.id, vp.country.name ORDER BY SUM(vp.visitCount) DESC")
    List<String> findMostVisitedCountryByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * Ranking de usuarios por países visitados
     */
    @Query("SELECT vp.user.id, vp.user.username, COUNT(DISTINCT vp.country.id) as countries " +
           "FROM VisitedPlace vp WHERE vp.status = 'VISITED' " +
           "GROUP BY vp.user.id, vp.user.username ORDER BY countries DESC")
    List<Object[]> findUsersRankedByCountriesVisited(Pageable pageable);

    /**
     * Obtiene la posición de un usuario en el ranking
     */
    @Query(value = "SELECT rank FROM (" +
           "SELECT user_id, RANK() OVER (ORDER BY COUNT(DISTINCT country_id) DESC) as rank " +
           "FROM visited_places WHERE status = 'VISITED' GROUP BY user_id) ranked " +
           "WHERE user_id = :userId", nativeQuery = true)
    Integer findUserRanking(@Param("userId") Long userId);
}

