package cs455.scaling.server.work;

import com.sun.org.apache.bcel.internal.generic.Select;
import cs455.scaling.server.Server;

import java.io.IOException;
import java.nio.ByteBuffer;
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
        receiveBuffer.clear();
        byte[] messageBytes = null;



    }

    private void readBufferTillBatchSize(byte[] messageBytes, SocketChannel socketToClient)
    {
        while (messageBytes.length < batchSize)
        {
            try
            {
                socketToClient.read(receiveBuffer);
                receiveBuffer.get(messageBytes);
                System.out.println(messageBytes);
            }
            catch (IOException e)
            {
                System.out.println("ReadMessage: Error while receiving message");
                e.printStackTrace();
            }
        }
        server.getMySelector().wakeup();
    }
}
