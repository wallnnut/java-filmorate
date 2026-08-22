package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.FilmReviewDto;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.services.FilmReviewService;
import ru.yandex.practicum.filmorate.services.RateFilmReviewService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class FilmReviewController {
    private final FilmReviewService reviewService;
    private final RateFilmReviewService rateReviewService;

    @PostMapping
    public FilmReviewDto addFilmReview(@RequestBody @Valid FilmReviewDto reviewDto) {
        log.info("Received request to add review: {}", reviewDto);
        return reviewService.addReview(reviewDto);
    }

    @PutMapping
    public FilmReviewDto updateFilmReview(@RequestBody @Valid FilmReviewDto reviewDto) {
        log.info("Received request to update review: {}", reviewDto);
        return reviewService.updateReview(reviewDto);
    }

    @GetMapping("/{id}")
    public FilmReviewDto getReviewById(@PathVariable Id id) {
        log.info("Received request to get review by id {}", id);
        return reviewService.getReviewById(id);
    }

    @DeleteMapping("/{id}")
    public void removeReview(@PathVariable Id id) {
        log.info("Received request to remove review {}", id);
        reviewService.removeReview(id);
    }

    @GetMapping
    public List<FilmReviewDto> getReviewsByFilmId(@RequestParam(required = false) Id filmId,
                                                  @RequestParam(defaultValue = "10") Integer count) {
        log.info("Received request to get reviews, filmId={}, count={}", filmId, count);
        return reviewService.getReviewsByFilmIdAndCount(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void likeReview(@PathVariable Id id, @PathVariable Id userId) {
        log.info("Received request to like review {} by user {}", id, userId);
        rateReviewService.like(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void dislikeReview(@PathVariable Id id, @PathVariable Id userId) {
        log.info("Received request to dislike review {} by user {}", id, userId);
        rateReviewService.dislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Id id, @PathVariable Id userId) {
        log.info("Received request to remove like/dislike from review {} by user {}", id, userId);
        rateReviewService.removeLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable Id id, @PathVariable Id userId) {
        log.info("Received request to remove dislike from review {} by user {}", id, userId);
        rateReviewService.removeDislike(id, userId);
    }
}
