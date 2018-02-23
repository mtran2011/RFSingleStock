package environment;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SingleStockEnvi {
	
	/*
	 * Run the training for traders associated with an exchange
	 */
	
	public static void runTrainingPhase(SingleStockExchange exchange, int ntrain) {
		exchange.resetEpisode();
		for (int i = 0; i < ntrain; i++) {
			for (SingleStockTrader trader : exchange.getTraders()) {
				trader.placeOrder();
			}
			
			exchange.simulate();
		}
	}
	
	/*
	 * Run the testing for traders associated with an exchange
	 * @return Map of each trader to their wealth during testing phase
	 */
	
	public static Map<SingleStockTrader, double[]> runTestingPhase(SingleStockExchange exchange, int ntest) {
		exchange.resetEpisode(); // reset all traders to original wealth
		Map<SingleStockTrader, double[]> wealths = new HashMap<SingleStockTrader, double[]>();
		Set<SingleStockTrader> traders = exchange.getTraders();
		for (SingleStockTrader trader : traders) {
			double[] oneTraderWealth = new double[ntest+1];
			oneTraderWealth[0] = trader.getWealth();
			wealths.put(trader, oneTraderWealth);
		}
		
		for (int i = 1; i <= ntest; i++) {
			for (SingleStockTrader trader : traders) {
				trader.placeOrder();
			}
			
			exchange.simulate();
			
			for (SingleStockTrader trader : traders) {
				double[] oneTraderWealth = wealths.get(trader);
				oneTraderWealth[i] = trader.getWealth();
			}
		}
		
		return wealths;
	}
}
