package com.forumviajeros.backend.service.visitedplace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.forumviajeros.backend.dto.country.CountrySummaryDTO;
import com.forumviajeros.backend.dto.visitedplace.TravelStatsDTO;
import com.forumviajeros.backend.dto.visitedplace.VisitedPlaceRequestDTO;
import com.forumviajeros.backend.dto.visitedplace.VisitedPlaceResponseDTO;
import com.forumviajeros.backend.exception.BadRequestException;
import com.forumviajeros.backend.exception.ResourceNotFoundException;
import com.forumviajeros.backend.model.Country;
import com.forumviajeros.backend.model.User;
import com.forumviajeros.backend.model.VisitedPlace;
import com.forumviajeros.backend.model.VisitedPlace.PlaceStatus;
import com.forumviajeros.backend.repository.CountryRepository;
import com.forumviajeros.backend.repository.UserRepository;
import com.forumviajeros.backend.repository.VisitedPlaceRepository;
import com.forumviajeros.backend.service.country.CountryService;

import lombok.RequiredArgsConstructor;

/**
 * Implementación del servicio de lugares visitados
 */
@Service
@RequiredArgsConstructor
@Transactional
public class VisitedPlaceServiceImpl implements VisitedPlaceService {

    private final VisitedPlaceRepository visitedPlaceRepository;
    private final CountryRepository countryRepository;
    private final UserRepository userRepository;
    private final CountryService countryService;

    // Total de países en el mundo (aproximado)
    private static final int TOTAL_COUNTRIES = 195;

    @Override
    public VisitedPlaceResponseDTO addVisitedPlace(Long userId, VisitedPlaceRequestDTO request) {
        User user = findUserById(userId);
        Country country = findCountryById(request.getCountryId());

        // Verificar si ya existe
        if (request.getCityName() == null) {
            visitedPlaceRepository.findByUserIdAndCountryIdAndCityNameIsNull(userId, request.getCountryId())
                    .ifPresent(vp -> {
                        throw new BadRequestException("Ya has registrado este país");
                    });
        } else {
            visitedPlaceRepository.findByUserIdAndCountryIdAndCityName(userId, request.getCountryId(), request.getCityName())
                    .ifPresent(vp -> {
                        throw new BadRequestException("Ya has registrado esta ciudad");
                    });
        }

        VisitedPlace place = VisitedPlace.builder()
                .user(user)
                .country(country)
                .cityName(request.getCityName())
                .status(request.getStatus())
                .visitDate(request.getVisitDate())
                .visitEndDate(request.getVisitEndDate())
                .notes(request.getNotes())
                .rating(request.getRating())
                .favorite(request.getFavorite() != null ? request.getFavorite() : false)
                .visitCount(request.getVisitCount() != null ? request.getVisitCount() : 1)
                .build();

        place = visitedPlaceRepository.save(place);
        return toResponseDTO(place);
    }

    @Override
    public VisitedPlaceResponseDTO updateVisitedPlace(Long userId, Long placeId, VisitedPlaceRequestDTO request) {
        VisitedPlace place = findPlaceByIdAndUser(placeId, userId);

        if (request.getStatus() != null) {
            place.setStatus(request.getStatus());
        }
        if (request.getVisitDate() != null) {
            place.setVisitDate(request.getVisitDate());
        }
        if (request.getVisitEndDate() != null) {
            place.setVisitEndDate(request.getVisitEndDate());
        }
        if (request.getNotes() != null) {
            place.setNotes(request.getNotes());
        }
        if (request.getRating() != null) {
            place.setRating(request.getRating());
        }
        if (request.getFavorite() != null) {
            place.setFavorite(request.getFavorite());
        }
        if (request.getVisitCount() != null) {
            place.setVisitCount(request.getVisitCount());
        }

        place = visitedPlaceRepository.save(place);
        return toResponseDTO(place);
    }

    @Override
    public void deleteVisitedPlace(Long userId, Long placeId) {
        VisitedPlace place = findPlaceByIdAndUser(placeId, userId);
        visitedPlaceRepository.delete(place);
    }

