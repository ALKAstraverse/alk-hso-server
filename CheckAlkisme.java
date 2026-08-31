
import java.sql.*;

public class CheckAlkisme {
    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://10.0.1.182:3306/hso2?autoReconnect=true&useUnicode=yes&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true";
        Connection conn = DriverManager.getConnection(url, "cucpro12@gmail.com", "VZPeIe=20RJV59={");
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM player WHERE name='alkisme';");
        if (rs.next()) {
            ResultSetMetaData meta = rs.getMetaData();
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                System.out.println(meta.getColumnName(i) + " = " + rs.getString(i));
            }
        }
        conn.close();
    }
}
