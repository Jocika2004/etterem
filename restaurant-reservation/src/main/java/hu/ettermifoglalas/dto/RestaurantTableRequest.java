package hu.ettermifoglalas.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class RestaurantTableRequest {

    @Min(1)
    @Max(100)
    private int tableNumber;

    @Min(1)
    @Max(20)
    private int capacity;

    private boolean nearWindow;
    private boolean accessible;

    public RestaurantTableRequest() {
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