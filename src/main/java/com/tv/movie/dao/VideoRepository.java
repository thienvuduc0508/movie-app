package com.tv.movie.dao;

import com.tv.movie.entity.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    @Query(
            "SELECT v FROM Video v WHERE " +
                    "(LOWER(v.title) LIKE LOWER(CONCAT('%', :search, '%'))  OR LOWER(v.description) LIKE LOWER(CONCAT('%', :search, '%')))"
    )
    Page<Video> searchVideos(@Param("search") String search, Pageable pageable);

    @Query("SELECT COALESCE(SUM(v.duration), 0) FROM Video v")
    long getTotalDuration();

    long countByPublished(boolean b);

    @Query("SELECT v FROM Video v WHERE v.published = true " +
            "AND (LOWER(v.title) LIKE LOWER(CONCAT('%', :search, '%'))  OR LOWER(v.description) LIKE LOWER(CONCAT('%', :search, '%')))" +
            "ORDER BY v.createdAt DESC"
    )
    Page<Video> searchPublishedVideos(@Param("search") String search, Pageable pageable);

    @Query("SELECT v FROM Video v WHERE v.published = true ORDER BY v.createdAt DESC")
    Page<Video> findPublishedVideos(Pageable pageable);

    @Query("SELECT v FROM Video v WHERE v.published = true ORDER BY RAND()")
    List<Video> findRandomPublishedVideos(Pageable pageable);

    @Query("SELECT v FROM User u JOIN u.watchlist v WHERE u.id = :userId AND v.published = true AND lower(v.title) like lower(concat('%', :search, '%')) OR lower(v.description) like lower(concat('%', :search, '%')) ")
    Page<Video> searchWatchlistByUserId(@Param("userId") Long userId, @Param("search") String search, Pageable pageable);

    @Query("SELECT v FROM User u JOIN u.watchlist v WHERE u.id = :userId AND v.published = true")
    Page<Video> findWatchlistByUserId(@Param("userId") Long userId, Pageable pageable);
}
