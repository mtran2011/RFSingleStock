package learner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import common.StateActionPair;
import weka.classifiers.trees.M5P;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class M5SarsaMatrixLearner extends SarsaMatrixLearner {
	
	private final int trainingLength = 1000; // number of training instances used to build a m5 tree
	private Set<M5P> m5trees; // set of trained m5 trees built so far
	private Instances trainingData; // collected data up to trainingLength steps
	
	public M5SarsaMatrixLearner(Set<Integer> actions, double initEpsilon, int targetCount, double learningRate,
			double discount) {
		super(actions, initEpsilon, targetCount, learningRate, discount);
		m5trees = new HashSet<M5P>();
		resetTrainingData();
	}
	
	private void resetTrainingData() {
		Attribute actionColumn = new Attribute("action");
		Attribute holdingColumn = new Attribute("holding");
		Attribute priceColumn = new Attribute("price");
		Attribute qValue = new Attribute("Q value");
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add(0, actionColumn);
		attributes.add(1, holdingColumn);
		attributes.add(2, priceColumn);
		attributes.add(3, qValue);
		trainingData = new Instances("trainingDataSet", attributes, trainingLength);
		trainingData.setClassIndex(trainingData.numAttributes()-1);
	}
	
	@Override
	protected void trainInternally(double reward, double nextQ) {
		double oldQ = getQ(lastStateAction);
		double newQ = oldQ + learningRate * (reward + discount * nextQ - oldQ);
		// store (lastStateAction, newQ) into trainingData instead of Qmap
		assert trainingData.size() < trainingLength;
		double[] saVals = lastStateAction.toArray();
		double[] xyRow = new double[saVals.length + 1];
		for (int i = 0; i < xyRow.length; i++) {
			if (i < saVals.length) {
				xyRow[i] = saVals[i];
			}else {
				xyRow[i] = newQ;
			}
		}
		Instance instance = new DenseInstance(1.0, xyRow);
		trainingData.add(instance);
		instance.setDataset(trainingData);
		
		// train a new tree and flush training data if needed
		if (trainingData.size() == trainingLength) {
			M5P tree = new M5P();
			try {
				tree.buildClassifier(trainingData);
				m5trees.add(tree);
				resetTrainingData();
			} catch (Exception e) {
				e.printStackTrace();
				assert false;
			}
		}
	}

	@Override
	protected Double getQ(StateActionPair sa) {
		if (m5trees.size() == 0) {
			return 0.0;
		}
		double[] saVals = sa.toArray();
		Instance instance = new DenseInstance(1.0, saVals);
		double sumPredictionVals = 0;
		for (M5P tree : m5trees) {
			try {
				sumPredictionVals += tree.classifyInstance(instance);
			} catch (Exception e) {
				e.printStackTrace();
				assert false;
			}
		}
		return sumPredictionVals / m5trees.size();
	}

}
