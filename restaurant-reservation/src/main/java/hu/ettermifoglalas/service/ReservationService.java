package hu.ettermifoglalas.service;

import hu.ettermifoglalas.dto.ReservationRequest;
import hu.ettermifoglalas.model.Reservation;
import hu.ettermifoglalas.model.RestaurantTable;
import hu.ettermifoglalas.repository.ReservationRepository;
import hu.ettermifoglalas.repository.RestaurantTableRepository;
import java.time.LocalTime;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    private static final long DURATION_HOURS = 2;

    private final ReservationRepository reservationRepository;
    private final RestaurantTableRepository tableRepository;

    public ReservationService(
            ReservationRepository reservationRepository,
            RestaurantTableRepository tableRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.tableRepository = tableRepository;
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Reservation createReservation(ReservationRequest request) {

        LocalDateTime dateTime = request.getReservationDateTime();

        validateReservationDateTime(dateTime);

        RestaurantTable table = tableRepository
                .findById(request.getTableId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "A kiválasztott asztal nem található."
                ));

        if (request.getGuestCount() > table.getCapacity()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Az asztal nem rendelkezik elegendő férőhellyel."
            );
        }

        if (request.isNearWindow() && !table.isNearWindow()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A kiválasztott asztal nem ablak mellett található."
            );
        }

        if (request.isAccessible() && !table.isAccessible()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A kiválasztott asztal nem akadálymentesen megközelíthető."
            );
        }

        List<Reservation> conflicts =
                reservationRepository.findConflictingReservations(
                        table.getId(),
                        dateTime.minusHours(DURATION_HOURS),
                        dateTime.plusHours(DURATION_HOURS)
                );

        if (!conflicts.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Az asztal ebben az időpontban már foglalt."
            );
        }

        Reservation reservation = new Reservation();

        reservation.setGuestName(request.getGuestName());
        reservation.setEmail(request.getEmail());
        reservation.setPhone(request.getPhone());
        reservation.setReservationDateTime(dateTime);
        reservation.setGuestCount(request.getGuestCount());
        reservation.setSpecialRequests(request.getSpecialRequests());
        reservation.setRestaurantTable(table);
        reservation.setStatus("PENDING");

        return reservationRepository.save(reservation);
    }
    public List<RestaurantTable> getAvailableTables(
            LocalDateTime dateTime,
            int guestCount,
            boolean nearWindow,
            boolean accessible
    ) {
        if (dateTime == null || dateTime.isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Adj meg egy jövőbeli időpontot."
            );
        }

        if (guestCount < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Legalább egy vendéget meg kell adni."
            );
        }

        return tableRepository.findAll().stream()
                .filter(table -> table.getCapacity() >= guestCount)
                .filter(table -> !nearWindow || table.isNearWindow())
                .filter(table -> !accessible || table.isAccessible())
                .filter(table -> reservationRepository
                        .findConflictingReservations(
                                table.getId(),
                                dateTime.minusHours(DURATION_HOURS),
                                dateTime.plusHours(DURATION_HOURS)
                        ).isEmpty())
                .toList();
    }

    public Reservation updateStatus(Long reservationId, String status) {
        String normalizedStatus = status == null
                ? ""
                : status.trim().toUpperCase();

        if (!List.of("CONFIRMED", "CANCELLED").contains(normalizedStatus)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Érvénytelen foglalási állapot."
            );
        }

        Reservation reservation = reservationRepository
                .findById(reservationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "A foglalás nem található."
                ));

        String currentStatus = reservation.getStatus();

        if (normalizedStatus.equals(currentStatus)) {
            return reservation;
        }

        if ("CANCELLED".equals(currentStatus)
                && "CONFIRMED".equals(normalizedStatus)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A lemondott foglalás már nem erősíthető meg."
            );
        }

        reservation.setStatus(normalizedStatus);

        return reservationRepository.save(reservation);
    }

    private void validateReservationDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A foglalás dátumát és időpontját meg kell adni."
            );
        }

        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A foglalás időpontja nem lehet a múltban."
            );
        }

        LocalTime time = dateTime.toLocalTime();

        if (time.isBefore(LocalTime.of(11, 0))
                || time.isAfter(LocalTime.of(20, 0))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Foglalás 11:00 és 20:00 közötti kezdési időponttal lehetséges."
            );
        }
    }
}