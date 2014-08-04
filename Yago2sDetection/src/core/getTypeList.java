package core;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class getTypeList {
	public List<String> getMovie() {
		ArrayList<String> list = new ArrayList<String>();
		InputStreamReader fr = null;
		BufferedReader br = null;
		try {
			fr = new InputStreamReader(new FileInputStream(
					"/Users/yangfan/Desktop/director.csv"));
			br = new BufferedReader(fr);
			String rec = null;
			String[] argsArr = null;
			while ((rec = br.readLine()) != null) {
				argsArr = rec.split(",");
				list.add(argsArr[0]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fr != null)
					fr.close();
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		return list;
	}
}