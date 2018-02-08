package environment;

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
	
	public static Map<String, Double> runOneCompleteTrial(int iteration) {
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
		
		int ntrain = 6000, ntest = 1000;
		return trainAndTest(exchange, ntrain, ntest);
	}

	public static void main(String[] args) {

	}

}
