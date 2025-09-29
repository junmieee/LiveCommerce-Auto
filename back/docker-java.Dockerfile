# Build stage
FROM gradle:8.5-jdk17

WORKDIR /app

# 빌드 설정 파일 복사
COPY back/java/gradlew ./gradlew
COPY back/java/gradle ./gradle
COPY back/java/build.gradle ./build.gradle
COPY back/java/settings.gradle ./settings.gradle
RUN chmod +x ./gradlew

# 의존성만 먼저 캐싱 (옵션)
RUN gradle dependencies || true

# 실행 포트 노출
EXPOSE 8081

# 기본 실행 명령
CMD ["./gradlew", "bootRun", "--no-daemon"]