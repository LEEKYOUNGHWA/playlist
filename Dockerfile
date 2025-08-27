# 빌드 스테이지: Spring Boot 애플리케이션을 빌드합니다.
FROM openjdk:17-jdk-slim as builder

# 컨테이너 내부 작업 디렉토리를 설정합니다.
WORKDIR /app

# Gradle Wrapper와 빌드 관련 파일을 복사합니다.
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# 모든 소스 코드를 복사합니다.
COPY src src

# gradlew 파일에 실행 권한을 추가합니다.
RUN chmod +x gradlew

# 프로젝트를 빌드하여 .jar 파일을 생성합니다.
RUN ./gradlew clean build -x test

# 실행 스테이지: 빌드된 .jar 파일을 실행하기 위한 최종 이미지를 만듭니다.
FROM openjdk:17-jdk-slim

# 컨테이너 내부에 작업 디렉토리를 설정합니다.
WORKDIR /app

# 빌드 스테이지에서 생성된 .jar 파일을 최종 이미지로 복사합니다.
# 이 단계에서 .jar 파일이 존재하기 때문에 에러가 발생하지 않습니다.
COPY --from=builder /app/build/libs/*.jar app.jar

# 애플리케이션이 사용할 포트를 외부에 노출합니다.
EXPOSE 8080

# 환경 변수 설정 (필요에 따라 추가/수정)
ENV YOUTUBE_API_KEY=${YOUTUBE_API_KEY}
ENV YOUTUBE_PLAYLIST_ID=${YOUTUBE_PLAYLIST_ID}

# 애플리케이션을 실행합니다.
ENTRYPOINT ["java", "-jar", "app.jar"]