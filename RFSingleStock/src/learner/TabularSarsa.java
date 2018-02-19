package learner;

import java.util.Set;

import common.StateActionPair;

public class TabularSarsa extends SarsaMatrixLearner {

	public TabularSarsa(Set<Integer> actions, double initEpsilon, int targetCount, double learningRate,
			double discount) {
		super(actions, initEpsilon, targetCount, learningRate, discount);
	}

	@Override
	protected Double getQ(StateActionPair sa) {
		return Qmap.getOrDefault(sa, 0.0);
	}
}
