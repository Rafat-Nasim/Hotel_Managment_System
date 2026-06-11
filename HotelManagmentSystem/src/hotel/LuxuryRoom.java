package hotel;

// Inheritance
public class LuxuryRoom extends Room {
    private static final long serialVersionUID = 1L;
    private String luxuryService;

    public LuxuryRoom(int roomNumber, double pricePerNight, String luxuryService) {
        super(roomNumber, pricePerNight);
        this.luxuryService = luxuryService;
    }

    @Override
    public String getRoomType() {
        return "Luxury Suite ✨ (" + luxuryService + ")";
    }
}