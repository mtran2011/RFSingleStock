package trader;

import common.SingleStockState;
import environment.SingleStockExchange;
import learner.Learner;

public class SingleStockTrader {
	private final double initWealth = 1e6; // the initial wealth this trader starts with
	
	private final String name;
	private final double utility;
	private final Learner learner;
	
	private int holding;
	private double wealth;
	private double lastSeenPrice;
	private double lastTransactionCost;
	private int stepCount;
	
	private double reward;
	private SingleStockState state;
	
	public SingleStockTrader(String name, double utility, Learner learner, SingleStockExchange exchange) {
		assert utility >= 0;
		this.name = name;
		this.utility = utility;
		this.learner = learner;
		resetEpisode(exchange);
	}
	
	/*
	 * Reset holding, wealth, etc. before an episode
	 * Register itself with an exchange and get updated price
	 */
	
	public void resetEpisode(SingleStockExchange exchange) {
		holding = 0;
		wealth = initWealth; // start with some nominal positive amount
		stepCount = -1; // reset to before the 0th step 
		exchange.registerTrader(this); // register self with exchange and get latest price
		
		lastTransactionCost = 0;
		reward = 0;
		learner.resetEpisode();
	}
	
	public void getNotified(double price) {
		if (stepCount == -1) {
			// at initialization
			lastSeenPrice = price;
			stepCount = 0;
			state = new SingleStockState(holding, lastSeenPrice);
		} else {
			double pnl = holding * (price - lastSeenPrice);
			lastSeenPrice = price;
			double deltaWealth = pnl - lastTransactionCost;
			wealth += deltaWealth;
			stepCount += 1;
			reward = deltaWealth - 0.5 * utility * Math.pow(deltaWealth - wealth / stepCount, 2);
			state = new SingleStockState(holding, price);
		}
	}
	
	public void placeOrder(SingleStockExchange exchange) {
		int order = stepCount < 1 ? learner.act(state) : learner.learnThenAct(reward, state);
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

	public String getName() {
		return name;
	}

	public double getWealth() {
		return wealth;
	}
	
	/*
	 * Getter methods (pkg visible) for testing purpose only
	 */
	
	int getHolding() {
		return holding;
	}

	double getLastSeenPrice() {
		return lastSeenPrice;
	}

	double getLastTransactionCost() {
		return lastTransactionCost;
	}

	int getStepCount() {
		return stepCount;
	}

	double getReward() {
		return reward;
	}

	SingleStockState getState() {
		return state;
	}

}