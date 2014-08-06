package test;

import java.io.IOException;

public class test {

	public static void main(String[] args){
		// TODO Auto-generated method stub
		depthTest aa = new depthTest();
		try {
//			aa.allPath("<Gong_Li>","<Zhang_Ziyi>");
			aa.findAllPath("<Gong_Li>","<Zhang_Ziyi>");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

}
