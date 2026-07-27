package mate.academy.dao.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import mate.academy.dao.MovieSessionDao;
import mate.academy.exception.DataProcessingException;
import mate.academy.lib.Dao;
import mate.academy.model.MovieSession;
import mate.academy.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

@Dao
public class MovieSessionDaoImpl implements MovieSessionDao {
    @Override
    public MovieSession add(MovieSession movieSession) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.persist(movieSession);
            transaction.commit();
            return movieSession;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DataProcessingException("Can't insert movieSession "
                    + movieSession, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public Optional<MovieSession> get(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(MovieSession.class, id));
        } catch (Exception e) {
            throw new DataProcessingException("Can't get a movie by id: " + id, e);
        }
    }

    @Override
    public List<MovieSession> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT mvs FROM "
                    + "MovieSession mvs LEFT JOIN FETCH "
                    + "mvs.movie LEFT JOIN FETCH mvs.cinemaHall ", MovieSession.class)
                    .getResultList();
        } catch (Exception e) {
            throw new DataProcessingException("Can't get"
                    + " list of all movies from database",e);
        }
    }

    public List<MovieSession> findAvailableSessions(Long movieId, LocalDate date) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

            return session.createQuery("SELECT mv FROM MovieSession mv "
                            + "LEFT JOIN FETCH mv.movie "
                            + "LEFT JOIN FETCH mv.cinemaHall "
                            + "WHERE mv.movie.id = :movieId "
                            + "AND mv.showTime BETWEEN :startOfDay "
                            + "AND :endOfDay", MovieSession.class)
                    .setParameter("movieId", movieId)
                    .setParameter("startOfDay", startOfDay)
                    .setParameter("endOfDay", endOfDay)
                    .getResultList();
        } catch (Exception e) {
            throw new DataProcessingException("Can't find availible "
                    + "sessions for movie: "
                    + movieId + " on date: " + date, e);
        }
    }
}
