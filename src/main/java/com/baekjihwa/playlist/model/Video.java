package com.baekjihwa.playlist.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

// *** Lombok 어노테이션 추가 ***
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor; // 기본 생성자 어노테이션
import lombok.AllArgsConstructor; // 모든 필드를 인자로 받는 생성자 어노테이션
import lombok.ToString; // toString() 메서드 자동 생성

/**
 * 데이터베이스의 'playlist_video' 테이블과 매핑되는 엔티티 클래스입니다.
 * 각 동영상의 속성을 정의합니다.
 */
@Entity
@Table(name = "playlist_video")
@Getter // 모든 필드에 대한 Getter 메서드를 자동 생성
@Setter // 모든 필드에 대한 Setter 메서드를 자동 생성
@ToString // toString() 메서드를 자동 생성 (모든 필드 포함)
// @NoArgsConstructor // Lombok의 NoArgsConstructor를 사용하면 아래 수동 생성자 대신 사용 가능
// @AllArgsConstructor // Lombok의 AllArgsConstructor를 사용하면 아래 수동 생성자 대신 사용 가능
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String videoId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String thumbnailUrl;

    private LocalDateTime publishedAt;

    @Column(nullable = false)
    private String channelTitle; // YouTube에 동영상을 업로드한 채널 이름 (재업로드 채널일 수 있음)

    @Column(nullable = false)
    private String channelId; // 업로드한 채널의 고유 ID

    // private String originalArtist; // 제거


    private String videoOwnerChannelTitle; // 동영상의 원본 콘텐츠 소유자 채널 이름 (nullable)
    private String videoOwnerChannelId; // 동영상의 원본 콘텐츠 소유자 채널 ID (nullable)


    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // 수동으로 작성된 기본 생성자 (롬복 @NoArgsConstructor 사용 시 제거 가능)
    // createdAt, updatedAt 초기화 로직 때문에 수동 생성자 유지
    public Video() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 모든 필드를 인자로 받는 생성자 (롬복 @AllArgsConstructor 사용 시 제거 가능)
    // Lombok의 @AllArgsConstructor는 모든 필드를 포함하므로, 이 생성자와 충돌할 수 있습니다.
    // 만약 이 생성자를 유지하려면 @AllArgsConstructor를 사용하지 않거나, 필요에 따라 조정합니다.
    public Video(String videoId, String title, String description, String thumbnailUrl, LocalDateTime publishedAt,
                 String channelTitle, String channelId,
                 String videoOwnerChannelTitle, String videoOwnerChannelId) {
        this.videoId = videoId;
        this.title = title;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.publishedAt = publishedAt;
        this.channelTitle = channelTitle;
        this.channelId = channelId;
        this.videoOwnerChannelTitle = videoOwnerChannelTitle;
        this.videoOwnerChannelId = videoOwnerChannelId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // --- Getter와 Setter 메소드는 Lombok이 자동 생성하므로 수동 작성 필요 없음 ---
    // public Long getId() { ... }
    // public void setId(Long id) { ... }
    // ...
    // public LocalDateTime getCreatedAt() { ... }
    // public void setCreatedAt(LocalDateTime createdAt) { ... }
    // public LocalDateTime getUpdatedAt() { ... }
    // public void setUpdatedAt(LocalDateTime updatedAt) { ... }

    // toString() 메서드도 Lombok이 자동 생성하므로 수동 작성 필요 없음
    // @Override
    // public String toString() { ... }
}