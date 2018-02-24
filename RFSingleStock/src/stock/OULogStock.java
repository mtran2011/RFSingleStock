package stock;

import java.util.Random;

public class OULogStock extends Stock {
	private double kappa;
	private double mu;
	private double sigma;
	private Random random;
	
	public OULogStock(double price, double minprice, double maxprice, int rounding, double kappa, double revertingLvl, double sigma) {
		super(price, minprice, maxprice, rounding);
		assert 1 >= kappa && kappa >= 0 && sigma >= 0;
		assert minprice < revertingLvl && revertingLvl < maxprice; 
		this.kappa = kappa;
		this.mu = Math.log(revertingLvl);
		this.sigma = sigma;
		random = new Random();
	}
	
	@Override
	public void simulate() {
		double price = getPrice();
		if (Double.compare(price, 0) == 0) {
			return;
		}
		double oldLog = Math.log(price);
		double dlogS = kappa * (mu - oldLog) + sigma * random.nextGaussian();
		price = price * Math.exp(dlogS);
		setPrice(price);
	}

}
