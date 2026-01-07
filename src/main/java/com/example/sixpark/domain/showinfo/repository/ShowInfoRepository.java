package com.example.sixpark.domain.showinfo.repository;

import com.example.sixpark.domain.showinfo.entity.ShowInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ShowInfoRepository extends JpaRepository<ShowInfo, Long> , ShowInfoCustomRepository{

    boolean existsByMt20id(String mt20id);


    // 장르별 공연 페이징 조회 (삭제되지 않은 공연만) - N+1 해결
    @Query(value = "SELECT s FROM ShowInfo s JOIN FETCH s.genre g WHERE g.id = :genreId AND s.isDeleted = false ",
            countQuery = "SELECT COUNT(s) FROM ShowInfo s WHERE s.genre.id = :genreId AND s.isDeleted = false ")
    Page<ShowInfo> findByGenreIdWithGenre(@Param("genreId") Long genreId, Pageable pageable);

    // ID로 공연 상세 조회 (Genre Fetch Join)
    @Query("SELECT s FROM ShowInfo s JOIN FETCH s.genre WHERE s.id = :id AND s.isDeleted = false")
    Optional<ShowInfo> findByIdWithGenre(@Param("id") Long id);

    // 공연 상세 정보 조회 (Genre + SHowPlace Fetch Join)
    @Query("SELECT s FROM ShowInfo s " +
            "LEFT JOIN FETCH s.genre " +
            "LEFT JOIN FETCH s.showPlace " +
            "WHERE s.id = :id " +
            "AND s.isDeleted = false")  // 삭제된 공연 제외
    Optional<ShowInfo> findByIdWithDetails(@Param("id") Long id);


    // ID 리스트로 ShowInfo 조회 (Genre 포함)
    @Query("SELECT s FROM ShowInfo s " +
            "LEFT JOIN FETCH s.genre " +
            "WHERE s.id IN :ids " +
            "AND s.isDeleted = false")
    List<ShowInfo> findByIdInWithGenre(@Param("ids") List<Long> ids);

    // 장르별 모든 ShowInfo ID 조회 (삭제되지 않은 것만)
    @Query("SELECT s.id FROM ShowInfo s " +
            "WHERE s.genre.id = :genreId " +
            "AND s.isDeleted = false")
    List<Long> findIdsByGenreId(@Param("genreId") Long genreId);

    /**
     * v1: 전체 조회 (검색 조건 없을 때)
     */
    @Query("SELECT DISTINCT s FROM ShowInfo s " +
            "LEFT JOIN FETCH s.genre " +
            "LEFT JOIN FETCH s.showPlace " +
            "WHERE s.isDeleted = false " +
            "ORDER BY s.id DESC")
    Page<ShowInfo> findAllActiveWithDetails(Pageable pageable);

    /**
     * v1: JPA 검색 (검색 조건 있을 때)
     */
    @Query("SELECT DISTINCT s FROM ShowInfo s " +
            "LEFT JOIN FETCH s.genre " +
            "LEFT JOIN FETCH s.showPlace sp " +
            "WHERE s.isDeleted = false " +
            "AND (" +
            "  (COALESCE(:prfnm, '') != '' AND s.prfnm LIKE CONCAT('%', :prfnm, '%')) OR " +
            "  (COALESCE(:prfcast, '') != '' AND s.prfcast LIKE CONCAT('%', :prfcast, '%')) OR " +
            "  (COALESCE(:area, '') != '' AND sp.area LIKE CONCAT('%', :area, '%')) OR " +
            "  (COALESCE(:fcltynm, '') != '' AND sp.fcltynm LIKE CONCAT('%', :fcltynm, '%')) OR " +
            "  (COALESCE(:dtguidance, '') != '' AND sp.dtguidance LIKE CONCAT('%', :dtguidance, '%'))" +
            ") " +
            "ORDER BY s.id DESC")
    Page<ShowInfo> searchShowInfosV1(
            @Param("prfnm") String prfnm,
            @Param("prfcast") String prfcast,
            @Param("area") String area,
            @Param("fcltynm") String fcltynm,
            @Param("dtguidance") String dtguidance,
            Pageable pageable
    );
}


