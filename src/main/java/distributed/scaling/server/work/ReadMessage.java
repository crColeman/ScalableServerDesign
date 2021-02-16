package cs455.scaling.server.work;

import com.sun.org.apache.bcel.internal.generic.Select;
import cs455.scaling.server.Server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;

public class ReadMessage extends Work
{
    private final ByteBuffer receiveBuffer = ByteBuffer.allocate(10192);

    public ReadMessage(SelectionKey key, Server server, int batchSize, int batchTime)
    {
        this.key = key;
        this.server = server;
        this.batchSize = batchSize;
        this.batchTime = batchTime;
        this.dataPacketList = new LinkedBlockingQueue<Byte[]>(batchSize);

    }

    @Override
    public void run()
    {
        SocketChannel socketToClient = (SocketChannel) key.channel();
        receiveBuffer.clear();
        byte[] messageBytes = new byte[8192];
        try
        {
            socketToClient.configureBlocking(false);
            socketToClient.register(server.getMySelector(), SelectionKey.OP_READ);
            readBufferTillBatchSize(messageBytes, socketToClient);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        System.out.println("Read");
    }

    private void readBufferTillBatchSize(byte[] messageBytes, SocketChannel socketToClient) throws IOException
    {
            int num = 0;
            while (num < 8192)
            {
                num = socketToClient.read(receiveBuffer);
            }
            receiveBuffer.rewind();
            receiveBuffer.get(messageBytes);

        key.interestOps(SelectionKey.OP_READ);
        server.getMySelector().wakeup();
        new HashMessage(key, server, messageBytes).run();
    }
}
