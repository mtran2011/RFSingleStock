package environment;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import common.AssetConfig;
import learner.Learner;
import learner.TabularQLearner;
import learner.TabularSarsa;
import stock.OULogStock;
import stock.Stock;
import trader.SingleStockTrader;

public class MainRunningWealth {
	
	private static final String rfSarsa = "RF Sarsa", tabQ = "Tabular Q", tabSarsa = "Tabular Sarsa";
	private static final int ntrain = (int) 1e7, ntest = 5000;
	
	private static Map<String, double[]> completeTrainingAndTesting() {
		double originalPrice = 50, minprice = 0.1, maxprice = 100;
		double kappa = Math.log(2) /5 , revertingLv = 50, sigma = 0.1;
		int lotsize = 10, rounding = 1;
		int maxholding = 10 * lotsize;
		
		Stock stock = new OULogStock(originalPrice, minprice, maxprice, rounding, kappa, revertingLv, sigma);
		AssetConfig config = new AssetConfig(lotsize, maxholding);
		SingleStockExchange exchange = new SingleStockExchange(stock, config);
		
		Set<Integer> actions = new HashSet<Integer>();
		for (int k = -5; k <= 5; k++) {
			actions.add(k*lotsize);
		}
		
		double initEpsilon = 0.10, learningRate = 0.001, discount = 0.999;
		int targetCount = ntrain; // when minimum epsilon of 0.001 kicks in
		Learner tabularQLearner = new TabularQLearner(actions, initEpsilon, targetCount, learningRate, discount);
		Learner tabularSarsaLearner = new TabularSarsa(actions, initEpsilon, targetCount, learningRate, discount);
		
		double utility = 1e-4;
		SingleStockTrader tabularQTrader = new SingleStockTrader(tabQ, utility, tabularQLearner, exchange);
		SingleStockTrader tabularSarsaTrader = new SingleStockTrader(tabSarsa, utility, tabularSarsaLearner, exchange);
		
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
		
		Map<String, double[]> wealthResult = completeTrainingAndTesting();
		File filename = new File("C:\\Users\\tranh\\Documents\\wealth after " + ntrain + "train" + ntest + "test.csv");
		Helpers.writeCsvTable(wealthResult, filename);
		
		long timeLen = System.currentTimeMillis() - startTime;
		System.out.print("Completed " + ntrain + " training and " + ntest + " testing steps in " 
				+ timeLen / (1000*1) + " seconds");
	}
}
