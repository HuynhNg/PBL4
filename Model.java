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
            String query = "SELECT f.FileName FROM Files f " +
                           "JOIN Authorization a ON f.FileID = a.FileID " +
                           "WHERE a.MSSV = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, MSSV);
            String result = "";
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result += rs.getString("FileName") + ",";
            }
            if (result.endsWith(",")) {
                result = result.substring(0, result.length() - 1);
            }
            return result;
        } catch (Exception e) {
            System.out.println("ERR getFile : " + e.getMessage());
            return "ERR";
        }
    }    
}
