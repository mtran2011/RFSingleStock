package learner;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import common.SingleStockState;
import common.StateActionPair;

public abstract class SarsaMatrixLearner extends MatrixLearner {

	public SarsaMatrixLearner(Set<Integer> actions, double initEpsilon, int targetCount, double learningRate,
			double discount) {
		super(actions, initEpsilon, targetCount, learningRate, discount);
	}

	protected void trainInternally(double reward, double nextQ) {
		double oldQ = getQ(lastStateAction);
		double newQ = oldQ + learningRate * (reward + discount * nextQ - oldQ);
		Qmap.put(lastStateAction, newQ);
	}
	
	@Override
	public int learnThenAct(double reward, SingleStockState state) {
		Map<Integer, Double> res = findEpsilonGreedyAction(state, true);
		Entry<Integer, Double> entry = res.entrySet().iterator().next();
		int action = entry.getKey();
		double nextQ = entry.getValue();
		
		if (lastStateAction != null) {
			trainInternally(reward, nextQ);
		}
		
		lastStateAction = new StateActionPair(state, action);
		count += 1;
		updateEpsilon();
		return action;
	}

}
