import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MyClient {
    public static void main(String[] args) {

        // Kết nối đến máy chủ
        try 
        {
            Socket socket = new Socket("127.0.0.1", 3000);
            DataInputStream input = new DataInputStream(socket.getInputStream()); 
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            dos.writeUTF("Login");
            dos.writeUTF("102220011");
            dos.writeUTF("12");
            // Nhận dữ liệu từ máy chủ?
            String message = input.readUTF();
            System.out.println("Received from server: " + message);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
