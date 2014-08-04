package core;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.kernel.Traversal;

public class findPath {
	private static final String DB_PATH = "/Users/yangfan/Downloads/neo4j-community-2.1.2/data/graph.db";

	static GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);

	double[] relationAccuracy = new double[] { 0.9552, 0.9528, 0.9562 };
	Label actor = DynamicLabel.label("actor");
	Label movie = DynamicLabel.label("movie");
	Label director = DynamicLabel.label("director");
	Label award = DynamicLabel.label("award");

	private static enum RelTypes implements RelationshipType {
		actedIn, directed, hasWonPrize
	}

	public List<Path> findAllPath(String startName, String endName) throws IOException {
		registerShutdownHook(graphDb);
		Transaction tx = graphDb.beginTx();
		ArrayList<Path> filterpaths = new ArrayList<Path>();
		try {
			Node start = getNode(startName);
			Node end = getNode(endName);
			PathFinder<Path> finder = GraphAlgoFactory.allPaths(PathExpanders.allTypesAndDirections(), 3);
			Iterable<Path> paths = finder.findAllPaths(start, end);
			HashMap<Double, Path> weightPath = new HashMap<Double, Path>();
			if(paths == null){
				return null;
			}
			if(!paths.iterator().hasNext()){
				return null;
			}
			for (Path path : paths) {
				if(path.length() == 0){
				}
				double beta = 1;
				double Pconf = calConfidence(path);
				double Pinfo = calInfo(path);
				double P = beta * Pconf + (1-beta) * Pinfo ;
				BigDecimal bg = new BigDecimal(P);
				P = bg.setScale(8, BigDecimal.ROUND_HALF_UP).doubleValue();
				weightPath.put(P, path);
			}
			if (weightPath.size() == 0) {
				return null;
			}
			if (weightPath.size() == 1) {
				Set<Double> set = weightPath.keySet();
				Object[] obj = set.toArray();
				Arrays.sort(obj);
				filterpaths.add(weightPath.get(obj[(set.size() - 1)]));
				System.out.println(weightPath.get(obj[(set.size() - 1)]).toString() + ":::::0");
			}
			if (weightPath.size() >= 2) {
				Set<Double> set = weightPath.keySet();
				Object[] obj = set.toArray();
				Arrays.sort(obj);
				filterpaths.add(weightPath.get(obj[(set.size() - 1)]));
				filterpaths.add(weightPath.get(obj[(set.size() - 2)]));
				System.out.println(weightPath.get(obj[(set.size() - 1)]).toString() + ":::::1");
				System.out.println(weightPath.get(obj[(set.size() - 2)]).toString() + ":::::2");
			}
		} finally {
			tx.finish();
		}
		return filterpaths;
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
			}
		}
		BigDecimal bg = new BigDecimal(accuracy);
		accuracy = bg.setScale(8, BigDecimal.ROUND_HALF_UP).doubleValue();
		return accuracy;
	}

	public double calInfo(Path path) throws IOException {
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

	public Label whetherSamelabel(List<String[]> tuplelist) {
		registerShutdownHook(graphDb);
		Transaction tx = graphDb.beginTx();
		int size = tuplelist.size();
		ArrayList<Label> labellist = new ArrayList<Label>();
		for (int i = 0; i < size; i++) {
			if (checkLabel(tuplelist.get(i)) == null) {
				break;
			} else {
				labellist.add(checkLabel(tuplelist.get(i)));
			}
		}
		if (size != labellist.size()) {
			tx.finish();
			return null;
		} else {
			Label label = existLabel(labellist);
			System.out.println(label.name());
			tx.finish();
			return existLabel(labellist);
		}
	}


	public String common(List<String[]> tuplelist) throws IOException {
		registerShutdownHook(graphDb);
		Transaction tx = graphDb.beginTx();
		int size = tuplelist.size();
		String str0, str1 = null;
		ArrayList<String> comparelist = new ArrayList<String>();
		try {
			for (int i = 0; i < size; i++) {
				String[] tuple = tuplelist.get(i);
				List<Path> pathlist = findAllPath(tuple[0], tuple[1]);
				if (pathlist == null) {
					return null;
				}

				if (pathlist.size() == 1) {
					Path path0 = pathlist.get(0);
					String metapath0 = findMetapath(path0);
					comparelist.add(metapath0);
					comparelist.add(null);
				}
				if (pathlist.size() == 2) {
					Path path0 = pathlist.get(0);
					Path path1 = pathlist.get(1);
					String metapath0 = findMetapath(path0);
					String metapath1 = findMetapath(path1);
					comparelist.add(metapath0);
					comparelist.add(metapath1);
				}
			}

			str0 = compareMetapath0(comparelist);

			if (comparelist.get(1) != null) {
				str1 = compareMetapath1(comparelist);
			}
			System.out.println("Common Relations:");
			if (str0 != null && str1 == null) {
				System.out.println(str0);
				return str0;
			}
			if (str0 == null && str1 != null) {
				System.out.println(str1);
				return str1;
			}
			if (str0 != null && str1 != null) {
				if (str0.equals(str1)) {
					findSimilarTuple(str0,tuplelist);
					return str0;
				}
				System.out.println(str0);
				System.out.println(str1);
				return str0 + ";" + str1;
			}

		} finally {
			tx.finish();
		}
		return null;

	}

	public List<String> findSimilar(String str) {
		String[] array = str.split(",");
		List<String> path = new ArrayList<String>();
		for (int i = 0; i < array.length; i++) {
			path.add(array[i]);
		}
		return path;
	}

	
	public Label checkColumnLabel(List<String> unitlist) {
		registerShutdownHook(graphDb);
		Transaction tx = graphDb.beginTx();
		List<String> labelnameList = new ArrayList<String>();
		int size = unitlist.size();

		for (int i = 0; i < size; i++) {
			Node node = getNode(unitlist.get(i));
			labelnameList.add(node.getLabels().iterator().next().name());
		}
		
		Set<String> set = new HashSet<String>(); 
		set.addAll(labelnameList); 
		if(set.size()==1){
			Label label = DynamicLabel.label(set.iterator().next());
			return label;
		}
		tx.finish();
		return null;
	}
	
	public List<List<String>> findSimilarTuple(String str,List<String[]> tuples) throws IOException {
		registerShutdownHook(graphDb);
		Transaction tx = graphDb.beginTx();
		List<String> metapathList = new ArrayList<String>();
		List<List<String>> everySimiTuple = new ArrayList<List<String>>();
		metapathList = findSimilar(str);
		Node node = null;

		try {
			for(int i=0;i<tuples.size();i++){
			if (metapathList.get(0).equals("actor")){
				node = graphDb.findNodesByLabelAndProperty(actor, "message", tuples.get(i)[0]).iterator().next();
			}
			if (metapathList.get(0).equals("movie")) {
				node = graphDb.findNodesByLabelAndProperty(movie, "message", tuples.get(i)[0]).iterator().next();
			}
			if (metapathList.get(0).equals("director")) {
				node = graphDb.findNodesByLabelAndProperty(director, "message", tuples.get(i)[0]).iterator().next();
			}
			if (metapathList.get(0).equals("award")){
				node = graphDb.findNodesByLabelAndProperty(award, "message", tuples.get(i)[0]).iterator().next();
			}

			String output = null;
			List<String> relevant = new ArrayList<String>();
			
			HashMap<Double, Path> weightPath = new HashMap<Double, Path>();
			
			if (metapathList.size() == 5) {
				
					if (metapathList.get(1).equals("actedIn") && metapathList.get(3).equals("actedIn")) {
						for (Path path : Traversal.description()
								.depthFirst()
								.relationships(RelTypes.actedIn)
								.evaluator(Evaluators.atDepth(2))
								.evaluator(Evaluators.includeWhereLastRelationshipTypeIs(RelTypes.actedIn))
								.traverse(node)) {
							output += path + "\n";
							relevant.add(path.endNode().getProperty("message").toString());
						}
						everySimiTuple.add(relevant);
					}
					if (metapathList.get(1).equals("actedIn") && metapathList.get(3).equals("directed")) {
						for (Path path : Traversal.description()
								.depthFirst()
								.relationships(RelTypes.actedIn)
								.relationships(RelTypes.directed)
								.evaluator(Evaluators.atDepth(2))
								.evaluator(Evaluators.includeWhereLastRelationshipTypeIs(RelTypes.directed))
								.traverse(node)) {
							relevant.add(path.endNode().getProperty("message").toString());
							output += path + "\n";
						}
						everySimiTuple.add(relevant);
					}
					if (metapathList.get(1).equals("directed") && metapathList.get(3).equals("actedIn")) {
						for (Path path : Traversal.description()
								.depthFirst()
								.relationships(RelTypes.directed)
								.relationships(RelTypes.actedIn)
								.evaluator(Evaluators.atDepth(2))
								.evaluator(Evaluators.includeWhereLastRelationshipTypeIs(RelTypes.actedIn))
								.traverse(node)) {
							relevant.add(path.endNode().getProperty("message").toString());
							output += path + "\n";
						}
						everySimiTuple.add(relevant);
					}
			}
			}		
		} finally {
			tx.finish();
		}
		return everySimiTuple;
	}

	public String compareMetapath0(List<String> comparelist) {
		int size = comparelist.size();
		String metapath0 = comparelist.get(0);
		for (int i = 2; i < size; i = i + 2) {

			String m = comparelist.get(i);
			String n = comparelist.get(i + 1);

			if (m != null && n != null) {
				if (!m.equals(metapath0) && !n.equals(metapath0)) {
					return null;
				}
				if (m.equals(metapath0) || n.equals(metapath0)) {
					continue;
				}
			}
			if (m != null && n == null) {
				if (m.equals(metapath0)) {
					continue;
				}
			}
			if (m == null) {
				return null;
			}
		}

		return metapath0;
	}

	public String compareMetapath1(List<String> comparelist) {
		int size = comparelist.size();
		String metapath1 = comparelist.get(1);
		for (int i = 2; i < size; i = i + 2) {
			String m = comparelist.get(i);
			String n = comparelist.get(i + 1);

			if (m != null && n != null) {
				if (!m.equals(metapath1) && !n.equals(metapath1)) {
					return null;
				}
				if (m.equals(metapath1) || n.equals(metapath1)) {
					continue;
				}
			}
			if (m != null && n == null) {
				if (m.equals(metapath1)) {
					return null;
				}
				if (m.equals(metapath1)) {
					continue;
				}
			}
			if (m == null) {
				return null;
			}
		}

		return metapath1;
	}

	public String findMetapath(Path path) {
		String metapath = "";
		Iterable<Node> nodes = path.nodes();
		Iterable<Relationship> relations = path.relationships();
		Iterator<Node> nodeitr = nodes.iterator();
		Iterator<Relationship> relationitr = relations.iterator();
		while (nodeitr.hasNext()) {
			Iterator<Label> labels = nodeitr.next().getLabels().iterator();
			metapath += labels.next().name() + ",";
			if (relationitr.hasNext()) {
				metapath += relationitr.next().getType().name() + ",";
			}
		}
		System.out.println("metapath:" + metapath);
		return metapath;
	}

	public Label existLabel(List<Label> labelist) {
		HashSet<Label> set = new HashSet<Label>(labelist);
		if (set.size() != 1) {
			return null;
		}
		return labelist.get(0);
	}

	public Label checkLabel(String[] tuple) {
		Node node1, node2 = null;
		String tuple1 = tuple[0];
		String tuple2 = tuple[1];
		node1 = getNode(tuple1);
		node2 = getNode(tuple2);
		Label label1 = node1.getLabels().iterator().next();
		Label label2 = node2.getLabels().iterator().next();
		if (label1.name().equals(label2.name())) {
			return label1;
		} else {
			return null;
		}
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

	private static void registerShutdownHook(final GraphDatabaseService graphDb) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				graphDb.shutdown();
			}
		});
	}
}