package stock;

import java.util.Random;

public class OULogStock extends Stock {
	private double kappa;
	private double mu;
	private double sigma;
	private Random random;
	
	public OULogStock(double price, double minprice, double maxprice, double kappa, double mu, double sigma) {		
		super(price, minprice, maxprice);
		assert kappa >= 0 && sigma >= 0 && mu >= 0;
		this.kappa = kappa;
		this.mu = mu;
		this.sigma = sigma;
		random = new Random();
	}
	
	@Override
	public void simulate() {
		// simulate over dt = 1.0;
		double price = getPrice();
		if (price == 0) {
			return;
		}
		double oldLog = Math.log(price);
		double dlogS = kappa * (mu - oldLog) + sigma * random.nextGaussian();
		price = price * Math.exp(dlogS);
		setPrice(price);
	}

}
