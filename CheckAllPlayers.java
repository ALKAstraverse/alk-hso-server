
import java.sql.*;

public class CheckAllPlayers {
    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://10.0.1.182:3306/hso2?autoReconnect=true&useUnicode=yes&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true";
        Connection conn = DriverManager.getConnection(url, "cucpro12@gmail.com", "VZPeIe=20RJV59={");
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT id, name, level, clazz, exp, site, detu FROM player;");
        while (rs.next()) {
            System.out.println(rs.getInt("id") + ": " + rs.getString("name") + " | lv: " + rs.getInt("level") + " | clazz: " + rs.getInt("clazz") + " | exp: " + rs.getLong("exp") + " | site: " + rs.getString("site") + " | detu: " + rs.getString("detu"));
        }
        conn.close();
    }
}
