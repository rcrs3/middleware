package crh

import (
    "fmt"
    "net"
	"os"
	"bufio"
)

type CRH struct {
	TransportType int
	Host string
	Port string
}

const (
	TCP = 0
	UDP = 1
	MIDDLEWARE = 2
)

func (c CRH) Send(msg []byte) {
	switch c.TransportType {
	case TCP:
		sendTcp(msg, c)
	case UDP:
		sendUdp(msg)
	case MIDDLEWARE:
		sendMiddleware(msg)
	}
}

func sendTcp(msg []byte, c CRH) {
	// connect to this socket
	conn, _ := net.Dial("tcp", "127.0.0.1:8081")
	fmt.Fprintf(conn, msg)
}

func sendUdp(msg []byte) {

}

func sendMiddleware(msg []byte) {

}

func Receive() {

}