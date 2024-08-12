import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientApp {
    public static void main(String[] args) {
        try  {
            Socket socket = new Socket("localhost", 8082);
            new Handler(socket).start();
            new WriterThread(socket).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static class Handler extends Thread {
        private Socket socket;
        private BufferedReader bufferedReader;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String message;
                while ((message = bufferedReader.readLine()) != null) {
                    System.out.println("\nFrom Server : " + message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private static class WriterThread extends Thread {
        private Socket socket;
        private PrintWriter printWriter;
        private Scanner scanner;

        public WriterThread(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                printWriter = new PrintWriter(socket.getOutputStream(), true);
                scanner = new Scanner(System.in);
                String message;
                while (true) {
                    System.out.print("Write massage to server: ");
                    message = scanner.nextLine();
                    printWriter.println(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
