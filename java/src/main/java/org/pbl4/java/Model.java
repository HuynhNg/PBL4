package org.pbl4.java;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Model {
    public Connection GetConnection() {
        String Database = "jdbc:mysql://localhost:3306/pbl4";
        String Username = "root";
        String Password = "";

        try {
            // Sử dụng driver mới
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Thiết lập kết nối
            Connection con = DriverManager.getConnection(Database, Username, Password);
            // System.out.println("Connect database successfully");
            return con;

        } catch (ClassNotFoundException e) {
            System.out.println("Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Err connect database: " + e.getMessage());
        }
        return null;
    }

    //User
    public boolean CheckLogin(String MSSV, String Password) {
        Connection con = GetConnection();
        if (con == null) {
            System.out.println("Cant connect with database");
            return false;
        }

        String query = "SELECT * FROM users WHERE MSSV = ? AND Password = ?";

        try {
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, MSSV);
            stmt.setString(2, Password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // System.out.println(rs);
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
    public boolean CheckMSSV(String MSSV){
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
    public boolean Register(String MSSV, String Password, String Name, String Class) {
        Connection con = GetConnection();
        if (con == null) {
            System.out.println("Cant connect with database");
            return false;
        }
        try {
            String query = "INSERT INTO Users (MSSV, Password) VALUES (?, ?)";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, MSSV);
            pstmt.setString(2, Password);
            pstmt.executeUpdate();
            query = "Insert into information (MSSV, Name, Class) value (?,?,?)";
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, MSSV);
            pstmt.setString(2, Name);
            pstmt.setString(3, Class);
            pstmt.executeUpdate();
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
    public String GetPassword(String MSSV) {
    	Connection con = GetConnection();
    	if (con == null) {
            System.out.println("Cant connect with database");
            return "ERR";
        }
    	try {
			String query = "Select Password from users where MSSV = ?";
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
    public boolean UpdatePassword(String MSSV, String Password) {
    	Connection con = GetConnection();
    	if (con == null) {
            System.out.println("Cant connect with database");
            return false;
        }
    	try {
			String query ="Update users set Password = ? where MSSV = ?";
			PreparedStatement pstmt = con.prepareStatement(query);
			pstmt.setString(1, Password);
			pstmt.setString(2, MSSV);
			pstmt.executeUpdate();
    		return true;
		} catch (Exception e) {
			System.err.println("Err Password:" + e);
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
    public String GetInformation(String MSSV) {
        Connection con = GetConnection();
        if (con == null) {
            System.out.println("Cant connect with database");
            return "ERR";
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String query = "SELECT MSSV, Name, Class, Data FROM information WHERE MSSV = ?";
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, MSSV);
            rs = pstmt.executeQuery();

            StringBuilder result = new StringBuilder();
            if (rs.next()) { 
                int columnCount = rs.getMetaData().getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    result.append(rs.getMetaData().getColumnName(i)).append(": ")
                          .append(rs.getString(i)).append("\n");
                }
            } else {
                result.append("No data found for MSSV: ").append(MSSV);
            }
            return result.toString();

        } catch (Exception e) {
            System.out.println("ERR get information : " + e.getMessage());
            return "ERR";
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                System.out.println("Err close the database: " + e.getMessage());
            }
        }
    }

    public boolean UpdateInformation(String MSSV,  String Name, String Class) {
        Connection con = GetConnection();
        if (con == null) {
            System.out.println("Cant connect with database");
            return false;
        }
        try {
            String query = "UPDATE information SET Name = ?, Class = ? WHERE MSSV = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1,Name);
            pstmt.setString(2,Class);
            pstmt.setString(3,MSSV);
            pstmt.executeUpdate();
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
    
    public String GetData(String MSSV) {
    	Connection con = GetConnection();
        if (con == null) {
            System.out.println("Cant connect with database");
            return "ERR";
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String query = "SELECT Data FROM information WHERE MSSV = ?";
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, MSSV);
            rs = pstmt.executeQuery();

            if (rs.next()) { 
                return rs.getString("Data");
            } 
            return "ERR";

        } catch (Exception e) {
            System.out.println("ERR get data : " + e.getMessage());
            return "ERR";
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                System.out.println("Err close the database: " + e.getMessage());
            }
        }
    }
    
    public String GetMSSVbyFileID(String FileID) {
    	Connection con = GetConnection();
        if (con == null) {
            System.out.println("Cant connect with database");
            return "ERR";
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String query = "SELECT MSSV FROM authorization WHERE FileID = ?";
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, FileID);
            rs = pstmt.executeQuery();
            if (rs.next()) { 
                return rs.getString("MSSV");
            } 
            return "ERR";

        } catch (Exception e) {
            System.out.println("ERR get MSSV by FileID : " + e.getMessage());
            return "ERR";
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                System.out.println("Err close the database: " + e.getMessage());
            }
        }
    }
    
    public String GetDatabyFileID(String FileID) {
    	Connection con = GetConnection();
        if (con == null) {
            System.out.println("Cant connect with database");
            return "ERR";
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String query = "SELECT Data FROM files WHERE FileID = ?";
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, FileID);
            rs = pstmt.executeQuery();
            if (rs.next()) { 
                return rs.getString("Data");
            } 
            return "ERR";

        } catch (Exception e) {
            System.out.println("ERR get data : " + e.getMessage());
            return "ERR";
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                System.out.println("Err close the database: " + e.getMessage());
            }
        }
    }
    
    public boolean UpdateData(String MSSV, double data) {
    	Connection con = GetConnection();
        if (con == null) {
            System.out.println("Can't connect with database");
            return false;
        }
        try {
			String query = "Update information Set Data = ? where MSSV = ?";
			PreparedStatement pstmt = con.prepareStatement(query);
			pstmt.setDouble(1, data);
			pstmt.setString(2, MSSV);
			pstmt.executeUpdate();
			return true;
		} catch (Exception e) {
            System.err.println("Err UpdateData:" + e);
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

    //File
    public String GetAllFileNameByMSSV(String MSSV) {
        Connection con = GetConnection();
        if (con == null) {
            System.out.println("Can't connect with database");
            return "ERR";
        }
        try {
            String query = "SELECT f.FileID,f.FileName FROM Files f " +
                           "JOIN Authorization a ON f.FileID = a.FileID " +
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
    
    public String GetFilenameByFileID(String FileID) {
    	Connection con = GetConnection();
        if (con == null) {
            System.out.println("Cant connect with database");
            return "ERR";
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String query = "SELECT FileName FROM files WHERE FileID = ?";
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, FileID);
            rs = pstmt.executeQuery();
            if (rs.next()) { 
                return rs.getString("FileName");
            } 
            return "ERR";

        } catch (Exception e) {
            System.out.println("ERR get Filename by FileID : " + e.getMessage());
            return "ERR";
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                System.out.println("Err close the database: " + e.getMessage());
            }
        }
    }
    
    public String GetMyFileName(String MSSV) {
    	Connection con = GetConnection();
    	if (con == null) {
            System.out.println("Can't connect with database");
            return "ERR";
        }
    	try {
    		String query = "SELECT f.FileID, f.FileName FROM Files f " +
		                    "JOIN Authorization a ON f.FileID = a.FileID " +
		                    "WHERE a.MSSV = ? and Role = 0";
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
    
    public String GetGuestFileName(String MSSV) {
    	Connection con = GetConnection();
    	if (con == null) {
            System.out.println("Can't connect with database");
            return "ERR";
        }
    	try {
    		String query = "SELECT f.FileID, f.FileName FROM Files f " +
		                    "JOIN Authorization a ON f.FileID = a.FileID " +
		                    "WHERE a.MSSV = ? and Role = 1";
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
    public String GetFileID(String FileName, String MSSV) {
        Connection con = GetConnection();
        if (con == null) {
            System.out.println("Can't connect with database");
            return "ERR";
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
        	String query = "SELECT Files.FileID " +
		                    "FROM Files " +
		                    "WHERE Files.FileName = ? AND Files.MSSV = ?";
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, FileName);
            pstmt.setString(2, MSSV);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("FileID");
            }
            return "ERR";
        } catch (Exception e) {
            System.out.println("ERR getFile: " + e.getMessage());
            return "ERR";
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                System.out.println("Err close the database: " + e.getMessage());
            }
        }
    }
    
    public boolean AddAutho(String MSSV, String FileID, int Role) {
//    	System.out.print(MSSV + "  " + FileID);
    	Connection con = GetConnection();
    	if (con == null) {
            System.out.println("Can't connect with database");
            return false;
        }
    	try {
    		String query = "INSERT INTO authorization (MSSV, FileID, Role) VALUES (?, ?, ?)";
		     PreparedStatement pstmt = con.prepareStatement(query);
		     pstmt.setString(1, MSSV);
		     pstmt.setString(2, FileID);
		     pstmt.setInt(3, Role);
		     pstmt.executeUpdate();
		     
		     return true;
			
		} catch (Exception e) {
            System.out.println("ERR add author : " + e.getMessage());
            e.printStackTrace();
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
    public boolean DelAutho(String MSSV, String FileID) {
    	Connection con = GetConnection();
    	if (con == null) {
            System.out.println("Can't connect with database");
            return false;
        }
    	try {
    		String query = "Delete from authorization  where MSSV =? and FileID = ?";
		     PreparedStatement pstmt = con.prepareStatement(query);
		     pstmt.setString(1, MSSV);
		     pstmt.setString(2, FileID);
		     pstmt.executeUpdate();
		     return true;
			
		} catch (Exception e) {
            System.out.println("ERR delauthor : " + e.getMessage());
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
    public Boolean DeleteAllAuthorByFileID(String FileID) {
    	Connection con = GetConnection();
    	if (con == null) {
            System.out.println("Can't connect with database");
            return false;
        }
    	try {
    		String query = "Delete from authorization  where FileID = ?";
		     PreparedStatement pstmt = con.prepareStatement(query);
		     pstmt.setString(1, FileID);
		     pstmt.executeUpdate();
		     return true;
			
		} catch (Exception e) {
            System.out.println("ERR delAllAuthor : " + e.getMessage());
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
    
    public String GetAllGuest(String FileID) {
    	Connection con = GetConnection();
    	if (con == null) {
            System.out.println("Can't connect with database");
            return "ERR";
        }
    	try {
    		String query = "Select MSSV from authorization  where FileID = ? and Role = 1";
		     PreparedStatement pstmt = con.prepareStatement(query);
		     pstmt.setString(1, FileID);
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
            System.out.println("ERR delauthor : " + e.getMessage());
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
    
    public boolean AddFile(String FileName, Long Data, String MSSV) {
    	Connection con = GetConnection();
    	if (con == null) {
            System.out.println("Can't connect with database");
            return false;
        }
    	try {
    		String query = "INSERT INTO files (FileName, Data, MSSV) VALUES (?, ?, ?)";
		     PreparedStatement pstmt = con.prepareStatement(query);
		     pstmt.setString(1, FileName);
		     pstmt.setLong(2, Data);
		     pstmt.setString(3, MSSV);
		     pstmt.executeUpdate();  
		     return true;
			
		} catch (Exception e) {
            System.out.println("ERR addFile : " + e.getMessage());
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
    
    public boolean DelFile(String FileID) {
        Connection con = GetConnection();
        if (con == null) {
            System.out.println("Can't connect with database");
            return false;
        }
        PreparedStatement pstmt = null;
        try {
            String query = "DELETE FROM files WHERE FileID = ?";
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, FileID);
            pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            System.out.println("ERR DelFile : " + e.getMessage());
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                System.out.println("Err close the database: " + e.getMessage());
            }
        }
    }
}

