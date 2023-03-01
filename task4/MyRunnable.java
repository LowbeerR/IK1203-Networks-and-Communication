import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class MyRunnable implements Runnable {
    //private int serverport = 0;
    private boolean shutdown = false;
    private Integer timeout = null;
    private Integer limit = null;
    private String hostname = null;
    private Socket socket = null;
    private int port = 0;
    private byte[] userInputBytes = new byte[0];

    public MyRunnable(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {

        byte[] fromClient = new byte[2048];
        int index = 0;
        try {
            socket.getInputStream().read(fromClient);
            for (int i = 0; i < 2048; i++) {
                if (fromClient[i] != 0)
                    index++;
            }

            byte[] fromClient2 = new byte[index];
            System.arraycopy(fromClient, 0, fromClient2, 0, index);

            String fullURL = new String(fromClient2, StandardCharsets.UTF_8);

            String[] url = fullURL.split(" ");

            if (!url[1].contains("/ask?hostname=") || !url[2].contains("HTTP/1.1") || !url[0].contains("GET")) {
                String ret = "HTTP/1.1 400 Bad Request\r\n";
                socket.getOutputStream().write(ret.getBytes(StandardCharsets.UTF_8));
                socket.close();
                // throw new Exception("400 Bad Request");
            } else {
                String[] url2 = url[1].split(new String("/ask?"));
                if (url2[1].contains("&")) {
                    String[] parse = url2[1].split("&");
                    for (String parameter : parse) {
                        String[] Values = parameter.split("=");

                        switch (Values[0]) {
                            case "?hostname":
                                hostname = Values[1];
                                break;
                            case "port":
                                port = Integer.parseInt(Values[1]);
                                break;
                            case "limit":
                                limit = Integer.parseInt(Values[1]);
                                break;
                            case "shutdown":
                                shutdown = true;
                                break;
                            case "timeout":
                                timeout = Integer.parseInt(Values[1]);
                                break;
                            case "string":
                                userInputBytes = Values[1].getBytes(StandardCharsets.UTF_8);
                                break;
                        }
                    }
                }

                if (hostname == null || port == 0) {
                    String ret = "HTTP/1.1 400 Bad Request\r\n";
                    socket.getOutputStream().write(ret.getBytes(StandardCharsets.UTF_8));
                    socket.close();
                    throw new Exception("400 Bad Request");
                }


                TCPClient tcpClient = new TCPClient(shutdown, timeout, limit);
                byte[] serverBytes = tcpClient.askServer(hostname, port, userInputBytes);
                if (serverBytes.length == 0) {
                    String ret = "HTTP/1.1 404 Not Found\r\n";
                    socket.getOutputStream().write(ret.getBytes(StandardCharsets.UTF_8));
                    socket.close();
                    throw new Exception("404 Not Found");
                } else {
                    String ret = "HTTP/1.1 200 OK\r\n\r\n";
                    socket.getOutputStream().write(ret.getBytes(StandardCharsets.UTF_8));
                    socket.getOutputStream().write(serverBytes);
                    socket.close();
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

