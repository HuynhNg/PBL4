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
            System.out.println("Connect database successfully");
            return con;

        } catch (ClassNotFoundException e) {
            System.out.println("Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Err connect database: " + e.getMessage());
        }
        return null; 
    }

    public boolean CheckLogin(String MSSV, String password) {
        Connection con = GetConnection();
        if (con == null) {
            System.out.println("Cant connect with database");
            return false;
        }

        String query = "SELECT * FROM users WHERE MSSV = ? AND PASSWORD = ?";

        try {
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, MSSV);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println(rs);
                System.out.println("Login successfully");
                return true;
            } else {
                System.out.println("login failed");
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
    public boolean Register(String MSSV, String PASSWORD, String Name, String Class){
        Connection con = GetConnection();
        if (con == null) {
            System.out.println("Cant connect with database");
            return false;
        }
        try {
            String query = "INSERT INTO Users (MSSV, PASSWORD) VALUES (?, ?)";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, MSSV);
            pstmt.setString(2, PASSWORD);
            pstmt.executeUpdate();
            query = "Insert into information (MSSV, Name, Class) value (?,?,?)";
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, MSSV);
            pstmt.setString(2, Name);
            pstmt.setString(3, Class);
            pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            System.err.println("Err:"+ e);
            return false;
        }
        
        
    }
}
