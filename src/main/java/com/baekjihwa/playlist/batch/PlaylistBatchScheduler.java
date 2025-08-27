package com.baekjihwa.playlist.batch;

import com.baekjihwa.playlist.client.YouTubeApiClient;
import com.baekjihwa.playlist.model.Video;
import com.baekjihwa.playlist.repository.VideoRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * YouTube 플레이리스트 데이터를 주기적으로 가져와 데이터베이스에 저장하는 배치 스케줄러입니다.
 * 실제 YouTube API와 연동하여 동작합니다.
 */
@Component
public class PlaylistBatchScheduler {

    private final VideoRepository videoRepository;
    private final YouTubeApiClient youtubeApiClient;

    @Value("${youtube.playlist-id}")
    private String youtubePlaylistId;

    public PlaylistBatchScheduler(VideoRepository videoRepository, YouTubeApiClient youtubeApiClient) {
        this.videoRepository = videoRepository;
        this.youtubeApiClient = youtubeApiClient;
    }

    @Scheduled(fixedRate = 300000) // 5분마다 실행 (테스트용: 5분 = 300000 밀리초)
    // @Scheduled(cron = "0 0 3 * * *") // 운영용: 매일 새벽 3시
    @Transactional
    public void updatePlaylistData() {
        System.out.println("====== 플레이리스트 데이터 업데이트 시작: " + LocalDateTime.now() + " ======");
        System.out.println("사용 중인 YouTube 플레이리스트 ID: " + (youtubePlaylistId != null && youtubePlaylistId.length() > 5 ? youtubePlaylistId.substring(0, 5) + "..." : "플레이리스트 ID 없음"));

        List<Video> fetchedVideos = null;
        try {
            fetchedVideos = youtubeApiClient.getPlaylistItems(youtubePlaylistId);
            System.out.println("YouTube API에서 " + fetchedVideos.size() + "개의 동영상 가져옴.");
        } catch (IOException e) {
            System.err.println("YouTube API 호출 중 오류 발생: " + e.getMessage());
            return;
        }

        int newVideosCount = 0;
        int updatedVideosCount = 0;

        for (Video newVideo : fetchedVideos) {
            Optional<Video> existingVideoOptional = videoRepository.findByVideoId(newVideo.getVideoId());

            if (existingVideoOptional.isPresent()) {
                Video existingVideo = existingVideoOptional.get();
                boolean changed = false;

                // 각 필드를 Objects.equals()를 사용하여 안전하게 비교하고 업데이트
                if (!Objects.equals(existingVideo.getTitle(), newVideo.getTitle())) { existingVideo.setTitle(newVideo.getTitle()); changed = true; }
                if (!Objects.equals(existingVideo.getDescription(), newVideo.getDescription())) { existingVideo.setDescription(newVideo.getDescription()); changed = true; }
                if (!Objects.equals(existingVideo.getThumbnailUrl(), newVideo.getThumbnailUrl())) { existingVideo.setThumbnailUrl(newVideo.getThumbnailUrl()); changed = true; }
                if (!Objects.equals(existingVideo.getPublishedAt(), newVideo.getPublishedAt())) { existingVideo.setPublishedAt(newVideo.getPublishedAt()); changed = true; }
                if (!Objects.equals(existingVideo.getChannelTitle(), newVideo.getChannelTitle())) { existingVideo.setChannelTitle(newVideo.getChannelTitle()); changed = true; }
                // if (!Objects.equals(existingVideo.getOriginalArtist(), newVideo.getOriginalArtist())) { existingVideo.setOriginalArtist(newVideo.getOriginalArtist()); changed = true; } // 제거
                if (!Objects.equals(existingVideo.getChannelId(), newVideo.getChannelId())) { existingVideo.setChannelId(newVideo.getChannelId()); changed = true; }
                if (!Objects.equals(existingVideo.getVideoOwnerChannelTitle(), newVideo.getVideoOwnerChannelTitle())) { existingVideo.setVideoOwnerChannelTitle(newVideo.getVideoOwnerChannelTitle()); changed = true; }
                if (!Objects.equals(existingVideo.getVideoOwnerChannelId(), newVideo.getVideoOwnerChannelId())) { existingVideo.setVideoOwnerChannelId(newVideo.getVideoOwnerChannelId()); changed = true; }


                if (changed) {
                    existingVideo.setUpdatedAt(LocalDateTime.now());
                    videoRepository.save(existingVideo);
                    updatedVideosCount++;
                    System.out.println("동영상 업데이트: " + existingVideo.getTitle());
                }
            } else {
                newVideo.setCreatedAt(LocalDateTime.now());
                newVideo.setUpdatedAt(LocalDateTime.now());
                videoRepository.save(newVideo);
                newVideosCount++;
                System.out.println("새로운 동영상 추가: " + newVideo.getTitle());
            }
        }

        System.out.println("새로 추가된 동영상: " + newVideosCount + "개");
        System.out.println("업데이트된 동영상: " + updatedVideosCount + "개");
        System.out.println("====== 플레이리스트 데이터 업데이트 완료: " + LocalDateTime.now() + " ======");
    }
}