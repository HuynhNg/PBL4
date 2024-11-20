package org.pbl4.java;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class PBL_Controller {
	private Socket socket;
	private DataInputStream dis;
    private DataOutputStream dos;
	
	public PBL_Controller(Socket socket) throws IOException {
        this.socket = socket;
        this.dis = new DataInputStream(socket.getInputStream());
        this.dos = new DataOutputStream(socket.getOutputStream());
    }
	public void Login(){
		
        try {
            String MSSV = dis.readUTF();
            String password = dis.readUTF();
            PBL_Model md = new PBL_Model();

            
            if (md.Login(MSSV, password)) {
            	int Root = md.GetFolderRoot(MSSV);
            	dos.writeUTF(Integer.toString(Root));
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
            PBL_Model md = new PBL_Model();
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

    public void CreateFolder(String Folderpath, String Foldername) {
        try {
            String baseFolder = "D:\\2024\\PBL4\\FileData";
            String folderPath = Paths.get(baseFolder, Folderpath, Foldername).toString();
            
            File folder = new File(folderPath);
            System.out.println("Đang tạo thư mục cho: " + Foldername);
            
            if (!folder.exists()) {
                if (folder.mkdirs()) {
                    System.out.println("Created folder in" + folderPath);
                } else {
                    System.out.println("Create folder failed");
                }
            } else {
                System.out.println("Folder already exists");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void UpdatePassword() {
    	try {
			String MSSV = dis.readUTF();
			String OldPassword = dis.readUTF();
			String Password = dis.readUTF();
			PBL_Model md = new PBL_Model();
			if(!md.CheckMSSV(MSSV)) {
				dos.writeUTF("MSSV not found");
				return;
			}
			if(!md.GetPassword(MSSV).equals(OldPassword)) {
				dos.writeUTF("Password is wrong");
				return ;
			}
			if(!md.ChangePassword(MSSV, Password)) {
				dos.writeUTF("Update password is not succesful");
				return;
			}
			dos.writeUTF("Update password successfully");
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
    }
    
    public void GetInformation() {
    	try {
			String MSSV = dis.readUTF();
			PBL_Model md = new PBL_Model();
			if(!md.CheckMSSV(MSSV)) {
				dos.writeUTF("MSSV not found");
				return;
			}
			String Infor = md.GetUserByMSSV(MSSV);
			if(Infor.equals("ERR")) {
				dos.writeUTF("Get failed");
				return;
			}
			dos.writeUTF(Infor);
			
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());	
			e.printStackTrace();
		}
    }
    
    public void UpdateInformation() {
    	try {
			String MSSV = dis.readUTF();
			String Name = dis.readUTF();
			String Class = dis.readUTF();
			PBL_Model md = new PBL_Model();
			if(!md.CheckMSSV(MSSV)) {
				dos.writeUTF("MSSV not found");
				return;
			}
			if(!md.UpdateUser(MSSV, Name, Class)) {
				dos.writeUTF("Update inforamtion is not successfull");
				return;
			}
			dos.writeUTF("Update successfull");
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
    }
    
    public void GetAllFileName() {
    	try {
			String MSSV = dis.readUTF();
			PBL_Model md = new PBL_Model();
			if(!md.CheckMSSV(MSSV)) {
				dos.writeUTF("MSSV not found");
				return;
			}
			
			int FolderRoot = md.GetFolderRoot(MSSV);
//			System.out.println(FolderRoot);
			String MyFolder = md.GetAllFolderbyParent(FolderRoot);
			String GuestFolder = md.GetAllGuestFolder(MSSV);
			if(MyFolder.equals("ERR") || GuestFolder.equals("ERR")) {
				dos.writeUTF("Get Folder is not successful");
				return;
			}
			if(MyFolder.length() != 0 && GuestFolder.length() != 0) {
				dos.writeUTF(MyFolder + ";" + GuestFolder);
//				System.out.println(MyFolder + ";" + GuestFolder);
			}
			if(MyFolder.length() == 0) {
				dos.writeUTF(GuestFolder);
//				System.out.println(GuestFolder);
				
			}else {
				dos.writeUTF(MyFolder);
//				System.out.println(MyFolder);
			}
			
			
			String MyFile = md.GetAllFileByFolderID(FolderRoot);
			String GuestFile = md.GetGuestFileNameByMSSV(MSSV);
//			System.out.println(MyFile + " ; " + GuestFile);
			if(MyFile.equals("ERR") || GuestFile.equals("ERR")) {
				dos.writeUTF("Get File is not successful");
				return;
			}
			if(MyFile.length() != 0 && GuestFile.length() != 0) {
				dos.writeUTF(MyFile + ";" + GuestFile);
			}
			if(MyFile.length() == 0) {
				dos.writeUTF(GuestFile);
			}else {
				dos.writeUTF(MyFile);
			}
			
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
    }
    
    public void GetMyFileName() {
    	try {
    		String MSSV = dis.readUTF();
			PBL_Model md = new PBL_Model();
			if(!md.CheckMSSV(MSSV)) {
				dos.writeUTF("MSSV not found");
				return;
			}
			int FolderRoot = md.GetFolderRoot(MSSV);
			String MyFolder = md.GetAllFolderbyParent(FolderRoot);
			if(MyFolder.equals("ERR")) {
				dos.writeUTF("Get Folder is not successful");
				return;
			}
			dos.writeUTF(MyFolder);

			
			
			String MyFile = md.GetAllFileByFolderID(FolderRoot);
			if(MyFile.equals("ERR")) {
				dos.writeUTF("Get File is not successful");
				return;
			}
			dos.writeUTF(MyFile);
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
    }
    
    public void GetGuestFileName() {
    	try {
    		String MSSV = dis.readUTF();
			PBL_Model md = new PBL_Model();
			if(!md.CheckMSSV(MSSV)) {
				dos.writeUTF("MSSV not found");
				return;
			}
			String GuestFolder = md.GetAllGuestFolder(MSSV);
			if(GuestFolder.equals("ERR")) {
				dos.writeUTF("Get Folder is not successful");
				return;
			}
			dos.writeUTF(GuestFolder);
			
			
			String GuestFile = md.GetGuestFileNameByMSSV(MSSV);
			if(GuestFile.equals("ERR")) {
				dos.writeUTF("Get File is not successful");
				return;
			}
			dos.writeUTF(GuestFile);
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
    }
    
    public void GetAllByFolderID() {
    	try {
    		int FolderID = Integer.parseInt(dis.readUTF());
    		PBL_Model md = new PBL_Model();
    		
    		String Folder = md.GetAllFolderbyParent(FolderID);
    		if(Folder.equals("ERR")) {
    			dos.writeUTF("Get Folder is not successful");
				return;
    		}
    		dos.writeUTF(Folder);
			
    		String FileName = md.GetAllFileByFolderID(FolderID);
    		if(FileName.equals("ERR")) {
    			dos.writeUTF("Get File is not successful");
				return;
    		}
    		dos.writeUTF(FileName);
			
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
    }
    
    public void CreateFolder() {
    	try {
    		String MSSV = dis.readUTF();
    		String FolderName = dis.readUTF();
    		int FolderParent = Integer.parseInt(dis.readUTF());
    		PBL_Model md =new PBL_Model();
    		
    		if(!md.GetMSSVByFolderID(FolderParent).equals(MSSV)) {
    			dos.writeUTF("MSSV cant create folder in this");
    			return;
    		}
    		
    		if(md.CheckFolderExits(FolderParent, FolderName)) {
    			dos.writeUTF("Folder already exits");
    			return;
    		}
    		
    		String ParentName = md.GetFolderNameByFolderID(FolderParent);
    		String ParentPath = md.GetFolderPath(FolderParent);
    		
    		if(ParentPath == null) {
    			ParentPath = "";
    		}
    		int FolderID = md.CreateFolder(FolderName, FolderParent, ParentPath + "\\" + ParentName);
    		if( FolderID == -1) {
    			dos.writeUTF("Create folder failded");
    			return;
    		}
    		md.CreateFolderrRole(MSSV, FolderID, 0);
    		CreateFolder(ParentPath + "\\" + ParentName, FolderName);
    		dos.writeUTF("Create folder successfully");
    		
    	}catch (Exception e) {
    		System.out.println("Error: " + e.getMessage());
    		e.printStackTrace();
		}
    }
    
    public void ChangeFolderName() {
        try {
        	String MSSV = dis.readUTF();
            int FolderID = Integer.parseInt(dis.readUTF());
            String FolderName = dis.readUTF();

            PBL_Model md = new PBL_Model();
            
            if(!md.GetMSSVByFolderID(FolderID).equals(MSSV)) {
            	dos.writeUTF("MSSV cant change foldername");
                return;
            }
            
            String OldName = md.GetFolderNameByFolderID(FolderID);
            
            int ParentID = md.GetFolderParent(FolderID);    
            if (ParentID == -1) {
                dos.writeUTF("Change folder name failed: Invalid parent ID.");
                return;
            }
            if(md.CheckFolderExits(ParentID, FolderName)) {
            	dos.writeUTF("FolderName already exits");
                return;
            }
            
            if (OldName.equals("ERR")) {
                dos.writeUTF("Change folder name failed: Folder ID not found.");
                return;
            }

            String Folderpath = md.GetFolderPath(FolderID);
            if (Folderpath.equals("ERR")) {
                dos.writeUTF("Change folder name failed: Folder path not found.");
                return;
            }

            String baseFolder = "D:\\2024\\PBL4\\FileData";
            Path OldfolderPath = Paths.get(baseFolder, Folderpath, OldName);
            Path NewfolderPath = Paths.get(baseFolder, Folderpath, FolderName);
            
            

            try {
                Files.move(OldfolderPath, NewfolderPath, StandardCopyOption.REPLACE_EXISTING);
                if(!md.UpdateFolderName(FolderID, FolderName)) {
                	dos.writeUTF("Rename folder failed");
                	return;
                }
                dos.writeUTF("Folder renamed successfully.");
                System.out.println("Folder renamed successfully from " + OldfolderPath + " to " + NewfolderPath);
            } catch (Exception e) {
                dos.writeUTF("Change folder name failed: Error renaming folder.");
                System.out.println("Error renaming folder: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void DeleteFolder() {
        try {
            // Nhận thông tin FolderID
        	String MSSV = dis.readUTF();
            int FolderID = Integer.parseInt(dis.readUTF());

            // Lấy thông tin thư mục từ model
            PBL_Model md = new PBL_Model();
            
            if(!md.GetMSSVByFolderID(FolderID).equals(MSSV)) {
            	dos.writeUTF("MSSV cant delete this folder");
                return;
            }
            
            String FolderName = md.GetFolderNameByFolderID(FolderID);
            if (FolderName.equals("ERR")) {
                dos.writeUTF("Delete Folder failed: Invalid Folder ID.");
                return;
            }

            String Folderpath = md.GetFolderPath(FolderID);
            if (Folderpath.equals("ERR")) {
                dos.writeUTF("Delete Folder failed: Folder path not found.");
                return;
            }

            String baseFolder = "D:\\2024\\PBL4\\FileData";
            Path folderPath = Paths.get(baseFolder, Folderpath, FolderName);
            System.out.println(folderPath.toString());

            // Kiểm tra xem thư mục có tồn tại hay không
            if (!Files.exists(folderPath)) {
                dos.writeUTF("Delete Folder failed: Folder does not exist.");
                return;
            }

            // Xóa thư mục và nội dung
            try {
                Files.walkFileTree(folderPath, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        try {
                            Files.delete(file); // Xóa file
                        } catch (IOException e) {
                            System.out.println("Error deleting file: " + file + " - " + e.getMessage());
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        try {
                            Files.delete(dir); // Xóa thư mục
                        } catch (IOException e) {
                            System.out.println("Error deleting directory: " + dir + " - " + e.getMessage());
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });

                // Cập nhật database
                if (!md.DeleteFolder(FolderID)) {
                    dos.writeUTF("Delete Folder failed: Database update error.");
                    return;
                }

                // Thông báo thành công
                dos.writeUTF("Delete Folder successfully.");
                System.out.println("Folder deleted successfully: " + folderPath);

            } catch (IOException e) {
                dos.writeUTF("Delete Folder failed: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void ShareFolder() {
    	try {
    		String Owner = dis.readUTF();
			int FolderID = Integer.parseInt(dis.readUTF());
			String MSSV = dis.readUTF();
			
			PBL_Model md = new PBL_Model();
			
			if(!md.GetMSSVByFolderID(FolderID).equals(Owner)) {
				dos.writeUTF("MSSV cant share this folder");
				return;
			}
			
			if(!md.CheckMSSV(MSSV)) {
				dos.writeUTF("MSSV not found");
				return;
			}
			
			if(md.CheckFolderRole(MSSV, FolderID)) {
				dos.writeUTF("Previously shared folder");
				return;
			}
			
			if(!md.CreateFolderrRole(MSSV, FolderID, 1)) {
				dos.writeUTF("Share folder failed");
				return;
			}
			dos.writeUTF("Share folder successfully");
			
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
		}
    }
    
    public void DeleteShareFolder() {
    	try {
    		String Owner = dis.readUTF();
    		int folderID = Integer.parseInt(dis.readUTF());
    		String MSSV = dis.readUTF();
    		PBL_Model md = new PBL_Model();
    		
    		if(!md.GetMSSVByFolderID(folderID).equals(Owner)) {
				dos.writeUTF("MSSV cant delete");
				return;
			}
    		
    		if(!md.DeleteFolderRole(MSSV, folderID)) {
    			dos.writeUTF("Delete failed");
    			return;
    		}
    		dos.writeUTF("Delete successfully");
    	}catch (Exception e) {
    		System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
		}
    }
    
    public void UploadFile() {
        try {
            String MSSV = dis.readUTF();
            String FileName = dis.readUTF();
            int FolderID = Integer.parseInt(dis.readUTF());
            Double FileSize = Double.parseDouble(dis.readUTF());
            long Data = FileSize.longValue();
            PBL_Model md = new PBL_Model();
            
            if(!md.GetMSSVByFolderID(FolderID).equals(MSSV)) {
            	dos.writeUTF("MSSV cannot upload file here.");
            	return;
            }
            // Kiểm tra MSSV
            if (!md.CheckMSSV(MSSV)) {
                dos.writeUTF("MSSV not found");
                return;
            }

            // Kiểm tra xem file đã tồn tại chưa
            if(md.CheckFileExits(FileName, FolderID)) {
            	dos.writeUTF("FileName already exits");
                return;
            }

            // Thêm thông tin file vào cơ sở dữ liệu
            int FileID = md.CreateFile(FileName, FolderID, FileSize);
            if (FileID == -1) {
                dos.writeUTF("Upload failed");
                return;
            }
            if(!md.CreateFileRole(MSSV, FileID, 0)) {
            	md.DeleteFile(FileID);
            	dos.writeUTF("Upload failed");
                return;
            }

            Double UserData = md.GetData(MSSV);
            if (UserData == -1.0) {
                md.DeleteFile(FileID);
                dos.writeUTF("Upload failed: Data retrieval error");
                return;
            }

            if (!md.UpdateDataUser(MSSV, UserData + FileSize)) {
            	md.DeleteFile(FileID);
                dos.writeUTF("Upload failed: Unable to update data");
                return;
            }

            String FolderName = md.GetFolderNameByFolderID(FolderID);
            String FolderPath = md.GetFolderPath(FolderID) + "\\" + FolderName;
            
            if (!ReceiveFile(FolderPath, FileName, Data)) {
            	md.DeleteFile(FileID);
                md.UpdateDataUser(MSSV, UserData);
                dos.writeUTF("Upload failed: File reception error");
                return;
            }
            
            UnZipFile(FileID);

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
    
    public boolean ReceiveFile(String FolderPath, String filename, Long fileSize) {
        String savePath = "D:\\2024\\PBL4\\FileData\\" + FolderPath + "\\" + filename + ".zip";
        File saveDir = new File("D:\\2024\\PBL4\\FileData\\" + FolderPath);

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
                file = new File("D:\\2024\\PBL4\\FileData\\" + FolderPath + "\\" + newFilename);
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
    
    public boolean UnZipFile(int FileID) {
        try {
            PBL_Model md = new PBL_Model();
            System.out.println(FileID);
            String FileName = md.GetFileNameByFileID(FileID);

            if (FileName.equals("ERR")) {
                System.out.println("UnZIp file failed");
                return false;
            }

            int FolderID = md.GetFolderIDByFileID(FileID);
            if (FolderID == -1) {
                System.out.println("UnZIp file failed");
                return false;
            }

            String FolderPath = md.GetFolderPath(FolderID);
            String FolderName = md.GetFolderNameByFolderID(FolderID);
            String baseFolder = "D:\\2024\\PBL4\\FileData";
            String folderPath = Paths.get(baseFolder, FolderPath, FolderName).toString();
            String FilePath = Paths.get(folderPath, FileName).toString();
            String FileZipPath = FilePath + ".zip";

            System.out.println(FileName);
            System.out.println(FileZipPath);

            File destDirectory = new File(folderPath);
            if (!destDirectory.exists()) {
                destDirectory.mkdirs();
            }

            // Sử dụng `try-with-resources` để đảm bảo luồng được đóng tự động
            try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(FileZipPath))) {
                ZipEntry entry = zipIn.getNextEntry();
                while (entry != null) {
                    String filePath = Paths.get(folderPath, entry.getName()).toString();
                    if (entry.isDirectory()) {
                        File dir = new File(filePath);
                        dir.mkdirs(); // Tạo thư mục nếu là entry dạng thư mục
                    } else {
                        extractFile(zipIn, filePath);
                    }
                    zipIn.closeEntry();
                    entry = zipIn.getNextEntry();
                }
                System.out.println("Giải nén thành công!");
            } catch (IOException e) {
                System.out.println("Lỗi khi giải nén: " + e.getMessage());
                e.printStackTrace();
                return false;
            }

            // Xóa file ZIP sau khi giải nén
            File zipFile = new File(FileZipPath);
            if (zipFile.exists()) {
                if (zipFile.delete()) {
                    System.out.println("File ZIP đã được xóa sau khi giải nén: " + FileZipPath);
                } else {
                    System.out.println("Không thể xóa file ZIP: " + FileZipPath);
                }
            } else {
                System.out.println("File ZIP không tồn tại: " + FileZipPath);
            }

            return true;
        } catch (Exception e) {
            System.out.println("Error in UnZipFile: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = zipIn.read(buffer)) != -1) {
                bos.write(buffer, 0, read);
            }
        }
    }

    
    public void DeleteFile() {
    	try {
    		String MSSV = dis.readUTF();
			int FileID = Integer.parseInt(dis.readUTF());
			PBL_Model md =new PBL_Model();
			
			if(!md.GetMSSVByFileID(FileID).equals(MSSV)) {
				dos.writeUTF("MSSV cant delete");
				return;
			}
			
			String FileName = md.GetFileNameByFileID(FileID);
			
			int FolderID = md.GetFolderIDByFileID(FileID);
			String FolderPath = md.GetFolderPath(FolderID);
			String FolderName = md.GetFolderNameByFolderID(FolderID);
			
			String baseFolder = "D:\\2024\\PBL4\\FileData";
			
			String filePath = Paths.get(baseFolder, FolderPath, FolderName, FileName).toString();
	        File file = new File(filePath);

	        // Kiểm tra file có tồn tại không
	        if (file.exists()) {
	            // Thực hiện xóa file
	            if (file.delete()) {
	                System.out.println("File đã được xóa thành công: " + filePath);
	                md.DeleteFile(FileID);
	                dos.writeUTF("Delete file successfully");
	                return ;
	            } else {
	                System.out.println("Không thể xóa file: " + filePath);
	            }
	        } else {
	            System.out.println("File không tồn tại: " + filePath);
	        }
	        
	        dos.writeUTF("Delete file failed");
            return ;
		} catch (Exception e) {
			System.out.println("Error in UploadFile: " + e.getMessage());
            e.printStackTrace();
		}
    }

}

