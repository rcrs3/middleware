package srh

import (
	"fmt"
	"net"
)

type SRH struct {
	TransportType int
	Host string
	Port string
}

const (
	TCP = 0
	UDP = 1
	MIDDLEWARE = 2
)

func Send(msg []byte) {
	
}

func (c SRH) Receive() {
	switch c.TransportType {
	case TCP:
		fmt.Println("TCP")
	case UDP:
		fmt.Println("RECEIVE UDP")
		receiveUdp(c)
		fmt.Println("AAAAAAAAA")
	case MIDDLEWARE:
		fmt.Println("MIDDLEWARE")
	}
}

func receiveUdp(c SRH) {

	addr := net.UDPAddr{
		Port: c.Port,
		IP:   net.ParseIP(c.Host),
	}

	ln, err := net.ListenUDP("udp", &addr)

	if err != nil {
        fmt.Println("Error Listenning:", err.Error())
        return
	}

	defer ln.Close()
	
	for {
		fmt.Println("VSF")
		handleConn(ln)	
	}
}

func handleConn(conn *net.UDPConn) {
	buffer := make([]byte, 1024)
	
	_, _, err := conn.ReadFromUDP(buffer)
	if err != nil {
		fmt.Println("Error Reading:", err.Error())
		return
	}

	fmt.Println("AJDOIEWAJ")	
	fmt.Println("Received from UDP client :  ", string(buffer))
}