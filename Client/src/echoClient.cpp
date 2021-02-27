#include <stdlib.h>
#include <thread>
#include "../include/connectionHandler.h"
#include "Decoder.h"
#include "Encoder.h"


Register::~Register() {
}
Decoder::~Decoder() {
}


int main (int argc, char *argv[]) {
    if (argc < 3) {
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    std::atomic<bool> isLogout (false);
    std::atomic<bool> stop (false);
    Decoder decoder(connectionHandler, isLogout,stop);

    if (!connectionHandler.connect()) {
        return 1;
    }

    std::thread thread1(&Decoder::run,&decoder);//

    while (!isLogout) {
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
        std::string line(buf);
        if (line == "LOGOUT"){
            Register reg(line, connectionHandler);
            stop.store(true);
            while (stop){}
        }
        else
            Register reg(line, connectionHandler);
    }

    thread1.join();
    return 0;
}