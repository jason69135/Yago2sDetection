package full;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import DBUtils.PostgresDBUtils;

public class getLabelandIndex {
private static final String DB_PATH = "/Users/yangfan/Desktop/neo4j-community-2.1.2/data/graph.db";

	
	static GraphDatabaseService graphDb = new GraphDatabaseFactory()
			.newEmbeddedDatabase(DB_PATH);
	List<Label> labels = new ArrayList<Label>();
	List<String> objectlist = new ArrayList<String>();
	List<String> indexlist = new ArrayList<String>();
	
	
	public void readTypecsv() throws SQLException, IOException {
		long t1, t2;
		t1 = System.currentTimeMillis();

		PostgresDBUtils dbu = new PostgresDBUtils();
		Connection conn = null;
		conn = dbu.getConnection();

		Statement st = conn.createStatement();
		ResultSet rs = null;
		FileWriter indexfw = new FileWriter("/Users/yangfan/Desktop/indexs.tsv");
		
		FileWriter labelfw = new FileWriter("/Users/yangfan/Desktop/labels.tsv");
		
		FileWriter strfw = new FileWriter("/Users/yangfan/Desktop/str.tsv");
		

		try {
			String sql = "select distinct subject from yagotypes";
			rs = st.executeQuery(sql);
			while (rs.next()) {
				String str = rs.getString(1);
				strfw.write(str);
				strfw.write("\n");
				str = parse(str);
				labelfw.write(str);
				labelfw.write("\n");
				str=str+"Index";
				indexfw.write(str);
				indexfw.write("\n");
			}
		} catch (SQLException e) {
			System.out.println("Data Error");
			e.printStackTrace();
		} finally {
			dbu.close(conn, st, rs);
			indexfw.close();
			labelfw.close();
			strfw.close();
		}

		t2 = System.currentTimeMillis();
		System.out.println(this.objectlist.size());
		System.out.println("Run Time:" + (t2 - t1) + "(ms)");

	}

	private String parse(String str) {
		String[] array = str.split("_");
		String s = "";
		for (int i = 2; i < array.length; i++) {
			s += array[i - 1];
		}
		return s;
	}

}
