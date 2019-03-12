package cs455.scaling.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Client
{
    final ConcurrentLinkedQueue<String> clientSideHashCodes = new ConcurrentLinkedQueue<>();
    private final SocketChannel socketChannel;
    private final AtomicInteger sentPacketCount = new AtomicInteger(0);
    private final ByteBuffer receiveBuffer = ByteBuffer.allocate(8192);

    private final int packetRate;
    private final int packetDelay;


    public Client(String serverHost, int serverPort, int messageRate) throws IOException
    {
        packetRate = messageRate;
        packetDelay = 1000 / packetRate;

        socketChannel = SocketChannel.open(new InetSocketAddress(serverHost, serverPort));
        socketChannel.configureBlocking(false);
        do
        {
            if (socketChannel.isConnected())
            {
                System.out.println("Client Connected");
                break;
            }
        }while (!socketChannel.finishConnect());
        /** Adapted from connect() example: http://tutorials.jenkov.com/java-nio/socketchannel.html **/
    }



    public void run()
    {
        while (true)
        {
            receiveBuffer.clear();
            byte[] hashResponseBytes = new byte[8192];

        }
    }

    private void initialize()
    {

        new Thread(new SenderThread(this, packetDelay)).start();
//        new Thread(new ClientStatistics()).start();
    }





    public void incrementSentPacketCount()
    {
        sentPacketCount.incrementAndGet();
    }

    public int getSentPacketCount()
    {
        return sentPacketCount.get();
    }
    SocketChannel getSocketChannel()
    {
        return socketChannel;
    }


    public static void printUsage()
    {
        System.out.println("USAGE: cs455.scaling.server.Server [server-host] [server-port] [message-rate]" +
                "\n- [server-host] int" +
                "\n- [server-port] int" +
                "\n- [message-rate] int");
    }

    private static int parseInt(String varName, String num)
    {
        int parsedNum=-1;
        try
        {
            parsedNum = Integer.parseInt(num);
            if(parsedNum < 0)
            {
                throw new NumberFormatException("Invalid Integer Parameters Passed\n");
            }
        }
        catch (NumberFormatException e)
        {
            System.out.printf("Error: Incorrect Input Argument Format: %s: User Entered: %s\nException: %s", varName,num, e);
            printUsage();
            System.exit(1);
        }
        return parsedNum;
    }

    public static void main(String[] args)
    {
        if (args.length != 3)
        {
            System.out.println("Error: 3 Required Arguments");
            printUsage();
        }

        Client client = null;
        try
        {
            client = new Client(args[0], parseInt("portnum", args[1]), parseInt("message-rate", args[2]));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        client.initialize();
        client.run();

    }
}
