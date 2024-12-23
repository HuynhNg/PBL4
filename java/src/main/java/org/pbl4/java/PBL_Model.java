package org.pbl4.java;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class PBL_Model {
	public static void main(String[] args){
		PBL_Model md = new PBL_Model();
//		System.out.println(md.Register("102220026", "123", "Nguyen Van B", "22T_DT1"));
//		System.out.println(md.Login("102220024", "123"));
//		System.out.println(md.GetPassword("102220024"));
//		try {
//			md.ChangePassword("102220024", "1234");
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		System.out.println(md.GetUserByMSSV("102220024"));
//		System.out.println(md.UpdateUser("102220024", "Nguyen Huyn", "22T_KHDL"));
//		System.out.println(md.CreateFolder("ab", 9, "102220025"));
//		System.out.println(md.UpdateFolderName(8, "NoteBook"));
//		System.out.println(md.GetFolderNameByFolderID(6));
//		System.out.println(md.DeleteFolder(8));
//		System.out.println(md.CreateFolderrRole("102220024", 15, 1));
//		System.out.println(md.DeleteFolderRole("102220025", 6));
//		System.out.println(md.CreateFile("abc.docx", 6, 10.5));
//		System.out.println(md.DeleteFile(3));
//		System.out.println(md.GetFileSize(4));
//		System.out.println(md.UpdateLocation(4, 9));
//		System.out.println(md.CreateFileRole("102220024", 4, 1));
//		System.out.println(md.DeleteFileRole("102220024", 4));
//		System.out.println(md.GetAllFileNameByMSSV("102220024"));
//		System.out.println(md.GetMyFileNameByMSSV("102220024"));
//		System.out.println(md.GetGuestFileNameByMSSV("102220025"));
//		System.out.println(md.GetFileNameByFolderID(9));
//		System.out.println(md.GetAllFolderbyParent(9));
//		System.out.println(md.GetFolderRoot("102220024"));
//		System.out.println(md.GetAllGuestFolder("102220024"));
//		System.out.println(md.GetAllFileByFolderID(9));
//		System.out.println(md.GetFolderPath(6));
//		System.out.println(md.CheckFolderExits(9, "abc"));
	}
	public Connection GetConnection() {
		String Database = "jdbc:mysql://localhost:3306/filemanagement";
        String Username = "root";
        String Password = "";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection(Database, Username, Password);
//             System.out.println("Connect database successfully");
            return con;

        } catch (ClassNotFoundException e) {
            System.out.println("Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Err connect database: " + e.getMessage());
        }
        return null;
	}
	
	
	//User
	public boolean CheckMSSV(String MSSV) {
		Connection con = GetConnection();
        if (con == null) {
            System.out.println("Cant connect with database");
            return false;
        }
        try {
            String query = "SELECT * FROM users WHERE MSSV = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, MSSV);
            ResultSet  rs = stmt.executeQuery();
            if(!rs.next()){
                return false;
            }
            return true;

        } catch (Exception e) {
            System.err.println("Err:" + e);
            return false;
        }finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Err close the database: " + e.getMessage());
            }
        }
	}
	public boolean Login(String MSSV, String Password) {
		Connection con = GetConnection();
        if (con == null) {
            System.out.println("Cant connect with database");
            return false;
        }

        String query = "SELECT * FROM Account WHERE MSSV = ? AND Password = ?";

        try {
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, MSSV);
            pstmt.setString(2, Password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Error query: " + e.getMessage());
            return false;
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Err close the database: " + e.getMessage());
            }
        }
	}
	
	public boolean Register(String MSSV, String Password, String Name, String Class) {
	    Connection con = GetConnection();
	    if (con == null) {
	        System.out.println("Cannot connect to the database");
	        return false;
	    }
	    PreparedStatement pstmt = null;
	    try {
	        String query = "INSERT INTO Users (MSSV, Name, Class) VALUES (?, ?, ?)";
	        pstmt = con.prepareStatement(query);
	        pstmt.setString(1, MSSV);
	        pstmt.setString(2, Name);
	        pstmt.setString(3, Class);
	        pstmt.executeUpdate();

	        query = "INSERT INTO Account (MSSV, Password) VALUES (?, ?)";
	        pstmt = con.prepareStatement(query);
	        pstmt.setString(1, MSSV);
	        pstmt.setString(2, Password);
	        pstmt.executeUpdate();
	        
	        int FolderID = CreateFolder(MSSV, 0,"");
	        CreateFolderrRole(MSSV, FolderID, 0);
	        
	        pstmt.close();
	        con.close();
	        return true;
	    } catch (Exception e) {
	        System.err.println("Error: " + e.getMessage());
	        return false;
	    } finally {
	        try {
	            if (con != null) {
	                con.close();
	            }
	        } catch (SQLException e) {
	            System.out.println("Error closing the database: " + e.getMessage());
	        }
	    }
	}

	public String GetPassword(String MSSV){
		Connection con = GetConnection();
		if(con == null) {
			System.out.println("Cant connect with database");
			return "ERR";
		}
		try {
			String query = "Select Password from Account where MSSV = ?";
			PreparedStatement pstmt = con.prepareStatement(query);
			pstmt.setString(1, MSSV);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				return rs.getString("Password");
			}
			return "ERR";
		} catch (Exception e) {
			System.err.println("Err Password:" + e);
            return "ERR";
		}finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Err close the database: " + e.getMessage());
            }
        }
	}
	
	public Double GetData(String MSSV) {
		Connection con = GetConnection();
		if(con == null) {
			System.out.println("Cant connect with database");
			return -1.0;
		}
		try {
			String query = "Select Data from Users where MSSV = ?";
			PreparedStatement pstmt = con.prepareStatement(query);
			pstmt.setString(1, MSSV);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				return rs.getDouble("Data");
			}
			return -1.0;
		} catch (Exception e) {
			System.err.println("Err Password:" + e);
            return -1.0;
		}finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Err close the database: " + e.getMessage());
            }
        }
	}
	
	public boolean ChangePassword(String MSSV, String Password){
		Connection con = GetConnection();
		if(con == null) {
			System.out.println("Cant connect with database");
			return false;
		}
		PreparedStatement pstmt = null;
		try {
	        String query = "Update Account set Password = ? where MSSV = ?";
	        pstmt = con.prepareStatement(query);
	        pstmt.setString(1, Password);
	        pstmt.setString(2, MSSV);
	        pstmt.executeUpdate();
	        pstmt.close();
	        con.close();
	        return true;
	    } catch (Exception e) {
	        System.err.println("Error: " + e.getMessage());
	        return false; 
	    } finally {
	        try {
	            if (con != null) {
	                con.close();
	            }
	        } catch (SQLException e) {
	            System.out.println("Error closing the database: " + e.getMessage());
	        }
	    }
	}
	
	public String GetUserByMSSV(String MSSV) {
		Connection con = GetConnection();
		if(con == null) {
			System.out.println("Cant connect with database");
			return "ERR";
		}
		try {
			String query = "Select * from Users where MSSV = ?";
			PreparedStatement pstmt = con.prepareStatement(query);
			pstmt.setString(1, MSSV);
			ResultSet rs = pstmt.executeQuery();
			StringBuilder result = new StringBuilder();
			if(rs.next()) {
				int columnCount = rs.getMetaData().getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    result.append(rs.getMetaData().getColumnName(i)).append(": ")
                          .append(rs.getString(i)).append("\n");
                }
			}else {
				result.append("User not found");
			}
			rs.close();
			pstmt.close();
			con.close();
			return result.toString();
		} catch (Exception e) {
	        System.err.println("Error: " + e.getMessage());
	        return "ERR"; 
	    } finally {
	        try {
	            if (con != null) {
	                con.close();
	            }
	        } catch (SQLException e) {
	            System.out.println("Error closing the database: " + e.getMessage());
	        }
	    }
	}
	
	public boolean UpdateUser(String MSSV, String Name, String Class, int Role) {
		Connection con = GetConnection();
		if(con == null) {
			System.out.println("Cant connect with database");
			return false;
		}
		try {
            String query = "UPDATE Users SET Name = ?, Class = ? , Role = ? WHERE MSSV = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1,Name);
            pstmt.setString(2,Class);
            pstmt.setString(3,MSSV);
            pstmt.setInt(4, Role);
            pstmt.executeUpdate();
            pstmt.close();
            con.close();
            return true;

        } catch (Exception e) {
            System.err.println("Err UpdateInfor:" + e);
            return false;
        }finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Err close the database: " + e.getMessage());
            }
        }
	}
	
	public boolean UpdateDataUser(String MSSV, Double Data) {
		Connection con = GetConnection();
		if(con == null) {
			System.out.println("Cant connect with database");
			return false;
		}
		try {
            String query = "UPDATE Users SET Data = ? WHERE MSSV = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setDouble(1,Data);
            pstmt.setString(2,MSSV);
            pstmt.executeUpdate();
            pstmt.close();
            con.close();
            return true;

        } catch (Exception e) {
            System.err.println("Err UpdateInfor:" + e);
            return false;
        }finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Err close the database: " + e.getMessage());
            }
        }
	}
	
	
	//Folder
	public int CreateFolder(String FolderName, int Parent, String FolderPath) {
	    Connection con = GetConnection();
	    if (con == null) {
	        System.out.println("Cannot connect with the database");
	        return -1; 
	    }
	    PreparedStatement pstmt = null;
	    ResultSet generatedKeys = null;

	    try {
	        String query = "INSERT INTO Folders (FolderName, FolderParent, FolderPath) VALUES (?, ?, ?)";
	        pstmt = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
	        pstmt.setString(1, FolderName);

	        if (Parent == 0) {
	            pstmt.setNull(2, java.sql.Types.INTEGER);
	        } else {
	            pstmt.setInt(2, Parent);
	        }
	        if (FolderPath.equals("")) {
	        	pstmt.setNull(3, java.sql.Types.VARCHAR);
	        }else {
	        	pstmt.setString(3, FolderPath);
	        }
	        pstmt.executeUpdate();

	        generatedKeys = pstmt.getGeneratedKeys();
	        if (generatedKeys.next()) {
	            return generatedKeys.getInt(1); 
	        }
	        return -1;
	    } catch (Exception e) {
	        System.err.println("Error: " + e.getMessage());
	    } finally {
	        try {
	            if (generatedKeys != null) generatedKeys.close();
	            if (pstmt != null) pstmt.close();
	            if (con != null) con.close();
	        } catch (SQLException e) {
	            System.out.println("Error closing the database: " + e.getMessage());
	        }
	    }
	    return -1; // Trả về -1 nếu có lỗi
	}

	
	public boolean UpdateFolderName(int FolderID, String FolderName) {
		Connection con = GetConnection();
		if(con == null) {
			System.out.println("Cant connect with database");
			return false;
		}
		PreparedStatement pstmt = null;
		try {
	    	String query = "Update Folders set FolderName = ? where FolderID = ?";
	        pstmt = con.prepareStatement(query);
	        pstmt.setString(1, FolderName);
	        pstmt.setInt(2, FolderID);
	        pstmt.executeUpdate();

	        pstmt.close();
	        con.close();
	        return true;
	    } catch (Exception e) {
	        System.err.println("Error: " + e.getMessage());
	        return false;
	    } finally {
	        try {
	            if (con != null) {
	                con.close();
	            }
	        } catch (SQLException e) {
	            System.out.println("Error closing the database: " + e.getMessage());
	        }
	    }
	}
	
	public String GetFolderNameByFolderID(int FolderID) {
		Connection con = GetConnection();
		if(con == null) {
			System.out.println("Cant connect with database");
			return "ERR";
		}
		try {
			String query = "Select FolderName from Folders where FolderID = ?";
			PreparedStatement pstmt = con.prepareStatement(query);
			pstmt.setInt(1, FolderID);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				return rs.getString("FolderName");
			}
			return "ERR";
		} catch (Exception e) {
			System.err.println("Err: " + e);
            return "ERR";
		}finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Err close the database: " + e.getMessage());
            }
        }
	}
	
	public boolean DeleteFolder(int FolderID) {
		Connection con = GetConnection();
		if(con == null) {
			System.out.println("Cant connect with database");
			return false;
		}
		PreparedStatement pstmt = null;
		try {
	    	String query = "Delete from Folders where FolderID = ?";
	        pstmt = con.prepareStatement(query);
	        pstmt.setInt(1, FolderID);
	        pstmt.executeUpdate();

	        pstmt.close();
	        con.close();
	        return true;
	    } catch (Exception e) {
	        System.err.println("Error: " + e.getMessage());
	        return false;
	    } finally {
	        try {
	            if (con != null) {
	                con.close();
	            }
	        } catch (SQLException e) {
	            System.out.println("Error closing the database: " + e.getMessage());
	        }
	    }
	}
	
	public boolean CreateFolderrRole(String MSSV,int FolderID, int Role) {
		Connection con = GetConnection();
		if(con == null) {
			System.out.println("Cant connect with database");
			return false;
		}
		PreparedStatement pstmt = null;
	    try {
	    	String query = "INSERT INTO FolderRole (MSSV, FolderID, Role) VALUES (?, ?, ?)";
	        pstmt = con.prepareStatement(query);
	        pstmt.setString(1, MSSV);
	        pstmt.setInt(2, FolderID);
	        pstmt.setInt(3, Role);
	        
	        pstmt.executeUpdate();
	        
	        pstmt.close();
	        con.close();
	        return true;
	    } catch (Exception e) {
	        System.err.println("Error: " + e.getMessage());
	        return false;
	    } finally {
	        try {
	            if (con != null) {
	                con.close();
	            }
	        } catch (SQLException e) {
	            System.out.println("Error closing the database: " + e.getMessage());
	        }
	    }
	}
	public boolean DeleteFolderRole(String MSSV, int FolderID) {
		Connection con = GetConnection();
		if(con == null) {
			System.out.println("Cant connect with database");
			return false;
		}
		PreparedStatement pstmt = null;
	    try {
	    	String query = "Delete from FolderRole where MSSV = ? and FolderID = ?";
	        pstmt = con.prepareStatement(query);
	        pstmt.setString(1, MSSV);
	        pstmt.setInt(2, FolderID);
	        
	        pstmt.executeUpdate();
	        
	        pstmt.close();
	        con.close();
	        return true;
	    } catch (Exception e) {
	        System.err.println("Error: " + e.getMessage());
	        return false;
	    } finally {
	        try {
	            if (con != null) {
	                con.close();
	            }
	        } catch (SQLException e) {
	            System.out.println("Error closing the database: " + e.getMessage());
	        }
	    }
	}
	
	
	//File
	public int CreateFile(String FileName, int FolderID, double FileSize) {
	    Connection con = GetConnection();
	    if (con == null) {
	        System.out.println("Can't connect with database");
	        return -1;
	    }
	    ResultSet generatedKeys = null;
	    try {
	        String query = "INSERT INTO files (FileName, FolderID, FileSize) VALUES (?, ?, ?)";
	        PreparedStatement pstmt = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
	        pstmt.setString(1, FileName);
	        pstmt.setInt(2, FolderID);
	        pstmt.setDouble(3, FileSize);
	        pstmt.executeUpdate();

	        generatedKeys = pstmt.getGeneratedKeys();
	        if (generatedKeys.next()) {
	            return generatedKeys.getInt(1);
	        }
	        return -1;

	    } catch (Exception e) {
	        System.out.println("ERR addFile : " + e.getMessage());
	        return -1;
	    } finally {
	        try {
	            if (generatedKeys != null) {
	                generatedKeys.close();
	            }
	            if (con != null) {
	                con.close();
	            }
	        } catch (SQLException e) {
	            System.out.println("Err close the database: " + e.getMessage());
	        }
	    }
	}
	
	public boolean UpdateFileName(int FileID, String FileName) {
		Connection con = GetConnection();
		if(con == null) {
			System.out.println("Cant connect with database");
			return false;
		}
		PreparedStatement pstmt = null;
		try {
	    	String query = "Update Files set FileName = ? where FileID = ?";
	        pstmt = con.prepareStatement(query);
	        pstmt.setString(1, FileName);
	        pstmt.setInt(2, FileID);
	        pstmt.executeUpdate();

	        pstmt.close();
	        con.close();
	        return true;
	    } catch (Exception e) {
	        System.err.println("Error: " + e.getMessage());
	        return false;
	    } finally {
	        try {
	            if (con != null) {
	                con.close();
	            }
	        } catch (SQLException e) {
	            System.out.println("Error closing the database: " + e.getMessage());
	        }
	    }
	}
	
	public double GetFileSize(int FileID) {
		Connection con = GetConnection();
		if(con == null) {
			System.out.println("Cant connect with database");
			return -1.0;
		}
		try {
			String query = "Select FileSize from Files where FileID = ?";
			PreparedStatement pstmt = con.prepareStatement(query);
			pstmt.setInt(1, FileID);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				return rs.getDouble("FileSIze");
			}
			return -1.0;
		} catch (Exception e) {
			System.err.println("Err:" + e);
            return -1.0;
		}finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Err close the database: " + e.getMessage());
            }
        }
	}
	
	public boolean UpdateLocation(int FileID, int FolderID) {
		Connection con = GetConnection();
		if(con == null) {
			System.out.println("Cant connect with database");
			return false;
		}
		try {
            String query = "UPDATE Files SET FolderID = ? WHERE FileID = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setDouble(1,FolderID);
            pstmt.setInt(2,FileID);
            pstmt.executeUpdate();
            pstmt.close();
            con.close();
            return true;

        } catch (Exception e) {
            System.err.println("Err UpdateInfor:" + e);
            return false;
        }finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Err close the database: " + e.getMessage());
            }
        }
	}

	
	public boolean DeleteFile(int FileID) {
		Connection con = GetConnection();
		if(con == null) {
			System.out.println("Cant connect with database");
			return false;
		}
		PreparedStatement pstmt = null;
		try {
	    	String query = "Delete from Files where FileID = ?";
	        pstmt = con.prepareStatement(query);
	        pstmt.setInt(1, FileID);
	        pstmt.executeUpdate();

	        pstmt.close();
	        con.close();
	        return true;
	    } catch (Exception e) {
	        System.err.println("Error: " + e.getMessage());
	        return false;
	    } finally {
	        try {
	            if (con != null) {
	                con.close();
	            }
	        } catch (SQLException e) {
	            System.out.println("Error closing the database: " + e.getMessage());
	        }
	    }
	}
	
	public boolean CreateFileRole(String MSSV, int FileID, int Role) {
		Connection con = GetConnection();
		if(con == null) {
			System.out.println("Cant connect with database");
			return false;
		}
		PreparedStatement pstmt = null;
	    try {
	    	String query = "INSERT INTO FileRole (MSSV, FileID, Role) VALUES (?, ?, ?)";
	        pstmt = con.prepareStatement(query);
	        pstmt.setString(1, MSSV);
	        pstmt.setInt(2, FileID);
	        pstmt.setInt(3, Role);
	        
	        pstmt.executeUpdate();
	        
	        pstmt.close();
	        con.close();
	        return true;
	    } catch (Exception e) {
	        System.err.println("Error: " + e.getMessage());
	        return false;
	    } finally {
	        try {
	            if (con != null) {
	                con.close();
	            }
	        } catch (SQLException e) {
	            System.out.println("Error closing the database: " + e.getMessage());
	        }
	    }
	}
	
	public boolean DeleteFileRole(String MSSV, int FileID) {
		Connection con = GetConnection();
		if(con == null) {
			System.out.println("Cant connect with database");
			return false;
		}
		PreparedStatement pstmt = null;
	    try {
	    	String query = "Delete from FileRole where MSSV = ? and FileID = ?";
	        pstmt = con.prepareStatement(query);
	        pstmt.setString(1, MSSV);
	        pstmt.setInt(2, FileID);
	        
	        pstmt.executeUpdate();
	        
	        pstmt.close();
	        con.close();
	        return true;
	    } catch (Exception e) {
	        System.err.println("Error: " + e.getMessage());
	        return false;
	    } finally {
	        try {
	            if (con != null) {
	                con.close();
	            }
	        } catch (SQLException e) {
	            System.out.println("Error closing the database: " + e.getMessage());
	        }
	    }
	}
	
	
	
	//Show
	public String GetAllFileNameByMSSV(String MSSV) {
		Connection con = GetConnection();
        if (con == null) {
            System.out.println("Can't connect with database");
            return "ERR";
        }
        try {
            String query = "SELECT f.FileID,f.FileName FROM Files f " +
                           "JOIN FileRole a ON f.FileID = a.FileID " +
                           "WHERE a.MSSV = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, MSSV);
            String result = "";
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result +=rs.getString("FileID") + ","+  rs.getString("FileName") + ";";
            }
            if (result.endsWith(";")) {
                result = result.substring(0, result.length() - 1);
            }
            return result;
        } catch (Exception e) {
            System.out.println("ERR getFile : " + e.getMessage());
            return "ERR";
        }finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Err close the database: " + e.getMessage());
            }
        }
	}
	
	public String GetMyFileNameByMSSV(String MSSV) {
		Connection con = GetConnection();
        if (con == null) {
            System.out.println("Can't connect with database");
            return "ERR";
        }
        try {
            String query = "SELECT f.FileID,f.FileName FROM Files f " +
                           "JOIN FileRole a ON f.FileID = a.FileID " +
                           "WHERE a.MSSV = ? and a.Role = 0";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, MSSV);
            String result = "";
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result +=rs.getString("FileID") + ","+  rs.getString("FileName") + ";";
            }
            if (result.endsWith(";")) {
                result = result.substring(0, result.length() - 1);
            }
            return result;
        } catch (Exception e) {
            System.out.println("ERR getFile : " + e.getMessage());
            return "ERR";
        }finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Err close the database: " + e.getMessage());
            }
        }
	}
	
	public String GetGuestFileNameByMSSV(String MSSV) {
		Connection con = GetConnection();
        if (con == null) {
            System.out.println("Can't connect with database");
            return "ERR";
        }
        try {
            String query = "SELECT f.FileID,f.FileName FROM Files f " +
                           "JOIN FileRole a ON f.FileID = a.FileID " +
                           "WHERE a.MSSV = ? and a.Role = 1";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, MSSV);
            String result = "";
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result +=rs.getString("FileID") + ","+  rs.getString("FileName") + ";";
            }
            if (result.endsWith(";")) {
                result = result.substring(0, result.length() - 1);
            }
            return result;
        } catch (Exception e) {
            System.out.println("ERR getFile : " + e.getMessage());
            return "ERR";
        }finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Err close the database: " + e.getMessage());
            }
        }
	}
	
	public String GetFileNameByFolderID(int FolderID) {
		Connection con = GetConnection();
        if (con == null) {
            System.out.println("Can't connect with database");
            return "ERR";
        }
        try {
            String query = "SELECT FileID, FileName FROM Files where FolderID = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, FolderID);
            String result = "";
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result +=rs.getString("FileID") + ","+  rs.getString("FileName") + ";";
            }
            if (result.endsWith(";")) {
                result = result.substring(0, result.length() - 1);
            }
            return result;
        } catch (Exception e) {
            System.out.println("ERR getFile : " + e.getMessage());
            return "ERR";
        }finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Err close the database: " + e.getMessage());
            }
        }
	}
	
	public String GetAllFolderbyParent(int FolderParent) {
		Connection con = GetConnection();
        if (con == null) {
            System.out.println("Can't connect with database");
            return "ERR";
        }
        try {
            String query = "SELECT FolderID, FolderName FROM Folders where FolderParent = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, FolderParent);
            String result = "";
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result +=rs.getString("FolderID") + ","+  rs.getString("FolderName") + ";";
            }
            if (result.endsWith(";")) {
                result = result.substring(0, result.length() - 1);
            }
            return result;
        } catch (Exception e) {
            System.out.println("ERR getFile : " + e.getMessage());
            return "ERR";
        }finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Err close the database: " + e.getMessage());
            }
        }
	}
	
	public int GetFolderRoot(String MSSV) {
		Connection con = GetConnection();
		if(con == null) {
			System.out.println("Cant connect with database");
			return -1;
		}
		try {
			String query = "Select FolderID from Folders where FolderName = ? and FolderParent IS NULL";
			PreparedStatement pstmt = con.prepareStatement(query);
			pstmt.setString(1, MSSV);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				return rs.getInt("FolderID");
			}
			return -1;
		} catch (Exception e) {
			System.err.println("Err: " + e);
            return -1;
		}finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Err close the database: " + e.getMessage());
            }
        }
	}
	
	public String GetAllGuestFolder(String MSSV) {
		Connection con = GetConnection();
        if (con == null) {
            System.out.println("Can't connect with database");
            return "ERR";
        }
        try {
            String query = "SELECT f.FolderID,f.FolderName FROM Folders f " +
                           "JOIN FolderRole a ON f.FolderID = a.FolderID " +
                           "WHERE a.MSSV = ? and a.Role = 1";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, MSSV);
            String result = "";
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result +=rs.getString("FolderID") + ","+  rs.getString("FolderName") + ";";
            }
            if (result.endsWith(";")) {
                result = result.substring(0, result.length() - 1);
            }
            return result;
        } catch (Exception e) {
            System.out.println("ERR getFile : " + e.getMessage());
            return "ERR";
        }finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Err close the database: " + e.getMessage());
            }
        }
	}
	
	public String GetAllFileByFolderID(int FolderID) {
		Connection con = GetConnection();
        if (con == null) {
            System.out.println("Can't connect with database");
            return "ERR";
        }
        try {
            String query = "SELECT FileID, FileName FROM Files where FolderID = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, FolderID);
            String result = "";
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result +=rs.getString("FileID") + ","+  rs.getString("FileName") + ";";
            }
            if (result.endsWith(";")) {
                result = result.substring(0, result.length() - 1);
            }
            return result;
        } catch (Exception e) {
            System.out.println("ERR getFile : " + e.getMessage());
            return "ERR";
        }finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Err close the database: " + e.getMessage());
            }
        }
	}
	
	public String GetFolderPath(int FolderID) {
		Connection con = GetConnection();
		if(con == null) {
			System.out.println("Cant connect with database");
			return "ERR";
		}
		try {
			String query = "Select FolderPath from Folders where FolderID = ?";
			PreparedStatement pstmt = con.prepareStatement(query);
			pstmt.setInt(1, FolderID);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				return rs.getString("FolderPath");
			}
			return "ERR";
		} catch (Exception e) {
			System.err.println("Err: " + e);
            return "ERR";
		}finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Err close the database: " + e.getMessage());
            }
        }
	}
	
	public boolean CheckFolderByFolderID(int FolderID) {
		Connection con = GetConnection();
        if (con == null) {
            System.out.println("Cant connect with database");
            return false;
        }
        try {
            String query = "SELECT * FROM folders WHERE FolderID = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, FolderID);
            ResultSet  rs = stmt.executeQuery();
            if(!rs.next()){
                return false;
            }
            return true;

        } catch (Exception e) {
            System.err.println("Err:" + e);
            return false;
        }finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Err close the database: " + e.getMessage());
            }
        }
	}
	
	public boolean CheckFolderExits(int ParentID, String FolderName) {
		Connection con = GetConnection();
        if (con == null) {
            System.out.println("Cant connect with database");
            return false;
        }
        try {
            String query = "SELECT * FROM folders WHERE FolderParent = ? and FolderName = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, ParentID);
            stmt.setString(2, FolderName);
            ResultSet  rs = stmt.executeQuery();
            if(!rs.next()){
                return false;
            }
            return true;

        } catch (Exception e) {
            System.err.println("Err:" + e);
            return false;
        }finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Err close the database: " + e.getMessage());
            }
        }
	}
	
	public int GetFolderParent(int FolderID) {
		Connection con = GetConnection();
		if(con == null) {
			System.out.println("Cant connect with database");
			return -1;
		}
		try {
			String query = "Select FolderParent from Folders where FolderID = ?";
			PreparedStatement pstmt = con.prepareStatement(query);
			pstmt.setInt(1, FolderID);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				return rs.getInt("FolderParent");
			}
			return -1;
		} catch (Exception e) {
			System.err.println("Err: " + e);
            return -1;
		}finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Err close the database: " + e.getMessage());
            }
        }
	}
	
	public boolean CheckFolderRole(String MSSV, int FolderID) {
		Connection con = GetConnection();
        if (con == null) {
            System.out.println("Cant connect with database");
            return false;
        }
        try {
            String query = "SELECT * FROM folderrole WHERE MSSV= ? and FolderID = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, MSSV);
            stmt.setInt(2, FolderID);
            ResultSet  rs = stmt.executeQuery();
            if(!rs.next()){
                return false;
            }
            return true;

        } catch (Exception e) {
            System.err.println("Err:" + e);
            return false;
        }finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Err close the database: " + e.getMessage());
            }
        }
	}
	
	public boolean CheckFileExits(String FileName, int FolderID) {
		Connection con = GetConnection();
        if (con == null) {
            System.out.println("Cant connect with database");
            return false;
        }
        try {
            String query = "SELECT * FROM files WHERE FolderID = ? and FileName = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, FolderID);
            stmt.setString(2, FileName);
            ResultSet  rs = stmt.executeQuery();
            if(!rs.next()){
                return false;
            }
            return true;

        } catch (Exception e) {
            System.err.println("Err:" + e);
            return false;
        }finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Err close the database: " + e.getMessage());
            }
        }
	}
	
	public int GetFolderIDByFileID(int FileID) {
		Connection con = GetConnection();
		if(con == null) {
			System.out.println("Cant connect with database");
			return -1;
		}
		try {
			String query = "Select FolderID from Files where FileID = ?";
			PreparedStatement pstmt = con.prepareStatement(query);
			pstmt.setInt(1, FileID);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				return rs.getInt("FolderID");
			}
			return -1;
		} catch (Exception e) {
			System.err.println("Err: " + e);
            return -1;
		}finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Err close the database: " + e.getMessage());
            }
        }
	}
	
	public String GetFileNameByFileID(int FileID) {
		Connection con = GetConnection();
		if(con == null) {
			System.out.println("Cant connect with database");
			return "ERR";
		}
		try {
			String query = "Select FileName from Files where FileID = ?";
			PreparedStatement pstmt = con.prepareStatement(query);
			pstmt.setInt(1, FileID);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				return rs.getString("FileName");
			}
			return "ERR";
		} catch (Exception e) {
			System.err.println("Err: " + e);
            return "ERR";
		}finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Err close the database: " + e.getMessage());
            }
        }
	}
	
	public String GetMSSVByFolderID(int FolderID) {
		Connection con = GetConnection();
		if(con == null) {
			System.out.println("Cant connect with database");
			return "ERR";
		}
		try {
			String query = "Select MSSV from folderrole where FolderID = ? and Role = 0";
			PreparedStatement pstmt = con.prepareStatement(query);
			pstmt.setInt(1, FolderID);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				return rs.getString("MSSV");
			}
			return "ERR";
		} catch (Exception e) {
			System.err.println("Err: " + e);
            return "ERR";
		}finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Err close the database: " + e.getMessage());
            }
        }
	}
	
	public String GetMSSVByFileID(int FileID) {
		Connection con = GetConnection();
		if(con == null) {
			System.out.println("Cant connect with database");
			return "ERR";
		}
		try {
			String query = "Select MSSV from filerole where FileID = ? and Role = 0";
			PreparedStatement pstmt = con.prepareStatement(query);
			pstmt.setInt(1, FileID);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				return rs.getString("MSSV");
			}
			return "ERR";
		} catch (Exception e) {
			System.err.println("Err: " + e);
            return "ERR";
		}finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Err close the database: " + e.getMessage());
            }
        }
	}
	
	public String GetAllFileGuest(int FileID) {
		Connection con = GetConnection();
    	if (con == null) {
            System.out.println("Can't connect with database");
            return "ERR";
        }
    	try {
    		String query = "Select MSSV from filerole  where FileID = ? and Role = 1";
		     PreparedStatement pstmt = con.prepareStatement(query);
		     pstmt.setInt(1, FileID);
		     ResultSet rs  = pstmt.executeQuery();
		     String result = "";
		     while (rs.next()) {
	                result += rs.getString("MSSV") + ",";
	            }
	            if (result.endsWith(",")) {
	                result = result.substring(0, result.length() - 1);
	            }
	         return result;
			
		} catch (Exception e) {
            System.out.println("ERR: " + e.getMessage());
            return "ERR";
        }finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Err close the database: " + e.getMessage());
            }
        }
	}
	
	public String GetAllFolderGuest(int FolderID) {
		Connection con = GetConnection();
    	if (con == null) {
            System.out.println("Can't connect with database");
            return "ERR";
        }
    	try {
    		String query = "Select MSSV from folderrole  where FolderID = ? and Role = 1";
		     PreparedStatement pstmt = con.prepareStatement(query);
		     pstmt.setInt(1, FolderID);
		     ResultSet rs  = pstmt.executeQuery();
		     String result = "";
		     while (rs.next()) {
	                result += rs.getString("MSSV") + ",";
	            }
	            if (result.endsWith(",")) {
	                result = result.substring(0, result.length() - 1);
	            }
	         return result;
			
		} catch (Exception e) {
            System.out.println("ERR: " + e.getMessage());
            return "ERR";
        }finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Err close the database: " + e.getMessage());
            }
        }
	}
	
	
	public String SreachFolder(String MSSV, String Word) {
		Connection con = GetConnection();
        if (con == null) {
            System.out.println("Can't connect with database");
            return "ERR";
        }
        try {
            String query = "SELECT f.FolderID,f.FolderName FROM Folders f " +
                           "JOIN FolderRole a ON f.FolderID = a.FolderID " +
                           "WHERE a.MSSV = ? and f.FolderName LIKE CONCAT('%', ?, '%');";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, MSSV);
            pstmt.setString(2, Word);
            String result = "";
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result +=rs.getString("FolderID") + ","+  rs.getString("FolderName") + ";";
            }
            if (result.endsWith(";")) {
                result = result.substring(0, result.length() - 1);
            }
            return result;
        } catch (Exception e) {
            System.out.println("ERR: " + e.getMessage());
            return "ERR";
        }finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Err close the database: " + e.getMessage());
            }
        }
	}
	
	public String SreachFile(String MSSV, String Word) {
		Connection con = GetConnection();
        if (con == null) {
            System.out.println("Can't connect with database");
            return "ERR";
        }
        try {
            String query = "SELECT f.FileID,f.FileName FROM Files f " +
                           "JOIN FileRole a ON f.FileID = a.FileID " +
                           "WHERE a.MSSV = ? and f.FileName LIKE CONCAT('%', ?, '%');";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, MSSV);
            pstmt.setString(2, Word);
            String result = "";
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result +=rs.getString("FileID") + ","+  rs.getString("FileName") + ";";
            }
            if (result.endsWith(";")) {
                result = result.substring(0, result.length() - 1);
            }
            return result;
        } catch (Exception e) {
            System.out.println("ERR: " + e.getMessage());
            return "ERR";
        }finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Err close the database: " + e.getMessage());
            }
        }
	}
	
	public String GetAllUser() {
		Connection con = GetConnection();
        if (con == null) {
            System.out.println("Can't connect with database");
            return "ERR";
        }
        try {
            String query = "Select MSSV, Name From Users where Role = 1";
            PreparedStatement pstmt = con.prepareStatement(query);
            String result = "";
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result +=rs.getString("MSSV") + ","+  rs.getString("Name") + ";";
            }
            if (result.endsWith(";")) {
                result = result.substring(0, result.length() - 1);
            }
            return result;
        } catch (Exception e) {
            System.out.println("ERR: " + e.getMessage());
            return "ERR";
        }finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Err close the database: " + e.getMessage());
            }
        }
	}
	
	public String ResetPassword(String MSSV) {
		Connection con = GetConnection();
        if (con == null) {
            System.out.println("Can't connect with database");
            return "ERR";
        }
        try { 
        	Random random = new Random();
        	int randomNumber = 100000 + random.nextInt(900000);
        	String Password = Integer.toString(randomNumber);
            String query = "UPDATE Account SET Password = ? WHERE MSSV = ?;";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, Password);
            pstmt.setString(2, MSSV);
            pstmt.executeUpdate();
            return Password;
        } catch (Exception e) {
            System.out.println("ERR: " + e.getMessage());
            return "ERR";
        }finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Err close the database: " + e.getMessage());
            }
        }
	}
	
	public int getRole(String MSSV) {
		Connection con = GetConnection();
        if (con == null) {
            System.out.println("Cant connect with database");
            return -1;
        }
        try {
            String query = "SELECT * FROM Users where MSSV = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, MSSV);
            ResultSet  rs = stmt.executeQuery();
            if(rs.next()) {
            	return rs.getInt("Role");
            }
            return -1;
        } catch (Exception e) {
            System.err.println("Err:" + e);
            return -1;
        }finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Err close the database: " + e.getMessage());
            }
        }
	}
}
