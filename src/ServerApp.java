import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class ServerApp {
    private static Set<PrintWriter> clientWriters = new HashSet<>();

    public static void main(String[] args) {
        System.out.println("Server Up & waiting for clients....");
        new ServerWriterThread().start();
        try (ServerSocket serverSocket = new ServerSocket(8082)) {

            while (true) {
                new Handler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static class Handler extends Thread {
        private Socket socket;
        private PrintWriter printWriter;
        private BufferedReader bufferedReader;
        private String clientAddress;

        public Handler(Socket socket) {
            this.socket = socket;
            this.clientAddress = socket.getRemoteSocketAddress().toString();
        }

        @Override
        public void run() {
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                printWriter = new PrintWriter(socket.getOutputStream(), true);
                synchronized (clientWriters) {
                    clientWriters.add(printWriter);
                }
                String message;
                while ((message = bufferedReader.readLine()) != null) {
                    System.out.println("\nReceived from "+clientAddress+": " + message);
                    synchronized (clientWriters) {
                        for (PrintWriter writer : clientWriters) {
                            writer.write(message);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                synchronized (clientWriters) {
                    clientWriters.remove(printWriter);
                }
            }
        }
    }

    private static class ServerWriterThread extends Thread {
        private Socket socket;
        private PrintWriter printWriter;
        private Scanner scanner;


        public void run() {
            try (Scanner scanner = new Scanner(System.in)) {
                String message;
                while (true) {
                    System.out.print("Write message to clients: ");
                    message = scanner.nextLine();
                    synchronized (clientWriters) {
                        for (PrintWriter writer : clientWriters) {
                            writer.println(message);
                        }
                    }
                }

            }catch (Exception e) {
                // Log the exception and ensure the server continues to run
                e.printStackTrace();
            }


        }
    }
}
