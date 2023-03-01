import java.io.IOException;
import java.net.*;

public class ConcHTTPAsk {
    public static void main(String[] args) throws IOException {
        try {
            int serverport = Integer.parseInt(args[0]);
            ServerSocket welcomeSocket = new ServerSocket(serverport);
            System.out.println(welcomeSocket.getLocalPort());

            while (true) {
                Socket socket = welcomeSocket.accept();
                Runnable conc = new MyRunnable(socket);
                Thread th = new Thread(conc);
                th.start();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}





