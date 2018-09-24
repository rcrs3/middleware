package crh

import (
    "fmt"
    "net"
    "os"
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
	l, err := net.Listen(c.Host, +":"+c.Port)
    if err != nil {
        fmt.Println("Error listening:", err.Error())
        os.Exit(1)
	}

	l.Write([]byte("Message received."))

	l.Close()
}

func sendUdp(msg []byte) {

}

func sendMiddleware(msg []byte) {

}

func Receive() {

}