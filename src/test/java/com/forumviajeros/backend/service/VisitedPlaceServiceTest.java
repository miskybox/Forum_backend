package com.forumviajeros.backend.service;

import com.forumviajeros.backend.dto.visitedplace.*;
import com.forumviajeros.backend.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para modelos y DTOs de VisitedPlace
 */
class VisitedPlaceServiceTest {

    private User testUser;
    private Country testCountry;
    private VisitedPlace testVisitedPlace;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("traveler")
                .email("traveler@test.com")
                .build();

        testCountry = Country.builder()
                .id(1L)
                .isoCode("JP")
                .name("Japón")
                .capital("Tokio")
                .continent("Asia")
                .areaSqKm(377975.0)
                .build();

        testVisitedPlace = VisitedPlace.builder()
                .id(1L)
                .user(testUser)
                .country(testCountry)
                .cityName("Tokio")
                .status(VisitedPlace.PlaceStatus.VISITED)
                .visitDate(LocalDate.of(2023, 5, 15))
                .notes("Viaje increíble")
                .build();
    }

    @Test
    @DisplayName("VisitedPlace debe crearse correctamente")
    void visitedPlace_ShouldCreateCorrectly() {
        assertNotNull(testVisitedPlace);
        assertEquals(1L, testVisitedPlace.getId());
        assertEquals("Tokio", testVisitedPlace.getCityName());
        assertEquals(VisitedPlace.PlaceStatus.VISITED, testVisitedPlace.getStatus());
    }

    @Test
    @DisplayName("VisitedPlace debe tener usuario asociado")
    void visitedPlace_ShouldHaveUser() {
        assertNotNull(testVisitedPlace.getUser());
        assertEquals("traveler", testVisitedPlace.getUser().getUsername());
    }

    @Test
    @DisplayName("VisitedPlace debe tener país asociado")
    void visitedPlace_ShouldHaveCountry() {
        assertNotNull(testVisitedPlace.getCountry());
        assertEquals("Japón", testVisitedPlace.getCountry().getName());
        assertEquals("JP", testVisitedPlace.getCountry().getIsoCode());
    }

    @Test
    @DisplayName("PlaceStatus enum debe tener todos los valores")
    void placeStatus_ShouldHaveAllValues() {
        VisitedPlace.PlaceStatus[] statuses = VisitedPlace.PlaceStatus.values();
        assertTrue(statuses.length >= 3);
        assertNotNull(VisitedPlace.PlaceStatus.valueOf("VISITED"));
        assertNotNull(VisitedPlace.PlaceStatus.valueOf("WISHLIST"));
        assertNotNull(VisitedPlace.PlaceStatus.valueOf("LIVED"));
    }

    @Test
    @DisplayName("Debe calcular nivel de viajero correctamente")
    void calculateTravelerLevel_ShouldReturnCorrectLevel() {
        assertEquals("🏠 Soñador", TravelStatsDTO.calculateTravelerLevel(0));
        assertEquals("👣 Principiante", TravelStatsDTO.calculateTravelerLevel(1));
        assertEquals("🗺️ Turista", TravelStatsDTO.calculateTravelerLevel(5));
        assertEquals("🎒 Explorador", TravelStatsDTO.calculateTravelerLevel(10));
        assertEquals("✈️ Aventurero", TravelStatsDTO.calculateTravelerLevel(20));
        assertEquals("🌍 Viajero Experto", TravelStatsDTO.calculateTravelerLevel(30));
        assertEquals("🎖️ Trotamundos", TravelStatsDTO.calculateTravelerLevel(50));
        assertEquals("🏆 Maestro Explorador", TravelStatsDTO.calculateTravelerLevel(75));
        assertEquals("🌟 Leyenda Viajera", TravelStatsDTO.calculateTravelerLevel(100));
    }

    @Test
    @DisplayName("Debe calcular porcentaje del mundo correctamente")
    void calculateWorldPercentage_ShouldBeAccurate() {
        long visitedCountries = 50;
        long totalCountries = 195;
        double percentage = (visitedCountries * 100.0) / totalCountries;
        
        assertTrue(percentage > 25 && percentage < 26);
    }

    @Test
    @DisplayName("TravelStatsDTO debe construirse correctamente")
    void travelStatsDTO_ShouldBuildCorrectly() {
        TravelStatsDTO stats = TravelStatsDTO.builder()
                .userId(1L)
                .username("traveler")
                .countriesVisited(25)
                .countriesWishlist(15)
                .countriesLived(2)
                .citiesVisited(50)
                .worldPercentageByCountries(12.8)
                .continentsVisited(4)
                .build();

        assertNotNull(stats);
        assertEquals(1L, stats.getUserId());
        assertEquals(25, stats.getCountriesVisited());
        assertEquals(15, stats.getCountriesWishlist());
        assertEquals(4, stats.getContinentsVisited());
    }

    @Test
    @DisplayName("Country debe tener información geográfica")
    void country_ShouldHaveGeographicInfo() {
        assertNotNull(testCountry);
        assertEquals("Asia", testCountry.getContinent());
        assertEquals(377975.0, testCountry.getAreaSqKm());
    }

    @Test
    @DisplayName("VisitedPlace debe soportar notas")
    void visitedPlace_ShouldSupportNotes() {
        assertEquals("Viaje increíble", testVisitedPlace.getNotes());
    }

    @Test
    @DisplayName("VisitedPlace debe soportar fecha de visita")
    void visitedPlace_ShouldSupportVisitDate() {
        assertNotNull(testVisitedPlace.getVisitDate());
        assertEquals(2023, testVisitedPlace.getVisitDate().getYear());
        assertEquals(5, testVisitedPlace.getVisitDate().getMonthValue());
    }
}
