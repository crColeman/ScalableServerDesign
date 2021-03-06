package cs455.scaling.server.work;

import cs455.scaling.hash.Hash;
import cs455.scaling.server.Server;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class HashMessage extends Work
{
    private final byte[] messagePayload;
    public HashMessage(SelectionKey key, Server server, byte[] messagePayload)
    {
        this.key = key;
        this.server = server;
        this.messagePayload = messagePayload;
    }



    @Override
    public void run()
    {
        System.out.println("Hash");
        String hash = Hash.SHA1FromBytes(messagePayload);
//        key.interestOps(SelectionKey.OP_READ);
        new ReplyMessage(key, server, hash).run();


    }
}
