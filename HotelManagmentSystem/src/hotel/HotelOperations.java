package hotel;

public interface HotelOperations {
    void bookRoom(int roomNumber, String guestName);
    void checkOutRoom(int roomNumber, int nightsStayed);
}