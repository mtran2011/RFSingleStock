package learner;

import java.util.Map;
import java.util.Set;

import common.SingleStockState;

public abstract class QMatrixLearner extends MatrixLearner {

	public QMatrixLearner(
			Set<Integer> actions, double initEpsilon, int targetCount, double learningRate, double discount) {
		super(actions, initEpsilon, targetCount, learningRate, discount);
	}

	private void trainInternally(double reward, SingleStockState state) {
		assert !atStepZero && lastStateAction != null;
		double oldQ = getQ(lastStateAction);
		// find the action with the max Q without using epsilon
		Map<Integer, Double> maxActionAndQ = findEpsilonGreedyAction(state, false);
		double maxQ = maxActionAndQ.values().iterator().next();
		Double newQ = oldQ + learningRate * (reward + discount * maxQ - oldQ);
		Qmap.put(lastStateAction, newQ);
	}
	
	@Override
	public int learnThenAct(double reward, SingleStockState state) {
		if (atStepZero) {
			return act(state);
		}
		trainInternally(reward, state);
		return act(state);
	}

}
