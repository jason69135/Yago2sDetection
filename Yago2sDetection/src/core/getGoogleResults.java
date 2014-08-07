package core;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.logging.Level;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class getGoogleResults {

	public String LinkGoogle(String word) throws Exception, Exception,
			Exception {
	    java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF); 

	    
		final WebClient webclient = new WebClient();
		final HtmlPage htmlpage = webclient.getPage("http://www.google.com.hk/");
		webclient.setCssEnabled(false);
		webclient.setJavaScriptEnabled(false);
		webclient.setRedirectEnabled(true); 
//		webclient.setThrowExceptionOnFailingStatusCode(false);
				
		final HtmlForm form = htmlpage.getFormByName("f"); 
		final HtmlSubmitInput button = form.getInputByValue("Google 搜尋");
		final HtmlTextInput textField = form.getInputByName("q");
		textField.setValueAttribute(word);
		final HtmlPage page2 = button.click();
		String num = page2.getElementById("resultStats").asText();
		return num;
	}

	public Long getNum(String str) {
		String str2 = "";
		if (str != null && !"".equals(str)) {
			for (int i = 0; i < str.length(); i++) {
				if (str.charAt(i) >= 48 && str.charAt(i) <= 57) {
					str2 += str.charAt(i);
				}
			}
		}
		long num = Long.valueOf(str2);
		return num;
	}

	public String parseString(String name){
		name = name.replaceAll("<","");
		name = name.replaceAll(">","");
		name = name.replaceAll("_"," ");
		return name;
	}
	
	public double calInfo(String name1, String predicate ,String name2) throws IOException {
		name1 = parseString(name1);
		name2 = parseString(name2);
		getGoogleResults getwords = new getGoogleResults();
		double Pinfo = 1;
		try {
			if (name1 != null && name2 != null) {
				if(predicate.equals("<hasGender>")){
					predicate = "";
				}
				if(predicate.equals("<actedIn>")){
					predicate = "<act>";
				}
				if(predicate.equals("<hasWonPrize>")){
					predicate = "<win>";
				}
				double a = getNum(getwords.LinkGoogle(name1 + predicate+ name2));
				double b = getNum(getwords.LinkGoogle(name2));
				Pinfo = a / b;
				BigDecimal bg = new BigDecimal(Pinfo);
				Pinfo = bg.setScale(8, BigDecimal.ROUND_HALF_UP).doubleValue();
			} else
				System.out.println("Google Search Over");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Pinfo;
	}
}