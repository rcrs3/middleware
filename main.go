package main


import (
	"./CRH"
	"./SRH"
)

const (
    CONN_HOST = "localhost"
    CONN_PORT = "3333"
    CONN_TYPE = "udp"
)

func main() {
	client := crh.CRH{1, CONN_HOST, CONN_PORT}
	client.Send([]byte("IRRU"))


	server := srh.SRH{1, CONN_HOST, CONN_PORT}
	server.Receive()
}