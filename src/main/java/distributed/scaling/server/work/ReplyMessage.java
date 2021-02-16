package cs455.scaling.server.work;

import cs455.scaling.server.Server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class ReplyMessage extends Work
{
    private final String hash;
    public ReplyMessage(SelectionKey key, Server server, String hash)
    {
        this.key = key;
        this.server = server;
        this.hash = hash;
    }
    @Override
    public void run()
    {
        SocketChannel socketToClient = (SocketChannel)key.channel();
        ByteBuffer sendBuffer = ByteBuffer.wrap(hash.getBytes());
        sendBuffer.flip();

        try
        {
            socketToClient.configureBlocking(false);
            socketToClient.register(server.getMySelector(), SelectionKey.OP_WRITE);
            while(sendBuffer.hasRemaining())
            {
                socketToClient.write(sendBuffer);
            }
            key.interestOps(SelectionKey.OP_READ);

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
//        server.getMySelector().wakeup();

    }
}
