//
// Created by eva on 12/31/18.
//

#ifndef UNTITLED_REGISTER_H
#define UNTITLED_REGISTER_H

#include <string>
#include <ostream>
#include <iostream>
#include "connectionHandler.h"
#include <iostream>
#include <vector>
#include <string>
#include <boost/algorithm/string/split.hpp>
#include <boost/algorithm/string/classification.hpp>

using boost::asio::ip::tcp;

using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;

class Register {
public:
    Register(std::string frame,ConnectionHandler &ch);
    void shortToBytes(short num, char* bytesArr);
    void encodeRegister();
    void encodeLogin();
    void encodeLogout();
    void encodeFollow();
    void encodePost();
    void encodePM();
    void encodeUserList();
    void encodeStat();

    virtual ~Register();                            // destructor
    Register(const Register &other);             // copy constructor
    Register & operator=(const Register &other);


private:
    std::string keyboard;
    ConnectionHandler &ch;
    char *b;
    int length;


};


#endif //UNTITLED_REGISTER_H
