package learner;

import java.util.Set;

import common.StateActionPair;
import smile.regression.RandomForest;

public class RFSarsaMatrixLearner extends SarsaMatrixLearner {

	private RandomForest forest;
	private final int ntrees = 30;
	private final int nodeSize = 5;
	private final int mtry = 2;
	private final int stepMultiple = 100;
	
	public RFSarsaMatrixLearner(Set<Integer> actions, double initEpsilon, int targetCount, double learningRate,
			double discount) {
		super(actions, initEpsilon, targetCount, learningRate, discount);
	}

	@Override
	protected double getQ(StateActionPair sa) {
		if (count < stepMultiple) {
			return 0;
		}
		if (count % stepMultiple == 0) {
			// prepare training data
			
		}
	}

}
