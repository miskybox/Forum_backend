package com.forumviajeros.backend.service.country;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.forumviajeros.backend.dto.country.CountryResponseDTO;
import com.forumviajeros.backend.dto.country.CountrySummaryDTO;
import com.forumviajeros.backend.exception.ResourceNotFoundException;
import com.forumviajeros.backend.model.Country;
import com.forumviajeros.backend.repository.CountryRepository;

import lombok.RequiredArgsConstructor;

/**
 * Implementación del servicio de países
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;

    @Override
    public List<CountrySummaryDTO> getAllCountries() {
        return countryRepository.findByActiveTrueOrderByNameAsc()
                .stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CountryResponseDTO getCountryById(Long id) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("País no encontrado con ID: " + id));
        return toResponseDTO(country);
    }

    @Override
    public CountryResponseDTO getCountryByIsoCode(String isoCode) {
        Country country = countryRepository.findByIsoCode(isoCode.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("País no encontrado con código: " + isoCode));
        return toResponseDTO(country);
    }

    @Override
    public List<CountrySummaryDTO> searchCountries(String query) {
        return countryRepository.searchByName(query)
                .stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CountrySummaryDTO> getCountriesByContinent(String continent) {
        return countryRepository.findByContinentIgnoreCaseOrderByNameAsc(continent)
                .stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllContinents() {
        return countryRepository.findAllContinents();
    }

    @Override
    public List<String> getRegionsByContinent(String continent) {
        return countryRepository.findRegionsByContinent(continent);
    }

    @Override
    public List<CountrySummaryDTO> getRandomCountries(int count) {
        return countryRepository.findRandomCountries(count)
                .stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    public long getTotalCountries() {
        return countryRepository.countByActiveTrue();
    }

    @Override
    public Double getTotalWorldArea() {
        Double total = countryRepository.sumTotalArea();
        return total != null ? total : 0.0;
    }

    /**
     * Convierte entidad a DTO resumido
     */
    private CountrySummaryDTO toSummaryDTO(Country country) {
        return CountrySummaryDTO.builder()
                .id(country.getId())
                .isoCode(country.getIsoCode())
                .name(country.getName())
                .capital(country.getCapital())
                .continent(country.getContinent())
                .flagUrl(country.getFlagUrl())
                .flagEmoji(country.getFlagEmoji())
                .build();
    }

    /**
     * Convierte entidad a DTO completo
     */
    private CountryResponseDTO toResponseDTO(Country country) {
        return CountryResponseDTO.builder()
                .id(country.getId())
                .isoCode(country.getIsoCode())
                .isoCode3(country.getIsoCode3())
                .name(country.getName())
                .nameEn(country.getNameEn())
                .officialName(country.getOfficialName())
                .capital(country.getCapital())
                .continent(country.getContinent())
                .region(country.getRegion())
                .currencyName(country.getCurrencyName())
                .currencyCode(country.getCurrencyCode())
                .currencySymbol(country.getCurrencySymbol())
                .flagUrl(country.getFlagUrl())
                .flagEmoji(country.getFlagEmoji())
                .population(country.getPopulation())
                .areaSqKm(country.getAreaSqKm())
                .latitude(country.getLatitude())
                .longitude(country.getLongitude())
                .languages(country.getLanguages())
                .funFacts(country.getFunFacts())
                .callingCode(country.getCallingCode())
                .timezone(country.getTimezone())
                .build();
    }
}

