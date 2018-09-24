package main

import (
    "fmt"
    "net"
    "os"
)

import (
	"./CRH"
)

const (
    CONN_HOST = "localhost"
    CONN_PORT = "3333"
    CONN_TYPE = "tcp"
)

func main() {
	ln, err := net.Listen("tcp", ":8080")
	if err != nil {
		// handle error
	}
	for {
		conn, err := ln.Accept()
		if err != nil {
			// handle error
		}
		
		reply := make([]byte, 1024)
		
		_, err = conn.Read(reply)
		if err != null {
			fmt.Println("error main")
			os.exit(1)
		}
	}
}