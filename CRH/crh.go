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
		fmt.Println("SEND UDP")
		sendUdp(msg, c)
	case MIDDLEWARE:
		sendMiddleware(msg)
	}
}

func sendTcp(msg []byte, c CRH) {
	
}

func sendUdp(msg []byte, c CRH) {
	RemoteAddr, err := net.ResolveUDPAddr("udp", c.Host + ":" + c.Port)

	if err != nil {
        fmt.Println("Error listening:", err.Error())
        os.Exit(1)
	}

	conn, err := net.DialUDP("udp", nil, RemoteAddr)

	defer conn.Close()

	if err != nil {
        fmt.Println("Error listening:", err.Error())
        os.Exit(1)
	}

	_, err = conn.Write(msg)

	if err != nil {
        fmt.Println("Error writing:", err.Error())
        os.Exit(1)
	}

}

func sendMiddleware(msg []byte) {

}

func (c CRH) Receive() {
	switch c.TransportType {
	case TCP:
		fmt.Println("TCP")
	case UDP:
		fmt.Println("UDP")
		receiveUdp(c)
	case MIDDLEWARE:
		fmt.Println("MIDDLEWARE")
	}
}

func receiveUdp(c CRH) {

}