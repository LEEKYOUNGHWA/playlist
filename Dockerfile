# --- playlist/Dockerfile ---

# 1. 빌드 환경 설정: Java 17을 기반으로 하는 경량화된 OpenJDK JDK 이미지를 사용합니다.
#    'jre-slim' 대신 'jdk-slim'을 사용하여 빌드 환경의 안정성을 높입니다.
FROM openjdk:17-jdk-slim

# 2. 메인테이너 정보 (선택 사항): 이미지의 작성자 정보를 명시합니다.
LABEL maintainer="Baek Jihwa <your.email@example.com>"

# 3. 작업 디렉토리 설정: 컨테이너 내부의 작업 디렉토리를 /app으로 설정합니다.
#    모든 애플리케이션 관련 파일들이 이 디렉토리에 복사됩니다.
WORKDIR /app

# 4. JAR 파일 복사: Gradle 빌드 결과물인 JAR 파일을 컨테이너의 /app 디렉토리로 복사합니다.
#    'ARG JAR_FILE=build/libs/*.jar'는 Docker가 'playlist/' (빌드 컨텍스트이자 프로젝트 루트) 폴더 안의
#    'build/libs/' 경로에서 JAR 파일을 찾도록 지시합니다.
#    'app.jar'는 컨테이너 내부에서 실행될 JAR 파일의 이름입니다.
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 5. 포트 노출: Spring Boot 애플리케이션이 사용할 포트를 외부에 노출합니다.
#    기본적으로 Spring Boot는 8080 포트를 사용합니다.
EXPOSE 8080

# 6. 컨테이너 실행 명령어 정의: 컨테이너가 시작될 때 실행될 명령어를 정의합니다.
#    -Dspring.profiles.active=local : Spring Boot 애플리케이션을 'local' 프로필로 실행하도록 명시합니다.
#    이 프로필은 H2 데이터베이스 사용 및 .env.local 참조 설정을 활성화합니다.
#    API 키 및 플레이리스트 ID는 'docker run' 명령어로 -e 옵션을 통해 주입하는 것이 좋습니다.
CMD ["java", "-jar", "-Dspring.profiles.active=local", "app.jar"]