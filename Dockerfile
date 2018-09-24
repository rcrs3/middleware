FROM golang
RUN go get github.com/streadway/amqp
WORKDIR /middle
COPY . /middle
CMD go run /middle/main.go