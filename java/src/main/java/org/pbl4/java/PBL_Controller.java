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
import java.util.zip.ZipOutputStream;

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
            	if(md.getRole(MSSV) == 0) {
            		dos.writeUTF("0");
            	}
            	else {
            		int Root = md.GetFolderRoot(MSSV);
                	dos.writeUTF("Login successfully");
                	dos.writeUTF(Integer.toString(Root));
            	}
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
			int Role = Integer.parseInt(dis.readUTF());
			PBL_Model md = new PBL_Model();
			if(!md.CheckMSSV(MSSV)) {
				dos.writeUTF("MSSV not found");
				return;
			}
			if(!md.UpdateUser(MSSV, Name, Class, Role)) {
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
				System.out.println(MyFolder + ";" + GuestFolder);
			}
			else {
				if(MyFolder.length() == 0) {
					dos.writeUTF(GuestFolder);
					System.out.println(GuestFolder);
					
				}else {
					dos.writeUTF(MyFolder);
					System.out.println(MyFolder);
				}
			}
			
			
			String MyFile = md.GetAllFileByFolderID(FolderRoot);
			String GuestFile = md.GetGuestFileNameByMSSV(MSSV);
			System.out.println(MyFile + " ; " + GuestFile);
			if(MyFile.equals("ERR") || GuestFile.equals("ERR")) {
				dos.writeUTF("Get File is not successful");
				return;
			}
			if(MyFile.length() != 0 && GuestFile.length() != 0) {
				dos.writeUTF(MyFile + ";" + GuestFile);
			}else {
				if(MyFile.length() == 0) {
					dos.writeUTF(GuestFile);
				}else {
					dos.writeUTF(MyFile);
				}
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
    			dos.writeUTF("MSSV cant create folder here");
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

            if (!md.GetMSSVByFolderID(FolderID).equals(MSSV)) {
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

            // Biến để tính tổng kích thước
            final long[] totalSize = {0};

            // Xóa thư mục và nội dung
            try {
                Files.walkFileTree(folderPath, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        try {
                            // Cộng kích thước file vào tổng
                            totalSize[0] += Files.size(file);

                            // Xóa file
                            Files.delete(file);
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
                
                double data = md.GetData(MSSV);
                data = data - totalSize[0];
                md.UpdateDataUser(MSSV, data);

                // Thông báo thành công kèm tổng kích thước
                dos.writeUTF("Delete Folder successfully. Total data deleted: " + totalSize[0] + " bytes.");
                System.out.println("Folder deleted successfully: " + folderPath);
//                System.out.println("Total data deleted: " + totalSize[0] + " bytes.");

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
            String FolderPath = md.GetFolderPath(FolderID);
            if(FolderPath == null) {
            	FolderPath = FolderName;
            }else {
            	FolderPath = FolderPath + "\\"+ FolderName;
            }
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
        System.out.println(FolderPath + "  " + filename);
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
            if(FolderPath == null) {
            	FolderPath = "";
            }
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
			if (FolderPath == null) {
				FolderPath = "";
			}
			String FolderName = md.GetFolderNameByFolderID(FolderID);
			
			String baseFolder = "D:\\2024\\PBL4\\FileData";
			
			String filePath = Paths.get(baseFolder, FolderPath, FolderName, FileName).toString();
	        File file = new File(filePath);

	        // Kiểm tra file có tồn tại không
	        if (file.exists()) {
	            // Thực hiện xóa file
	            if (file.delete()) {
	                System.out.println("File đã được xóa thành công: " + filePath);
	                Double FileSize = md.GetFileSize(FileID);
	                Double Data = md.GetData(MSSV);
	                md.DeleteFile(FileID);
	                md.UpdateDataUser(MSSV, Data- FileSize);
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
			System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
		}
    }
    
    public void RenameFile() {
    	try {
			String MSSV = dis.readUTF();
			int FileID = Integer.parseInt(dis.readUTF());
			String FileName = dis.readUTF();
			
			PBL_Model md = new PBL_Model();
			if(!md.GetMSSVByFileID(FileID).equals(MSSV)) {
				dos.writeUTF("Cant rename this file");
				return;
			}
			
			int FolderID = md.GetFolderIDByFileID(FileID);
			if(md.CheckFileExits(FileName, FolderID)) {
				dos.writeUTF("Filename already exits");
				return;
			}
			
			String baseFolder = "D:\\2024\\PBL4\\FileData";
			String FolderPath = md.GetFolderPath(FolderID);
			if(FolderPath == null) {
				FolderPath = "";
			}
			String NameFolder = md.GetFolderNameByFolderID(FolderID);
			String OldName = md.GetFileNameByFileID(FileID);
			
			String NewPath = Paths.get(baseFolder, FolderPath, NameFolder,FileName).toString();
			String OldPath = Paths.get(baseFolder, FolderPath, NameFolder,OldName).toString();
			
			File currentFile = new File(OldPath);
            File newFile = new File(NewPath);
			
			if (!currentFile.exists()) {
                System.out.println("File không tồn tại: " + OldPath);
                dos.writeUTF("File is not exits");
                return;
            }

            // Kiểm tra nếu file đích đã tồn tại
            if (newFile.exists()) {
                System.out.println("File đích đã tồn tại: " + NewPath);
                dos.writeUTF("Filename already exits");
                return;
            }

            // Đổi tên file
            if (currentFile.renameTo(newFile)) {
            	md.UpdateFileName(FileID, FileName);
                System.out.println("Đổi tên file thành công: " + NewPath);
                dos.writeUTF("Rename successfully");
                return;
            } else {
            	dos.writeUTF("Rename failed");
                return;
            }
			
			
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
		}
    }
    public static String zipFile(String filePath) {
        String zipFilePath = filePath + ".zip";
        try (FileOutputStream fos = new FileOutputStream(zipFilePath);
             ZipOutputStream zos = new ZipOutputStream(fos);
             FileInputStream fis = new FileInputStream(filePath)) {
        	ZipEntry zipEntry = new ZipEntry(new File(filePath).getName());

            zos.putNextEntry(zipEntry);

            byte[] buffer = new byte[8192];
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

                byte[] buffer = new byte[8192];
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
			int FileID = Integer.parseInt(dis.readUTF());
			PBL_Model md = new PBL_Model();
			
			String FileName = md.GetFileNameByFileID(FileID);
			int FolderID = md.GetFolderIDByFileID(FileID);
			String FolderPath = md.GetFolderPath(FolderID);
			if (FolderPath == null) {
				FolderPath = "";
			}
			String FolderName = md.GetFolderNameByFolderID(FolderID);
			
			String baseFolder = "D:\\2024\\PBL4\\FileData";
			
			String filePath = Paths.get(baseFolder, FolderPath, FolderName, FileName).toString();
			dos.writeUTF(FileName);
			String FileZipPath = zipFile(filePath);
			sendfile(FileZipPath);
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
			
		} catch (Exception e) {
			 System.out.println("Lỗi khi gửi file: " + e.getMessage());
		}
    }
    
    public void AddGuest() {
    	try {
    		String Owner = dis.readUTF();
    		String MSSV = dis.readUTF();
			int FileID = Integer.parseInt(dis.readUTF());
			PBL_Model md = new PBL_Model();
			if(!md.GetMSSVByFileID(FileID).equals(Owner)) {
				dos.writeUTF("Cant share this file");
				return;
			}
    		if(!md.CheckMSSV(MSSV)) {
    			dos.writeUTF("MSSV not found");
    			return;
    		}
    		if(!md.CreateFileRole(MSSV, FileID, 1)) {
				dos.writeUTF("Add guest failed");
				return;
			}
    		dos.writeUTF("Share file successfully");
    		return;
		} catch (Exception e) {
			System.out.println("Loi khi them nguoi xem: " + e.getMessage());
		}
    }
    
    public void DelGuest() {
    	try {
    		String Owner = dis.readUTF();
    		String MSSV = dis.readUTF();
			int FileID = Integer.parseInt(dis.readUTF());
			PBL_Model md = new PBL_Model();
			if(!md.GetMSSVByFileID(FileID).equals(Owner)) {
				dos.writeUTF("Cant share this file");
				return;
			}
    		if(!md.CheckMSSV(MSSV)) {
    			dos.writeUTF("MSSV not found");
    			return;
    		}
    		if(!md.DeleteFileRole(MSSV, FileID)) {
				dos.writeUTF("Delete failed");
				return;
			}
    		dos.writeUTF("Delete successfully");
    		return;
		} catch (Exception e) {
			System.out.println("Loi khi Xoa nguoi xem: " + e.getMessage());
		}
    }
    
    public void GetAllFileGuest() {
    	try {
    		String MSSV = dis.readUTF();
			int FileID = Integer.parseInt(dis.readUTF());
			PBL_Model md = new PBL_Model();
			if(!md.GetMSSVByFileID(FileID).equals(MSSV)) {
				dos.writeUTF("Cant get");
				return;
			}
			String Guest = md.GetAllFileGuest(FileID);
			if (Guest.equals("ERR")) {
				dos.writeUTF("Cant get guest");
			}
			System.out.print(Guest);
			dos.writeUTF(Guest);
			
		} catch (Exception e) {
			System.out.println("Loi khi tim nguoi xem: " + e.getMessage());
		}
    }
    
    public void GetAllFolderGuest() {
    	try {
    		String MSSV = dis.readUTF();
			int FolderID = Integer.parseInt(dis.readUTF());
			PBL_Model md = new PBL_Model();
			System.out.println(md.GetMSSVByFolderID(FolderID));
			if(!md.GetMSSVByFolderID(FolderID).equals(MSSV)) {
				dos.writeUTF("Cant get");
				return;
			}
			String Guest = md.GetAllFolderGuest(FolderID);
			if (Guest.equals("ERR")) {
				dos.writeUTF("Cant get guest");
			}
			System.out.print(Guest);
			dos.writeUTF(Guest);
			
		} catch (Exception e) {
			System.out.println("Loi khi tim nguoi xem: " + e.getMessage());
			e.printStackTrace();
		}
    }

    public static String zipFolder(String sourceFolderPath, String zipFilePath) {
        try (FileOutputStream fos = new FileOutputStream(zipFilePath);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            File sourceFolder = new File(sourceFolderPath);
            if (sourceFolder.exists() && sourceFolder.isDirectory()) {
                zipDirectory(sourceFolder, sourceFolder.getName(), zos);
            }

            System.out.println("Thư mục đã được nén thành công!");
            return zipFilePath;
        } catch (IOException e) {
            e.printStackTrace();
            return null;  // Trả về null nếu có lỗi
        }
    }


    // Hàm nén thư mục và các file trong thư mục
    private static void zipDirectory(File folderToZip, String parentFolder, ZipOutputStream zos) throws IOException {
        // Đảm bảo lấy danh sách tất cả file và thư mục trong thư mục hiện tại
        File[] files = folderToZip.listFiles();
        if (files == null || files.length == 0) {
            // Nếu thư mục rỗng, thêm nó vào file ZIP
            ZipEntry zipEntry = new ZipEntry(parentFolder + "/");
            zos.putNextEntry(zipEntry);
            zos.closeEntry();
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                // Đệ quy xử lý thư mục con
                zipDirectory(file, parentFolder + "/" + file.getName(), zos);
            } else {
                // Xử lý file
                try (FileInputStream fis = new FileInputStream(file)) {
                    // Tạo một entry trong file ZIP
                    String entryName = parentFolder + "/" + file.getName();
                    ZipEntry zipEntry = new ZipEntry(entryName);
                    zos.putNextEntry(zipEntry);

                    // Ghi dữ liệu file vào entry
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

    
    public void DownloadFolder() {
    	try {
			int FolderID = Integer.parseInt(dis.readUTF());
			
			PBL_Model md = new PBL_Model();
			String FolderPath = md.GetFolderPath(FolderID);
			if(FolderPath == null ) {
				FolderPath = "";
			}
			String FolderName = md.GetFolderNameByFolderID(FolderID);
			String baseFolder = "D:\\2024\\PBL4\\FileData";
			String sourceFolderPath = Paths.get(baseFolder, FolderPath, FolderName).toString(); // Đường dẫn thư mục cần nén
	        String zipFilePath = sourceFolderPath + ".zip"; // Đường dẫn file ZIP
	        
	        String result = zipFolder(sourceFolderPath, zipFilePath);
	        File FolderZip = new File(result);
	        dos.writeUTF(FolderZip.getName()); // Ghi tên file
	        dos.writeUTF(String.valueOf(FolderZip.length())); // Chuyển kích thước file (long) thành chuỗi trước khi ghi

	        sendfile(zipFilePath);
	        File zipFile = new File(zipFilePath);
            if (zipFile.exists()) {
                if (zipFile.delete()) {
                    System.out.println("File ZIP đã được xóa sau khi giải nén: " + zipFilePath);
                } else {
                    System.out.println("Không thể xóa file ZIP: " + zipFilePath);
                }
            } else {
                System.out.println("File ZIP không tồn tại: " + zipFilePath);
            }
	        
		} catch (Exception e) {
			System.out.println("Loi khi tim nguoi xem: " + e.getMessage());
			e.printStackTrace();
		}
    }
    
    public void UploadFolder() {
    	try {
    		String MSSV = dis.readUTF();
    		String FolderName = dis.readUTF();
    		int ParentFolderID = Integer.parseInt(dis.readUTF());
    		long FolderSize = Long.parseLong(dis.readUTF());
    		
			PBL_Model md =new PBL_Model();
			
			if(!md.GetMSSVByFolderID(ParentFolderID).equals(MSSV)) {
				dos.writeUTF("MSSV cant Upload this file here");
				return;
			}
			
			if(md.CheckFolderExits(ParentFolderID, FolderName)) {
				dos.writeUTF("FoldeeName already exits");
				return;
			}
			String baseFolder = "D:\\2024\\PBL4\\FileData";
			String ParentPath = md.GetFolderPath(ParentFolderID);
			String ParentName = md.GetFolderNameByFolderID(ParentFolderID);
			if(ParentPath == null) {
				ParentPath = "";
			}
			ParentPath = Paths.get(ParentPath,ParentName).toString();
			String FullParentPath = Paths.get(baseFolder,ParentPath).toString();
			String FolderPath  = Paths.get(ParentPath,FolderName).toString();
			String zipFilePath = Paths.get(FullParentPath, FolderName+".zip").toString();
			String savePath = "D:\\2024\\PBL4\\FileData\\" + ParentPath + "\\" + FolderName + ".zip";
	        File saveDir = new File("D:\\2024\\PBL4\\FileData\\" + ParentPath);
	        System.out.println(ParentPath + "  " + FolderName);
	        // Tạo thư mục nếu chưa tồn tại
	        if (!saveDir.exists()) {
	            saveDir.mkdirs();
	        }

	        File file = new File(savePath);
	        // Kiểm tra tên file đã tồn tại chưa, nếu có thì đổi tên
	        if (file.exists()) {
	            String baseName = FolderName.substring(0, FolderName.lastIndexOf("."));
	            String extension = FolderName.substring(FolderName.lastIndexOf("."));
	            int counter = 1;

	            while (file.exists()) {
	                String newFilename = baseName + "_" + counter + extension;
	                file = new File("D:\\2024\\PBL4\\FileData\\" + ParentPath + "\\" + newFilename);
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
	            while (totalBytesRead < FolderSize && (bytesRead = bis.read(buffer, 0, buffer.length)) != -1) {
	                bos.write(buffer, 0, bytesRead);
	                totalBytesRead += bytesRead;
	                System.out.println("Đã nhận được: " + totalBytesRead + " / " + FolderSize + " bytes");
	            }

	            bos.flush();

	            // Kiểm tra xem file đã nhận đủ chưa
	            if (totalBytesRead == FolderSize) {
	                System.out.println("Đã nhận đầy đủ file và lưu tại: " + savePath);
	                dos.writeUTF("Upload successfully");
	            } else {
	                System.out.println("Cảnh báo: File nhận không đầy đủ! Đã nhận: " + totalBytesRead + " / " + FolderSize);
	                dos.writeUTF("Upload folder failed");
	                return;
	            }
	        } catch (IOException e) {
	            System.out.println("Lỗi khi nhận file: " + e.getMessage());
	            dos.writeUTF("Upload folder failed");
                return;
	        }
	     
			
			File destDir = new File(FullParentPath);
	        if (!destDir.exists()) {
	            destDir.mkdirs(); // Tạo thư mục đích nếu chưa tồn tại
	        }
	        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath))) {
	            ZipEntry entry = zipIn.getNextEntry();
	            // Duyệt qua các entry trong file ZIP
	            while (entry != null) {
	                String filePath = FullParentPath + File.separator + entry.getName();
	                if (!entry.isDirectory()) {
	                    // Nếu entry là file, giải nén file
	                    extractFile(zipIn, filePath);
	                } else {
	                    // Nếu entry là folder, tạo folder
	                    File dir = new File(filePath);
	                    dir.mkdirs();
	                }
	                zipIn.closeEntry();
	                entry = zipIn.getNextEntry();
	            }
	        }catch (Exception e) {
				e.printStackTrace();
			}
	        
	        File zipFile = new File(zipFilePath);
            if (zipFile.exists()) {
                if (zipFile.delete()) {
                    System.out.println("File ZIP đã được xóa sau khi giải nén: " + zipFilePath);
                } else {
                    System.out.println("Không thể xóa file ZIP: " + zipFilePath);
                }
            } else {
                System.out.println("File ZIP không tồn tại: " + zipFilePath);
            }

			
	        
	        File director = new File("D:\\2024\\PBL4\\FileData\\" + ParentPath + "\\" + FolderName);
	        AddFolderFile(director, ParentFolderID);
	        System.out.println("Add full file and folder");
			
		} catch (Exception e) {
			System.out.println("ERR: " + e.getMessage());
			e.printStackTrace();
		}
    }
    
    public void AddFolderFile(File dir, int FolderParent) {
    	if (!dir.exists()) {
            System.out.println("Thư mục không tồn tại: " + dir.getAbsolutePath());
            return;
        }
    	try {
    		
    		PBL_Model md = new PBL_Model();
    		String MSSV = md.GetMSSVByFolderID(FolderParent);
    		if (dir.isDirectory()) {
                File[] files = dir.listFiles();
                String ParentName = md.GetFolderNameByFolderID(FolderParent);
        		String ParentPath = md.GetFolderPath(FolderParent);
        		
        		if(ParentPath == null) {
        			ParentPath = "";
        		}
        		int FolderID = md.CreateFolder(dir.getName(), FolderParent, ParentPath + "\\" + ParentName);
        		
        		md.CreateFolderrRole(MSSV, FolderID, 0);
                if (files != null) {
                    for (File file : files) {
                    	AddFolderFile(file, FolderID);
                    }
                }
            }else {
            	int FileID = md.CreateFile(dir.getName(), FolderParent, dir.length());
            	md.CreateFileRole(MSSV, FileID, 0);
            	double data = md.GetData(MSSV) + (double) dir.length();
            	md.UpdateDataUser(MSSV, data);
            }
		} catch (Exception e) {
			System.out.println("ERR: " + e.getMessage());
			e.printStackTrace();
		}
    }
    
    
    public void Search() {
    	try {
			String MSSV = dis.readUTF();
			String Word = dis.readUTF();
			
			PBL_Model md = new PBL_Model();
			
			String Folder = md.SreachFolder(MSSV, Word);
			String File = md.SreachFile(MSSV, Word);
			
			dos.writeUTF(Folder);
			dos.writeUTF(File);
			dos.flush();
			
		} catch (Exception e) {
			System.out.println("ERR: " + e.getMessage());
			e.printStackTrace();
		}
    }
    
    public void GetAllUser() {
    	try {
			String MSSV = dis.readUTF();
			PBL_Model md = new PBL_Model();
			if(md.getRole(MSSV) != 0) {
				dos.writeUTF("you are not an administrator");
				dos.flush();
				return;
			}
			String Users = md.GetAllUser();
			dos.writeUTF(Users);
			dos.flush();
		} catch (Exception e) {
			System.out.println("ERR: " + e.getMessage());
			e.printStackTrace();
		}
    }
    
    public void ResetPassword() {
    	try {
			String MSSV = dis.readUTF();
			PBL_Model md = new PBL_Model();
			
			if(!md.CheckMSSV(MSSV)) {
				dos.writeUTF("MSSV not found");
				return;
			}
			String NewPass = md.ResetPassword(MSSV);
			if(NewPass.equals("ERR")) {
				dos.writeUTF("Cant reset password");
				return;
			}
			dos.writeUTF(NewPass);
			dos.flush();
		} catch (Exception e) {
			System.out.println("ERR: " + e.getMessage());
			e.printStackTrace();
		}
    }
}

