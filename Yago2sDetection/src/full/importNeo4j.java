package full;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;

import DBUtils.PostgresDBUtils;

public class importNeo4j {
	private static final String DB_PATH = "/Users/yangfan/Desktop/neo4j-community-2.1.2/data/graph.db";

	static GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
	List<String> indexlist = new ArrayList<String>();
	private static enum RelTypes implements RelationshipType {
		actedIn
	}
	
	
	public void main() throws SQLException, IOException {
		long t1, t2;
		t1 = System.currentTimeMillis();

		File csv = new File("/Users/yangfan/Desktop/indexs.tsv");
		BufferedReader br = new BufferedReader(new FileReader(csv));
		String line = "";
		
		while ((line = br.readLine()) != null){

			this.indexlist.add(line);
		}
		
		PostgresDBUtils dbu = new PostgresDBUtils();
		Connection conn = null;
		conn = dbu.getConnection();
		Statement st = conn.createStatement();
		System.out.println(indexlist.size());
		ResultSet rs = null;
		registerShutdownHook(graphDb);
		Transaction tx = graphDb.beginTx();
		try {
			String sql = "select * from yagofacts where predicate ='<actedIn>'";

			rs = st.executeQuery(sql);

			int count = 0;

			while (rs.next()) {
	 
				count++;	
				String subject = rs.getString(2).toString();
				String object = rs.getString(4).toString();

				Node start;
				Node end;

				System.out.println(count);
				start = findNode(subject);
				end = findNode(object);
				if(start == null){
					System.out.println("qqqqqqq");
				}
				if(end==null){
					end = graphDb.createNode();
					end.setProperty("message", object);
				}
				Relationship relationship;
				relationship = start.createRelationshipTo(end,RelTypes.actedIn);
				relationship.setProperty("message", "actedIn");
				
			}
			tx.success();
		} catch (SQLException e) {
			System.out.println("Data Error");
			e.printStackTrace();
		} finally {
			dbu.close(conn, st, rs);
		} 
		tx.finish();
		graphDb.shutdown();
		t2 = System.currentTimeMillis();
		System.out.println("Run Time:" + (t2 - t1) + "(ms)");
	}

	private Node findNode(String nodeName){
		for (int i = 0;i<this.indexlist.size();i++){
			IndexManager index = graphDb.index();
			Index<Node> tmp = index.forNodes( indexlist.get(i) );
			IndexHits<Node> hits = tmp.get( "message",nodeName );
			if(hits.hasNext()){
				Node node = hits.next();
				return node;
			}
		}
		return null;
	}
	
	private static void registerShutdownHook(final GraphDatabaseService graphDb) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				graphDb.shutdown();
			}
		});
	}
}
