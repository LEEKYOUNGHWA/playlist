package com.baekjihwa.playlist.client;

import com.baekjihwa.playlist.model.Video;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.Thumbnail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
// import java.util.regex.Matcher; // 제거
// import java.util.regex.Pattern; // 제거

/**
 * YouTube Data API와 통신하여 플레이리스트 데이터를 가져오는 클라이언트입니다.
 * 스프링 빈으로 등록되어 다른 컴포넌트에서 주입받아 사용될 수 있습니다.
 */
@Component
public class YouTubeApiClient {

    @Value("${youtube.api-key}")
    private String youtubeApiKey;

    private static final String APPLICATION_NAME = "PlaylistService";
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    // private static final Pattern ARTIST_PATTERN = Pattern.compile("^\\s*([\\w\\s\\p{L}\\p{N}&\\-]+?)\\s*[-–—]\\s*(.+)$"); // 제거

    private YouTube getYouTubeService() {
        return new YouTube.Builder(new NetHttpTransport(), JSON_FACTORY, httpRequest -> {})
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * 특정 플레이리스트 ID에 해당하는 동영상 목록을 YouTube Data API에서 가져옵니다.
     * YouTube API 키는 스프링 환경 변수 주입을 통해 사용됩니다.
     *
     * @param playlistId 가져올 플레이리스트의 고유 ID
     * @return 가져온 동영상 목록 (Video 객체 리스트)
     * @throws IOException API 호출 중 네트워크 또는 파싱 오류 발생 시
     */
    public List<Video> getPlaylistItems(String playlistId) throws IOException {
        YouTube youtube = getYouTubeService();
        List<Video> videos = new ArrayList<>();
        String nextToken = null;

        do {
            YouTube.PlaylistItems.List playlistItemRequest = youtube.playlistItems()
                    .list(Arrays.asList("snippet", "contentDetails"));
            playlistItemRequest.setPlaylistId(playlistId);
            playlistItemRequest.setKey(youtubeApiKey);
            playlistItemRequest.setMaxResults(50L);
            playlistItemRequest.setPageToken(nextToken);

            PlaylistItemListResponse playlistItemResponse = playlistItemRequest.execute();

            for (PlaylistItem item : playlistItemResponse.getItems()) {
                String videoId = item.getContentDetails().getVideoId();
                String title = item.getSnippet().getTitle();
                String description = item.getSnippet().getDescription();
                Thumbnail defaultThumbnail = item.getSnippet().getThumbnails().getDefault();
                String thumbnailUrl = (defaultThumbnail != null) ? defaultThumbnail.getUrl() : null;
                String channelTitle = item.getSnippet().getChannelTitle();
                String channelId = item.getSnippet().getChannelId();

                String videoOwnerChannelTitle = item.getSnippet().getVideoOwnerChannelTitle();
                String videoOwnerChannelId = item.getSnippet().getVideoOwnerChannelId();

                LocalDateTime publishedAt = null;
                if (item.getSnippet().getPublishedAt() != null) {
                    publishedAt = LocalDateTime.parse(item.getSnippet().getPublishedAt().toString().replace("Z", "+00:00"), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                }

                // String originalArtist = extractArtistFromTitle(title); // 제거

                // Video 생성자에서 originalArtist 관련 필드 제거
                videos.add(new Video(videoId, title, description, thumbnailUrl, publishedAt,
                        channelTitle, channelId, // originalArtist 제거
                        videoOwnerChannelTitle, videoOwnerChannelId));
            }
            nextToken = playlistItemResponse.getNextPageToken();
        } while (nextToken != null);

        return videos;
    }

    // private String extractArtistFromTitle(String title) { // 제거
    //     if (title == null || title.trim().isEmpty()) {
    //         return null;
    //     }
    //     Matcher matcher = ARTIST_PATTERN.matcher(title);
    //     if (matcher.matches()) {
    //         return matcher.group(1).trim();
    //     }
    //     return null;
    // }
}