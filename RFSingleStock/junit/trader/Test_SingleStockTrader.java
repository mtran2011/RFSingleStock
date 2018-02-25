package trader;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

import common.AssetConfig;
import common.SingleStockState;
import environment.SingleStockExchange;
import learner.Learner;
import stock.Stock;

class Test_SingleStockTrader {

	@Test
	void testConstructor() {
		String name = "testTrader";
		double utility = 0.1;
		Learner learner = mock(Learner.class);
		Stock stock = mock(Stock.class);
		double price = 31.25678;
		when(stock.getPrice()).thenReturn(price);
		AssetConfig config = new AssetConfig(1, 5);
		SingleStockExchange exchange = new SingleStockExchange(stock, config);
		SingleStockTrader trader = new SingleStockTrader(name, utility, learner, exchange);
		
		assertEquals(0, trader.getHolding());
		assertEquals(price, trader.getLastSeenPrice());
		assertEquals(0, trader.getLastTransactionCost());
		assertEquals(0, trader.getStepCount());
		assertEquals(0, trader.getReward());
		assertEquals(new SingleStockState(0, price), trader.getState());
	}

}