    @Override
    @Transactional(readOnly = true)
    public VisitedPlaceResponseDTO getVisitedPlaceById(Long placeId) {
        VisitedPlace place = visitedPlaceRepository.findById(placeId)
                .orElseThrow(() -> new ResourceNotFoundException("VisitedPlace", "id", placeId));
        return toResponseDTO(place);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VisitedPlaceResponseDTO> getUserPlaces(Long userId) {
        return visitedPlaceRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VisitedPlaceResponseDTO> getUserPlacesPaginated(Long userId, Pageable pageable) {
        return visitedPlaceRepository.findByUserId(userId, pageable)
                .map(this::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VisitedPlaceResponseDTO> getUserPlacesByStatus(Long userId, PlaceStatus status) {
        return visitedPlaceRepository.findByUserIdAndStatusOrderByVisitDateDesc(userId, status)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VisitedPlaceResponseDTO> getUserFavoritePlaces(Long userId) {
        return visitedPlaceRepository.findByUserIdAndFavoriteTrueOrderByVisitDateDesc(userId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public VisitedPlaceResponseDTO toggleFavorite(Long userId, Long placeId) {
        VisitedPlace place = findPlaceByIdAndUser(placeId, userId);
        place.setFavorite(!place.getFavorite());
        place = visitedPlaceRepository.save(place);
        return toResponseDTO(place);
    }

    @Override
    @Transactional(readOnly = true)
    public TravelStatsDTO getUserTravelStats(Long userId) {
        User user = findUserById(userId);

        // Contar países por estado
        long countriesVisited = visitedPlaceRepository.countDistinctCountriesByUserIdAndStatus(userId, PlaceStatus.VISITED);
        long countriesWishlist = visitedPlaceRepository.countDistinctCountriesByUserIdAndStatus(userId, PlaceStatus.WISHLIST);
        long countriesLived = visitedPlaceRepository.countDistinctCountriesByUserIdAndStatus(userId, PlaceStatus.LIVED);
        long citiesVisited = visitedPlaceRepository.countCitiesByUserIdAndStatus(userId, PlaceStatus.VISITED);

        // Área visitada
        Double areaVisited = visitedPlaceRepository.sumVisitedAreaByUserId(userId);
        Double totalWorldArea = countryService.getTotalWorldArea();

        // Porcentajes
        double percentByArea = totalWorldArea > 0 ? (areaVisited / totalWorldArea) * 100 : 0;
        double percentByCountries = (countriesVisited * 100.0) / TOTAL_COUNTRIES;

        // Continentes
        List<String> continents = visitedPlaceRepository.findVisitedContinentsByUserId(userId);

        // Países por continente
        Map<String, Integer> countriesByContinent = new HashMap<>();
        List<Object[]> continentCounts = visitedPlaceRepository.countCountriesByContinent(userId);
        for (Object[] row : continentCounts) {
            countriesByContinent.put((String) row[0], ((Long) row[1]).intValue());
        }

        // País más visitado
        List<String> mostVisited = visitedPlaceRepository.findMostVisitedCountryByUserId(userId, PageRequest.of(0, 1));
        String mostVisitedCountry = mostVisited.isEmpty() ? null : mostVisited.get(0);

        // Lugar favorito
        List<VisitedPlace> favorites = visitedPlaceRepository.findByUserIdAndFavoriteTrueOrderByVisitDateDesc(userId);
        String favoritePlace = favorites.isEmpty() ? null : 
            (favorites.get(0).getCityName() != null ? 
                favorites.get(0).getCityName() + ", " + favorites.get(0).getCountry().getName() : 
                favorites.get(0).getCountry().getName());

        // Nivel de viajero
        String travelerLevel = TravelStatsDTO.calculateTravelerLevel((int) countriesVisited);

        // Badges
        List<String> badges = calculateBadges((int) countriesVisited, continents.size(), (int) citiesVisited);

        // Ranking
        Integer ranking = visitedPlaceRepository.findUserRanking(userId);

        return TravelStatsDTO.builder()
                .userId(userId)
                .username(user.getUsername())
                .countriesVisited((int) countriesVisited)
                .countriesWishlist((int) countriesWishlist)
                .countriesLived((int) countriesLived)
                .citiesVisited((int) citiesVisited)
                .worldPercentageByArea(Math.round(percentByArea * 100.0) / 100.0)
                .worldPercentageByCountries(Math.round(percentByCountries * 100.0) / 100.0)
                .totalAreaVisitedSqKm(areaVisited)
                .continentsVisited(continents.size())
                .continentsList(continents)
                .countriesByContinent(countriesByContinent)
                .mostVisitedCountry(mostVisitedCountry)
                .favoritePlace(favoritePlace)
                .travelerLevel(travelerLevel)
                .badges(badges)
                .globalRanking(ranking)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserVisitedCountry(Long userId, Long countryId) {
        return visitedPlaceRepository.existsByUserIdAndCountryIdAndStatus(userId, countryId, PlaceStatus.VISITED);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TravelStatsDTO> getTravelersRanking(int limit) {
        List<Object[]> ranking = visitedPlaceRepository.findUsersRankedByCountriesVisited(PageRequest.of(0, limit));
        List<TravelStatsDTO> result = new ArrayList<>();

        int position = 1;
        for (Object[] row : ranking) {
            Long userId = (Long) row[0];
            String username = (String) row[1];
            Integer countries = ((Long) row[2]).intValue();

            result.add(TravelStatsDTO.builder()
                    .userId(userId)
                    .username(username)
                    .countriesVisited(countries)
                    .travelerLevel(TravelStatsDTO.calculateTravelerLevel(countries))
                    .globalRanking(position++)
                    .build());
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getUserRanking(Long userId) {
        return visitedPlaceRepository.findUserRanking(userId);
    }

    // === Métodos auxiliares ===

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    private Country findCountryById(Long countryId) {
        return countryRepository.findById(countryId)
                .orElseThrow(() -> new ResourceNotFoundException("Country", "id", countryId));
    }

    private VisitedPlace findPlaceByIdAndUser(Long placeId, Long userId) {
        VisitedPlace place = visitedPlaceRepository.findById(placeId)
                .orElseThrow(() -> new ResourceNotFoundException("VisitedPlace", "id", placeId));

        if (!place.getUser().getId().equals(userId)) {
            throw new BadRequestException("No tienes permiso para modificar este lugar");
        }

        return place;
    }

    private List<String> calculateBadges(int countries, int continents, int cities) {
        List<String> badges = new ArrayList<>();

        if (countries >= 1) badges.add("🎒 Primer Paso");
        if (countries >= 5) badges.add("🗺️ Explorador");
        if (countries >= 10) badges.add("✈️ Viajero Frecuente");
        if (countries >= 25) badges.add("🌍 Ciudadano del Mundo");
        if (countries >= 50) badges.add("🏆 Trotamundos");
        if (countries >= 100) badges.add("👑 Leyenda Viajera");

        if (continents >= 2) badges.add("🌐 Intercontinental");
        if (continents >= 5) badges.add("🌏 Cinco Continentes");
        if (continents >= 7) badges.add("⭐ Todos los Continentes");

        if (cities >= 10) badges.add("🏙️ Urbanita");
        if (cities >= 50) badges.add("🌆 Coleccionista de Ciudades");

        return badges;
    }

    private VisitedPlaceResponseDTO toResponseDTO(VisitedPlace place) {
        return VisitedPlaceResponseDTO.builder()
                .id(place.getId())
                .userId(place.getUser().getId())
                .username(place.getUser().getUsername())
                .country(CountrySummaryDTO.builder()
                        .id(place.getCountry().getId())
                        .isoCode(place.getCountry().getIsoCode())
                        .name(place.getCountry().getName())
                        .capital(place.getCountry().getCapital())
                        .continent(place.getCountry().getContinent())
                        .flagUrl(place.getCountry().getFlagUrl())
                        .flagEmoji(place.getCountry().getFlagEmoji())
                        .build())
                .cityName(place.getCityName())
                .status(place.getStatus())
                .visitDate(place.getVisitDate())
                .visitEndDate(place.getVisitEndDate())
                .notes(place.getNotes())
                .rating(place.getRating())
                .favorite(place.getFavorite())
                .visitCount(place.getVisitCount())
                .createdAt(place.getCreatedAt())
                .updatedAt(place.getUpdatedAt())
                .build();
    }
}

