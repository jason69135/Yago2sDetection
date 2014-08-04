package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.neo4j.graphdb.Label;

import core.findPath;

public class findPathServlet extends HttpServlet {

	private static final long serialVersionUID = 9043562256277183595L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		StringBuffer tuple1A = new StringBuffer(request.getParameter("tuple1A"));
		StringBuffer tuple1B = new StringBuffer(request.getParameter("tuple1B"));
		StringBuffer tuple2A = new StringBuffer(request.getParameter("tuple2A"));
		StringBuffer tuple2B = new StringBuffer(request.getParameter("tuple2B"));
		StringBuffer tuple3A = new StringBuffer(request.getParameter("tuple3A"));
		StringBuffer tuple3B = new StringBuffer(request.getParameter("tuple3B"));
		StringBuffer tuple4A = new StringBuffer(request.getParameter("tuple4A"));
		StringBuffer tuple4B = new StringBuffer(request.getParameter("tuple4B"));
		tuple1A.insert(0, "<");
		tuple1A.append(">");
		tuple1B.insert(0, "<");
		tuple1B.append(">");
		tuple2A.insert(0, "<");
		tuple2A.append(">");
		tuple2B.insert(0, "<");
		tuple2B.append(">");
		tuple3A.insert(0, "<");
		tuple3A.append(">");
		tuple3B.insert(0, "<");
		tuple3B.append(">");
		tuple4A.insert(0, "<");
		tuple4A.append(">");
		tuple4B.insert(0, "<");
		tuple4B.append(">");

		long t1, t2;
		t1 = System.currentTimeMillis();
		PrintWriter out = response.getWriter();
		findPath im = new findPath();
		String[] tuple1 = new String[] { tuple1A.toString(), tuple1B.toString() };
		String[] tuple2 = new String[] { tuple2A.toString(), tuple2B.toString() };
		String[] tuple3 = new String[] { tuple3A.toString(), tuple3B.toString() };
		String[] tuple4 = new String[] { tuple4A.toString(), tuple4B.toString() };
		StringBuffer sbf = new StringBuffer();
		sbf.append("<h3>");

		ArrayList<String[]> tuplelist = new ArrayList<String[]>();
		tuplelist.add(tuple1);
		if (tuple2[0].length() != 2 && tuple2[1].length() != 2) {
			tuplelist.add(tuple2);
		}
		if (tuple3[0].length() != 2 && tuple3[1].length() != 2) {
			tuplelist.add(tuple3);
		}
		if (tuple4[0].length() != 2 && tuple4[1].length() != 2) {
			tuplelist.add(tuple4);
		}
		Label label = im.whetherSamelabel(tuplelist);
		
		ArrayList<String> columnA = new ArrayList<String>();
		ArrayList<String> columnB = new ArrayList<String>();
		for(int i=0;i<tuplelist.size();i++){
			columnA.add(tuplelist.get(i)[0]);
			columnB.add(tuplelist.get(i)[1]);
		}
		Label Alabel = im.checkColumnLabel(columnA);
		Label Blabel = im.checkColumnLabel(columnB);
		if (Alabel != null && Blabel != null) {
			sbf.append(" ");
			sbf.append("As are:"+Alabel);
			sbf.append(" ");
			sbf.append("Bs are:"+Blabel);
		}
		if (Alabel == null && Blabel != null) {
			sbf.append(" ");
			sbf.append("Bs are:"+Blabel);
		}
		if (Alabel != null && Blabel == null) {
			sbf.append(" ");
			sbf.append("As are:"+Alabel);
		}
		sbf.append("<p>");
		if (label == null) {
			sbf.append(" ");
		}else{
		sbf.append("Both of them are:");
		sbf.append(" ");
		sbf.append(label);
		}
		String attr = im.common(tuplelist);
		if(attr == null){
			sbf.append("No other relationship!");
		}else{
			sbf.append(" ");
			sbf.append(attr);
			sbf.append(".</h3>");
			
			List<List<String>> simi = im.findSimilarTuple(attr,tuplelist);
			sbf.append("              ");
			for(int i=0;i<simi.size();i++){
			sbf.append("Tuple"+i+":Similar result:");
			sbf.append("\n");
			List<String> subsimi= new ArrayList<String>();
			subsimi = simi.get(i);
			for(int j=0;j<subsimi.size();j++){
				String str = subsimi.get(j).replace("<", "");
				str = str.replace(">", "");
			sbf.append(str);
			sbf.append("</h3>");
			sbf.append("<p>");
			}
			sbf.append("                       ");
		}
	}
		out.write(sbf.toString());
		out.close();
		t2 = System.currentTimeMillis();
		System.out.println("Run Time:" + (t2 - t1) + "(ms)");
}
}