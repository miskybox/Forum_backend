package com.forumviajeros.backend.service.country;

import java.util.List;

import com.forumviajeros.backend.dto.country.CountryResponseDTO;
import com.forumviajeros.backend.dto.country.CountrySummaryDTO;

/**
 * Servicio para gestión de países
 */
public interface CountryService {

    /**
     * Obtiene todos los países
     */
    List<CountrySummaryDTO> getAllCountries();

    /**
     * Obtiene un país por ID
     */
    CountryResponseDTO getCountryById(Long id);

    /**
     * Obtiene un país por código ISO
     */
    CountryResponseDTO getCountryByIsoCode(String isoCode);

    /**
     * Busca países por nombre (autocompletado)
     */
    List<CountrySummaryDTO> searchCountries(String query);

    /**
     * Obtiene países por continente
     */
    List<CountrySummaryDTO> getCountriesByContinent(String continent);

    /**
     * Obtiene la lista de continentes
     */
    List<String> getAllContinents();

    /**
     * Obtiene regiones de un continente
     */
    List<String> getRegionsByContinent(String continent);

    /**
     * Obtiene países aleatorios (para trivia)
     */
    List<CountrySummaryDTO> getRandomCountries(int count);

    /**
     * Obtiene el total de países
     */
    long getTotalCountries();

    /**
     * Obtiene el área total del mundo
     */
    Double getTotalWorldArea();
}

