import java.sql.*;

public class DBConnector {   
    private static final String USER = "root";
    private static final String PW = "";
    public static final String DB_NAME = "tetris_database";
    public static final String TABLE_NAME = "user_info";

    private static final String DB_SERVER_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_URL = DB_SERVER_URL + DB_NAME;

    private DBConnector() {
        
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PW);
    }

    public static Connection getServerConnection() throws SQLException {
        return DriverManager.getConnection(DB_SERVER_URL, USER, PW);
    }
    
}

class DBInitializer {

    public void init() throws Exception {
        try (Connection con = DBConnector.getServerConnection();
        Statement stmt = con.createStatement()) {
            // 동일 이름의 데이터베이스가 없으면 생성
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DBConnector.DB_NAME);
        }

        try (Connection con = DBConnector.getConnection(); 
        Statement stmt = con.createStatement()) {
            // 동일 이름의 테이블이 없으면 생성
            /*
            * id, nickname은 고유한 값
            * best_score는 기본적으로 0
            * user_number는 각 데이터의 키
            */
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS " +
                DBConnector.TABLE_NAME + " ( " +
                "id VARCHAR(15) NOT NULL UNIQUE, " +
                "pw VARCHAR(20) NOT NULL, " +
                "nickname VARCHAR(10) NOT NULL UNIQUE, " +
                "best_score INT NOT NULL DEFAULT 0, " +
                "user_number INT PRIMARY KEY AUTO_INCREMENT " +
                ");" 
            );
        }
    }

    public DBInitializer() {
        
    }
}

