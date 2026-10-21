package hu.ettermifoglalas.controller;

import hu.ettermifoglalas.dto.ReservationRequest;
import hu.ettermifoglalas.model.Reservation;
import hu.ettermifoglalas.service.ReservationService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(
            ReservationService reservationService
    ) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public List<Reservation> getAllReservations() {
        return reservationService.getAllReservations();
    }
    @PatchMapping("/{id}/status")
    public Reservation updateStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        return reservationService.updateStatus(id, status);
    }

    @PostMapping
    public ResponseEntity<Reservation> createReservation(
            @Valid @RequestBody ReservationRequest request
    ) {
        Reservation created =
                reservationService.createReservation(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }
}