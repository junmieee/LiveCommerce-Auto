package main

import (
	"fmt"
	"net/http"
)

func main() {
	http.HandleFunc("/ping", func(w http.ResponseWriter, r *http.Request) {
		fmt.Fprintln(w, "pong from Go")
	})
	fmt.Println("Go server running on :8080")
	http.ListenAndServe(":8080", nil)
}