package cs455.scaling.server;

import cs455.scaling.server.work.AcceptConnection;
import cs455.scaling.server.work.ReadMessage;
import cs455.scaling.server.work.Work;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;


public class Server
{
    private final InetSocketAddress serverConnectionAddress;

    private Selector mySelector;
    private final ServerSocketChannel serverSocketChannel;

    private final AtomicInteger clientConnectionCount = new AtomicInteger(0);
    private final AtomicInteger replySentCount = new AtomicInteger(0);
//    private final


    public final ThreadPoolManager threadPoolManager;
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
        new Thread(new ServerStatistics(this)).start();
        ConcurrentLinkedQueue<Work> newBatch = new ConcurrentLinkedQueue<>();
        while (true)
        {
            try
            {
                mySelector.selectNow();
                Set<SelectionKey> selectedKeys = mySelector.selectedKeys();
                iterateKeys(selectedKeys, newBatch);
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
    private void iterateKeys(Set<SelectionKey> keySet, ConcurrentLinkedQueue<Work> newBatch)
    {
        Iterator<SelectionKey> keyIterator = keySet.iterator();

        while (keyIterator.hasNext())
        {
            if (newBatch.size() == batchSize)
            {
                threadPoolManager.addWork(newBatch);
                newBatch = new ConcurrentLinkedQueue<>();
            }

            SelectionKey key = keyIterator.next();
            keyIterator.remove();
            if (!key.isValid())
            {
                System.out.println("Invalid Key");
                try
                {
                    key.channel().close();
                    decrementClientConnectionCount();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                continue;
            }

            if (key.isAcceptable())
            {
                key.interestOps(key.interestOps() & ~SelectionKey.OP_ACCEPT);
                newBatch.add(new AcceptConnection(key, this));
                threadPoolManager.addWork(newBatch);
//                keyIterator.remove();
                continue;

            }else if (key.isReadable())
            {
                key.interestOps(SelectionKey.OP_WRITE);
//                serverSocketChannel.register(mySelector, key.interestOps() & ~SelectionKey.OP_READ);
//                System.out.println("Add Read Message");
                newBatch.add(new ReadMessage(key, this, batchSize, batchTime));
            }
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

    public void incrementReplySentCount()
    {
        replySentCount.incrementAndGet();
    }

    public int getReplySentCount()
    {
        return replySentCount.get();
    }

    public synchronized Selector getMySelector()
    {
        return mySelector;
    }

    public ServerSocketChannel getServerSocketChannel()
    {
        return serverSocketChannel;
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
