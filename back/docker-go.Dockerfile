# Build stage
FROM golang:1.21 as builder

WORKDIR /app
COPY ./go/go.mod ./go/go.sum ./
RUN go mod download
COPY ./go/ ./
RUN go build -o main .

# Run stage
FROM debian:bookworm-slim
WORKDIR /app
COPY --from=builder /app/main .
EXPOSE 8080
CMD ["./main"]