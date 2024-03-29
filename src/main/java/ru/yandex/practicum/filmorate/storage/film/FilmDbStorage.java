package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.MpaRating;

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
                (rs, rowNum) -> new Film(rs.getInt("film_id"), rs.getString("name"), rs.getString("description"),
                        rs.getDate("releaseDate").toLocalDate(), rs.getInt("duration"),
                        findMpa(rs.getInt("mpa_rating_id"))));
//                findGenres(rs.getInt("id"))));
    }

    private MpaRating findMpa(int ratingId) {
        MpaRating mpa  = null;
        SqlRowSet rows = jdbcTemplate.queryForRowSet("select * from MPA_RATING where \"rating_id\" = ?", ratingId);
//
//        if (rows.next()) {
//            Integer mpaId = rows.getInt("rating_id");
//            log.info("Нашли rating_id рейтинга - {}", mpaId);
//
//            SqlRowSet rowsRating = jdbcTemplate.queryForRowSet("SELECT * FROM ratings WHERE id = ?", mpaId);
//            if (rowsRating.next()) {
//                String mpaName = rowsRating.getString("name");
//                log.info("Нашли name рейтинга - {}", mpaName);
//
//                mpa = new Mpa(mpaId, mpaName);
//            } else {
//                throw new ValidationException("Не найден name рейтинга для id - " + mpaId);
//            }
//        }
        return mpa;
    }


    @Override
    public Film getFilmById(int id) {
        SqlRowSet rows = jdbcTemplate.queryForRowSet("select * from FILM where \"film_id\" = ?", id);

        if (rows.next()) {
            Film film = new Film(rows.getInt("film_id"), rows.getString("name"), rows.getString("description"),
                    rows.getDate("releaseDate").toLocalDate(), rows.getInt("duration"),
                    findMpa(rows.getInt("mpa_rating_id")));
            return film;
        } else {
            String e = String.format("Film with id %s not found", id);
            log.error(e);
            throw new NotFoundException(e);
        }
    }

    public Film getFilmByName(String filmName) {
        SqlRowSet rows = jdbcTemplate.queryForRowSet("select * from FILM where \"name\" = ?", filmName);

        if (rows.next()) {
            Film film = new Film(rows.getInt("film_id"), rows.getString("name"), rows.getString("description"),
                    rows.getDate("releaseDate").toLocalDate(), rows.getInt("duration"),
                    findMpa(rows.getInt("mpa_rating_id")));
            return film;
        } else {
            String e = String.format("Film with name %s not found", filmName);
            log.error(e);
            throw new NotFoundException(e);
        }
    }

    @Override
    public Film addFilm(Film film) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("select * from FILM where \"film_id\" = ?", film.getId());
        if (!sqlRowSet.next()) {
            SqlRowSet mpaSqlRowSet = jdbcTemplate.queryForRowSet("select * from MPA_RATING where \"rating_id\" = ?",
                    film.getMpaRating());
            if (!mpaSqlRowSet.next()) {
                jdbcTemplate.update(
                        "insert into FILM (\"name\", \"description\", \"releaseDate\", \"duration\", "
                        + "\"mpa_rating_id\") values (?, ?, ?, ?, ?)",
                        film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                        film.getMpaRating());
                Film createdFilm = getFilmByName(film.getName());
                SqlRowSet genreSqlRowSet = jdbcTemplate.queryForRowSet("select * from FILM_GENRE where \"film_id\" = ?",
                        createdFilm.getId());

//                if(!genreSqlRowSet.next()){
//                    jdbcTemplate.update("INSERT INTO FILM_GENRE (\"film_id\")")
//                }
                return createdFilm;
            } else {
                String e = String.format("Film rating  %s not found", film.getMpaRating());
                log.error(e);
                throw new ValidationException(e);
            }
        } else {
            String e = String.format("Film with id %s exists", film.getId());
            log.error(e);
            throw new ValidationException(e);
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
            log.error(e);
            throw new NotFoundException(e);
        }
    }

    @Override
    public Film updateFilm(Film film) {
        if (getFilmById(film.getId()) != null) {
            jdbcTemplate.update(
                    "update FILM set \"name\" = ?, \"description\" = ?, \"releaseDate\" = ?, \"duration\" = ?, "
                    + "\"mpa_rating_id\" = ? where \"film_id\" = ?",
                    film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                    film.getMpaRating(), film.getId());
        } else {
            String e = String.format("Film with id %s not found", film.getId());
            log.error(e);
            throw new NotFoundException(e);
        }
        return getFilmById(film.getId());
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        SqlRowSet rows = jdbcTemplate.queryForRowSet(
                "select * from FILM_LIKES where \"user_id\" = ? and \"film_id\" = ?", userId, filmId);

        // ставим лайк фильму только если его не было
        if (!rows.next()) {
            jdbcTemplate.update("insert into FILM_LIKES (\"user_id\", \"film_id\") values (?, ?)", userId, filmId);
        }
    }

    @Override
    public void removeLike(Integer filmId, Integer userId) {
        SqlRowSet rows = jdbcTemplate.queryForRowSet(
                "select * from FILM_LIKES where \"user_id\" = ? and \"film_id\" = ?", userId, filmId);

        // убираем лайк фильму только если он был
        if (rows.next()) {
            jdbcTemplate.update("delete from FILM_LIKES where \"user_id\" = ? and \"film_id\" = ?", userId, filmId);
        }
    }

    @Override
    public List<Film> getNFilms(Integer count) {
        String sql
                = "select \"film_id\", count(*) cnt from FILM_LIKES l group by \"film_id\" order by cnt desc limit ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> getFilmById(rs.getInt("film_id")), count);
    }
}
