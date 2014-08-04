package core;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;

import DBUtils.PostgresDBUtils;

public class nodeOperation {

	private static final String DB_PATH = "/Users/yangfan/Downloads/neo4j-community-2.1.2/data/graph.db";

	static GraphDatabaseService graphDb = new GraphDatabaseFactory()
			.newEmbeddedDatabase(DB_PATH);
	Label actor = DynamicLabel.label("actor");
	Label movie = DynamicLabel.label("movie");
	Label director = DynamicLabel.label("director");
	Label award = DynamicLabel.label("award");

	public void readTypecsv() throws SQLException {
		long t1, t2;
		t1 = System.currentTimeMillis();

		PostgresDBUtils dbu = new PostgresDBUtils();
		Connection conn = null;
		conn = dbu.getConnection();
		Statement st = conn.createStatement();
		ResultSet rs = null;
		try {
			String movie_sql = "select distinct subject from yagofacts where object = '<wordnet_award_106696483>' and predicate = 'rdf:type'";

			rs = st.executeQuery(movie_sql);

			while (rs.next()) {
				String str = rs.getString(1);
				createAwardNode(str);
			}
		} catch (SQLException e) {
			System.out.println("Data Error");
			e.printStackTrace();
		} finally {
			dbu.close(conn, st, rs);
		}

		graphDb.shutdown();
		t2 = System.currentTimeMillis();
		System.out.println("Run Time:" + (t2 - t1) + "(ms)");

	}

	boolean createMovieNode(String nodeName) {
		registerShutdownHook(graphDb);

		Transaction tx = graphDb.beginTx();
		try {
			Index<Node> movieIndex = graphDb.index().forNodes("movieIndex");
			Node node = graphDb.createNode();
			node.setProperty("message", nodeName);
			node.addLabel(movie);
			movieIndex.add(node, "message", nodeName);
			tx.success();

		} finally {
			tx.finish();

		}
		return true;
	}
	
	boolean createActorNode(String nodeName) {
		registerShutdownHook(graphDb);

		Transaction tx = graphDb.beginTx();
		try {
			Index<Node> actorIndex = graphDb.index().forNodes("actorIndex");
			Node node = graphDb.createNode();
			node.setProperty("message", nodeName);
			node.addLabel(actor);
			actorIndex.add(node, "message", nodeName);
			tx.success();

		} finally {
			tx.finish();
		}
		return true;
	}
	
	boolean createAwardNode(String nodeName) {
		registerShutdownHook(graphDb);

		Transaction tx = graphDb.beginTx();
		try {
			Index<Node> awardIndex = graphDb.index().forNodes("awardIndex");
			Node node = graphDb.createNode();
			node.setProperty("message", nodeName);
			node.addLabel(award);
			awardIndex.add(node, "message", nodeName);
			tx.success();

		} finally {
			tx.finish();

		}
		return true;

	}
	boolean createDirectorNode(String nodeName) {
		registerShutdownHook(graphDb);

		Transaction tx = graphDb.beginTx();
		try {
			Index<Node> directorIndex = graphDb.index().forNodes("directorIndex");
			Node node = graphDb.createNode();
			node.setProperty("message", nodeName);
			node.addLabel(director);
			directorIndex.add(node, "message", nodeName);
			tx.success();

		} finally {
			tx.finish();

		}
		return true;

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
