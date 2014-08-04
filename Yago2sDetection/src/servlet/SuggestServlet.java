package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DBUtils.PostgresDBUtils;

/**
 * ajax
 * 
 * @title: SuggestServlet
 */

public class SuggestServlet extends HttpServlet {

	private static final long serialVersionUID = 6429054973446767234L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PostgresDBUtils dbu = new PostgresDBUtils();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		
		 response.setCharacterEncoding("utf-8");  
		
		PrintWriter out = response.getWriter();
		String id = request.getParameter("id");
		String inputid = request.getParameter("inputid");
		conn = dbu.getConnection();
		String sql = " SELECT distinct subject FROM yagofacts WHERE subject LIKE '"+id+"%' LIMIT 7";
		StringBuffer sbf = new StringBuffer();
		sbf.append("<ul id='sug'>");
		int pos = 0;
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				String str = rs.getString(1);
				str = str.substring(1, str.length()-1);
				sbf.append("<li onkeydown=\"if(getKeyCode(event) == 13)form_submit();\" onmouseover=\"theMouseOver(" + pos + ");\" onmouseout=\"theMouseOut("+ pos +");\" onclick=\"theMouseClick("+ pos +","+ inputid +")\">" + str + "</li>");
				pos++;
			}
			
		} catch (SQLException e) {
			System.out.println("fetching error");
			e.printStackTrace();
		} finally {
			dbu.close(conn, ps, rs);
		}
		sbf.append("</ul>");
		out.write(sbf.toString());
		out.close();
	}

}
