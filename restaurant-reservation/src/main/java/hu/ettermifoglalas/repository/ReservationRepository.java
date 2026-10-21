package hu.ettermifoglalas.repository;

import hu.ettermifoglalas.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository
        extends JpaRepository<Reservation, Long> {

    @Query("""
        SELECT r
        FROM Reservation r
        WHERE r.restaurantTable.id = :tableId
          AND r.status <> 'CANCELLED'
          AND r.reservationDateTime > :start
          AND r.reservationDateTime < :end
        """)
    List<Reservation> findConflictingReservations(
            @Param("tableId") Long tableId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
    boolean existsByRestaurantTable_Id(Long tableId);
}