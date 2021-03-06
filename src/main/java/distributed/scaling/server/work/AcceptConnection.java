package cs455.scaling.server.work;

import cs455.scaling.server.Server;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class AcceptConnection extends Work
{

    public AcceptConnection(SelectionKey key, Server server)
    {
        this.key = key;
        this.server = server;
    }
    @Override
    public void run()
    {
        try
        {
            acceptConnection();
            key.channel().register(server.getMySelector(), SelectionKey.OP_ACCEPT);

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void acceptConnection() throws IOException
    {
        System.out.println("Accept Connection");
        SocketChannel clientChannel = ((ServerSocketChannel)key.channel()).accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(server.getMySelector(), SelectionKey.OP_READ);
        server.incrementClientConnectionCount();
        server.getMySelector().wakeup();
    }
}
