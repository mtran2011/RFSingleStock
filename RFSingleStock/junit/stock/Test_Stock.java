package stock;

import junit.framework.TestCase;

public class Test_Stock extends TestCase {
	
	private final double tol = 1e-6;
	
	public void test_setPrice() {
		double price = 30, minprice = 0.1, maxprice = 100;
		Stock stock = new Stock(price, minprice, maxprice) {
			@Override
			public void simulate() {
				// TODO Auto-generated method stub
			}
		};
		
		assertEquals(price, stock.getPrice(), tol);
		
		stock.setPrice(maxprice + 0.1);
		assertEquals(maxprice, stock.getPrice(), tol);
		stock.setPrice(minprice - tol*2);
		assertEquals(minprice, stock.getPrice(), tol);
	}
}
