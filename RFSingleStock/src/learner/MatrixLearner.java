package learner;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import common.SingleStockState;
import common.StateActionPair;

/**
 * Abstract class for matrix-based Q-learning and Sarsa
 */
public abstract class MatrixLearner implements Learner {
	private Set<Integer> actions;
	private double epsilon;
	
	protected StateActionPair lastStateAction;
	
	private final double minEpsilon = 0.001;
	private final int targetCount; // when minEpsilon kicks in
	private final int targetCountSplit = 4; // epsilon is flat for the first 1/4 of target count, then linear decay to min
	private final double initEpsilon; 
	private final double slope; // the slope of epsilon linear decay
	protected int count; // number of steps completed, used to update epsilon
	
	protected Map<StateActionPair, Double> Qmap; // map a pair of (s,a) to Q(s,a)
	
	protected final double learningRate;
	protected final double discount;
	
	private Random random;
	
	private Integer getRandomAction() {
		int size = actions.size();
		assert size > 0;
		int item = random.nextInt(size);
		int i = 0;
		for (Integer a: actions) {
			if (i == item) {
				return a;
			}
			i++;
		}
		assert false; // Execution should never reach this point
		return null;
	}
	
	protected void updateEpsilon() {
		if (count < targetCount / targetCountSplit) {
			epsilon = initEpsilon;
		} else if (count > targetCount) {
			epsilon = minEpsilon;
		} else {
			epsilon = initEpsilon + slope * (count - targetCount / targetCountSplit);
		}
	}
	
	protected abstract Double getQ(StateActionPair sa);
	
	protected Map<Integer, Double> findEpsilonGreedyAction(SingleStockState state, boolean useEpsilon) {
		// return a map of the action (an int) to its Q(state,action) value
		Integer bestAction = null;
		Double bestQ = null;
		if (useEpsilon && random.nextDouble() < epsilon) {
			bestAction = getRandomAction();
			bestQ = getQ(new StateActionPair(state, bestAction));
		} else {
			int countMaxQ = 0;
			for (Integer action: actions) {
				if (bestAction == null || bestQ == null) {
					countMaxQ = 1;
					bestQ = getQ(new StateActionPair(state, action));
					bestAction = action;
					countMaxQ++;
				} else {
					Double newQ = getQ(new StateActionPair(state, action));
					if (newQ.compareTo(bestQ) > 0) {
						countMaxQ = 1;
						bestQ = newQ;
						bestAction = action;
						countMaxQ++;
					} else if (newQ.compareTo(bestQ) == 0) {
						// randomly choose either the existing bestAction or the new found action
						// this is a true method for randomizing action that has the same q value
						if (random.nextFloat() <  1.0 / countMaxQ) {
							bestAction = action;
						}
						countMaxQ++;
					}
				}
			}
		}
		HashMap<Integer, Double> res = new HashMap<Integer, Double>();
		res.put(bestAction, bestQ);
		return res;
	}
	
	public MatrixLearner(
			Set<Integer> actions, double initEpsilon, int targetCount, double learningRate, double discount) {
		assert actions.size() > 0;
		assert minEpsilon < initEpsilon && initEpsilon <= 1;
		assert targetCount >= targetCountSplit;
		assert learningRate > 0 && learningRate <= 1;
		assert 0 < discount && discount < 1;
		
		this.actions = actions;
		this.initEpsilon = initEpsilon;
		this.targetCount = targetCount;
		this.learningRate = learningRate;
		this.discount = discount;
		
		slope = (minEpsilon - initEpsilon) * 1.0 / (targetCount - targetCount / targetCountSplit);
		epsilon = initEpsilon;
		
		count = 0;
		Qmap = new HashMap<StateActionPair, Double>();
		random = new Random();
		
		lastStateAction = null;
	}
	
	@Override
	public void resetEpisode() {
		lastStateAction = null;
	}
	
	@Override
	public int act(SingleStockState state) {
		Map<Integer, Double> res = findEpsilonGreedyAction(state, true);
		int action = res.keySet().iterator().next();
		lastStateAction = new StateActionPair(state, action);
		count += 1;
		updateEpsilon();
		return action;
	}
	
}
