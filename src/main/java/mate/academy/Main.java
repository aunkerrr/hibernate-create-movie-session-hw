package mate.academy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import mate.academy.lib.Injector;
import mate.academy.model.CinemaHall;
import mate.academy.model.Movie;
import mate.academy.model.MovieSession;
import mate.academy.service.CinemaHallService;
import mate.academy.service.MovieService;
import mate.academy.service.MovieSessionService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.academy");

    public static void main(String[] args) {
        MovieService movieService = (MovieService)
                injector.getInstance(MovieService.class);

        Movie fastAndFurious = new Movie("Fast and Furious");
        fastAndFurious.setDescription("An action film about street racing, heists, and spies.");
        movieService.add(fastAndFurious);

        CinemaHallService cinemaHallService = (CinemaHallService)
                injector.getInstance(CinemaHallService.class);

        CinemaHall redHall = new CinemaHall();
        redHall.setCapacity(100);
        redHall.setDescription("Red VIP hall");
        cinemaHallService.add(redHall);

        MovieSession tomorrowSession = new MovieSession();
        tomorrowSession.setCinemaHall(redHall);
        tomorrowSession.setMovie(fastAndFurious);
        tomorrowSession.setShowTime(LocalDateTime.now().plusDays(1));

        MovieSessionService movieSessionService = (MovieSessionService)
                injector.getInstance(MovieSessionService.class);

        movieSessionService.add(tomorrowSession);

        LocalDate tomorrowDate = LocalDate.now().plusDays(1);
        List<MovieSession> availiableSessions = movieSessionService
                .findAvailableSessions(fastAndFurious.getId(), tomorrowDate);

        System.out.println("Available sessions for tomorrow: ");
        availiableSessions.forEach(System.out::println);

        System.out.println("All cinema halls available: ");
        System.out.println(cinemaHallService.getAll());

        System.out.println(movieService.get(fastAndFurious.getId()));
        movieService.getAll().forEach(System.out::println);
    }
}
