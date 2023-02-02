package tcpclient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPClient {

    public TCPClient() {
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
            //Send data
            clientSocket.getOutputStream().write(toServerBytes);
            //Collect data, read return -1 when data is done
            int serverData = (clientSocket.getInputStream().read());
            while (serverData != -1) {
                buffer.write(serverData);
                serverData = (clientSocket.getInputStream().read());
            }
            return buffer.toByteArray();
        }
        catch(IOException ex){
            System.out.println("error with connection" + ex);
        }
        return new byte[0];
    }
}
