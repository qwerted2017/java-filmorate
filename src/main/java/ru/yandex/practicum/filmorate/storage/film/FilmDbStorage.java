package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.models.MpaRating;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Qualifier("Repository")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getFilms() {
        String sql = "select * from FILM";
        return jdbcTemplate.query(sql,
                                  (rs, rowNum) -> new Film(rs.getInt("film_id"),
                                                           getLikesCount(rs.getInt("film_id")),
                                                           rs.getString("name"),
                                                           rs.getString("description"),
                                                           rs.getDate("releaseDate").toLocalDate(),
                                                           rs.getInt("duration"),
                                                           findFilmRatings(rs.getInt("mpa_rating_id")),
                                                           findFilmGenres(rs.getInt("film_id"))));
    }

    @Override
    public List<Film> getNFilms(Integer count) {
        String sql = "select \"film_id\", count(*) cnt from FILM_LIKES l group by \"film_id\" order by cnt desc limit ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> getFilmById(rs.getInt("film_id")), count);
    }

    @Override
    public Film getFilmById(int id) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet("select * from FILM where \"film_id\" = ?", id);

        if (rs.next()) {
            return createFilm(rs);
        } else {
            String e = String.format("Film with id %s not found", id);
            throw new NotFoundException(e);
        }
    }

    public Film getFilmByName(String filmName) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet("select * from FILM where \"name\" = ?", filmName);

        if (rs.next()) {
            return createFilm(rs);
        } else {
            String e = String.format("Film with name %s not found", filmName);
            throw new NotFoundException(e);
        }
    }

    @Override
    public Film addFilm(Film film) {

        validateRatingFilm(film);

        Film existingFilm = null;
        try {
            existingFilm = getFilmByName(film.getName());
        } catch (NotFoundException e) {
            log.warn("Film with name {} not found", film.getName());
        }

        if (existingFilm == null) {
            jdbcTemplate.update(
                    "insert into FILM (\"name\", \"description\", \"releaseDate\", \"duration\") values (?, ?, ?, ?)",
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration());
        } else {
            updateFilm(existingFilm);
        }
        setFilmRating(film);
        existingFilm = getFilmByName(film.getName());
        setFilmGenres(existingFilm, film);
        return getFilmById(existingFilm.getId());
    }

    @Override
    public Film updateFilm(Film film) {
        if (getFilmById(film.getId()) != null) {
            jdbcTemplate.update("delete from FILM_GENRE where \"film_id\" = ?", film.getId());
            jdbcTemplate.update(
                    "update FILM set \"name\" = ?, \"description\" = ?, \"releaseDate\" = ?, \"duration\" = ? where \"film_id\" = ?",
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getId());
            setFilmRating(film);
            Film existingFilm = getFilmByName(film.getName());
            setFilmGenres(film, existingFilm);
        } else {
            String e = String.format("Film with id %s not found", film.getId());
            throw new NotFoundException(e);
        }
        return getFilmById(film.getId());
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        SqlRowSet rows = jdbcTemplate.queryForRowSet(
                "select * from FILM_LIKES where \"user_id\" = ? and \"film_id\" = ?",
                userId,
                filmId);

        if (!rows.next()) {
            jdbcTemplate.update("insert into FILM_LIKES (\"user_id\", \"film_id\") values (?, ?)", userId, filmId);
        }
    }

    @Override
    public void removeLike(Integer filmId, Integer userId) {
        SqlRowSet rows = jdbcTemplate.queryForRowSet(
                "select * from FILM_LIKES where \"user_id\" = ? and \"film_id\" = ?",
                userId,
                filmId);

        if (rows.next()) {
            jdbcTemplate.update("delete from FILM_LIKES where \"user_id\" = ? and \"film_id\" = ?", userId, filmId);
        }
    }

    @Override
    public void deleteFilm(Film film) {
        if (getFilmById(film.getId()) != null) {
            jdbcTemplate.update("delete from FILM where \"film_id\" = ?", film.getId());
            jdbcTemplate.update("delete from FILM_GENRE where \"film_id\" = ?", film.getId());
            jdbcTemplate.update("delete from FILM_LIKES where \"film_id\" = ?", film.getId());
        } else {
            String e = String.format("Film with id %s not found", film.getId());
            throw new NotFoundException(e);
        }
    }

    private Film createFilm(SqlRowSet rs) {
        return new Film(rs.getInt("film_id"),
                        getLikesCount(rs.getInt("film_id")),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDate("releaseDate").toLocalDate(),
                        rs.getInt("duration"),
                        findFilmRatings(rs.getInt("mpa_rating_id")),
                        findFilmGenres(rs.getInt("film_id")));
    }

    private MpaRating findFilmRatings(int ratingId) {
        SqlRowSet rows = jdbcTemplate.queryForRowSet("select * from MPA_RATING where \"rating_id\" = ?", ratingId);
        if (rows.next()) {
            return new MpaRating(rows.getInt("rating_id"), rows.getString("name"));
        } else {
            String e = String.format("Rating with %s not found", ratingId);
            throw new ValidationException(e);
        }
    }

    private List<Genre> findFilmGenres(int filmId) {

        String sql = "select g2.\"genre_id\", g2.\"name\" from FILM_GENRE as f inner join GENRE G2 on f.\"genre_id\" = G2.\"genre_id\" where \"film_id\" = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("name")), filmId);
    }

    private void setFilmRating(Film film) {

            Integer mpaId = film.getMpa().getId();
            if (mpaId != null) {

                SqlRowSet rows = jdbcTemplate.queryForRowSet("select * from MPA_RATING where \"rating_id\" = ?", mpaId);

                if (rows.next()) {
                    jdbcTemplate.update("update FILM set \"mpa_rating_id\"=? where \"name\" =?", mpaId, film.getName());

                } else {
                    String e = "Rating with id %s not found" + mpaId;
                    throw new ValidationException(e);
                }
            }
    }

    private void setFilmGenres(Film existingFilm, Film rawFilm) {
        List<Genre> genres = rawFilm.getGenres();
        if (genres == null) {
            return;
        }
        for (Genre genre : genres) {
            int genreId = genre.getId();
            SqlRowSet genreExists = jdbcTemplate.queryForRowSet("select * from GENRE where \"genre_id\" = ?", genreId);

            if (!genreExists.next()) {
                String e = String.format("Genre with id %s from film's %s properties not found",
                                         genreId,
                                         rawFilm.getName());
                throw new ValidationException(e);
            } else {
                SqlRowSet fimGenreExists = jdbcTemplate.queryForRowSet(
                        "select * from film_genre where \"film_id\" = ? and \"genre_id\" = ?",
                        existingFilm.getId(),
                        genreId);
                if (!fimGenreExists.next()) {
                    jdbcTemplate.update("insert into film_genre (\"film_id\", \"genre_id\") values (?,?)",
                                        existingFilm.getId(),
                                        genreId);
                }
            }
        }
    }

    private boolean validateRatingFilm(Film film) {
        MpaRating rating = film.getMpa();
        if (rating == null) {
            String e = String.format("Film %s doesn't contain any rating", film.getName());
            throw new ValidationException(e);
        }
        return findFilmRatings(rating.getId()).getId().equals(rating.getId());
    }

    private List<Integer> getLikesCount(int filmId) {
        List<Integer> likesCount = new ArrayList<>();
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("select * from FILM_LIKES where \"film_id\" = ?", filmId);
        while (sqlRowSet.next()) {
            likesCount.add(sqlRowSet.getInt("user_id"));
        }
        return likesCount;
    }
}
