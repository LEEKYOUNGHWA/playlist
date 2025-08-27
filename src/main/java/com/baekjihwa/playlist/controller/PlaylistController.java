package com.baekjihwa.playlist.controller;

import com.baekjihwa.playlist.model.Video;
import com.baekjihwa.playlist.repository.VideoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 플레이리스트 동영상 검색을 위한 REST API 컨트롤러입니다.
 * 클라이언트(예: Next.js 프런트엔드)에서 이 API를 호출하여 동영상 데이터를 조회합니다.
 *
 * '@Tag' 어노테이션은 Swagger UI에서 이 컨트롤러의 API들을 'Playlist Video API'라는 그룹으로 묶고
 * 설명을 추가하는 역할을 합니다.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Playlist Video API", description = "플레이리스트 동영상 검색 및 조회 API")
public class PlaylistController {

    private final VideoRepository videoRepository;

    public PlaylistController(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    /**
     * 모든 동영상 목록을 반환하는 API 엔드포인트입니다.
     * HTTP GET 요청: /api/videos
     *
     * '@Operation' 어노테이션은 Swagger UI에서 이 API의 요약과 설명을 제공합니다.
     */
    @GetMapping("/videos")
    @Operation(summary = "모든 동영상 조회", description = "데이터베이스에 저장된 모든 플레이리스트 동영상을 조회합니다.")
    public List<Video> getAllVideos() {
        System.out.println("모든 동영상 목록 요청 수신.");
        return videoRepository.findAll();
    }

    /**
     * 제목, 설명, 원본 채널 이름 중 특정 검색어가 포함된 동영상 목록을 검색하는 API 엔드포인트입니다.
     * HTTP GET 요청: /api/videos/search?query=검색어
     *
     * @param query 검색할 문자열 (쿼리 파라미터, 선택 사항)
     * @return 검색 결과에 해당하는 동영상 목록 (검색어가 없으면 모든 동영상 반환)
     */
    @GetMapping("/videos/search")
    @Operation(summary = "동영상 검색", description = "동영상 제목 또는 원본 채널 이름으로 동영상을 검색합니다. 검색어가 없으면 모든 동영상을 반환합니다.")
    public List<Video> searchVideos(
            @Parameter(description = "검색할 키워드 (동영상 제목 또는 원본 채널 이름에서 검색)", example = "Young Gun")
            @RequestParam(required = false) String query) {
        System.out.println("동영상 검색 요청 수신. 검색어: " + (query != null ? query : "없음"));
        if (query == null || query.trim().isEmpty()) {
            return videoRepository.findAll(); // 검색어가 없으면 모든 동영상 반환
        }
        // !!! 수정된 검색 로직: videoOwnerChannelTitle 또는 title에서 검색 !!!
        return videoRepository.findByVideoOwnerChannelTitleContainingIgnoreCaseOrTitleContainingIgnoreCase(query, query);
    }
}