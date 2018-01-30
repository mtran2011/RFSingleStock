package environment;

import common.SingleStockState;
import learner.Learner;

public class SingleStockTrader {
	private String name;
	private double utility;
	private Learner learner;
	private SingleStockExchange exchange;
	
	private int holding;
	private double lastTransactionCost;
	private double lastSeenPrice;
	private double wealth;
	private int stepCount;
	
	private double reward;
	private SingleStockState state;
	
	public SingleStockTrader(String name, double utility, Learner learner, SingleStockExchange exchange) {
		assert utility >= 0;
		this.name = name;
		this.utility = utility;
		this.learner = learner;
		this.exchange = exchange;
		
		this.holding = 0;
		this.wealth = 0;
		this.stepCount = -1;
		
		this.exchange.registerTrader(this);
	}
	
	public void getNotified(double price) {
		if (stepCount == -1) {
			// at initialization
			lastSeenPrice = price;
			stepCount += 1;
			state = new SingleStockState(holding, lastSeenPrice);
			return;
		}
		
		
	}
	public void resetEpisode() {
		// TODO Auto-generated method stub
		
	}
}
