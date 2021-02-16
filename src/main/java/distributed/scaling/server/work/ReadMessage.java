package cs455.scaling.server.work;

import com.sun.org.apache.bcel.internal.generic.Select;
import cs455.scaling.server.Server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class ReadMessage extends Work
{
    private final ByteBuffer receiveBuffer = ByteBuffer.allocate(10192);

    public ReadMessage(SelectionKey key, Server server, int batchSize, int batchTime)
    {
        this.key = key;
        this.server = server;
        this.batchSize = batchSize;
        this.batchTime = batchTime;
    }

    @Override
    public void run()
    {
        SocketChannel socketToClient = (SocketChannel) key.channel();
        try
        {
            socketToClient.configureBlocking(false);
            socketToClient.register(server.getMySelector(), SelectionKey.OP_READ);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        receiveBuffer.clear();
        byte[] messageBytes = new byte[8192];
        readBufferTillBatchSize(messageBytes, socketToClient);
        key.interestOps(SelectionKey.OP_READ);
        server.getMySelector().wakeup();

    }

    private void readBufferTillBatchSize(byte[] messageBytes, SocketChannel socketToClient)
    {
        try
        {
            int num = 0;
            while (num < 1)
            {
                num = socketToClient.read(receiveBuffer);
            }
            receiveBuffer.rewind();
            receiveBuffer.get(messageBytes);
        }
        catch (IOException e)
        {
            System.out.println("ReadMessage: Error while receiving message");
            e.printStackTrace();
        }

        server.threadPoolManager.addWork(new HashMessage(key, server, messageBytes));
    }
}
