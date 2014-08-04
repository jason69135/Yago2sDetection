package core;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.PathExpanders;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.kernel.Traversal;
import org.neo4j.tooling.GlobalGraphOperations;


public class test {

	private static enum RelTypes implements RelationshipType {
		actedIn, directed, hasWonPrize,hasGender,wasBornIn,hasGivenName,hasFamilyName
	}
	
	
	public static void main(String[] args) throws SQLException, IOException {

//		final String DB_PATH = "/Users/yangfan/Downloads/neo4j-community-2.1.2/data/graph.db";
//	
//		GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
//		Transaction tx = graphDb.beginTx();

		
//		PathFinder<Path> finder = GraphAlgoFactory.allPaths(PathExpanders.allTypesAndDirections(), 3);
//		Iterable<Path> paths = finder.findAllPaths(node1, node2);
//		for (Path path : paths) {
//			System.out.println(path.toString());
//		}
		
//		System.out.println(node1.getLabels().iterator().next().name());
//		System.out.println(node1.getRelationships().iterator().next().getProperty("message"));
//		tx.finish();
		findPath im = new findPath();
		String[] tuple1 = new String[]{"<Zhang_Ziyi>", "<Fan_Bingbing>"};
//		String[] tuple2 = new String[]{"<Gong_Li>", "<Zhang_Ziyi>"};
//		String[] tuple3 = new String[]{"<Gong_Li>", "<Zhou_Xun>"};
		ArrayList<String[]> tuplelist = new ArrayList<String[]>();
		tuplelist.add(tuple1);
//		tuplelist.add(tuple2);
//		tuplelist.add(tuple3);
		im.whetherSamelabel(tuplelist);
		im.findAllPath(tuple1[0],tuple1[1]);
//		im.findAllPath(tuple2[0],tuple2[1]);
//		im.findAllPath(tuple3[0],tuple3[1]);
		im.common(tuplelist);
//		importOtherProperty aa = new importOtherProperty();
//		aa.importOther();
	}

}
