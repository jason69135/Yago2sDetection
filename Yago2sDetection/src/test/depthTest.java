package test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.PathExpanders;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;

import core.getGoogleResults;

public class depthTest{
	private static String DB_PATH = "/Users/yangfan/Downloads/neo4j-community-2.1.2/data/graph.db";
	static GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
	static double[] relationAccuracy = new double[] { 0.9552, 0.9528, 0.9562, 0.999, 0.959 };
	
	public List<Path> findAllPath(String startName, String endName) throws IOException, InterruptedException {
		registerShutdownHook(graphDb);
		Transaction tx = graphDb.beginTx();
		ArrayList<Path> filterpaths = new ArrayList<Path>();
		try {
			Node start = getNode(startName);
			Node end = getNode(endName);
			PathFinder<Path> finder = GraphAlgoFactory.allPaths(PathExpanders.allTypesAndDirections(), 5);
			Iterable<Path> paths = finder.findAllPaths(start, end);
			HashMap<Double, Path> weightPath = new HashMap<Double, Path>();
			if(paths == null){
				return null;
			}
			if(!paths.iterator().hasNext()){
				return null;
			}
			int j=0;
			for (Path path : paths) {
				if(path.length() == 0){
				}
				double beta = 0.1;
				double Pconf = calConfidence(path);
				double Pinfo = calInfo(path);
				double P = beta * Pconf + (1-beta) * Pinfo ;
				BigDecimal bg = new BigDecimal(P);
				P = bg.setScale(8, BigDecimal.ROUND_HALF_UP).doubleValue();
				weightPath.put(P, path);
				System.out.println(j);
				j++;
			}
			if (weightPath.size() == 0) {
				System.out.println("Paht does not exist!");
			}
			if (weightPath.size() != 0) {
				Set<Double> set = weightPath.keySet();
				Object[] obj = set.toArray();
				Arrays.sort(obj);
				for(int i =0;i<obj.length;i++){
					System.out.println(weightPath.get(obj[i])+"::::::::::::"+obj[i]);
				}
			}

		} finally {
			tx.finish();
		}
		return filterpaths;
	}
	
	public Node getNode(String nodename) {
		IndexManager index = graphDb.index();
		Index<Node> actorIndex = index.forNodes("actorIndex");
		Index<Node> movieIndex = index.forNodes("movieIndex");
		Index<Node> awardIndex = index.forNodes("awardIndex");
		Index<Node> directorIndex = index.forNodes("directorIndex");
		Node node = null;
		if (actorIndex.get("message", nodename).hasNext()) {
			IndexHits<Node> hits = actorIndex.get("message", nodename);
			node = hits.getSingle();
			return node;
		} else if (movieIndex.get("message", nodename).hasNext()) {
			IndexHits<Node> hits = movieIndex.get("message", nodename);
			node = hits.getSingle();
			return node;
		} else if (awardIndex.get("message", nodename).hasNext()) {
			IndexHits<Node> hits = awardIndex.get("message", nodename);
			node = hits.getSingle();
			return node;
		} else if (directorIndex.get("message", nodename).hasNext()) {
			IndexHits<Node> hits = directorIndex.get("message", nodename);
			node = hits.getSingle();
			return node;
		}
		return null;
	}
	
	public double calConfidence(Path path) {
		double accuracy = 1;
		Iterable<Relationship> relations = path.relationships();
		Iterator<Relationship> itr = relations.iterator();
		while (itr.hasNext()) {
			Relationship relationship = itr.next();
			if (relationship.getProperty("message").equals("<actedIn>")) {
				accuracy = accuracy * relationAccuracy[0];
			} else if (relationship.getProperty("message").equals("<directed>")) {
				accuracy = accuracy * relationAccuracy[1];
			} else if (relationship.getProperty("message").equals("<hasWonPrize>")) {
				accuracy = accuracy * relationAccuracy[2];
			} else if (relationship.getProperty("message").equals("<hasGender>")) {
				accuracy = accuracy * relationAccuracy[3];
			} else if (relationship.getProperty("message").equals("<wasBornIn>")) {
				accuracy = accuracy * relationAccuracy[4];
			}
		}
		BigDecimal bg = new BigDecimal(accuracy);
		accuracy = bg.setScale(8, BigDecimal.ROUND_HALF_UP).doubleValue();
		return accuracy;
	}

	public double calInfo(Path path) throws IOException, InterruptedException {
		double info = 1;
		Iterable<Relationship> relations = path.relationships();
		Iterator<Relationship> itr = relations.iterator();
		getGoogleResults google = new getGoogleResults();
		while (itr.hasNext()) {
			Relationship relationship = itr.next();
			Node start = relationship.getStartNode();
			Node end = relationship.getEndNode();
			String startName = start.getProperty("message").toString();
			String endName = end.getProperty("message").toString();
			String predicate = relationship.getProperty("message").toString();
			info = info * google.calInfo(startName, predicate,endName);
		}
		BigDecimal bg = new BigDecimal(info);
		info = bg.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
		return info;
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
