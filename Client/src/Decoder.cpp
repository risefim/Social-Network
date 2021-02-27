//
// Created by amromin@wincs.cs.bgu.ac.il on 1/3/19.
//

#include "Decoder.h"

Decoder::Decoder(ConnectionHandler &ch,std::atomic<bool> &isLogout,std::atomic<bool> &stop):ch(ch),isLogout(isLogout),stop(stop){}

void Decoder::run(){
    char answer[2];
    while(!isLogout){
        ch.getBytes(answer,2);
        process(answer);
    }
}

void Decoder::process(char *ans){
        short opCode=bytesToShort(ans);

        switch(opCode) {
            case 9: {
                std::string opodeString = "NOTIFICATION";
                char NotificationType[1];
                ch.getBytes(NotificationType, 1);

                std::string PmPublic_string;
                if (NotificationType[0] == '\0') {
                    PmPublic_string = "PM";
                }
                if (NotificationType[0] == '\1') {
                    PmPublic_string = "Public";
                }
                std::string user_name;
                ch.getLine(user_name);

                std::string content;
                ch.getLine(content);



                std::cout << opodeString + " " + PmPublic_string + " " + user_name + " " + content << std::endl;
                break;
            }



            case 10: {

                std::string opodeString = "ACK";

                char MessageOpcode[2];
                ch.getBytes(MessageOpcode, 2);
                short MessageOP = ch.bytesToShort(MessageOpcode);

                switch (MessageOP) {
                    case 1: {
                        std::cout << opodeString + " " + std::to_string(MessageOP) << std::endl;
                        break;
                    }
                    case 2: {
                        std::cout << opodeString + " " + std::to_string(MessageOP) << std::endl;
                        break;
                    }
                    case 3: {
                        std::cout << opodeString + " " + std::to_string(MessageOP) << std::endl;
                        ch.close();
                        isLogout.store(true);
                        break;
                    }
                    case 4: {
                        char UsersNum [2];
                        ch.getBytes(UsersNum,2);
                        int usersnum=bytesToShort(UsersNum);
                        std::string list;
                        for(int i=0;i<usersnum;i++) {
                            std::string user;
                            ch.getLine(user);

                            list.append(user + " ");
                        }
                        char STAM [1];
                        ch.getBytes(STAM,1);

                        std::cout << opodeString + " " + std::to_string(MessageOP)+" " + std::to_string(usersnum)+" "+list << std::endl;
                        break;
                    }
                    case 5: {
                        std::cout << opodeString + " " + std::to_string(MessageOP) << std::endl;
                        break;
                    }
                    case 6: {
                        std::cout << opodeString + " " + std::to_string(MessageOP) << std::endl;
                        break;
                    }
                    case 7: {// same as 3
                        char UsersNum [2];
                        ch.getBytes(UsersNum,2);
                        int usersnum=bytesToShort(UsersNum);
                        std::string list;
                        for(int i=0;i<usersnum;i++) {
                            std::string user;
                            ch.getLine(user);
                            list.append(user + " ");
                        }

                        char STAM [1];
                        ch.getBytes(STAM,1);

                        std::cout << opodeString + " " + std::to_string(MessageOP)+" " + std::to_string(usersnum)+" "+list << std::endl;
                        break;
                    }

                    case 8: {
                        char numOfPosts [2];
                        ch.getBytes(numOfPosts,2);
                        int numOfPosts_int=bytesToShort(numOfPosts);

                        char numOfFollowers [2];
                        ch.getBytes(numOfFollowers,2);
                        int numOfFollowers_int=bytesToShort(numOfFollowers);

                        char numOfFollowing [2];
                        ch.getBytes(numOfFollowing,2);
                        int numOfFollowing_int=bytesToShort(numOfFollowing);

                        std::cout << opodeString + " " + std::to_string(MessageOP) + " " + std::to_string(numOfPosts_int)+ " " + std::to_string(numOfFollowers_int)+ " " + std::to_string(numOfFollowing_int) << std::endl;
                        break;
                    }
                }
                break;
            }

            case 11: {

                std::string opodeString = "ERROR";
                char MessageOpcode[2];
                ch.getBytes(MessageOpcode, 2);
                short MessageOP = ch.bytesToShort(MessageOpcode);

                std::cout << opodeString + " " + std::to_string(MessageOP) << std::endl;
                break;
            }

        }
        stop.store(false);

}
short Decoder::bytesToShort(char* bytesArr)
{
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}
