package hu.ettermifoglalas;

import hu.ettermifoglalas.model.RestaurantTable;
import hu.ettermifoglalas.repository.RestaurantTableRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RestaurantTableRepository repository;

    public DataInitializer(RestaurantTableRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() == 0) {
            repository.saveAll(List.of(
                    new RestaurantTable(1, 2, false, false),
                    new RestaurantTable(2, 2, true, false),
                    new RestaurantTable(3, 4, true, false),
                    new RestaurantTable(4, 4, false, true),
                    new RestaurantTable(5, 6, false, false),
                    new RestaurantTable(6, 8, true, true)
            ));

            System.out.println("A próbaasztalok sikeresen létrejöttek.");
        }
    }
}