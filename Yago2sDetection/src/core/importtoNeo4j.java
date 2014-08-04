package core;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import DBUtils.PostgresDBUtils;


public class importtoNeo4j {
	private static final String DB_PATH = "/Users/yangfan/Downloads/neo4j-community-2.1.2/data/graph.db";

	static GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
	Label actor = DynamicLabel.label("actor");
	Label movie = DynamicLabel.label("movie");
	Label director = DynamicLabel.label("director");
	Label award = DynamicLabel.label("award");


	private static enum RelTypes implements RelationshipType {
		actedIn, directed, hasWonPrize
	}

	public void main() throws SQLException {
		long t1, t2;
		t1 = System.currentTimeMillis();


		PostgresDBUtils dbu = new PostgresDBUtils();
		Connection conn = null;
		conn = dbu.getConnection();
		Statement st = conn.createStatement();
		
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
				String predicate = rs.getString(3).toString();
				String object = rs.getString(4).toString();

				Node start ;
				Node end;

				System.out.println(count);

				if(graphDb.findNodesByLabelAndProperty(actor, "message", subject).iterator().hasNext()){
					start = graphDb.findNodesByLabelAndProperty(actor, "message", subject).iterator().next();
					end = graphDb.findNodesByLabelAndProperty(movie, "message", object).iterator().next();
					Relationship relationship;
					relationship = start.createRelationshipTo(end,RelTypes.actedIn);

					relationship.setProperty("message", predicate);
				}else{
					start = graphDb.createNode();
					start.setProperty("message", subject);
					start.addLabel(actor);
					end = graphDb.findNodesByLabelAndProperty(movie, "message", object).iterator().next();
					Relationship relationship;
					relationship = start.createRelationshipTo(end,RelTypes.actedIn);

					relationship.setProperty("message", predicate);
				}



				
//				start = actorIndex.get("message", subject).getSingle();
//				if(start == null){
//					start = directorIndex.get("message", subject).getSingle();
//					if(start == null){
//						continue;
//					}else{
//					end = awardIndex.get("message", object).getSingle();
//					Relationship relationship;
//					relationship = start.createRelationshipTo(end,RelTypes.hasWonPrize);
//					relationship.setProperty("message", predicate);
//					}
//				}else{
//					end = awardIndex.get("message", object).getSingle();
//					Relationship relationship;
//					relationship = start.createRelationshipTo(end,RelTypes.hasWonPrize);
//					relationship.setProperty("message", predicate);
//				}
			}
			tx.success();
		} catch (SQLException e) {
			System.out.println("Data Error");
			e.printStackTrace();
		} finally {
			dbu.close(conn, st, rs);
			tx.finish();
		} 
		graphDb.shutdown();
		t2 = System.currentTimeMillis();
		System.out.println("Run Time:" + (t2 - t1) + "(ms)");
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