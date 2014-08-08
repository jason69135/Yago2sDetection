package core;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;


public class test {

//	private static enum RelTypes implements RelationshipType {
//		actedIn, directed, hasWonPrize,hasGender,wasBornIn,hasGivenName,hasFamilyName
//	}
	
	
	public static void main(String[] args) throws SQLException, IOException {
		long t1, t2;
		t1 = System.currentTimeMillis();
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
		String[] tuple1 = new String[]{"<Zhang_Ziyi>", "<Li_Bingbing>"};
//		String[] tuple2 = new String[]{"<Zhang_Ziyi>", "<Fan_Bingbing>"};
//		String[] tuple3 = new String[]{"<Zhou_Xun>", "<Li_Bingbing>"};
//		String[] tuple4 = new String[]{"<Tang_Wei>", "<Zhou_Xun>"};
//		String[] tuple5 = new String[]{"<Tom_Cruise>", "<Brad_Pitt>"};
//		String[] tuple6 = new String[]{"<Tang_Wei>", "<Brad_Pitt>"};
		ArrayList<String[]> tuplelist = new ArrayList<String[]>();
		tuplelist.add(tuple1);
//		tuplelist.add(tuple2);
//		tuplelist.add(tuple3);
//		tuplelist.add(tuple4);
//		tuplelist.add(tuple5);
//		tuplelist.add(tuple6);
		im.whetherSamelabel(tuplelist);
//		im.findAllPath(tuple1[0],tuple1[1]);
//		im.findAllPath(tuple2[0],tuple2[1]);
//		im.findAllPath(tuple3[0],tuple3[1]);
		im.common(tuplelist);
		t2 = System.currentTimeMillis();
		System.out.println("Run Time:" + (t2 - t1) + "(ms)");
	}

}
