import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class MyClient {
    Socket socket;
    DataInputStream dis;
    DataOutputStream dos;
    public void  startClient() {
        try {
            socket = new Socket("127.0.0.1", 3000);
            dis = new DataInputStream(socket.getInputStream()); 
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            System.out.println("Err connect to Server: " +  e.getMessage());
        }
    }
    public static void main(String[] args) {
        MyClient  client = new MyClient();
        client.startClient();
        client.Register();
    }
    public void Login(){
        try {
            dos.writeUTF("Login");
            dos.writeUTF("102220011");
            dos.writeUTF("123");
            String message = dis.readUTF();
            System.out.println("Received from server: " + message);
        } catch (Exception e) {
            System.out.println("Login Err: " + e.getMessage());
        }
    }
    public void Register(){
        try {
            dos.writeUTF("Register");
            dos.writeUTF("102220030");
            dos.writeUTF("Nguyen Van A");
            dos.writeUTF("22T_DT2");
            dos.writeUTF("123");
            String message = dis.readUTF();
            System.out.println("Received from server: " + message);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
