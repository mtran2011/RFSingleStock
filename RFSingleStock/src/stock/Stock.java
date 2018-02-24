package stock;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.util.Precision;

public abstract class Stock {
	private final int[] possibleRoundings = new int[] {0, 1, 2}; 
	
	private double price;
	private double minprice;
	private double maxprice;
	private int rounding;
	
	public Stock(double price, double minprice, double maxprice, int rounding) {
		assert minprice < price && price < maxprice;
		assert minprice > 0;
		assert ArrayUtils.contains(possibleRoundings, rounding);
		
		this.minprice = minprice;
		this.maxprice = maxprice;
		this.rounding = rounding;
		setPrice(price);
	}
	
	protected void setPrice(double price) {
		price = Math.min(price, maxprice);
		price = Math.max(price, minprice);
		this.price = Precision.round(price, rounding);
	}

	public double getPrice() {
		return price;
	}
	
	public int getRounding() {
		return rounding;
	}
	
	/*
	 * Simulate the stock over time step dt = 1.0
	 */
	
	public abstract void simulate();
}
