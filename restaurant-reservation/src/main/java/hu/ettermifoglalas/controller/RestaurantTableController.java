
package hu.ettermifoglalas.controller;

import hu.ettermifoglalas.model.RestaurantTable;
import hu.ettermifoglalas.repository.RestaurantTableRepository;
import hu.ettermifoglalas.service.ReservationService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tables")
public class RestaurantTableController {

    private final RestaurantTableRepository repository;
    private final ReservationService reservationService;

    public RestaurantTableController(
            RestaurantTableRepository repository,
            ReservationService reservationService
    ) {
        this.repository = repository;
        this.reservationService = reservationService;
    }

    @GetMapping
    public List<RestaurantTable> getAllTables() {
        return repository.findAll();
    }

    @GetMapping("/available")
    public List<RestaurantTable> getAvailableTables(
            @RequestParam LocalDateTime dateTime,
            @RequestParam int guestCount,
            @RequestParam(defaultValue = "false") boolean nearWindow,
            @RequestParam(defaultValue = "false") boolean accessible
    ) {
        return reservationService.getAvailableTables(
                dateTime,
                guestCount,
                nearWindow,
                accessible
        );
    }
}
