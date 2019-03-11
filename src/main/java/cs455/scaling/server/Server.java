package cs455.scaling.server;

import cs455.scaling.server.work.AcceptConnection;
import cs455.scaling.server.work.ReadMessage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;


public class Server
{
    private final InetSocketAddress serverConnectionAddress;

    private Selector mySelector;
    private final ServerSocketChannel serverSocketChannel;

    private final AtomicInteger clientConnectionCount = new AtomicInteger(0);
    private final ThreadPoolManager threadPoolManager;
    private final int batchSize;
    private final int batchTime;
    /**
     * @param serverPort
     * @param threadPoolSize
     * @param batchSize
     * @param batchTime
     * @throws IOException
     */
    public Server(int serverPort, int threadPoolSize, int batchSize, int batchTime) throws IOException
    {
        serverConnectionAddress = new InetSocketAddress(serverPort);
        serverSocketChannel = ServerSocketChannel.open().bind(serverConnectionAddress);
        threadPoolManager = new ThreadPoolManager(threadPoolSize, batchSize, batchTime);
        initializeServer();
        threadPoolManager.initializeThreadPool();
        this.batchTime = batchTime;
        this.batchSize = batchSize;
    }

    /**
     * @throws IOException
     */
    private void initializeServer() throws IOException
    {
        mySelector = Selector.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(mySelector, SelectionKey.OP_ACCEPT);
    }

    private void run()
    {
        while (true)
        {
            try
            {
                mySelector.selectNow();
                Set<SelectionKey> selectedKeys = mySelector.selectedKeys();
                iterateKeys(selectedKeys);
            }
            catch (Exception e)
            {
                System.out.println("Server Run: Error" + e);
                break;
            }
            mySelector.wakeup();
        }
    }


    /**
     * @param keySet
     */
    private void iterateKeys(Set<SelectionKey> keySet) throws ClosedChannelException
    {
        Iterator<SelectionKey> keyIterator = keySet.iterator();

        while (keyIterator.hasNext())
        {
            SelectionKey key = keyIterator.next();
            if (!key.isValid())
            {
                System.out.println("Valid Key");
                continue;
            }

            if (key.isAcceptable())
            {
                System.out.println("Key Acceptable");
                serverSocketChannel.register(mySelector, key.interestOps() & ~SelectionKey.OP_ACCEPT);
                threadPoolManager.addWork(new AcceptConnection(key, this));
            }else if (key.isReadable())
            {
                System.out.println("Receiving Message");
                serverSocketChannel.register(mySelector, key.interestOps() & ~SelectionKey.OP_READ);
                threadPoolManager.addWork(new ReadMessage(key, this, batchSize, batchTime));
            }

            keyIterator.remove();
        }
    }


       ///////////////////////////
      /////Connection Count://///
     /////Accessor/Mutator//////
    ///////////////////////////

    /**
     * @return Current Client Connections
     */
    public int getClientConnectionCount()
    {
        return clientConnectionCount.get();
    }

    public void incrementClientConnectionCount()
    {
        clientConnectionCount.incrementAndGet();
    }

    public void decrementClientConnectionCount()
    {
        clientConnectionCount.decrementAndGet();
    }

    public Selector getMySelector()
    {
        return mySelector;
    }

      //////////////////////////////
     /////Command Line Input///////
    //////////////////////////////

    public static void printUsage()
    {
        System.out.println("USAGE: cs455.scaling.server.Server [portnum] [thread-pool-size] [batch-size] [batch-time]" +
                "\n- [portnum] int" +
                "\n- [thread-pool-size] int" +
                "\n- [batch-size] int" +
                "\n- [batch-time] int");
    }

    private static int parseInt(String varName, String num)
    {
        int parsedNum=-1;
        try
        {
            parsedNum = Integer.parseInt(num);
            if(parsedNum < 0)
            {
                throw new NumberFormatException("Variable must be greater than or equal to 0. A port request of 0 will auto assign an open port\n");
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

    public static void main(String[] args) throws IOException
    {
        if (args.length != 4)
        {
            System.out.println("Error: 4 Required Arguments");
            printUsage();
            System.exit(1);
        }
        Server server = new Server(parseInt("portnum", args[0]), parseInt("thread-pool-size", args[1]), parseInt("batch-size", args[2]), parseInt("batch-time", args[3]));
        server.run();


    }
}
