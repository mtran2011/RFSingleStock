package common;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.apache.commons.math3.util.Precision;

import junit.framework.TestCase;

public class Test_StateActionPair extends TestCase {
	public void testEquals() {
		int holding1 = 10;
		int holding2 = holding1;
		double price = 25.78143;
		int[] roundings = {0, 1, 2};
		for (int rounding : roundings) {
			double p1 = Precision.round(price, rounding);
			double delta = Math.pow(10, -(rounding + 2));
			double p2 = Precision.round(price + delta, rounding);
			SingleStockState state1 = new SingleStockState(holding1, p1);
			SingleStockState state2 = new SingleStockState(holding2, p2);
			assertEquals(state1, state2);
			assertEquals(state1.hashCode(), state2.hashCode());
			assertArrayEquals(state1.toArray(), state2.toArray());
		}
	}
}
