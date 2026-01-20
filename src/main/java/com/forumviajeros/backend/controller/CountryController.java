package com.forumviajeros.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.forumviajeros.backend.dto.country.CountryResponseDTO;
import com.forumviajeros.backend.dto.country.CountrySummaryDTO;
import com.forumviajeros.backend.service.country.CountryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para gestión de países
 */
@RestController
@RequestMapping("/api/countries")
@RequiredArgsConstructor
@Tag(name = "Countries", description = "API para consultar información de países")
public class CountryController {

    private final CountryService countryService;

    @GetMapping
    @Operation(summary = "Obtener todos los países")
    public ResponseEntity<List<CountrySummaryDTO>> getAllCountries() {
        return ResponseEntity.ok(countryService.getAllCountries());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener país por ID")
    public ResponseEntity<CountryResponseDTO> getCountryById(@PathVariable Long id) {
        return ResponseEntity.ok(countryService.getCountryById(id));
    }

    @GetMapping("/code/{isoCode}")
    @Operation(summary = "Obtener país por código ISO")
    public ResponseEntity<CountryResponseDTO> getCountryByIsoCode(@PathVariable String isoCode) {
        return ResponseEntity.ok(countryService.getCountryByIsoCode(isoCode));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar países por nombre")
    public ResponseEntity<List<CountrySummaryDTO>> searchCountries(@RequestParam String q) {
        return ResponseEntity.ok(countryService.searchCountries(q));
    }

    @GetMapping("/continent/{continent}")
    @Operation(summary = "Obtener países por continente")
    public ResponseEntity<List<CountrySummaryDTO>> getCountriesByContinent(@PathVariable String continent) {
        return ResponseEntity.ok(countryService.getCountriesByContinent(continent));
    }

    @GetMapping("/continents")
    @Operation(summary = "Obtener lista de continentes")
    public ResponseEntity<List<String>> getAllContinents() {
        return ResponseEntity.ok(countryService.getAllContinents());
    }

    @GetMapping("/regions/{continent}")
    @Operation(summary = "Obtener regiones de un continente")
    public ResponseEntity<List<String>> getRegionsByContinent(@PathVariable String continent) {
        return ResponseEntity.ok(countryService.getRegionsByContinent(continent));
    }

    @GetMapping("/random")
    @Operation(summary = "Obtener países aleatorios")
    public ResponseEntity<List<CountrySummaryDTO>> getRandomCountries(
            @RequestParam(defaultValue = "5") int count) {
        return ResponseEntity.ok(countryService.getRandomCountries(count));
    }

    @GetMapping("/stats")
    @Operation(summary = "Obtener estadísticas globales")
    public ResponseEntity<CountryStatsResponse> getCountryStats() {
        return ResponseEntity.ok(new CountryStatsResponse(
                countryService.getTotalCountries(),
                countryService.getTotalWorldArea()
        ));
    }

    /**
     * DTO para respuesta de estadísticas
     */
    public record CountryStatsResponse(long totalCountries, Double totalAreaSqKm) {}
}

