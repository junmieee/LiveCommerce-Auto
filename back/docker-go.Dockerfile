FROM golang:1.21

WORKDIR /app

# air 설치
RUN go install github.com/cosmtrek/air@v1.40.4

COPY back/go/go.mod .
COPY back/go/go.sum .
RUN go mod download

COPY back/go/ .

# .air.toml은 go 소스 폴더(back/go)에 만들어둬야 함(없으면 자동생성됨)
EXPOSE 8080

CMD ["air", "-c", ".air.toml"]