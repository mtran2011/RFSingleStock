package stock;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class Test_OULogStock {
	
	private final double delta = 1e-6;
	
	@Test
	public void simulateFromRevertingLevel() {
		double price = 30.23, minprice = 0.1, maxprice = 100, kappa = 0.1, sigma = 0;
		double reversion = price;
		int rounding = 2;
		Stock stock = new OULogStock(price, minprice, maxprice, rounding, kappa, reversion, sigma);
		stock.simulate();
		assertEquals(price, stock.getPrice(), delta);
	}
	
	@Test
	public void simulateKappaEqualOne() {
		double price = 30, minprice = 0.1, maxprice = 100; 
		double kappa = 1, sigma = 0;
		double revertingLevel = 50;
		int rounding = 2;
		Stock stock = new OULogStock(price, minprice, maxprice, rounding, kappa, revertingLevel, sigma);
		stock.simulate();
		assertEquals(revertingLevel, stock.getPrice(), delta);
	}
	
}
