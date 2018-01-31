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
	private boolean atStepZero; // true if standing at beginning of an episode
	
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
			atStepZero = true;
			stepCount += 1;
			state = new SingleStockState(holding, lastSeenPrice);
			return;
		}
		double pnl = holding * (price - lastSeenPrice);
		lastSeenPrice = price;
		double deltaWealth = pnl - lastTransactionCost;
		wealth += deltaWealth;
		stepCount += 1;
		reward = deltaWealth - 0.5 * utility * Math.pow(deltaWealth - wealth / stepCount, 2);
		state = new SingleStockState(holding, lastSeenPrice);
	}
	
	public void resetEpisode() {
		holding = 0;
		wealth = 0;
		stepCount = -1;
		exchange.registerTrader(this);
		
		lastTransactionCost = 0;
		reward = 0;
		learner.resetEpisode();
	}
	
	public void placeOrder() {
		int order;
		if (atStepZero) {
			order = learner.act(state);
		} else {
			order = learner.learnThenAct(reward, state);
		}
		int maxHolding = exchange.getMaxHolding();
		if ((holding + order) > maxHolding) {
			order = maxHolding - holding;
		}
		if ((holding + order) < -maxHolding) {
			order = -maxHolding - holding;
		}
		lastTransactionCost = exchange.execute(order);
		holding += order;
	}
}