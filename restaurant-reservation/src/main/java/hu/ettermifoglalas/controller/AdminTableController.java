package hu.ettermifoglalas.controller;

import hu.ettermifoglalas.dto.RestaurantTableRequest;
import hu.ettermifoglalas.model.RestaurantTable;
import hu.ettermifoglalas.repository.ReservationRepository;
import hu.ettermifoglalas.repository.RestaurantTableRepository;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/admin/tables")
public class AdminTableController {

    private final RestaurantTableRepository tableRepository;
    private final ReservationRepository reservationRepository;

    public AdminTableController(
            RestaurantTableRepository tableRepository,
            ReservationRepository reservationRepository
    ) {
        this.tableRepository = tableRepository;
        this.reservationRepository = reservationRepository;
    }

    @GetMapping
    public List<RestaurantTable> getTables() {
        return tableRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<RestaurantTable> createTable(
            @Valid @RequestBody RestaurantTableRequest request
    ) {
        checkUniqueTableNumber(request.getTableNumber(), null);

        RestaurantTable table = new RestaurantTable(
                request.getTableNumber(),
                request.getCapacity(),
                request.isNearWindow(),
                request.isAccessible()
        );

        RestaurantTable saved = tableRepository.save(table);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public RestaurantTable updateTable(
            @PathVariable Long id,
            @Valid @RequestBody RestaurantTableRequest request
    ) {
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Az asztal nem található."
                ));

        checkUniqueTableNumber(request.getTableNumber(), id);

        table.setTableNumber(request.getTableNumber());
        table.setCapacity(request.getCapacity());
        table.setNearWindow(request.isNearWindow());
        table.setAccessible(request.isAccessible());

        return tableRepository.save(table);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTable(@PathVariable Long id) {
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Az asztal nem található."
                ));

        if (reservationRepository.existsByRestaurantTable_Id(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Az asztalhoz foglalás tartozik, ezért nem törölhető."
            );
        }

        tableRepository.delete(table);

        return ResponseEntity.noContent().build();
    }

    private void checkUniqueTableNumber(int tableNumber, Long currentId) {
        boolean duplicate = tableRepository.findAll().stream()
                .anyMatch(table ->
                        table.getTableNumber() == tableNumber
                                && (currentId == null
                                || !table.getId().equals(currentId))
                );

        if (duplicate) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ez az asztalszám már létezik."
            );
        }
    }
}