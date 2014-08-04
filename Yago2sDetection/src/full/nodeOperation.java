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

public class nodeOperation {

	private static final String DB_PATH = "/Users/yangfan/Desktop/neo4j-community-2.1.2/data/graph.db";

	
	static GraphDatabaseService graphDb = new GraphDatabaseFactory()
			.newEmbeddedDatabase(DB_PATH);
	List<Label> labels = new ArrayList<Label>();
	List<String> objectlist = new ArrayList<String>();
	List<String> indexlist = new ArrayList<String>();
	
	
	public void readTypecsv() throws SQLException, IOException {
		long t1, t2;
		t1 = System.currentTimeMillis();

//		PostgresDBUtils dbu = new PostgresDBUtils();
//		Connection conn = null;
//		conn = dbu.getConnection();

//		Statement st = conn.createStatement();
//		ResultSet rs = null;

//		try {
//			String sql = "select distinct subject from yagotypes";
//			rs = st.executeQuery(sql);
//			while (rs.next()) {
//				String str = rs.getString(1);
//				this.objectlist.add(str);
//				String labelName = parse(str);
//				Label label = DynamicLabel.label(labelName);
//				this.labels.add(label);
//			}
//		} catch (SQLException e) {
//			System.out.println("Data Error");
//			e.printStackTrace();
//		} finally {
//			dbu.close(conn, st, rs);
//		}

		File csv = new File("/Users/yangfan/Desktop/indexs.tsv");
		BufferedReader br = new BufferedReader(new FileReader(csv));
		String line = "";
		
		File csv1 = new File("/Users/yangfan/Desktop/labels.tsv");
		BufferedReader br1 = new BufferedReader(new FileReader(csv1));
		String line1 = "";
		
		File csvhaha = new File("/Users/yangfan/Desktop/labels.tsv");
		BufferedReader brhaha = new BufferedReader(new FileReader(csvhaha));
		String linehaha = "";
		
		File csv2 = new File("/Users/yangfan/Desktop/str.tsv");
		BufferedReader br2 = new BufferedReader(new FileReader(csv2));
		String line2 = "";
		
		
		while ((linehaha = brhaha.readLine()) != null){
			Label label = DynamicLabel.label(linehaha);
			this.labels.add(label);
		}
		
		PostgresDBUtils dbu1 = new PostgresDBUtils();
		Connection conn1 = null;
		conn1 = dbu1.getConnection();
		Statement st1 = conn1.createStatement();
		ResultSet rs1 = null;
		Transaction tx = graphDb.beginTx();
		
		
		try {
			int i = 0;
			while ((line = br.readLine()) != null && (line1 = br1.readLine()) != null  && (line2 = br2.readLine()) != null && i < 200) {
				String index = line;
				String label = line1;
				String str = line2;
				
				String sql1 = "select distinct subject from yagofacts where object = '" + str + "' and predicate = 'rdf:type'";
				rs1 = st1.executeQuery(sql1);
				Index<Node> XXXindex = graphDb.index().forNodes(index);
				while (rs1.next()) {
					createNode(rs1.getString(1), XXXindex, label);
					System.out.println(rs1.getString(1)+"::::"+index+"::::::::"+i);
				}
				i++;
			}
			tx.success();
		} catch (SQLException e) {
//			System.out.println("Data Error");
//			e.printStackTrace();
		} finally {
//			dbu1.close(conn1, st1, rs1);
			tx.finish();
		}

		t2 = System.currentTimeMillis();
		System.out.println(this.objectlist.size());
		System.out.println("Run Time:" + (t2 - t1) + "(ms)");

	}

	boolean createNode(String nodeName, Index<Node> XXXindex, String labelName) throws IOException {
		registerShutdownHook(graphDb);
		if(checkNodeexist(nodeName)==null){
			Node node = graphDb.createNode();
			node.setProperty("message", nodeName);
			Label label = DynamicLabel.label(labelName);
			node.addLabel(label);
			XXXindex.add(node, "message", nodeName);
		}else{
			Node node = checkNodeexist(nodeName);
			Label label = DynamicLabel.label(labelName);
			node.addLabel(label);
		}
		return true;
	}
	
	public Node checkNodeexist(String nodeName) throws IOException{
		for (int i = 0;i<this.labels.size();i++){
			if(graphDb.findNodesByLabelAndProperty(this.labels.get(i), "message", nodeName).iterator().hasNext()){
				return graphDb.findNodesByLabelAndProperty(this.labels.get(i), "message", nodeName).iterator().next();
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
