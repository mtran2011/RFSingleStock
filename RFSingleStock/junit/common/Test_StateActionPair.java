package common;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.apache.commons.math3.util.Precision;

import junit.framework.TestCase;

public class Test_StateActionPair extends TestCase {
	public void testEquals() {
		int holding1 = 10, holding2 = 10;
		double p1 = 25.78, p2 = 25.78;
		int action1 = -5, action2 = -5;
		SingleStockState state1 = new SingleStockState(holding1, p1);
		SingleStockState state2 = new SingleStockState(holding2, p2);
		StateActionPair pair1 = new StateActionPair(state1, action1);
		StateActionPair pair2 = new StateActionPair(state2, action2);
		assertEquals(pair1, pair2);
		assertEquals(pair1.hashCode(), pair2.hashCode());
		assertArrayEquals(pair1.toArray(), pair2.toArray());
	}
}
