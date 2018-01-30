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
	private double wealth;
	private int stepCount;
	
	private double reward;
	private SingleStockState state;
	
	public void getNotified(double price) {
		// TODO Auto-generated method stub
		
	}
	public void resetEpisode() {
		// TODO Auto-generated method stub
		
	}
}
