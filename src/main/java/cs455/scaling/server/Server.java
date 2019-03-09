package cs455.scaling.server;

import jdk.nashorn.internal.ir.LexicalContextNode;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Server
{
    private InetSocketAddress serverConnectionAddress;
    private WorkerThread workerThread;
    private Selector mySelector;
    private ServerSocketChannel serverSocketChannel;
    private int batchSize;
    private int batchTime;
    private int threadPoolSize;

    public Server(int serverPort, int threadPoolSize, int batchSize, int batchTime)
    {
        serverConnectionAddress = new InetSocketAddress(serverPort);
        this.batchSize = batchSize;
        this.batchTime = batchTime;
        this.threadPoolSize = threadPoolSize;
    }

    public void initialize() throws IOException
    {
        mySelector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open().bind(serverConnectionAddress);
        serverSocketChannel.configureBlocking(false);
        SelectionKey selectionKey = serverSocketChannel.register(mySelector, SelectionKey.OP_ACCEPT);
        selectionKey.attach(new ConnectionCreator());

    }

    class ConnectionCreator implements Runnable
    {
        public void run()
        {
            try
            {
                SocketChannel connection = serverSocketChannel.accept();
                if(connection.isConnected()){

                }
            }
            catch (IOException e)
            {
                System.out.println("Error occurred in ConnectionCreator:" + e);
            }
        }
    }
}
