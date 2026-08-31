
import java.sql.*;

public class CheckDB {
    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://10.0.1.182:3306/hso2?autoReconnect=true&useUnicode=yes&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true";
        Connection conn = DriverManager.getConnection(url, "cucpro12@gmail.com", "VZPeIe=20RJV59={");
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT id, name, level, clazz, exp, detu FROM player;");
        while (rs.next()) {
            System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") + ", Lv: " + rs.getInt("level") + ", Clazz: " + rs.getInt("clazz") + ", Exp: " + rs.getLong("exp") + ", Detu: " + rs.getString("detu"));
        }
        conn.close();
    }
}
