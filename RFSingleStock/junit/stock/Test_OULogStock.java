package stock;

import junit.framework.TestCase;

public class Test_OULogStock extends TestCase {
	
	private final double delta = 1e-4;
	
	public void test_simulateFromRevertingLevel() {
		double price = 30, minprice = 0.1, maxprice = 100, kappa = 0.1, sigma = 0;
		double mu = Math.log(price);
		Stock stock = new OULogStock(price, minprice, maxprice, kappa, mu, sigma);
		stock.simulate();
		assertEquals(price, stock.getPrice(), delta);
	}
	
	public void test_simulateOneStep() {
		double price = 30, minprice = 0.1, maxprice = 100; 
		double kappa = 1, sigma = 0;
		double revertingLevel = 50;
		double mu = Math.log(revertingLevel);
		Stock stock = new OULogStock(price, minprice, maxprice, kappa, mu, sigma);
		stock.simulate();
		assertEquals(revertingLevel, stock.getPrice(), delta);
	}
	
}
