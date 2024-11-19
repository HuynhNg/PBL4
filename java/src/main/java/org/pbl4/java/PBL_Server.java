package org.pbl4.java;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class PBL_Server {

	public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(3000);
        System.out.println("Server is on port 3000...");
        
        while (true) {
            try {
                Socket socket = server.accept();
                System.out.println(socket.getInetAddress() + " Client connected");
                PBLServer tmp = new PBLServer(socket);
                tmp.start();
            } catch (IOException e) {
                System.out.println("Error while accepting client connection: " + e.getMessage());
            }
        }
    }
}

class PBLServer extends Thread{
	private Socket socket;
	private DataInputStream dis;
	public PBLServer(Socket socket) throws IOException {
        this.socket = socket;
        this.dis = new DataInputStream(socket.getInputStream());
    }
	public void run() {
        try {
            String message = dis.readUTF();
            System.out.println("Received from client: " + message);

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
                case "GetInformation":
                	GetInformation();
                	break;
                case "UpdateInformation":
                	UpdateInformation();
                	break;
                case "GetAllFileName":
                	GetAllFileName();
                	break;
                case "GetMyFileName":
                	GetMyFileName();
                	break;
                case "GetGuestFileName":
                	GetGuestFileName();
                	break;
                case "GetAllByFolderID":
                	GetAllByFolderID();
                	break;
                case "CreateFolder":
                	CreateFolder();
                	break;
                case "ChangeFolderName":
                	ChangeFolderName();
                	break;
                case "DeleteFolder":
                	DeleteFolder();
                	break;
//                case "UploadFile":
//                	UploadFile();
//                	break;
//                case "DelFile":
//                	DelFile();
//                	break;
//                case "DownloadFile":
//                	DownloadFile();
//                	break;
//                case "AddGuest":
//                	AddGuest();
//                	break;
//                case "DelGuest":
//                	DelGuest();
//                	break;
//                case "GetAllGuest":
//                	GetAllGuest();
//                	break;
                default:
                    System.out.println("Unknown command: " + message);
                    break;
            }
        } catch (IOException e) {
            System.out.println("Error handling client: " + e.getMessage());
        }finally {
            try {
                if (dis != null) dis.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }
	
	public void Login() {
		try {
			PBL_Controller ctl = new PBL_Controller(socket);
			ctl.Login();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
	}
	
	public void Register() {
		try {
			PBL_Controller ctl = new PBL_Controller(socket);
			ctl.Register();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
	}
	
	public void UpdatePassword() {
		try {
			PBL_Controller ctl = new PBL_Controller(socket);
			ctl.UpdatePassword();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
	}
	
	public void GetInformation() {
		try {
			PBL_Controller ctl = new PBL_Controller(socket);
			ctl.GetInformation();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
	}
	
	public void UpdateInformation() {
		try {
			PBL_Controller ctl = new PBL_Controller(socket);
			ctl.UpdateInformation();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
	}
	
	public void GetAllFileName() {
		try {
			PBL_Controller ctl = new PBL_Controller(socket);
			ctl.GetAllFileName();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } 
	}
	
	public void GetMyFileName() {
		try {
			PBL_Controller ctl = new PBL_Controller(socket);
			ctl.GetMyFileName();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
	}
	
	public void GetGuestFileName() {
		try {
			PBL_Controller ctl = new PBL_Controller(socket);
			ctl.GetGuestFileName();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
	}
	
	public void GetAllByFolderID() {
		try {
			PBL_Controller ctl = new PBL_Controller(socket);
			ctl.GetAllByFolderID();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
	}
	
	public void CreateFolder() {
		try {
			PBL_Controller ctl = new PBL_Controller(socket);
			ctl.CreateFolder();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
	}
	
	public void ChangeFolderName() {
		try {
			PBL_Controller ctl = new PBL_Controller(socket);
			ctl.ChangeFolderName();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
	}
	
	public void DeleteFolder() {
		try {
			PBL_Controller ctl = new PBL_Controller(socket);
			ctl.DeleteFolder();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
	}
}