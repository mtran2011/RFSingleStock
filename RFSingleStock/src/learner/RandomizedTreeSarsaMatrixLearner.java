package learner;

import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import common.StateActionPair;
import smile.regression.RandomForest;

public class RandomizedTreeSarsaMatrixLearner extends SarsaMatrixLearner {
	
	private List<RandomForest> trees;
	private final int nodeSize = 5;
	private final int mtry = 2; // number of random features selected for each tree
	private final int stepMultiple = (int) 1e5;

	public RandomizedTreeSarsaMatrixLearner(Set<Integer> actions, double initEpsilon, int targetCount,
			double learningRate, double discount) {
		super(actions, initEpsilon, targetCount, learningRate, discount);
	}

	@Override
	protected Double getQ(StateActionPair sa) {
		if (count < stepMultiple) {
			return 0.0;
		}
		if (count % stepMultiple == 0) {
			// prepare training data
			int ntrain = Qmap.size();
			int nfeatures = sa.toArray().length;
			double[][] xtrain = new double[ntrain][nfeatures];
			double[] ytrain = new double[ntrain];
			int row = 0;
			for (Entry<StateActionPair, Double> entry: Qmap.entrySet()) {
				double[] oneRow = entry.getKey().toArray();
				for (int i = 0; i < oneRow.length; i++) {
					xtrain[row][i] = oneRow[i]; 
				}
				ytrain[row] = entry.getValue();
				row++;
			}
			// train the random forest of 1 tree, this is a randomized tree
			// add the tree to list of trees
			RandomForest forest = new RandomForest(xtrain, ytrain, 1, Qmap.size(), nodeSize, mtry);
			trees.add(forest);
		}
		// use the list of trees to predict 
		double[] x = sa.toArray();
		double prediction = 0;
		for (RandomForest tree : trees) {
			prediction += tree.predict(x);
		}
		return prediction / trees.size();
		
	}

}
