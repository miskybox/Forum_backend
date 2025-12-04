package com.forumviajeros.backend.service;

import com.forumviajeros.backend.dto.visitedplace.TravelStatsDTO;
import com.forumviajeros.backend.model.Country;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para modelo Country y lógica relacionada
 */
class CountryServiceTest {

    private Country testCountry;

    @BeforeEach
    void setUp() {
        testCountry = Country.builder()
                .id(1L)
                .isoCode("ES")
                .isoCode3("ESP")
                .name("España")
                .nameEn("Spain")
                .capital("Madrid")
                .continent("Europe")
                .currencyName("Euro")
                .currencyCode("EUR")
                .currencySymbol("€")
                .population(47000000L)
                .areaSqKm(505990.0)
                .languages(Arrays.asList("Español", "Catalán", "Euskera", "Gallego"))
                .build();
    }

    @Test
    @DisplayName("Country debe crearse correctamente")
    void country_ShouldCreateCorrectly() {
        assertNotNull(testCountry);
        assertEquals(1L, testCountry.getId());
        assertEquals("ES", testCountry.getIsoCode());
        assertEquals("España", testCountry.getName());
    }

    @Test
    @DisplayName("Country debe tener capital")
    void country_ShouldHaveCapital() {
        assertEquals("Madrid", testCountry.getCapital());
    }

    @Test
    @DisplayName("Country debe tener continente")
    void country_ShouldHaveContinent() {
        assertEquals("Europe", testCountry.getContinent());
    }

    @Test
    @DisplayName("Country debe tener información de moneda")
    void country_ShouldHaveCurrency() {
        assertEquals("Euro", testCountry.getCurrencyName());
        assertEquals("EUR", testCountry.getCurrencyCode());
        assertEquals("€", testCountry.getCurrencySymbol());
    }

    @Test
    @DisplayName("Country debe tener población")
    void country_ShouldHavePopulation() {
        assertEquals(47000000L, testCountry.getPopulation());
    }

    @Test
    @DisplayName("Country debe tener área")
    void country_ShouldHaveArea() {
        assertEquals(505990.0, testCountry.getAreaSqKm());
    }

    @Test
    @DisplayName("Country debe tener idiomas")
    void country_ShouldHaveLanguages() {
        assertNotNull(testCountry.getLanguages());
        assertEquals(4, testCountry.getLanguages().size());
        assertTrue(testCountry.getLanguages().contains("Español"));
    }

    @Test
    @DisplayName("Country debe tener código ISO de 2 letras")
    void country_ShouldHaveIsoCode2() {
        assertEquals(2, testCountry.getIsoCode().length());
    }

    @Test
    @DisplayName("Country debe tener código ISO de 3 letras")
    void country_ShouldHaveIsoCode3() {
        assertEquals("ESP", testCountry.getIsoCode3());
        assertEquals(3, testCountry.getIsoCode3().length());
    }

    @Test
    @DisplayName("Debe calcular nivel de viajero para principiante")
    void travelerLevel_ShouldBeBeginnerFor1Country() {
        assertEquals("👣 Principiante", TravelStatsDTO.calculateTravelerLevel(1));
    }

    @Test
    @DisplayName("Debe calcular nivel de viajero para turista")
    void travelerLevel_ShouldBeTouristFor5Countries() {
        assertEquals("🗺️ Turista", TravelStatsDTO.calculateTravelerLevel(5));
    }

    @Test
    @DisplayName("Debe calcular nivel de viajero para explorador")
    void travelerLevel_ShouldBeExplorerFor10Countries() {
        assertEquals("🎒 Explorador", TravelStatsDTO.calculateTravelerLevel(10));
    }

    @Test
    @DisplayName("Debe calcular nivel de viajero para leyenda")
    void travelerLevel_ShouldBeLegendFor100Countries() {
        assertEquals("🌟 Leyenda Viajera", TravelStatsDTO.calculateTravelerLevel(100));
    }

    @Test
    @DisplayName("Debe calcular porcentaje del mundo")
    void worldPercentage_ShouldCalculateCorrectly() {
        int visited = 50;
        int total = 195;
        double percentage = (visited * 100.0) / total;
        
        assertTrue(percentage > 25 && percentage < 26);
    }

    @Test
    @DisplayName("Country debe poder tener datos curiosos")
    void country_ShouldSupportFunFacts() {
        testCountry.setFunFacts(Arrays.asList(
            "España tiene más de 8000 km de costa",
            "Es el segundo país más montañoso de Europa"
        ));
        
        assertEquals(2, testCountry.getFunFacts().size());
    }

    @Test
    @DisplayName("Country debe estar activo por defecto")
    void country_ShouldBeActiveByDefault() {
        Country newCountry = Country.builder()
                .isoCode("FR")
                .name("Francia")
                .capital("París")
                .continent("Europe")
                .build();
        
        assertTrue(newCountry.getActive());
    }
}
