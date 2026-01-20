package com.forumviajeros.backend.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa un país con toda su información geográfica y cultural.
 * Utilizada tanto para el mapa de viajes como para la trivia geográfica.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "countries")
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Código ISO 3166-1 alpha-2 del país (ej: "ES", "FR", "US")
     */
    @Column(name = "iso_code", nullable = false, unique = true, length = 2)
    private String isoCode;

    /**
     * Código ISO 3166-1 alpha-3 del país (ej: "ESP", "FRA", "USA")
     */
    @Column(name = "iso_code_3", length = 3)
    private String isoCode3;

    /**
     * Nombre del país en español
     */
    @Column(nullable = false)
    private String name;

    /**
     * Nombre del país en inglés
     */
    @Column(name = "name_en")
    private String nameEn;

    /**
     * Nombre oficial del país
     */
    @Column(name = "official_name")
    private String officialName;

    /**
     * Capital del país
     */
    @Column(nullable = false)
    private String capital;

    /**
     * Continente al que pertenece
     */
    @Column(nullable = false)
    private String continent;

    /**
     * Región/subregión (ej: "Europa Occidental", "América del Sur")
     */
    private String region;

    /**
     * Moneda oficial - nombre
     */
    @Column(name = "currency_name")
    private String currencyName;

    /**
     * Código de la moneda (ej: "EUR", "USD")
     */
    @Column(name = "currency_code", length = 3)
    private String currencyCode;

    /**
     * Símbolo de la moneda (ej: "€", "$")
     */
    @Column(name = "currency_symbol", length = 10)
    private String currencySymbol;

    /**
     * URL o path a la imagen de la bandera
     */
    @Column(name = "flag_url")
    private String flagUrl;

    /**
     * Emoji de la bandera (ej: "🇪🇸")
     */
    @Column(name = "flag_emoji", length = 10)
    private String flagEmoji;

    /**
     * Población del país
     */
    private Long population;

    /**
     * Área en kilómetros cuadrados
     */
    @Column(name = "area_sq_km")
    private Double areaSqKm;

    /**
     * Latitud del centro del país
     */
    private Double latitude;

    /**
     * Longitud del centro del país
     */
    private Double longitude;

    /**
     * Idiomas oficiales del país
     */
    @ElementCollection
    @CollectionTable(name = "country_languages", joinColumns = @JoinColumn(name = "country_id"))
    @Column(name = "language")
    @Builder.Default
    private List<String> languages = new ArrayList<>();

    /**
     * Datos curiosos sobre el país para la trivia
     */
    @ElementCollection
    @CollectionTable(name = "country_fun_facts", joinColumns = @JoinColumn(name = "country_id"))
    @Column(name = "fun_fact", length = 500)
    @Builder.Default
    private List<String> funFacts = new ArrayList<>();

    /**
     * Código de llamada telefónica internacional
     */
    @Column(name = "calling_code", length = 10)
    private String callingCode;

    /**
     * Zona horaria principal
     */
    private String timezone;

    /**
     * Indica si el país está activo para mostrar en la app
     */
    @Builder.Default
    private Boolean active = true;
}

