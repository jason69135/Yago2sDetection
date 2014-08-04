package core;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;

import DBUtils.PostgresDBUtils;

public class importOtherProperty {
	private static final String DB_PATH = "/Users/yangfan/Downloads/neo4j-community-2.1.2/data/graph.db";

	static GraphDatabaseService graphDb = new GraphDatabaseFactory()
			.newEmbeddedDatabase(DB_PATH);
	Label actor = DynamicLabel.label("actor");
	Label movie = DynamicLabel.label("movie");
	Label director = DynamicLabel.label("director");
	Label award = DynamicLabel.label("award");
	Label gender = DynamicLabel.label("gender");
	Label birthPlace = DynamicLabel.label("birthPlace");
	Label givenname = DynamicLabel.label("givenname");
	Label familyname = DynamicLabel.label("familyname");
	
	private static enum RelTypes implements RelationshipType {
		actedIn, directed, hasWonPrize, hasGender, wasBornIn
	}
	
	public void importOther() throws SQLException{
		long t1, t2;
		t1 = System.currentTimeMillis();

		PostgresDBUtils dbu = new PostgresDBUtils();
		Connection conn = null;
		conn = dbu.getConnection();
		Statement st = conn.createStatement();
		ResultSet rs = null;
		Transaction tx = graphDb.beginTx();;
		
		try {
			String sql = "select * from yagofacts where predicate = '<hasFamilyName>';";

			rs = st.executeQuery(sql);
			int i = 0;

			while (rs.next()) {
				String subject = rs.getString(2).toString();
				String predicate = rs.getString(3).toString();
				String object = rs.getString(4).toString();
		

				Node nodeA = getNode(subject);
				Node nodeB = getBirthplace(object);
				if(nodeA != null){
					if(nodeB == null){
						Node node = graphDb.createNode();
						node.setProperty("message", object);
						node.addLabel(familyname);
						Relationship relationship;
						relationship = nodeA.createRelationshipTo(node, RelTypes.actedIn);
						relationship.setProperty("message", predicate);
						System.out.println(i);
					}else{
						Relationship relationship;
						relationship = nodeA.createRelationshipTo(nodeB, RelTypes.actedIn);
						relationship.setProperty("message", predicate);
					}
				}
				i++;
			}
		} catch (SQLException e) {
			System.out.println("Data Error");
			e.printStackTrace();
		} finally {
			dbu.close(conn, st, rs);
			
		}
		tx.success();
		tx.finish();
		graphDb.shutdown();
		t2 = System.currentTimeMillis();
		System.out.println("Run Time:" + (t2 - t1) + "(ms)");
	}
	
	public Node getBirthplace(String nodename){
		Iterator<Node> nodes = graphDb.findNodesByLabelAndProperty(familyname, "message", nodename).iterator();
		if(nodes.hasNext()){
			Node node = nodes.next();
			return node;
		}
		return null;
	}
	
	public Node getNode(String nodename) {
		IndexManager index = graphDb.index();
		Index<Node> actorIndex = index.forNodes("actorIndex");
		Index<Node> directorIndex = index.forNodes("directorIndex");
		Node node = null;
		if (actorIndex.get("message", nodename).hasNext()) {
			IndexHits<Node> hits = actorIndex.get("message", nodename);
			node = hits.getSingle();

			return node;
		} else if (directorIndex.get("message", nodename).hasNext()) {
			IndexHits<Node> hits = directorIndex.get("message", nodename);
			node = hits.getSingle();

			return node;
		}
		return null;
	}
	
}
