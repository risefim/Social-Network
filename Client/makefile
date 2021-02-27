CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude

LDFLAGS:=-lboost_system -pthread



all: EchoClient

	g++ -o bin/BGSclient bin/connectionHandler.o bin/echoClient.o bin/Encoder.o bin/Decoder.o $(LDFLAGS)



EchoClient: bin/connectionHandler.o bin/echoClient.o bin/Decoder.o bin/Encoder.o

	

bin/connectionHandler.o: src/connectionHandler.cpp

	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp



bin/echoClient.o: src/echoClient.cpp

	g++ $(CFLAGS) -o bin/echoClient.o src/echoClient.cpp



bin/Encoder.o: src/Encoder.cpp

	g++ $(CFLAGS) -o bin/Encoder.o src/Encoder.cpp



bin/Decoder.o: src/Decoder.cpp

	g++ $(CFLAGS) -o bin/Decoder.o src/Decoder.cpp

	

.PHONY: clean

clean:

	rm -f bin/*
