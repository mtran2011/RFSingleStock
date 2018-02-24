package stock;

import junit.framework.TestCase;

public class Test_Stock extends TestCase {
	
	private final double tol = 1e-6;
	
	public void test_setPriceMinMax() {
		double price = 30, minprice = 0.1, maxprice = 100;
		int rounding = 2;
		Stock stock = new Stock(price, minprice, maxprice, rounding) {
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
	
	public void test_roundPrice() {
		double price = 30.351247, minprice = 0.1, maxprice = 100;
		int[] roundings = {0, 1, 2};
		for (int rounding : roundings) {
			Stock stock = new Stock(price, minprice, maxprice, rounding) {
				@Override
				public void simulate() {
					// TODO Auto-generated method stub
				}
			};
			switch (rounding) {
			case 0:
				assertEquals(30, stock.getPrice(), tol);
				break;
			case 1:
				assertEquals(30.4, stock.getPrice(), tol);
				break;
			default:
				assertEquals(30.35, stock.getPrice(), tol);
				break;
			}
		}
	}
}
