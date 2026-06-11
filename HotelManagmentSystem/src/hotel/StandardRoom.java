package hotel;

// Inheritance
public class StandardRoom extends Room {
    private static final long serialVersionUID = 1L;

    public StandardRoom(int roomNumber, double pricePerNight) {
        super(roomNumber, pricePerNight);
    }

    @Override
    public String getRoomType() {
        return "Standard Basic 🛏️";
    }
}