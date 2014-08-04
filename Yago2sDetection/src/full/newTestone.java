package full;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;

import DBUtils.PostgresDBUtils;

public class newTestone {
private static final String DB_PATH = "/Users/yangfan/Desktop/neo4j-community-2.1.2/data/graph.db";

	
	static GraphDatabaseService graphDb = new GraphDatabaseFactory()
			.newEmbeddedDatabase(DB_PATH);
	
	
	public void readTypecsv() throws SQLException, IOException {
		long t1, t2;
		t1 = System.currentTimeMillis();

		PostgresDBUtils dbu = new PostgresDBUtils();
		Connection conn = null;
		conn = dbu.getConnection();

		Statement st = conn.createStatement();
		ResultSet rs = null;
		registerShutdownHook(graphDb);
		Transaction tx = graphDb.beginTx();
		int count = 0;
		try {
			Index<Node> index = graphDb.index().forNodes("allIndex");
			System.out.println("1");
			String sql = "select distinct subject from yagofacts";
			rs = st.executeQuery(sql);

			while (rs.next()) {
				count++;	
				String subject = rs.getString(1).toString();
//				subject = parse(subject);
				createNode(subject,index);
				System.out.println(count);
			}
			tx.success();
		} catch (SQLException e) {
			System.out.println("Data Error");
			e.printStackTrace();
		} finally {
			dbu.close(conn, st, rs);
			tx.finish();
		}

		System.out.println("count:::"+count);
		t2 = System.currentTimeMillis();
		System.out.println("Run Time:" + (t2 - t1) + "(ms)");

	}

//	private String parse(String str) {
//		String[] array = str.split("_");
//		String s = "";
//		for (int i = 2; i < array.length; i++) {
//			s += array[i - 1];
//		}
//		return s;
//	}

	boolean createNode(String nodeName, Index<Node> index) throws IOException {
		
			Node node = graphDb.createNode();
			node.setProperty("message", nodeName);
			index.add(node, "message", nodeName);
		return true;
	}
	
//	public Node checkNodeexist(String nodeName) throws IOException{
//		for (int i = 0;i<this.indexlist.size();i++){
//			IndexManager index = graphDb.index();
//			Index<Node> tmp = index.forNodes( this.indexlist.get(i) );
//			IndexHits<Node> hits = tmp.get( "message",nodeName );
//			if(hits.hasNext()){
//				Node node = hits.getSingle();
//				return node;
//			}
//		}
//		return null;
//	}

	private static void registerShutdownHook(final GraphDatabaseService graphDb) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				graphDb.shutdown();
			}
		});
	}
}
