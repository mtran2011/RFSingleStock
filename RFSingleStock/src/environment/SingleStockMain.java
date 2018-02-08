package environment;

import java.util.HashSet;
import java.util.Set;

import common.AssetConfig;
import learner.Learner;
import learner.RFSarsaMatrixLearner;
import learner.TabularQLearner;
import learner.TabularSarsa;
import stock.OULogStock;
import stock.Stock;

public class SingleStockMain {
	
	public static void runTrainAndTest(int iteration) {
		// TODO
		double currentprice = 75, minprice = 0.1, maxprice = 150;
		double kappa = Math.log(2)/3, mu = Math.log(50), sigma = 0.1; // reversion level is 50
		Stock stock = new OULogStock(currentprice, minprice, maxprice, kappa, mu, sigma);
		// rounding to 2 decimals so each tick is 1 cent, max holding is 100 lots
		int lotsize = 100, rounding = 2;
		AssetConfig config = new AssetConfig(lotsize, rounding, 100*lotsize);
		SingleStockExchange exchange = new SingleStockExchange(stock, config);
		
		Set<Integer> actions = new HashSet<Integer>();
		for (int k=-5; k<=5; k++) {
			actions.add(k);
		}
		
		double initEpsilon = 0.15, learningRate = 0.5, discount = 0.999;
		int targetCount = 6000; // when minimum epsilon of 0.001 kicks in
		Learner rfSarsa = new RFSarsaMatrixLearner(actions, initEpsilon, targetCount, learningRate, discount);
		Learner tabularQ = new TabularQLearner(actions, initEpsilon, targetCount, learningRate, discount);
		Learner tabularSarsa = new TabularSarsa(actions, initEpsilon, targetCount, learningRate, discount);
		
		double utility = 0.001;
		SingleStockTrader rfSarsaTrader = new SingleStockTrader("RF Sarsa", utility, rfSarsa, exchange);
		SingleStockTrader tabularQTrader = new SingleStockTrader("Tabular Q", utility, tabularQ, exchange);
		SingleStockTrader tabularSarsaTrader = new SingleStockTrader("Tabular Sarsa", utility, tabularSarsa, exchange);		
	}

	public static void main(String[] args) {

	}

}
