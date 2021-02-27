//
// Created by eva on 12/31/18.
//

#include <vector>
#include "Encoder.h"


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

Register::Register(std::string frame,ConnectionHandler &ch):keyboard(frame),ch(ch),b(),length() {
    std::vector<std::string> tokens;
    split(tokens, frame, boost::is_any_of(" "));
    if (tokens.at(0) == "REGISTER") {
        keyboard= keyboard.erase(0,9);

        encodeRegister();
    }
    if (tokens.at(0) == "LOGIN") {
        keyboard= keyboard.erase(0,6);

        encodeLogin();

    }
    if (tokens.at(0) == "LOGOUT") {
        keyboard= keyboard.erase(0,7);

        encodeLogout();

    }
    if (tokens.at(0) == "FOLLOW") {
        keyboard= keyboard.erase(0,7);

        encodeFollow();
    }
    if (tokens.at(0) == "POST") {
        keyboard= keyboard.erase(0,5);

        encodePost();
    }
    if (tokens.at(0) == "PM") {
        keyboard= keyboard.erase(0,3);

        encodePM();
    }
    if (tokens.at(0) == "USERLIST") {
        keyboard= keyboard.erase(0,9);

        encodeUserList();
    }
    if (tokens.at(0) == "STAT") {
        keyboard= keyboard.erase(0,5);

        encodeStat();
    }

}
void Register::encodeRegister(){
    for(char &c:keyboard){
        if (c==' '){
            c='\0';
        }
    }
    length=2+keyboard.length()+1;
    b = new char[length];
    short op=1;
    char opcode[2];

    shortToBytes(op,opcode);
    b[0]=opcode[0];
    b[1]=opcode[1];

    for(int i=0;i<(int)keyboard.length();i++){
        b[i+2]=keyboard[i];
    }
    b[2+keyboard.length()] = '\0';


    if (!ch.sendBytes(b,length)) { // connection lost
        std::cout<<"Disconnected"<<std::endl;

    }


}


void Register::encodeLogin(){
    for(char &c:keyboard){
        if (c==' '){
            c='\0';
        }
    }
    length=2+keyboard.length()+1;
    b = new char[length];
    short op=2;
    char opcode[2];

    shortToBytes(op,opcode);
    b[0]=opcode[0];
    b[1]=opcode[1];

    for(int i=0;i<(int)keyboard.length();i++){
        b[i+2]=keyboard[i];
    }
    b[2+keyboard.length()] = '\0';


    if (!ch.sendBytes(b,length)) { // connection lost
        std::cout<<"Disconnected"<<std::endl;

    }
}

void Register::encodeLogout(){

    length=2;
    b = new char[length];
    short op=3;
    char opcode[2];

    shortToBytes(op,opcode);
    b[0]=opcode[0];
    b[1]=opcode[1];


    if (!ch.sendBytes(b,length)) { // connection lost
        std::cout<<"Disconnected"<<std::endl;
    }
}


void Register::encodeFollow() {
    short follow_unfollow=(short)std::stoi(keyboard.substr(0,1));
    keyboard= keyboard.erase(0,2);
    std::string numOfUsers =keyboard.substr(0,keyboard.find(' '));
    keyboard= keyboard.erase(0,numOfUsers.length()+1);

    short numOfUsers_short=(short)std::stoi(numOfUsers);

    for(char &c:keyboard){
        if (c==' '){
            c='\0';
        }
    }

    length=2+1+2+keyboard.length()+1;
    b = new char[length];
    short op=4;
    char opcode[2];

    shortToBytes(op,opcode);
    b[0]=opcode[0];
    b[1]=opcode[1];

    if(follow_unfollow==1){
        b[2]='\1';
    }
    else{
        b[2]='\0';
    }

    char userNum[2];

    shortToBytes(numOfUsers_short,userNum);
    b[3]=userNum[0];
    b[4]=userNum[1];

    for(int i=0;i<(int)keyboard.length();i++){
        b[i+5]=keyboard[i];
    }
    b[5+keyboard.length()] = '\0';


    if (!ch.sendBytes(b,length)) { // connection lost
        std::cout<<"Disconnected"<<std::endl;
    }

}


void Register::encodePost(){

    length=2+keyboard.length()+1;
    b = new char[length];
    short op=5;
    char opcode[2];

    shortToBytes(op,opcode);
    b[0]=opcode[0];
    b[1]=opcode[1];


    for(int i=0;i<(int)keyboard.length();i++){
        b[i+2]=keyboard[i];
    }

    b[2+keyboard.length()] = '\0';



    if (!ch.sendBytes(b,length)) { // connection lost
        std::cout<<"Disconnected"<<std::endl;
    }
}


void Register::encodePM(){
    std::string user_name =keyboard.substr(0,keyboard.find(' '));
    keyboard= keyboard.erase(0,user_name.length()+1);

    length=2+user_name.length()+1+keyboard.length()+1;
    b = new char[length];
    short op=6;
    char opcode[2];

    shortToBytes(op,opcode);
    b[0]=opcode[0];
    b[1]=opcode[1];

    for(int i=0;i<(int)user_name.length();i++){
        b[i+2]=user_name[i];
    }

    b[2+user_name.length()]='\0';


    for(int i=0;i<(int)keyboard.length();i++){
        b[2+user_name.length()+1+i]=keyboard[i];
    }

    b[2+user_name.length()+1+keyboard.length()] = '\0';



    if (!ch.sendBytes(b,length)) { // connection lost
        std::cout<<"Disconnected"<<std::endl;

    }
}


void Register::encodeUserList(){
    length=2;
    b = new char[length];
    short op=7;
    char opcode[2];

    shortToBytes(op,opcode);
    b[0]=opcode[0];
    b[1]=opcode[1];



    if (!ch.sendBytes(b,length)) { // connection lost
        std::cout<<"Disconnected"<<std::endl;
    }
}
void Register::encodeStat(){

    length=2+keyboard.length()+1;
    b = new char[length];
    short op=8;
    char opcode[2];

    shortToBytes(op,opcode);
    b[0]=opcode[0];
    b[1]=opcode[1];

    for(int i=0;i<(int)keyboard.length();i++){
        b[i+2]=keyboard[i];
    }
    b[2+keyboard.length()] = '\0';



    if (!ch.sendBytes(b,length)) { // connection lost
        std::cout<<"Disconnected"<<std::endl;
    }

}


void Register::shortToBytes(short num, char* bytesArr)
{
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}




