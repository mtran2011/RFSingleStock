package environment;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import common.AssetConfig;
import learner.Learner;
import learner.RFSarsaMatrixLearner;
import learner.TabularQLearner;
import learner.TabularSarsa;
import stock.OULogStock;
import stock.Stock;

public class SingleStockMain {
	
	private static final String rfsarsa = "RF Sarsa", tabularq = "Tabular Q", tabularsarsa = "Tabular Sarsa";
	private static final String[] traderNames = {rfsarsa, tabularq, tabularsarsa};
	private static final int ntrain = 100, ntest = 100, ntrials = 5;
	
	public static Map<String, Double> trainAndTest(SingleStockExchange exchange, int ntrain, int ntest) {
		// run training, then run testing, then return a map of trader's name to its Sharpe ratio during ntest
		// run training first
		exchange.resetEpisode();
		for (int i=1; i <= ntrain; i++) {
			for (SingleStockTrader trader: exchange.getTraders()) {
				trader.placeOrder();
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
		for (int i=0; i < ntest; i++) {
			for (SingleStockTrader trader: exchange.getTraders()) {
				if (i==0) {
					previousWealth.put(trader, trader.getWealth());
				}
				
				trader.placeOrder();
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
			double average = 0;
			for (double ret: returns) {
				average += ret;
			}
			average = average / returns.length;
			double stdev = 0;
			for (double ret: returns) {
				stdev += Math.pow(ret - average, 2);
			}
			stdev = stdev / (returns.length - 1);
			stdev = Math.sqrt(stdev);
			sharpe.put(trader.getName(), average / stdev);
		}
		return sharpe;
	}
	
	public static Map<String, Double> runOneCompleteTrial() {
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
		SingleStockTrader rfSarsaTrader = new SingleStockTrader(rfsarsa, utility, rfSarsa, exchange);
		SingleStockTrader tabularQTrader = new SingleStockTrader(tabularq, utility, tabularQ, exchange);
		SingleStockTrader tabularSarsaTrader = new SingleStockTrader(tabularsarsa, utility, tabularSarsa, exchange);
		
		return trainAndTest(exchange, ntrain, ntest);
	}

	public static void writeCsv(Map<String, double[]> sharpeRatios) {
		String filename = "C:\\Users\\tranh\\Documents\\" + ntrain + "train" + ntest + "test";
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
		Map<String, double[]> sharpeRatios = new HashMap<String, double[]>();
		for (String name: traderNames) {
			sharpeRatios.put(name, new double[ntrials]);
		}
		for (int i=0; i < ntrials; i++) {
			Map<String, Double> trialResult = runOneCompleteTrial();
			for (String name: traderNames) {
				sharpeRatios.get(name)[i] = trialResult.get(name).doubleValue();
			}
		}
		writeCsv(sharpeRatios);
	}

}
