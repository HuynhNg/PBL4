import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MyServer {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(3000);
        System.out.println("Server is on port 3000...");
        
        while (true) {
            try {
                Socket socket = server.accept();
                System.out.println(socket.getInetAddress() + "Client connected");
                Server tmp = new Server(socket);
                tmp.start();
            } catch (IOException e) {
                System.out.println("Error while accepting client connection: " + e.getMessage());
            }
        }
    }
}

class Server extends Thread {
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    public Server(Socket socket) throws IOException {
        this.socket = socket;
        this.dis = new DataInputStream(socket.getInputStream());
        this.dos = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            // Read client message
            String message = dis.readUTF();
            System.out.println("Received from client: " + message);

            // Respond based on message content
            if ("Login".equals(message)) {
                String MSSV = dis.readUTF();
                String password = dis.readUTF();
                Model md = new Model();
                if (md.CheckLogin(MSSV, password)) {
                    dos.writeUTF("Login Success");
                } else {
                    dos.writeUTF("Login Failed");
                }
            } else if("Register".equals(message)) {
                String MSSV = dis.readUTF();
                String Name = dis.readUTF();
                String Class = dis.readUTF();
                String password = dis.readUTF();
                Model md = new Model();
                if (md.Register(MSSV,password, Name, Class )) {
                    dos.writeUTF("Register successfully");
                }
                else{
                    dos.writeUTF("Register failed");
                }
            }
        } catch (IOException e) {
            System.out.println("Error handling client: " + e.getMessage());
        }
    }
}
