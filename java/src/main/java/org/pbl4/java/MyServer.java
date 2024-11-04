package org.pbl4.java;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
                case "GetInformation":
                	GetInformation();
                	break;
                case "UpdateInformation":
                	UpdateInformation();
                	break;
                case "GetData":
                	GetData();
                	break;
                case "UpdateData":
                	UpdateData();
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
                case "UploadFile":
                	UploadFile();
                	break;
                case "DelFile":
                	DelFile();
                	break;
                case "DownloadFile":
                	DownloadFile();
                	break;
                case "AddGuest":
                	AddGuest();
                	break;
                case "DelGuest":
                	DelGuest();
                	break;
                case "GetAllGuest":
                	GetAllGuest();
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
            String MSSV = dis.readUTF();
            String password = dis.readUTF();
            Model md = new Model();

            
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
            CreateFolder("",MSSV);
            dos.writeUTF("Register Success");

        } catch (IOException e) {
            System.out.println("Error in Register: " + e.getMessage());
        }
    }

    public void CreateFolder(String filepath, String filename) {
        try {
            String baseFolder = "D:\\2024\\PBL4\\FileData";
            String folderPath = Paths.get(baseFolder, filepath, filename).toString();
            
            File folder = new File(folderPath);
            System.out.println("Đang tạo thư mục cho: " + filename);
            
            if (!folder.exists()) {
                if (folder.mkdirs()) {
                    System.out.println("Thư mục đã được tạo thành công!");
                } else {
                    System.out.println("Không thể tạo thư mục.");
                }
            } else {
                System.out.println("Thư mục đã tồn tại.");
            }

        } catch (Exception e) {
            System.out.println("Lỗi trong CreateFolder: " + e.getMessage());
        }
    }

    public void UpdatePassword() {
    	try {
			String MSSV = dis.readUTF();
			String OldPassword = dis.readUTF();
			String Password = dis.readUTF();
			Model md = new Model();
			if(!md.CheckMSSV(MSSV)) {
				dos.writeUTF("MSSV not found");
				return;
			}
			if(!md.GetPassword(MSSV).equals(OldPassword)) {
				dos.writeUTF("Password is wrong");
				return ;
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
    public void GetInformation() {
    	try {
			String MSSV = dis.readUTF();
			Model md = new Model();
			if(!md.CheckMSSV(MSSV)) {
				dos.writeUTF("MSSV not found");
				return;
			}
			String Infor = md.GetInformation(MSSV);
			if(Infor.equals("ERR")) {
				dos.writeUTF("Get failed");
				return;
			}
			dos.writeUTF(Infor);
			
		} catch (Exception e) {
			System.out.println("Error in get information: " + e.getMessage());		
		}
    }
    public void UpdateInformation() {
    	try {
			String MSSV = dis.readUTF();
			String Name = dis.readUTF();
			String Class = dis.readUTF();
			Model md = new Model();
			if(!md.CheckMSSV(MSSV)) {
				dos.writeUTF("MSSV not found");
				return;
			}
			if(!md.UpdateInformation(MSSV, Name, Class)) {
				dos.writeUTF("Update inforamtion is not successfull");
				return;
			}
			dos.writeUTF("Update successfull");
		} catch (Exception e) {
			System.out.println("Error in Update information: " + e.getMessage());
		}
    }
    public void GetData() {
    	try {
			String MSSV = dis.readUTF();
			Model md = new Model();
			if(!md.CheckMSSV(MSSV)) {
				dos.writeUTF("MSSV not found");
				return;
			}
			String Data = md.GetData(MSSV);
			if(Data.equals("ERR")) {
				dos.writeUTF("Get data is not successful");
				return;
			}
			dos.writeUTF(Data);
			
		} catch (Exception e) {
			System.out.println("Error in get data: " + e.getMessage());
		}
    }
    public void UpdateData() {
    	try {
			String MSSV = dis.readUTF();
			Double Data = Double.parseDouble(dis.readUTF());
			Model md = new Model();
			if(!md.CheckMSSV(MSSV)) {
				dos.writeUTF("MSSV not found");
				return;
			}
			if(!md.UpdateData(MSSV, Data)) {
				dos.writeUTF("Update data is not successful");
				return;
			}
			dos.writeUTF("Update successfull");
			
		} catch (Exception e) {
			System.out.println("Error in get data: " + e.getMessage());
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
			System.out.print(FileName);
			if(FileName.equals("ERR")) {
				dos.writeUTF("Get filename is not successful");
				return;
			}
			dos.writeUTF(FileName);
			
		} catch (Exception e) {
			System.out.println("Error in Update password: " + e.getMessage());
		}
    }
    public void GetMyFileName() {
    	try {
    		String MSSV = dis.readUTF();
			Model md = new Model();
			if(!md.CheckMSSV(MSSV)) {
				dos.writeUTF("MSSV not found");
				return;
			}
			String Filename = md.GetMyFileName(MSSV);
			if(Filename.equals("ERR")) {
				dos.writeUTF("Get filename is not successful");
				return;
			}
			dos.writeUTF(Filename);
		} catch (Exception e) {
			System.out.println("Error in Update password: " + e.getMessage());
		}
    }
    public void GetGuestFileName() {
    	try {
    		String MSSV = dis.readUTF();
			Model md = new Model();
			if(!md.CheckMSSV(MSSV)) {
				dos.writeUTF("MSSV not found");
				return;
			}
			String Filename = md.GetGuestFileName(MSSV);
			if(Filename.equals("ERR")) {
				dos.writeUTF("Get filename is not successful");
				return;
			}
			dos.writeUTF(Filename);
		} catch (Exception e) {
			System.out.println("Error in Update password: " + e.getMessage());
		}
    }
    
    public static String zipFile(String filePath) {
        String zipFilePath = filePath + ".zip";
        try (FileOutputStream fos = new FileOutputStream(zipFilePath);
             ZipOutputStream zos = new ZipOutputStream(fos);
             FileInputStream fis = new FileInputStream(filePath)) {
        	ZipEntry zipEntry = new ZipEntry(new File(filePath).getName());

            zos.putNextEntry(zipEntry);

            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }

            zos.closeEntry();
            System.out.println("File đã được nén thành công: " + zipFilePath);

        } catch (IOException e) {
            System.out.println("Lỗi khi nén file: " + e.getMessage());
        }
        return zipFilePath;
    }
    
    public void sendfile(String FilePath) {
    	try {
            String zipFilePath = zipFile(FilePath); 
            File file = new File(zipFilePath);

            try (FileInputStream fis = new FileInputStream(file);
                 BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream())) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                }

                bos.flush();
                System.out.println("File đã được gửi tới client!");
            }
        } catch (Exception e) {
            System.out.println("Lỗi khi gửi file: " + e.getMessage());
        }
    }
    
    public void DownloadFile() {
    	try {
			String FileID = dis.readUTF();
			Model md = new Model();
			String MSSV = md.GetMSSVbyFileID(FileID);
			String FileName = md.GetFilenameByFileID(FileID);
			String FilePath = "D:\\2024\\PBL4\\FileData\\" + MSSV + "\\" + FileName ;
			sendfile(FilePath);
			
		} catch (Exception e) {
			 System.out.println("Lỗi khi gửi file: " + e.getMessage());
		}
    }
    
    public void UploadFile() {
    	try {
			String MSSV = dis.readUTF();
			String FileName = dis.readUTF();
			Double Data = Double.parseDouble(dis.readUTF());
			Model md = new Model();
			if(!md.CheckMSSV(MSSV)) {
				dos.writeUTF("MSSV not found");
				return;
			}
			if(!md.AddFile(FileName,Data)) {
				dos.writeUTF("Upload failed");
				return;
			}
			String FileID = md.GetFileID(FileName,MSSV);
			if(!md.AddAutho(MSSV, FileID, 0)) {
				while(!md.DelFile(FileID));
				dos.writeUTF("Upload failed");
				return;
			}
			String Da = md.GetData(MSSV);
			if(Da.equals("ERR")) {
				while(!md.DelFile(FileID));
				while(!md.DelAutho(MSSV, FileID));
				dos.writeUTF("Upload failed");
				return;
			}
			if(!md.UpdateData(MSSV, Double.parseDouble(Da)+ Data)) {
				while(!md.DelFile(FileID));
				while(!md.DelAutho(MSSV, FileID));
				dos.writeUTF("Upload failed");
				return;
			}
			if(!ReceiveFile(MSSV,FileName)) {
				while(!md.DelFile(FileID));
				while(!md.DelAutho(MSSV, FileID));
				while(!md.UpdateData(MSSV, Double.parseDouble(Da)))
				dos.writeUTF("Upload failed");
				return;
			}
			dos.writeUTF("Upload successfull");
			
		} catch (Exception e) {
			System.out.println("Error in Upload file: " + e.getMessage());
		}
    }
    
    public boolean ReceiveFile(String MSSV,String filename) {
    	try {
    		String savePath = "D:\\2024\\PBL4\\FileData\\" + MSSV + "\\" + filename +".zip";
            BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
            FileOutputStream fos = new FileOutputStream(Paths.get(savePath).toString());
            
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = bis.read(buffer, 0, buffer.length)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            
            fos.flush();
            fos.close();
            System.out.println("File đã được nhận và lưu tại: " + savePath);
            return true;
        } catch (Exception e) {
            System.out.println("Lỗi khi nhận file: " + e.getMessage());
            return false;
        }
    }
    
    public void AddGuest() {
    	try {
    		String MSSV = dis.readUTF();
			String FileID = dis.readUTF();
    		Model md = new Model();
    		if(!md.AddAutho(MSSV, FileID, 1)) {
				dos.writeUTF("Upload failed");
				return;
			}
    		dos.writeUTF("Them nguoi xem thanh cong");
    		return;
		} catch (Exception e) {
			System.out.println("Loi khi them nguoi xem: " + e.getMessage());
		}
    }
    public void DelGuest() {
    	try {
    		Model md = new Model();
    		String MSSV = dis.readUTF();
			String FileID = dis.readUTF();
    		if(!md.DelAutho(MSSV, FileID)) {
				dos.writeUTF("Xoa nguoi xem khong thanh cong");
				return;
			}
    		dos.writeUTF("Xoa nguoi xem thanh cong");
    		return;
		} catch (Exception e) {
			System.out.println("Loi khi Xoa nguoi xem: " + e.getMessage());
		}
    }
    
    public void GetAllGuest() {
    	try {
			String FileID = dis.readUTF();
			Model md = new Model();
			String Guest = md.GetAllGuest(FileID);
			if (Guest.equals("ERR")) {
				dos.writeUTF("loi khi tim nguoi xem file");
			}
			dos.writeUTF(Guest);
			
		} catch (Exception e) {
			System.out.println("Loi khi tim nguoi xem: " + e.getMessage());
		}
    }
    
    public void DelFile() {
    	try {
			String FileID = dis.readUTF();
    		Model md = new Model();
    		if(!md.DelFile(FileID)) {
				dos.writeUTF("Xoa file khong thanh cong");
			}
    		String MSSV = md.GetMSSVbyFileID(FileID);
    		String Data = md.GetData(MSSV);
    		String FileData = md.GetDatabyFileID(FileID);
    		md.UpdateData(MSSV, Double.parseDouble(Data)- Double.parseDouble(FileData));
    		dos.writeUTF("Xoa file thanh cong");
    		return;
		} catch (Exception e) {
			System.out.println("Loi khi Xoa file: " + e.getMessage());
		}
    }
}

