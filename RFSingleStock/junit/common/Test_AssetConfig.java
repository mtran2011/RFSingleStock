package common;

import junit.framework.TestCase;

public class Test_AssetConfig extends TestCase {
	
	private final double delta = 1e-4;
	
	public void test_getTick() {
		int lotsize = 10, maxholding = 100;
		for (int rounding : new int[] {0,1,2}) {
			AssetConfig config = new AssetConfig(lotsize, rounding, maxholding);
			switch (rounding) {
			case 0:
				assertEquals(1, config.getTick(), delta);
				break;
			case 1:
				assertEquals(0.1, config.getTick(), delta);
				break;
			case 2:
				assertEquals(0.01, config.getTick(), delta);
				break;
			}
		}
	}
}
