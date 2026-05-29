package interfaces;

import models.Booking;
import models.BookingStatus;
import models.Accommodation;
import models.User;
import java.util.List;

public interface Searchable {
    List<Booking> searchBookingsByUserName(String name);
    List<Accommodation> searchAccommodationsByName(String name);
    List<User> searchUsersByName(String name);
    List<Booking> searchBookingsByDateRange(String startDate, String endDate, BookingStatus status);
}