package environment;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import common.AssetConfig;
import learner.Learner;
import learner.RFSarsaMatrixLearner;
import learner.TabularQLearner;
import learner.TabularSarsa;
import stock.OULogStock;
import stock.Stock;

public class MainRunningSharpe {
	
	private static final String rfsarsa = "RF Sarsa", tabularq = "Tabular Q", tabularsarsa = "Tabular Sarsa";
	private static final String[] traderNames = {rfsarsa, tabularq, tabularsarsa};
	private static final int ntrain = 1000000, ntest = 5000, ntrials = 10;
	
	/*
	 * Runs training and testing for a given exchange and its traders
	 * @return A map of each trader's name to its Sharpe ratio during testing steps
	 */
	
	public static Map<String, Double> trainAndTest(SingleStockExchange exchange) {
		// run training, then run testing, then return a map of trader's name to its Sharpe ratio during ntest
		// run training first
		exchange.resetEpisode();
		
		for (int i=0; i < ntrain; i++) {
			for (SingleStockTrader trader: exchange.getTraders()) {
				trader.placeOrder(exchange);
			}
			exchange.simulate();
		}
		
		// run testing
		Map<SingleStockTrader, double[]> traderRets = new HashMap<SingleStockTrader, double[]>();
		for (SingleStockTrader trader: exchange.getTraders()) {
			// store the one step return of each trader
			traderRets.put(trader, new double[ntest]);
		}
		
		exchange.resetEpisode();
		
		Map<SingleStockTrader, Double> previousWealth = new HashMap<SingleStockTrader, Double>();
		for (SingleStockTrader trader: exchange.getTraders()) {
			previousWealth.put(trader, trader.getWealth());
		}
		
		for (int i=0; i < ntest; i++) {
			for (SingleStockTrader trader: exchange.getTraders()) {
				trader.placeOrder(exchange);
			}
			
			exchange.simulate();
			
			for (SingleStockTrader trader: exchange.getTraders()) {
				double[] returns = traderRets.get(trader);
				returns[i] = (trader.getWealth() - previousWealth.get(trader)) / previousWealth.get(trader);
				previousWealth.put(trader, trader.getWealth());
			}
		}
		
		Map<String, Double> sharpe = new HashMap<String, Double>();
		for (SingleStockTrader trader: exchange.getTraders()) {
			double[] returns = traderRets.get(trader);
			DescriptiveStatistics desc = new DescriptiveStatistics(returns);
			sharpe.put(trader.getName(), desc.getMean() / desc.getStandardDeviation());
		}
		return sharpe;
	}
	
	/*
	 * Runs one trial from initializing the exchange and learners to training and testing
	 * @return A map of each trader's name to its Sharpe ratio during testing steps
	 */
	
	public static Map<String, Double> runOneCompleteTrial() {
		double originalPrice = 30, minprice = 0.1, maxprice = 100;
		double kappa = 0.1, mu = Math.log(50), sigma = 0.1; // reversion price level is 50
		int lotsize = 100, rounding = 0;
		int maxholding = 10 * lotsize;
		
		Stock stock = new OULogStock(originalPrice, minprice, maxprice, rounding, kappa, mu, sigma);
		AssetConfig config = new AssetConfig(lotsize, maxholding);
		SingleStockExchange exchange = new SingleStockExchange(stock, config);
		
		Set<Integer> actions = new HashSet<Integer>();
		for (int k = -5; k <= 5; k++) {
			actions.add(k*lotsize);
		}
		
		double initEpsilon = 0.15, learningRate = 0.5, discount = 0.999;
		int targetCount = ntrain; // when minimum epsilon of 0.001 kicks in
		// Learner rfSarsa = new RFSarsaMatrixLearner(actions, initEpsilon, targetCount, learningRate, discount);
		Learner tabularQ = new TabularQLearner(actions, initEpsilon, targetCount, learningRate, discount);
		Learner tabularSarsa = new TabularSarsa(actions, initEpsilon, targetCount, learningRate, discount);
		
		double utility = 0.1;
		// SingleStockTrader rfSarsaTrader = new SingleStockTrader(rfsarsa, utility, rfSarsa, exchange);
		SingleStockTrader tabularQTrader = new SingleStockTrader(tabularq, utility, tabularQ, exchange);
		SingleStockTrader tabularSarsaTrader = new SingleStockTrader(tabularsarsa, utility, tabularSarsa, exchange);
		
		return trainAndTest(exchange);
	}

	public static void writeCsv(Map<String, double[]> sharpeRatios) {
		String filename = "C:\\Users\\MinhHa\\Documents\\" + ntrain + "train" + ntest + "test.csv";
		String delimiter = ",";
		
		FileWriter fileWriter = null;
		BufferedWriter bufferedWriter = null;
		try {
			fileWriter = new FileWriter(filename);
			bufferedWriter = new BufferedWriter(fileWriter);
			
			bufferedWriter.write(String.join(delimiter, traderNames));
			bufferedWriter.newLine();
			
			for (int i=0; i < ntrials; i++) {
				for (int k=0; k < traderNames.length; k++) {
					bufferedWriter.write(String.format("%.4f", sharpeRatios.get(traderNames[k])[i]));
					if (k < (traderNames.length-1)) {
						bufferedWriter.write(delimiter);
					}
				}
				if (i < (ntrials-1)) {
					bufferedWriter.newLine();
				}
			}
		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter");
			e.printStackTrace();
		} finally {
			try {
				bufferedWriter.close();
				fileWriter.close();
			} catch (Exception e2) {
				System.out.println("Error closing output file");
				e2.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		
		Map<String, double[]> sharpeRatios = new HashMap<String, double[]>();
		for (String name: traderNames) {
			sharpeRatios.put(name, new double[ntrials]);
		}
		for (int i=0; i < ntrials; i++) {
			Map<String, Double> trialResult = runOneCompleteTrial();
			for (String name: traderNames) {
				if (trialResult.containsKey(name)) {
					sharpeRatios.get(name)[i] = trialResult.get(name).doubleValue();
				}
			}
		}
		writeCsv(sharpeRatios);
		
		long timeLen = System.currentTimeMillis() - startTime;
		System.out.print("Completed " + ntrials + " trials, each with " + ntrain + " training and " 
				+ ntest + " testing steps, in " 
				+ timeLen / (1000*1) + " seconds");
	}

}
