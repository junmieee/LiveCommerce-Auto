# 1. Node 버전 고정된 공식 이미지 사용
FROM node:20-alpine

# 2. 작업 디렉토리 생성
WORKDIR /app

# 3. 의존성 설치를 위한 package 파일만 복사
COPY front/package*.json ./
RUN npm install

# 4. 전체 소스 복사 (코드/설정 등)
COPY front/ ./

# 5. 포트 열기
EXPOSE 3000

# 6. 앱 실행 명령
CMD ["npm", "run", "dev"]