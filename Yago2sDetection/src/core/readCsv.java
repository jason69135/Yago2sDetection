package core;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import DBUtils.PostgresDBUtils;

/**
 * java
 * 
 * @title: readCsv
 */
public class readCsv {
	public static void main(String[] args) throws SQLException {
		long startTime=System.currentTimeMillis(); 
		PostgresDBUtils dbu = new PostgresDBUtils();
		Connection conn = null;
		conn = dbu.getConnection();
		Statement st1 = conn.createStatement();
		Statement st2 = conn.createStatement();
		ResultSet rs = null;

		getTypeList gt = new getTypeList();
		List<String> movielist = new ArrayList<String>();
		movielist = gt.getMovie();
		try {
			for (int i = 0; i < movielist.size(); i++) {
				String subject = movielist.get(i).toString();
				if (subject.indexOf("'") > -1) {
					subject = subject.replace("'", "''");
				}
				String sql = " select * from yagofacts where subject = '"
						+ subject + "' and predicate like '<%'";
				rs = st1.executeQuery(sql);
				while (rs.next()) {
					String subject1 = rs.getString(2);
					String predicate = rs.getString(3);
					if (subject1.indexOf("'") > -1) {
						subject1 = subject1.replace("'", "''");
					}
					String object = rs.getString(4);
					if (predicate.indexOf("'") > -1) {
						predicate = predicate.replace("'", "''");
					}
					if (object.indexOf("'") > -1) {
						object = object.replace("'", "''");
					}
					String s = " insert into yagoarea(id,subject,predicate,object,value) values ('"+rs.getString(1)+"','"+subject1+"','"+predicate+"','"+object+"','"+rs.getDouble(5)+"')";
					st2.executeUpdate(s);
				}
			}
		} catch (SQLException e) {
			System.out.println("Data Error");
			e.printStackTrace();
		} finally {
			dbu.close(conn, st1, rs);
		}
		long endTime=System.currentTimeMillis(); 
		System.out.println("Time:"+(endTime-startTime)+"ms");
	}
}
