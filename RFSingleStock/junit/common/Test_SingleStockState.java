package common;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import junit.framework.TestCase;

public class Test_SingleStockState extends TestCase {
	public void testEquals() {
		int holding1 = 10, holding2 = 10;
		double p1 = 25.78, p2 = 25.78;
		SingleStockState state1 = new SingleStockState(holding1, p1);
		SingleStockState state2 = new SingleStockState(holding2, p2);
		assertEquals(state1, state2);
		assertEquals(state1.hashCode(), state2.hashCode());
		assertArrayEquals(state1.toArray(), state2.toArray());
	}
}
