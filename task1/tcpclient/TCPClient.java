package tcpclient;
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;

public class TCPClient {

    public TCPClient() {
    }
    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {
        try {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Socket clientSocket = new Socket(hostname, port);
        byte[] fromServerBuffer = new byte[1024];

        int fromUserLength = toServerBytes.length;
        clientSocket.getOutputStream().write(toServerBytes,0, fromUserLength);

        int fromServerLength = clientSocket.getInputStream().read(fromServerBuffer);
        buffer.write(fromServerBuffer,0,fromServerLength);

        return buffer.toByteArray();

        }
        catch(IOException ex){
            System.out.println("error with connection" + ex);
        }
        return new byte[0];
    }
}
