import java.net.*;
import java.io.*;

public class TCPClient {
    public boolean shutdown;
    public Integer timeout;
    public Integer limit;

    public TCPClient(boolean shutdown, Integer timeout, Integer limit) {
        this.shutdown = shutdown;
        this.timeout = timeout;
        this.limit = limit;
    }

    public byte[] askServer(String hostname, int port)throws IOException{
        String toServer = "";
        byte[] toServerBytes = toServer.getBytes();
        return askServer(hostname,port,toServerBytes);
    }

    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            Socket clientSocket = new Socket(hostname, port);

            if(timeout != null)
                clientSocket.setSoTimeout(timeout);
            //Send data
            clientSocket.getOutputStream().write(toServerBytes);
            if(shutdown)
                clientSocket.shutdownOutput();


            //Collect data, read return -1 when data is done
            try{
                int serverData = (clientSocket.getInputStream().read());
                if (limit != null && limit == 1)
                    clientSocket.close();
                while (serverData != -1) {
                    buffer.write(serverData);
                    serverData = (clientSocket.getInputStream().read());

                    if(limit != null && clientSocket.getInputStream().available() == 0)
                        clientSocket.close();
                    if(limit != null && limit == buffer.size()+1)
                        clientSocket.close();
                }
            }catch(IOException ex){ //TimeoutException
                clientSocket.close();
            }
            return buffer.toByteArray();
        }
        catch(IOException ex){
            System.out.println("error with connection: " + ex);
        }


        return new byte[0];
    }
}