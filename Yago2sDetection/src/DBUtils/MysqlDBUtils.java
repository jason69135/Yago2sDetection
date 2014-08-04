package DBUtils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * java连接postgres数据库
 * 
 * @title: DBUtils
 */
public class MysqlDBUtils {
	private Connection conn;

	public MysqlDBUtils() {
		try {
			Class.forName("org.postgresql.Driver").newInstance();
			this.conn = DriverManager
					.getConnection(
							"jdbc:mysql://localhost:3306/yago?useUnicode=true&characterEncoding=UTF-8",
							"root", "root");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() {
		return this.conn;
	}

	public void close(Connection conn, Statement ts, ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
			if (ts != null) {
				ts.close();
			}
			if (conn != null) {
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
