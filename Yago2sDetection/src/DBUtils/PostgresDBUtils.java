package DBUtils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * java connect postgres
 * 
 * @title: DBUtils
 */
public class PostgresDBUtils {
	private Connection conn;

	public PostgresDBUtils() {
		try {
			Class.forName("org.postgresql.Driver").newInstance();
			this.conn = DriverManager
					.getConnection(
							"jdbc:postgresql://localhost:5432/postgres?useUnicode=true&characterEncoding=UTF-8",
							"postgres", "postgres");
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
