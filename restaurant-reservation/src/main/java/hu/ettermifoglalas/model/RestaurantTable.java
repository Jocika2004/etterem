package hu.ettermifoglalas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "restaurant_tables")
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int tableNumber;
    private int capacity;
    private boolean nearWindow;
    private boolean accessible;

    public RestaurantTable() {
    }

    public RestaurantTable(
            int tableNumber,
            int capacity,
            boolean nearWindow,
            boolean accessible
    ) {
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.nearWindow = nearWindow;
        this.accessible = accessible;
    }

    public Long getId() {
        return id;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isNearWindow() {
        return nearWindow;
    }

    public void setNearWindow(boolean nearWindow) {
        this.nearWindow = nearWindow;
    }

    public boolean isAccessible() {
        return accessible;
    }

    public void setAccessible(boolean accessible) {
        this.accessible = accessible;
    }
}