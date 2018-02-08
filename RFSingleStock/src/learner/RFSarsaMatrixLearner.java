package learner;

import java.util.Map.Entry;
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
			int ntrain = Qmap.size();
			int nfeatures = 3;
			double[][] xtrain = new double[ntrain][nfeatures];
			double[] ytrain = new double[ntrain];
			int row = 0;
			for (Entry<StateActionPair, Double> entry: Qmap.entrySet()) {
				double[] x = entry.getKey().toArray();
				System.arraycopy(x, 0, xtrain[row], 0, x.length);
				ytrain[row] = entry.getValue();
				row++;
			}
			// train the random forest
			forest = new RandomForest(xtrain, ytrain, ntrees, Qmap.size(), nodeSize, mtry);
		}
		// use the rf to predict 
		double[] x = sa.toArray();
		return forest.predict(x);
	}
}
