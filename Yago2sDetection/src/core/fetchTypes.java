package core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class fetchTypes {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			File csv = new File("/Users/yangfan/Desktop/yagoSimpleTaxonomy.tsv");
			File outFile = new File("/Users/yangfan/Desktop/outFile.tsv");

			BufferedReader br = new BufferedReader(new FileReader(csv));
			BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));


			String line = "";
			while ((line = br.readLine()) != null) {
			
				if(line.contains("rdfs:subClassOf") && !line.contains("owl:Thing") && !line.contains("<yagoGeoEntity>")){
		               writer.write(line);
		               writer.newLine();
				}
			}
			br.close();
			writer.close();

		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

}
