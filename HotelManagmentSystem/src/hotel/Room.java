package hotel;

import java.io.Serializable;

// Encapsulation & Abstraction
public abstract class Room implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int roomNumber;
    private double pricePerNight;
    private boolean isBooked;
    private String guestName;

    public Room(int roomNumber, double pricePerNight) {
        this.roomNumber = roomNumber;
        this.pricePerNight = pricePerNight;
        this.isBooked = false;
        this.guestName = "";
    }

    // Getters and Setters (Encapsulation)
    public int getRoomNumber() { return roomNumber; }
    public double getPricePerNight() { return pricePerNight; }
    public boolean isBooked() { return isBooked; }
    
    public void setBooked(boolean booked) { this.isBooked = booked; }
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    // Abstract method overridden by subclasses (Polymorphism)
    public abstract String getRoomType();
}