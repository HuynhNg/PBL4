package org.pbl4.java;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

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
    		String FolderName = dis.readUTF();
    		int FolderParent = Integer.parseInt(dis.readUTF());
    		PBL_Model md =new PBL_Model();
    		
    		if(md.CheckFolderExits(FolderParent, FolderName)) {
    			dos.writeUTF("Folder already exits");
    		}
    		
    		String ParentName = md.GetFolderNameByFolderID(FolderParent);
    		String ParentPath = md.GetFolderPath(FolderParent);
    		
    		if(ParentPath == null) {
    			ParentPath = "";
    		}
    		if(md.CreateFolder(FolderName, FolderParent, ParentPath + "\\" + ParentName) == -1) {
    			dos.writeUTF("Create folder failded");
    			return;
    		}
    		CreateFolder(ParentPath + "\\" + ParentName, FolderName);
    		dos.writeUTF("Create folder successfully");
    		
    	}catch (Exception e) {
    		System.out.println("Error: " + e.getMessage());
    		e.printStackTrace();
		}
    }
    
    public void ChangeFolderName() {
        try {
            int FolderID = Integer.parseInt(dis.readUTF());
            String FolderName = dis.readUTF();

            PBL_Model md = new PBL_Model();
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
            int FolderID = Integer.parseInt(dis.readUTF());

            // Lấy thông tin thư mục từ model
            PBL_Model md = new PBL_Model();
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



}

