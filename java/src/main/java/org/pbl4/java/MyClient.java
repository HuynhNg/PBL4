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
import java.net.Socket;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


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
//        client.Login();
//        client.Register();
//        client.UpdatePassword();
//        client.GetInformation();
//        client.UpdateInformation();
//        client.GetFile();
//        client.GetAllByFolderID();
//        client.CreateFolder();
//      client.ChangeFolderName();
        	client.DeleteFolder();
//        client.ShareFolder();
//        client.DaleteShareFolder();
//        client.DeleteFile();
//        client.RenameFile();
//        client.GetData();
//        client.UpdateData();
//        client.UploadFile();
//        String savePath = "D:\\2024\\PBL4\\FileData\\received_file.zip";
//        client.ReceiveFile(savePath);
//        client.AddGuest();
//        client.GetAllGuest();
//        client.sendFile();
//        client.UploadFolder();
//        client.Search();
//        client.GetAllUser();
//        client.ResetPassword();

    }
    public void Login(){
        try {
            dos.writeUTF("Login");
            dos.writeUTF("Admin");
            dos.writeUTF("00000000");
            String message = dis.readUTF();
            System.out.println("Received from server: " + message);
        } catch (Exception e) {
            System.out.println("Login Err: " + e.getMessage());
        }
    }
    public void Register(){
        try {
            dos.writeUTF("Register");
            dos.writeUTF("102220025");
            dos.writeUTF("Nguyen Van A");
            dos.writeUTF("22T_KDHL");
            dos.writeUTF("123");
            String message = dis.readUTF();
            System.out.println("Received from server: " + message);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    public void UpdatePassword(){
        try {
            dos.writeUTF("UpdatePassword");
            dos.writeUTF("102220051");
            dos.writeUTF("123");
            dos.writeUTF("1234");
            String message = dis.readUTF();
            System.out.println("Received from server: " + message);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    
    public void GetInformation() {
    	try {
            dos.writeUTF("GetInformation");
            dos.writeUTF("102220024");
            String message = dis.readUTF();
            System.out.println("Received from server: " + message);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    
    public void UpdateInformation() {
    	try {
            dos.writeUTF("UpdateInformation");
            dos.writeUTF("102220024");
            dos.writeUTF("Nguyen Huynh");
            dos.writeUTF("22T_KHDL");
            String message = dis.readUTF();
            System.out.println("Received from server: " + message);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    public void GetFile() {
    	try {
            dos.writeUTF("GetAllFileName");
            dos.writeUTF("102220024");
            String message = dis.readUTF();
            System.out.println("Folder: " + message);
            String message1 = dis.readUTF();
            System.out.println("File: " + message1);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    public void GetAllByFolderID() {
    	try {
    		dos.writeUTF("GetAllByFolderID");
            dos.writeUTF("2");
            String message = dis.readUTF();
            System.out.println("Folder: " + message);
            String message1 = dis.readUTF();
            System.out.println("File: " + message1);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    
    public void CreateFolder() {
    	try {
    		dos.writeUTF("CreateFolder");
    		dos.writeUTF("102220024");
            dos.writeUTF("Book");
            dos.writeUTF("1");
            String message = dis.readUTF();
            System.out.println("Folder: " + message);
            String message1 = dis.readUTF();
            System.out.println("File: " + message1);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    public void ChangeFolderName() {
    	try {
    		dos.writeUTF("ChangeFolderName");
    		dos.writeUTF("102220024");
            dos.writeUTF("5");
            dos.writeUTF("aaaaaa");
            String message = dis.readUTF();
            System.out.println("Received from server: " + message);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    
    public void DeleteFolder() {
    	try {
    		dos.writeUTF("DeleteFolder");
    		dos.writeUTF("102220024");
            dos.writeUTF("4");
            String message = dis.readUTF();
            System.out.println("Received from server: " + message);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    
    public void ShareFolder() {
    	try {
    		dos.writeUTF("ShareFolder");
    		dos.writeUTF("102220025");
            dos.writeUTF("35");
            dos.writeUTF("102220024");
            String message = dis.readUTF();
            System.out.println("Received from server: " + message);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    
    public void DaleteShareFolder() {
    	try {
    		dos.writeUTF("DeleteShareFolder");
    		dos.writeUTF("102220024");
            dos.writeUTF("20");
            dos.writeUTF("102220025");
            String message = dis.readUTF();
            System.out.println("Received from server: " + message);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    
    public void DeleteFile() {
    	try {
			dos.writeUTF("DeleteFile");
			dos.writeUTF("102220024");
			dos.writeUTF("13");
			String message = dis.readUTF();
            System.out.println("Received from server: " + message);
		} catch (Exception e) {
			// TODO: handle exception
		}
    }
    
    public void RenameFile() {
    	try {
			dos.writeUTF("RenameFile");
			dos.writeUTF("102220024");
			dos.writeUTF("12");
			dos.writeUTF("Báo cáo.docx");
			String message = dis.readUTF();
            System.out.println("Received from server: " + message);
		} catch (Exception e) {
			// TODO: handle exception
		}
    }
    
    public void AddGuest() {
    	try {
            dos.writeUTF("AddGuest");
            dos.writeUTF("102220024");
            dos.writeUTF("102220025");
            dos.writeUTF("13");
            
            String message = dis.readUTF();
            System.out.println("Received from server: " + message);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    public void GetAllGuest() {
    	try {
//            dos.writeUTF("GetAllFileGuest");
    		dos.writeUTF("GetAllFolderGuest");
            dos.writeUTF("102220024");
            dos.writeUTF("6");
    		
//            dos.writeUTF("AddGuest");
//            dos.writeUTF("102220050");
//            dos.writeUTF("1");
            
            String message = dis.readUTF();
            System.out.println("Received from server: " + message);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    public void UpdateData() {
    	try {
            dos.writeUTF("UpdateData");
            dos.writeUTF("102220024");
            dos.writeUTF("80.8");
            String message = dis.readUTF();
            System.out.println("Received from server: " + message);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    public void UploadFile() {
    	try {
            dos.writeUTF("UploadFile");
            dos.writeUTF("102220024");
            dos.writeUTF("a.docx");
            dos.writeUTF("80.8");
            String message = dis.readUTF();
            System.out.println("Received from server: " + message);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    public void ReceiveFile(String savePath) {
        try {
        	dos.writeUTF("DownloadFolder");
        	dos.writeUTF("1");
            InputStream is = socket.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            FileOutputStream fos = new FileOutputStream(Paths.get(savePath).toString());
            
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = bis.read(buffer, 0, buffer.length)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            
            fos.flush();
            fos.close();
            System.out.println("File đã được nhận và lưu tại: " + savePath);
        } catch (Exception e) {
            System.out.println("Lỗi khi nhận file: " + e.getMessage());
        }
    }
    public void sendFile() {
        try {
            // Đường dẫn file
            File file = new File("D:\\2024\\cnpm.zip");
            if (!file.exists()) {
                System.out.println("File không tồn tại: " + file.getAbsolutePath());
                return;
            }

            System.out.println("Bắt đầu gửi file: " + file.getName());
            System.out.println("Kích thước file: " + file.length() + " bytes");

            // Gửi thông tin file
            dos.writeUTF("UploadFolder"); // Loại yêu cầu
            dos.writeUTF("102220024"); // MSSV
            dos.writeUTF("cnpm"); // Tên file
            dos.writeUTF("1");
            dos.writeUTF(String.valueOf(file.length())); // Kích thước file

            // Gửi dữ liệu file
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                long totalBytesSent = 0;

                while ((bytesRead = fis.read(buffer)) != -1) {
                    dos.write(buffer, 0, bytesRead);
                    totalBytesSent += bytesRead;
                    System.out.println("Đã gửi: " + totalBytesSent + " / " + file.length() + " bytes");
                }
            }

            // Nhận phản hồi từ server
            String response = dis.readUTF();
            System.out.println("Phản hồi từ server: " + response);

        } catch (IOException e) {
            System.out.println("Lỗi khi gửi file: " + e.getMessage());
        } finally {
            // Đảm bảo đóng socket và luồng
            try {
                if (dis != null) dis.close();
                if (dos != null) dos.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                System.out.println("Lỗi khi đóng kết nối: " + e.getMessage());
            }
        }
    }
    
    public void UploadFolder() {
    	try {
    		String folderPath = "D:\\2024\\PBL4\\FileData\\102220024"; // Đường dẫn thư mục cần upload
            String folderName = new File(folderPath).getName();
            int parentFolderID = 0; // ID của thư mục cha (0 nếu là thư mục gốc)
            double folderSize = getFolderSize(folderPath);
            dos.writeUTF("UploadFolder"); // Chức năng UploadFolder
            dos.writeUTF(folderName); // Tên thư mục
            dos.writeUTF("1"); // ID thư mục cha
            dos.writeUTF(String.valueOf(folderSize)); // Kích thước thư mục

            // Bước 2: Nén thư mục thành file ZIP và gửi file ZIP
            String zipFilePath = folderPath + ".zip";
            zipFolder(folderPath, zipFilePath);

            // Gửi file ZIP
            File zipFile = new File(zipFilePath);
            try (FileInputStream fis = new FileInputStream(zipFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    dos.write(buffer, 0, bytesRead);
                }
            }
            System.out.println("File ZIP đã được gửi lên server!");

            // Nhận phản hồi từ server
            String response = dis.readUTF();
            System.out.println("Server response: " + response);

            // Xóa file ZIP tạm thời
            zipFile.delete();
		} catch (Exception e) {
			// TODO: handle exception
		}
    }
    private static double getFolderSize(String folderPath) {
        File folder = new File(folderPath);
        double size = 0;

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            for (File file : files) {
                if (file.isFile()) {
                    size += file.length();
                } else if (file.isDirectory()) {
                    size += getFolderSize(file.getAbsolutePath());
                }
            }
        }

        return size / 1024.0; // Trả về kích thước tính bằng KB
    }

    // Hàm nén thư mục thành file ZIP
    private static void zipFolder(String sourceFolderPath, String zipFilePath) {
        try (FileOutputStream fos = new FileOutputStream(zipFilePath);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            File sourceFolder = new File(sourceFolderPath);
            if (sourceFolder.exists() && sourceFolder.isDirectory()) {
                zipDirectory(sourceFolder, sourceFolder.getName(), zos);
            }

            System.out.println("Thư mục đã được nén thành công!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Hàm nén thư mục và tất cả các tệp con vào trong file ZIP
    private static void zipDirectory(File folderToZip, String parentFolder, ZipOutputStream zos) throws IOException {
        File[] files = folderToZip.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                zipDirectory(file, parentFolder + "/" + file.getName(), zos);
            } else {
                try (FileInputStream fis = new FileInputStream(file)) {
                    ZipEntry zipEntry = new ZipEntry(parentFolder + "/" + file.getName());
                    zos.putNextEntry(zipEntry);

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        zos.write(buffer, 0, bytesRead);
                    }
                    zos.closeEntry();
                }
            }
        }
    }
    
    public void Search() {
        try {
        	dos.writeUTF("Search");
        	dos.writeUTF("102220024");
        	dos.writeUTF("TanKhoi");
            
        	String Folder = dis.readUTF();
        	String File = dis.readUTF();
        	System.out.println("folder : "+Folder);
        	System.out.println("file: " + File);
        } catch (Exception e) {
            System.out.println("Lỗi khi nhận file: " + e.getMessage());
        }
    }
    
    public void GetAllUser() {
        try {
        	dos.writeUTF("GetAllUser");
        	dos.writeUTF("Admin");
            
        	String Users = dis.readUTF();
        	System.out.println("users : "+Users);
        } catch (Exception e) {
            System.out.println("Lỗi khi nhận file: " + e.getMessage());
        }
    }
    public void ResetPassword() {
        try {
        	dos.writeUTF("ResetPassword");
        	dos.writeUTF("102220025");
            
        	String Users = dis.readUTF();
        	System.out.println("Pass : "+Users);
        } catch (Exception e) {
            System.out.println("Lỗi khi nhận file: " + e.getMessage());
        }
    }
}

