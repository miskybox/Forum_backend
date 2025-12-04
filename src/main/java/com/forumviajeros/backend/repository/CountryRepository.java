package com.forumviajeros.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.forumviajeros.backend.model.Country;

/**
 * Repositorio para la entidad Country
 */
@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

    /**
     * Busca un país por su código ISO alpha-2
     */
    Optional<Country> findByIsoCode(String isoCode);

    /**
     * Busca un país por su código ISO alpha-3
     */
    Optional<Country> findByIsoCode3(String isoCode3);

    /**
     * Busca un país por nombre (ignorando mayúsculas)
     */
    Optional<Country> findByNameIgnoreCase(String name);

    /**
     * Obtiene todos los países de un continente
     */
    List<Country> findByContinentIgnoreCaseOrderByNameAsc(String continent);

    /**
     * Obtiene todos los países de una región
     */
    List<Country> findByRegionIgnoreCaseOrderByNameAsc(String region);

    /**
     * Obtiene todos los países activos ordenados por nombre
     */
    List<Country> findByActiveTrueOrderByNameAsc();

    /**
     * Busca países por nombre (parcial, para autocompletado)
     */
    @Query("SELECT c FROM Country c WHERE c.active = true AND " +
           "(LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.nameEn) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Country> searchByName(@Param("search") String search);

    /**
     * Obtiene la lista de continentes únicos
     */
    @Query("SELECT DISTINCT c.continent FROM Country c WHERE c.active = true ORDER BY c.continent")
    List<String> findAllContinents();

    /**
     * Obtiene la lista de regiones de un continente
     */
    @Query("SELECT DISTINCT c.region FROM Country c WHERE c.continent = :continent AND c.active = true ORDER BY c.region")
    List<String> findRegionsByContinent(@Param("continent") String continent);

    /**
     * Cuenta el total de países activos
     */
    long countByActiveTrue();

    /**
     * Suma el área total de todos los países
     */
    @Query("SELECT SUM(c.areaSqKm) FROM Country c WHERE c.active = true")
    Double sumTotalArea();

    /**
     * Obtiene países aleatorios (para trivia)
     */
    @Query(value = "SELECT * FROM countries WHERE active = true ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<Country> findRandomCountries(@Param("limit") int limit);

    /**
     * Obtiene países de un continente para trivia (aleatorios)
     */
    @Query(value = "SELECT * FROM countries WHERE active = true AND LOWER(continent) = LOWER(:continent) ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<Country> findRandomCountriesByContinent(@Param("continent") String continent, @Param("limit") int limit);

    /**
     * Verifica si existe un país con el código ISO dado
     */
    boolean existsByIsoCode(String isoCode);
}

