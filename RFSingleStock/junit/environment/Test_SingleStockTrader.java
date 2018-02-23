package environment;

import org.mockito.Mock;
import org.mockito.Mockito;

import common.SingleStockState;
import junit.framework.TestCase;
import learner.Learner;
import stock.Stock;

public class Test_SingleStockTrader extends TestCase {
	
	private final double delta = 1e-6;
	
	public void test_constructor() {
		String name = "test_trader";
		double utility = 0.001;
		Learner learner = Mockito.mock(Learner.class);
		/*
		SingleStockExchange exchange = new SingleStockExchange(stock, config)
		SingleStockTrader trader = new SingleStockTrader(name, utility, learner, exchange)
		*/
	}
}
