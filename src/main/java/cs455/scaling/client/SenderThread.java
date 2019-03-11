package cs455.scaling.client;

import cs455.scaling.hash.Hash;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;

public class SenderThread implements Runnable
{
    private final Client client;
    private final ByteBuffer sendBuffer;
    private final int packetDelay;


    public SenderThread(Client client, int packetDelay)
    {
        this.packetDelay = packetDelay;
        this.client = client;
        sendBuffer = ByteBuffer.allocate(8192);
    }

    public void run()
    {
        Random randomPacketPayloadGenerator = new Random();
        while (true)
        {
            byte[] payload = new byte[8192];
            randomPacketPayloadGenerator.nextBytes(payload);
            sendBuffer.clear();
            sendBuffer.put(payload);
            sendBuffer.flip();
            writeBuffer();

            client.clientSideHashCodes.add(new Hash(payload).getHash());
            try
            {
                Thread.sleep(packetDelay);
            }
            catch (InterruptedException e)
            {
                System.out.println("Sender Thread Interrupter: " + e);
                System.exit(1);
            }
        }
    }

    private void writeBuffer()
    {
        System.out.println("Writing Packet to Server");
        while (sendBuffer.hasRemaining())
        {
            try
            {
                client.getSocketChannel().write(sendBuffer);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                System.exit(-1);
            }
        }
        client.incrementSentPacketCount();
    }

}
