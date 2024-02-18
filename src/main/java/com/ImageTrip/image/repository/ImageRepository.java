package com.ImageTrip.image.repository;

import com.ImageTrip.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    // 태그 또는 사용자이름이 주어진 문자열을 포함하는 이미지를 검색 (OR i.member.name LIKE %:searchTerm% 나중에 추가)
    @Query("SELECT i FROM Image i WHERE (:searchTerm IS NULL OR :searchTerm = '' OR i.tag LIKE %:searchTerm% ) AND i.memberId = :memberId")
    List<Image> findAllBySearchAndMemberId(@Param("searchTerm") String searchTerm, @Param("memberId") long memberId);

    @Query("SELECT i FROM Image i WHERE (:searchTerm IS NULL OR :searchTerm = '' OR i.tag LIKE %:searchTerm% ) AND i.shared = :shared")
    List<Image> findAllBySearchAndShared(@Param("searchTerm") String searchTerm, @Param("shared") boolean shared);


    @Modifying
    @Query("UPDATE Image i SET i.tag = :tag, i.shared = :shared WHERE i.imageId = :imageId")
    int updateImageTagAndShared(@Param("imageId") Long imageId, @Param("tag") String tag, @Param("shared") boolean shared);

}
