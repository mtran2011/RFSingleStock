package environment;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import common.AssetConfig;
import learner.Learner;
import learner.M5SarsaMatrixLearner;
import learner.RFSarsaMatrixLearner;
import learner.TabularQLearner;
import learner.TabularSarsa;
import stock.OULogStock;
import stock.Stock;
import trader.SingleStockTrader;

public class MainRunningWealth {
	
	private static final String rfSarsa = "RF Sarsa", tabQ = "Tabular Q", tabSarsa = "Tabular Sarsa", m5Sarsa = "M5 tree Sarsa";
	private static final int ntrain = (int) 3e4; 
	private static final int ntest = 5000;
	
	/*
	 * Initialize variables and complete one training and one testing 
	 * @return Map of trader's name to their wealths during testing phase
	 */
	
	private static Map<String, double[]> completeTrainingAndTesting() {
		double originalPrice = 50, minprice = 0.1, maxprice = 100;
		double kappa = Math.log(2) / 5 , revertingLv = 50, sigma = 0.1;
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
		Learner rfSarsaLearner = new RFSarsaMatrixLearner(actions, initEpsilon, targetCount, learningRate, discount);
		Learner m5SarsaLearner = new M5SarsaMatrixLearner(actions, initEpsilon, targetCount, learningRate, discount);
		
		double utility = 1e-4;
		SingleStockTrader tabularQTrader = new SingleStockTrader(tabQ, utility, tabularQLearner, exchange);
		SingleStockTrader tabularSarsaTrader = new SingleStockTrader(tabSarsa, utility, tabularSarsaLearner, exchange);
		SingleStockTrader rfSarsaTrader = new SingleStockTrader(rfSarsa, utility, rfSarsaLearner, exchange);
		SingleStockTrader m5SarsaTrader = new SingleStockTrader(m5Sarsa, utility, m5SarsaLearner, exchange);
		
		SingleStockEnvi.runTrainingPhase(exchange, ntrain);
		Map<SingleStockTrader, double[]> wealths = SingleStockEnvi.runTestingPhase(exchange, ntest);
		
		Map<String, double[]> result = new HashMap<String, double[]>(); 
		for (Entry<SingleStockTrader, double[]> entry : wealths.entrySet()) {
			result.put(entry.getKey().getName(), entry.getValue());
		}
		return result;
	}
	
	/*
	 * Get result from training, testing and then write to csv file wealth PnL during testing
	 */
	
	private static void runWealthPerformance() {
		long startTime = System.currentTimeMillis();
		
		Map<String, double[]> wealthResult = completeTrainingAndTesting();
		File filename = new File("C:\\Users\\tranh\\Documents\\wealth after " + ntrain + "train" + ntest + "test.csv");
		Helpers.writeCsvTable(wealthResult, filename);
		
		long timeLen = System.currentTimeMillis() - startTime;
		System.out.print("Completed " + ntrain + " training and " + ntest + " testing steps in " 
				+ timeLen / (1000*1) + " seconds");
	}
	
	/*
	 * Repeat training and testing for ntrials times, each trial giving a Sharpe ratio during testing phase
	 * @return Map of trader's name to a column of Sharpe ratio during the trials' testing phase
	 */
	
	private static void runSharpePerformance(int ntrials) {
		long startTime = System.currentTimeMillis();
		Map<String, double[]> sharpeRatios = new HashMap<String, double[]>();
		for (int i = 0; i < ntrials; i++) {
			Map<String, double[]> wealthResult = completeTrainingAndTesting();
			if (i == 0) {
				for (String name : wealthResult.keySet()) {
					sharpeRatios.put(name, new double[ntrials]);
				}
			}
			
			for (Entry<String, double[]> entry : wealthResult.entrySet()) {
				String name = entry.getKey();
				double[] wealth = entry.getValue();
				sharpeRatios.get(name)[i] = Helpers.calculateSharpe(wealth);
			}
		}
		
		File filename = new File("C:\\Users\\tranh\\Documents\\sharpe with " + ntrain + "train" + ntest + "test.csv");
		Helpers.writeCsvTable(sharpeRatios, filename);
		long timeLen = System.currentTimeMillis() - startTime;
		System.out.print("Completed " + ntrials + " number of trials, each having " 
				+ ntrain + " training and " + ntest + " testing steps in " 
				+ timeLen / (1000*60) + " minutes");
	}
	
	public static void main(String[] args) {
		// TODO
		int ntrials = 100;
		runSharpePerformance(ntrials);
	}
}
