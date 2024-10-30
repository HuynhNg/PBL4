package org.pbl4.java;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import org.apache.commons.net.ftp.FTPClient;

public class MyServer {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(3000);
        System.out.println("Server is on port 3000...");
        
        while (true) {
            try {
                Socket socket = server.accept();
                System.out.println(socket.getInetAddress() + " Client connected");
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
            switch (message) {
                case "Login":
                    Login();
                    break;
                case "Register":
                    Register();
                    break;
                case "UpdatePassword":
                	UpdatePassword();
                	break;
                default:
                    System.out.println("Unknown command: " + message);
                    break;
            }
        } catch (IOException e) {
            System.out.println("Error handling client: " + e.getMessage());
        }
    }

    public void Login(){
        try {
            // Read MSSV and password from client
            String MSSV = dis.readUTF();
            String password = dis.readUTF();
            Model md = new Model();

            
            // Check login credentials
            if (md.CheckLogin(MSSV, password)) {
                dos.writeUTF("Login Success");
            } else {
                dos.writeUTF("Login Failed");
            }
        } catch (IOException e) {
            System.out.println("Error in Login: " + e.getMessage());
        }
    }

    public void Register(){
        try {
            String MSSV = dis.readUTF();
            String Name = dis.readUTF();
            String Class = dis.readUTF();
            String password = dis.readUTF();
            Model md = new Model();
            if (md.CheckMSSV(MSSV)) {
                dos.writeUTF("MSSV already exists");
                return;
            }
            if (!md.Register(MSSV, password, Name, Class)) {
                dos.writeUTF("Register failed");
                return;
            }
            CreateFolder(MSSV);
//            CreateUser(MSSV, password);
            dos.writeUTF("Register Success");

        } catch (IOException e) {
            System.out.println("Error in Register: " + e.getMessage());
        }
    }

    public void CreateFolder(String MSSV) {
        try {
            String server = "localhost"; 
            int port = 21;               
            String user = "admin";  
            String pass = "00000000"; // Đổi thành mật khẩu của bạn

            FTPClient ftpClient = new FTPClient();
            // Kết nối đến máy chủ FTP
            ftpClient.connect(server, port);
            boolean login = ftpClient.login(user, pass);

            if (login) {
                String newDirPath = "/" + MSSV; // Tạo thư mục theo tên MSSV

                boolean directoryCreated = ftpClient.makeDirectory(newDirPath);

                if (directoryCreated) {
                    System.out.println("Create Folder successfully: " + newDirPath);
                } else {
                    System.out.println("Create folder failder");
                }

                // Đóng kết nối
                ftpClient.logout();
            } else {
                System.out.println("Login successfully");
            }

            ftpClient.disconnect();

        } catch (Exception e) {
            System.out.println("Error in CreateFolder: " + e.getMessage());
        }
    }
//
//    public void CreateUser(String MSSV, String Password) {
//        try {
//            String FILEZILLA_CONFIG_PATH = "C:\\xampp\\FileZillaFTP\\FileZilla Server.xml";
//            
//            // Đọc nội dung hiện tại của file
//            String content = new String(Files.readAllBytes(Paths.get(FILEZILLA_CONFIG_PATH)));
//
//            // Tạo nội dung người dùng mới
//            String newUser = "    <User Name=\"" + MSSV + "\">\n" +
//                "        <Option Name=\"Pass\">" + Password + "</Option>\n" +
//                "        <Option Name=\"Group\">Work</Option>\n" + // Thêm thuộc tính Group
//                "        <Option Name=\"Bypass server userlimit\">0</Option>\n" +
//                "        <Option Name=\"User Limit\">0</Option>\n" +
//                "        <Option Name=\"IP Limit\">0</Option>\n" +
//                "        <Option Name=\"Enabled\">1</Option>\n" +
//                "        <Option Name=\"Comments\"></Option>\n" +
//                "        <Option Name=\"ForceSsl\">0</Option>\n" +
//                "        <IpFilter>\n" +
//                "            <Disallowed />\n" +
//                "            <Allowed />\n" +
//                "        </IpFilter>\n" +
//                "        <Permissions>\n" +
//                "            <Permission Dir=\"D:\\2024\\PBL4\\FileData\\" + MSSV + "\">\n" +
//                "                <Option Name=\"FileRead\">1</Option>\n" +
//                "                <Option Name=\"FileWrite\">1</Option>\n" +
//                "                <Option Name=\"FileDelete\">1</Option>\n" +
//                "                <Option Name=\"FileAppend\">0</Option>\n" +
//                "                <Option Name=\"DirCreate\">1</Option>\n" +
//                "                <Option Name=\"DirDelete\">1</Option>\n" +
//                "                <Option Name=\"DirList\">1</Option>\n" +
//                "                <Option Name=\"DirSubdirs\">1</Option>\n" +
//                "                <Option Name=\"IsHome\">1</Option>\n" +
//                "                <Option Name=\"AutoCreate\">0</Option>\n" +
//                "            </Permission>\n" +
//                "        </Permissions>\n" +
//                "        <SpeedLimits DlType=\"0\" DlLimit=\"10\" ServerDlLimitBypass=\"0\" UlType=\"0\" UlLimit=\"10\" ServerUlLimitBypass=\"0\">\n" +
//                "            <Download />\n" +
//                "            <Upload />\n" +
//                "        </SpeedLimits>\n" +
//                "    </User>\n";
//
//            // Chèn người dùng mới vào cuối danh sách các người dùng
//            String modifiedContent = content.replace("</Users>", newUser + "</Users>");
//            
//            // Ghi lại nội dung mới vào file
//            Files.write(Paths.get(FILEZILLA_CONFIG_PATH), modifiedContent.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
//
//            System.out.println("User " + MSSV + " đã được thêm vào FileZilla Server.");
//
//        } catch (Exception e) {
//            System.out.println("Error in CreateUser: " + e.getMessage());
//        }
//    }
    public void UpdatePassword() {
    	try {
			String MSSV = dis.readUTF();
			String Password = dis.readUTF();
			Model md = new Model();
			if(!md.CheckMSSV(MSSV)) {
				dos.writeUTF("MSSV not found");
				return;
			}
			if(!md.UpdatePassword(MSSV, Password)) {
				dos.writeUTF("Update password is not succesful");
				return;
			}
			dos.writeUTF("Update password successfully");
		} catch (Exception e) {
			System.out.println("Error in Update password: " + e.getMessage());
		}
    }
    
    
    public void GetAllFileName() {
    	try {
			String MSSV = dis.readUTF();
			Model md = new Model();
			if(!md.CheckMSSV(MSSV)) {
				dos.writeUTF("MSSV not found");
				return;
			}
			String FileName = md.GetAllFileNameByMSSV(MSSV);
			if(!FileName.equals("ERR")) {
				dos.writeUTF("Get filename is not successful");
				return;
			}
			dos.writeUTF(FileName);
			
		} catch (Exception e) {
			System.out.println("Error in Update password: " + e.getMessage());
		}
    }

}
