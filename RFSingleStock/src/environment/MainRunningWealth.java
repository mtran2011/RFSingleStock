package environment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import common.AssetConfig;
import fansi.Str;
import learner.Learner;
import learner.TabularQLearner;
import learner.TabularSarsa;
import stock.OULogStock;
import stock.Stock;

public class MainRunningWealth {
	
	private static final String rfSarsa = "RF Sarsa", tabularQ = "Tabular Q", tabularSarsa = "Tabular Sarsa";
	private static final int ntrain = 1000000, ntest = 5000;
	
	private static Map<String, double[]> completeTrainingAndTesting() {
		double originalPrice = 30, minprice = 0.1, maxprice = 100;
		double kappa = 0.1, mu = Math.log(50), sigma = 0.1; // reversion price level is 50
		Stock stock = new OULogStock(originalPrice, minprice, maxprice, kappa, mu, sigma);
		// rounding to 2 decimals means each tick is 1 cent, or 0 decimal means each tick is 1 dollar
		int lotsize = 100, rounding = 0;
		int maxholding = 10 * lotsize;
		AssetConfig config = new AssetConfig(lotsize, rounding, maxholding);
		
		SingleStockExchange exchange = new SingleStockExchange(stock, config);
		
		Set<Integer> actions = new HashSet<Integer>();
		for (int k = -5; k <= 5; k++) {
			actions.add(k*lotsize);
		}
		
		double initEpsilon = 0.15, learningRate = 0.5, discount = 0.999;
		int targetCount = ntrain; // when minimum epsilon of 0.001 kicks in
		// Learner rfSarsa = new RFSarsaMatrixLearner(actions, initEpsilon, targetCount, learningRate, discount);
		Learner tabularQLearner = new TabularQLearner(actions, initEpsilon, targetCount, learningRate, discount);
		Learner tabularSarsaLearner = new TabularSarsa(actions, initEpsilon, targetCount, learningRate, discount);
		
		double utility = 0.01;
		// SingleStockTrader rfSarsaTrader = new SingleStockTrader(rfsarsa, utility, rfSarsa, exchange);
		SingleStockTrader tabularQTrader = new SingleStockTrader(tabularQ, utility, tabularQLearner, exchange);
		SingleStockTrader tabularSarsaTrader = new SingleStockTrader(tabularSarsa, utility, tabularSarsaLearner, exchange);
		
		SingleStockEnvi.runTrainingPhase(exchange, ntrain);
		Map<SingleStockTrader, double[]> wealths = SingleStockEnvi.runTestingPhase(exchange, ntest);
		
		Map<String, double[]> result = new HashMap<String, double[]>(); 
		for (Entry<SingleStockTrader, double[]> entry : wealths.entrySet()) {
			result.put(entry.getKey().getName(), entry.getValue());
		}
		return result;
	}
	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		
		
		long timeLen = System.currentTimeMillis() - startTime;
		System.out.print("Completed " + ntrain + " training and " + ntest + " testing steps in " 
				+ timeLen / (1000*1) + " seconds");
	}
}
