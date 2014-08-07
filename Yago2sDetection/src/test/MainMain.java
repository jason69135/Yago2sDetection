package test;

import java.io.IOException;

public class MainMain {


	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		long t1, t2;
		t1 = System.currentTimeMillis();
		depthTest aa = new depthTest();
		try {
			aa.findAllPath("<Gong_Li>","<Zhang_Ziyi>");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		t2 = System.currentTimeMillis();
		System.out.println("Run Time:" + (t2 - t1) + "(ms)");
	}

}
