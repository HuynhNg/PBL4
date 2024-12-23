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
        }finally {
            // Đảm bảo đóng kết nối sau khi xử lý xong
            try {
                if (dis != null) dis.close();
                if (dos != null) dos.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
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
            File file = new File(FilePath);

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
			String FilePath = "D:\\2024\\PBL4\\FileData\\" + MSSV + "\\" + FileName + ".zip";
			dos.writeUTF(FileName);
			sendfile(FilePath);
			
		} catch (Exception e) {
			 System.out.println("Lỗi khi gửi file: " + e.getMessage());
		}
    }
    
    public void UploadFile() {
        try {
            String MSSV = dis.readUTF();
            String FileName = dis.readUTF();
            long Data = Long.parseLong(dis.readUTF());
            Model md = new Model();

            // Kiểm tra MSSV
            if (!md.CheckMSSV(MSSV)) {
                dos.writeUTF("MSSV not found");
                return;
            }

            // Kiểm tra xem file đã tồn tại chưa
            if (!md.GetFileID(FileName, MSSV).equals("ERR")) {
                dos.writeUTF("File already exists");
                return;
            }

            // Thêm thông tin file vào cơ sở dữ liệu
            if (!md.AddFile(FileName, Data, MSSV)) {
                dos.writeUTF("Upload failed: Unable to add file information");
                return;
            }

            String FileID = md.GetFileID(FileName, MSSV);

            // Thêm quyền cho file
            if (!md.AddAutho(MSSV, FileID, 0)) {
                md.DelFile(FileID);
                dos.writeUTF("Upload failed: Unable to add authorization");
                return;
            }

            String Da = md.GetData(MSSV);
            if (Da.equals("ERR")) {
                md.DelAutho(MSSV, FileID);
                md.DelFile(FileID);
                dos.writeUTF("Upload failed: Data retrieval error");
                return;
            }

            // Cập nhật dữ liệu
            if (!md.UpdateData(MSSV, Double.parseDouble(Da) + Data)) {
                md.DelAutho(MSSV, FileID);
                md.DelFile(FileID);
                dos.writeUTF("Upload failed: Unable to update data");
                return;
            }

            // Nhận file từ client
            if (!ReceiveFile(MSSV, FileName, Data)) {
                md.DelAutho(MSSV, FileID);
                md.DelFile(FileID);
                md.UpdateData(MSSV, Double.parseDouble(Da));
                dos.writeUTF("Upload failed: File reception error");
                return;
            }

        } catch (IOException e) {
            System.out.println("Error in UploadFile: " + e.getMessage());
            e.printStackTrace();
            try {
                dos.writeUTF("Upload failed due to server error");
            } catch (IOException ex) {
                System.out.println("Error sending failure response: " + ex.getMessage());
            }
        }
    }

    public boolean ReceiveFile(String MSSV, String filename, Long fileSize) {
        String savePath = "D:\\2024\\PBL4\\FileData\\" + MSSV + "\\" + filename;
        File saveDir = new File("D:\\2024\\PBL4\\FileData\\" + MSSV);

        // Tạo thư mục nếu chưa tồn tại
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }

        File file = new File(savePath);
        // Kiểm tra tên file đã tồn tại chưa, nếu có thì đổi tên
        if (file.exists()) {
            String baseName = filename.substring(0, filename.lastIndexOf("."));
            String extension = filename.substring(filename.lastIndexOf("."));
            int counter = 1;

            while (file.exists()) {
                String newFilename = baseName + "_" + counter + extension;
                file = new File("D:\\2024\\PBL4\\FileData\\" + MSSV + "\\" + newFilename);
                counter++;
            }
        }

        System.out.println("Bắt đầu nhận file và lưu tại: " + savePath);
        try (BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
             FileOutputStream fos = new FileOutputStream(savePath);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {

            byte[] buffer = new byte[8192]; // Đồng bộ với buffer client
            long totalBytesRead = 0;
            int bytesRead;

            // Nhận file từ client
            while (totalBytesRead < fileSize && (bytesRead = bis.read(buffer, 0, buffer.length)) != -1) {
                bos.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                System.out.println("Đã nhận được: " + totalBytesRead + " / " + fileSize + " bytes");
            }

            bos.flush();

            // Kiểm tra xem file đã nhận đủ chưa
            if (totalBytesRead == fileSize) {
                System.out.println("Đã nhận đầy đủ file và lưu tại: " + savePath);
                dos.writeUTF("Upload successful");
                return true;
            } else {
                System.out.println("Cảnh báo: File nhận không đầy đủ! Đã nhận: " + totalBytesRead + " / " + fileSize);
                dos.writeUTF("Upload incomplete");
                return false;
            }
        } catch (IOException e) {
            System.out.println("Lỗi khi nhận file: " + e.getMessage());
            try {
                dos.writeUTF("Upload failed");
            } catch (IOException ex) {
                System.out.println("Lỗi khi gửi phản hồi lỗi: " + ex.getMessage());
            }
            return false;
        }
    }



    public void AddGuest() {
    	try {
    		String MSSV = dis.readUTF();
			String FileID = dis.readUTF();
    		Model md = new Model();
    		System.out.println(MSSV);
    		if(!md.CheckMSSV(MSSV)) {
    			dos.writeUTF("MSSV not found");
    			return;
    		}
    		if(!md.AddAutho(MSSV, FileID, 1)) {
				dos.writeUTF("Add guest failed");
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
			System.out.print(Guest);
			dos.writeUTF(Guest);
			
		} catch (Exception e) {
			System.out.println("Loi khi tim nguoi xem: " + e.getMessage());
		}
    }
    
    public void DelFile() {
    	try {
			String FileID = dis.readUTF();
			System.out.print(FileID);
    		Model md = new Model();
    		String MSSV = md.GetMSSVbyFileID(FileID);
    		String Data = md.GetData(MSSV);
    		String FileData = md.GetDatabyFileID(FileID);
    		if(!md.DeleteAllAuthorByFileID(FileID)) {
    			dos.writeUTF("Xoa file khong thanh cong");
    			return;
    		}
    		if(!md.DelFile(FileID)) {
				dos.writeUTF("Xoa file khong thanh cong");
				return;
			}
    		md.UpdateData(MSSV, Double.parseDouble(Data)- Double.parseDouble(FileData));
    		dos.writeUTF("Xoa file thanh cong");
    		return;
		} catch (Exception e) {
			System.out.println("Loi khi Xoa file: " + e.getMessage());
			e.printStackTrace();
		}
    }
}

