//
// Created by amromin@wincs.cs.bgu.ac.il on 1/3/19.
//

#ifndef UNTITLED_DECODER_H
#define UNTITLED_DECODER_H


#include <connectionHandler.h>


class Decoder {
public:
    Decoder(ConnectionHandler &ch,std::atomic<bool> &isLogout,std::atomic<bool> &stop);
    void process(char *ans);
    void run();
    short bytesToShort(char* bytesArr);

    virtual ~Decoder();                            // destructor
    Decoder(const Decoder &other);             // copy constructor
    Decoder & operator=(const Decoder &other);


private:
    ConnectionHandler &ch;
    std::atomic<bool> &isLogout;
    std::atomic<bool> &stop;


};


#endif //UNTITLED_DECODER_H
