package stock;

public abstract class Stock {
	private double price;
	private double minprice;
	private double maxprice;	
	
	public Stock(double price, double minprice, double maxprice) {
		assert minprice < price && price < maxprice;
		assert minprice > 0;
		this.price = price;
		this.minprice = minprice;
		this.maxprice = maxprice;
	}
	
	protected void setPrice(double price) {
		price = Math.min(price, maxprice);
		price = Math.max(price, minprice);
		this.price = price;
	}

	public double getPrice() {
		return price;
	}
	
	public abstract void simulate();
}
