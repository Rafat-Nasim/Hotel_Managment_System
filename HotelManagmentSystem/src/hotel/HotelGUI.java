package hotel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HotelGUI implements HotelOperations {
    
    private List<Room> rooms;
    private final String FILE_NAME = "hotel_data.dat";

    private JFrame frame;
    private DefaultListModel<String> listModel;
    private JList<String> roomDisplayList;
    private JComboBox<String> filterDropdown;

    // --- APP COLOR PALETTE ---
    private final Color headerColor = new Color(20, 22, 30);     
    private final Color bgColor = new Color(30, 33, 40);         
    private final Color btnBookColor = new Color(40, 167, 69);   
    private final Color btnCheckOutColor = new Color(220, 53, 69); 

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HotelGUI app = new HotelGUI();
            app.frame.setVisible(true);
        });
    }

    public HotelGUI() {
        rooms = new ArrayList<>();
        loadHotelDataFromFile();

        if (rooms.isEmpty()) {
            rooms.add(new StandardRoom(101, 50.0));
            rooms.add(new StandardRoom(102, 50.0));
            rooms.add(new StandardRoom(103, 55.0));
            rooms.add(new StandardRoom(104, 60.0));
            
            rooms.add(new LuxuryRoom(201, 150.0, "AC + Free WiFi"));
            rooms.add(new LuxuryRoom(202, 160.0, "AC + Jacuzzi"));
            rooms.add(new LuxuryRoom(203, 180.0, "Ocean View Balcony"));
            rooms.add(new LuxuryRoom(204, 250.0, "VIP King Suite"));
            
            saveHotelDataToFile();
        }
        initializeAppUI();
    }

    @Override
    public void bookRoom(int roomNumber, String guestName) {
        for (Room r : rooms) {
            if (r.getRoomNumber() == roomNumber) {
                r.setBooked(true);
                r.setGuestName(guestName);
                break;
            }
        }
        saveHotelDataToFile();
        refreshRoomList();
    }

    @Override
    public void checkOutRoom(int roomNumber, int nightsStayed) {
        for (Room r : rooms) {
            if (r.getRoomNumber() == roomNumber) {
                double totalExpense = r.getPricePerNight() * nightsStayed;
                
                String receipt = "=== HOTEL BILL RECEIPT ===\n\n" +
                                 "Guest Name: " + r.getGuestName() + "\n" +
                                 "Room Checked Out: Room " + r.getRoomNumber() + "\n" +
                                 "Room Grade: " + r.getRoomType() + "\n" +
                                 "Price Per Night: $" + r.getPricePerNight() + "\n" +
                                 "Total Nights Stayed: " + nightsStayed + "\n" +
                                 "---------------------------\n" +
                                 "TOTAL EXPENSE DUE: $" + totalExpense + "\n\n" +
                                 "Status: PAID successfully.";
                
                JOptionPane.showMessageDialog(frame, receipt, "Check-Out Complete", JOptionPane.INFORMATION_MESSAGE);
                
                r.setBooked(false);
                r.setGuestName("");
                break;
            }
        }
        saveHotelDataToFile();
        refreshRoomList();
    }

    private void initializeAppUI() {
        frame = new JFrame("Grand Hotel Management Console");
        frame.setSize(600, 680);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(bgColor);

        JPanel topPanel = new JPanel(new BorderLayout(10, 15));
        topPanel.setBackground(headerColor);
        topPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Grand Hotel Nexus");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new BorderLayout(10, 0));
        filterPanel.setOpaque(false);
        
        JLabel filterLabel = new JLabel("Filter View Status:");
        filterLabel.setForeground(Color.WHITE);
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        String[] viewOptions = {"Show All Rooms", "Available Rooms Only", "Booked Rooms Only"};
        filterDropdown = new JComboBox<>(viewOptions);
        filterDropdown.setFont(new Font("Segoe UI", Font.BOLD, 14));
        filterDropdown.addActionListener(e -> refreshRoomList());

        filterPanel.add(filterLabel, BorderLayout.WEST);
        filterPanel.add(filterDropdown, BorderLayout.CENTER);
        topPanel.add(filterPanel, BorderLayout.SOUTH);

        frame.add(topPanel, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        roomDisplayList = new JList<>(listModel);
        roomDisplayList.setFont(new Font("Segoe UI", Font.BOLD, 15));
        roomDisplayList.setFixedCellHeight(50);
        roomDisplayList.setBorder(new EmptyBorder(10, 15, 10, 15));
        roomDisplayList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        roomDisplayList.setBackground(bgColor);
        roomDisplayList.setForeground(new Color(230, 230, 230));
        roomDisplayList.setSelectionBackground(new Color(50, 55, 70));
        roomDisplayList.setSelectionForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(roomDisplayList);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(bgColor);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        bottomPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        bottomPanel.setBackground(headerColor);

        JButton btnBook = createStyledButton("➕ Check-In / Book Room", btnBookColor);
        JButton btnCheckOut = createStyledButton("🔔 Process Check-Out", btnCheckOutColor);

        bottomPanel.add(btnBook);
        bottomPanel.add(btnCheckOut);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        btnBook.addActionListener(e -> {
            String selectedItem = roomDisplayList.getSelectedValue();
            if (selectedItem == null) {
                JOptionPane.showMessageDialog(frame, "Please pick a room from the terminal list first.");
                return;
            }

            int roomNum = extractRoomNumber(selectedItem);
            Room selectedRoom = findRoom(roomNum);

            if (selectedRoom.isBooked()) {
                JOptionPane.showMessageDialog(frame, "System Alert: Room " + roomNum + " is already occupied!", "Room Unavailable", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String guestName = JOptionPane.showInputDialog(frame, "Enter Guest Full Name:");
            if (guestName != null && !guestName.trim().isEmpty()) {
                bookRoom(roomNum, guestName.trim());
                JOptionPane.showMessageDialog(frame, "Room " + roomNum + " successfully allocated to " + guestName);
            }
        });

        btnCheckOut.addActionListener(e -> {
            String selectedItem = roomDisplayList.getSelectedValue();
            if (selectedItem == null) {
                JOptionPane.showMessageDialog(frame, "Please pick a room from the terminal list first.");
                return;
            }

            int roomNum = extractRoomNumber(selectedItem);
            Room selectedRoom = findRoom(roomNum);

            if (!selectedRoom.isBooked()) {
                JOptionPane.showMessageDialog(frame, "System Alert: Can't check out. Room " + roomNum + " is currently vacant.", "Action Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String nightsStr = JOptionPane.showInputDialog(frame, "Enter number of nights stayed to evaluate room expense:");
            if (nightsStr != null) {
                try {
                    int nights = Integer.parseInt(nightsStr.trim());
                    if (nights <= 0) throw new NumberFormatException();
                    
                    checkOutRoom(roomNum, nights);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid input. Please enter a valid count number.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        refreshRoomList();
    }

    private void refreshRoomList() {
        listModel.clear();
        int filterIndex = filterDropdown.getSelectedIndex();

        for (Room r : rooms) {
            if (filterIndex == 1 && r.isBooked()) continue;
            if (filterIndex == 2 && !r.isBooked()) continue;

            String statusSymbol = r.isBooked() ? "🔴 OCCUPIED (" + r.getGuestName() + ")" : "🟢 AVAILABLE";
            String lineDisplay = "Room " + r.getRoomNumber() + "  |  " + r.getRoomType() + "  |  💵 $" + r.getPricePerNight() + "/night  |  " + statusSymbol;
            listModel.addElement(lineDisplay);
        }
    }

    private JButton createStyledButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private int extractRoomNumber(String listItem) {
        String roomPart = listItem.split("  \\|  ")[0];
        return Integer.parseInt(roomPart.replace("Room ", "").trim());
    }

    private Room findRoom(int roomNumber) {
        for (Room r : rooms) {
            if (r.getRoomNumber() == roomNumber) return r;
        }
        return null;
    }

    private void saveHotelDataToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(rooms);
        } catch (IOException e) {
            System.out.println("Critical error securing cloud database write.");
        }
    }

    @SuppressWarnings("unchecked")
    private void loadHotelDataFromFile() {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                rooms = (List<Room>) ois.readObject();
            } catch (Exception e) {
                System.out.println("Fresh initialization cycle initiated.");
            }
        }
    }
}