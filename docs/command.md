# 1. Docker 컨테이너 실행
docker-compose up -d

# 2. 컨테이너 상태 확인
docker-compose ps

# 3. Gradle 빌드 (QueryDSL Q클래스 생성)
./gradlew clean compileJava

# 4. 애플리케이션 실행
./gradlew bootRun



# 컨테이너 중지 및 삭제
docker-compose down -v

# 다시 시작 (볼륨도 새로 생성)
docker-compose up -d

# 상태 확인
docker-compose ps
