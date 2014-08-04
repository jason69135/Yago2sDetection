package test;

import java.io.IOException;

public class main {


	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		depthTest aa = new depthTest();
		try {
			aa.findAllPath("<Gong_Li>","<Zhang_Ziyi>");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
