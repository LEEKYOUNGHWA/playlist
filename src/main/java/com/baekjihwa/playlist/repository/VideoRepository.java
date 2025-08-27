package com.baekjihwa.playlist.repository;

import com.baekjihwa.playlist.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * 'Video' 엔티티에 대한 데이터베이스 CRUD(생성, 읽기, 업데이트, 삭제) 작업을 처리하는 레포지토리 인터페이스입니다.
 * Spring Data JPA가 이 인터페이스의 메서드 이름을 기반으로 필요한 데이터베이스 쿼리를 자동으로 생성해 줍니다.
 */
@Repository // 이 인터페이스가 데이터 접근 계층의 컴포넌트임을 나타냄
public interface VideoRepository extends JpaRepository<Video, Long> {

    /**
     * YouTube 동영상 ID(videoId)를 기준으로 동영상을 찾습니다.
     * Optional<Video>를 반환하여 null 대신 값이 없을 수도 있음을 명시합니다.
     */
    Optional<Video> findByVideoId(String videoId);

    /**
     * 동영상 제목(title) 또는 설명(description)에 특정 검색어가 포함된 동영상 목록을 검색합니다.
     * 'ContainingIgnoreCase'는 대소문자를 구분하지 않고 검색하는 기능을 제공합니다.
     */
    List<Video> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String titleQuery, String descriptionQuery);

    /**
     * 동영상의 원본 채널 이름 (videoOwnerChannelTitle) 또는 동영상 제목 (title)에
     * 특정 검색어가 포함된 동영상 목록을 검색합니다.
     * 'ContainingIgnoreCase'는 대소문자를 구분하지 않고 검색하는 기능을 제공합니다.
     *
     * @param videoOwnerChannelTitleQuery 원본 채널 이름 검색어
     * @param titleQuery 동영상 제목 검색어
     * @return 검색 결과에 해당하는 동영상 목록
     */
    List<Video> findByVideoOwnerChannelTitleContainingIgnoreCaseOrTitleContainingIgnoreCase(String videoOwnerChannelTitleQuery, String titleQuery);
}