package com.forumviajeros.backend.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa un lugar visitado (o por visitar) por un usuario.
 * Permite trackear el progreso de viajes del usuario en el mapa virtual.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "visited_places", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "country_id", "city_name"})
})
@EntityListeners(AuditingEntityListener.class)
public class VisitedPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Usuario que ha visitado/quiere visitar el lugar
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * País visitado
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    /**
     * Ciudad específica (opcional, si es null significa todo el país)
     */
    @Column(name = "city_name")
    private String cityName;

    /**
     * Estado del lugar: visitado, wishlist, o lugar donde ha vivido
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PlaceStatus status = PlaceStatus.VISITED;

    /**
     * Fecha de la primera visita (aproximada)
     */
    @Column(name = "visit_date")
    private LocalDate visitDate;

    /**
     * Fecha de fin de visita (para estancias largas)
     */
    @Column(name = "visit_end_date")
    private LocalDate visitEndDate;

    /**
     * Notas personales sobre el lugar
     */
    @Column(length = 1000)
    private String notes;

    /**
     * Puntuación personal del lugar (1-5 estrellas)
     */
    private Integer rating;

    /**
     * Indica si quiere destacar este lugar en su perfil
     */
    @Builder.Default
    private Boolean favorite = false;

    /**
     * Número de veces que ha visitado el lugar
     */
    @Column(name = "visit_count")
    @Builder.Default
    private Integer visitCount = 1;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Estados posibles de un lugar
     */
    public enum PlaceStatus {
        /** Lugar ya visitado */
        VISITED,
        /** En lista de deseos para visitar */
        WISHLIST,
        /** Lugar donde el usuario ha vivido */
        LIVED,
        /** Actualmente viviendo ahí */
        LIVING
    }
}

