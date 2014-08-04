package full;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
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


public class test {	
	
	public static void main(String[] args) throws SQLException, IOException {

//		nodeOperation no = new nodeOperation();
//		no.readTypecsv();
		List<Label> labels = new ArrayList<Label>();
		File csv0 = new File("/Users/yangfan/Desktop/labels 2.tsv");
		BufferedReader br0 = new BufferedReader(new FileReader(csv0));
		String line0 = "";
		
		
		while ((line0 = br0.readLine()) != null){
			Label label = DynamicLabel.label(line0);
			labels.add(label);
		}
		
		
		final String DB_PATH = "/Users/yangfan/Desktop/neo4j-community-2.1.2/data/graph.db";
		GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
		Transaction tx = graphDb.beginTx();
	
		
		for (int i = 0;i<labels.size();i++){
			if(graphDb.findNodesByLabelAndProperty(labels.get(i), "message", "<David_Mendelblatt>").iterator().hasNext()){
				System.out.println(graphDb.findNodesByLabelAndProperty(labels.get(i), "message", "<David_Mendelblatt>").iterator().next().getLabels().iterator().next().name());
			}
		}

		tx.success();
		tx.finish();
	}

}
