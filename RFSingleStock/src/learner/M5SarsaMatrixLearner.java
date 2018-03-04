package learner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import common.StateActionPair;
import weka.classifiers.trees.M5P;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instances;

public class M5SarsaMatrixLearner extends SarsaMatrixLearner {
	
	private final int trainingLength = 1000; // num of training instances used to build a m5 tree
	private Set<M5P> m5trees; // set of trained m5 trees built so far
	private Instances trainingData; // collected data up to trainingLength instances
	
	public M5SarsaMatrixLearner(Set<Integer> actions, double initEpsilon, int targetCount, double learningRate,
			double discount) {
		super(actions, initEpsilon, targetCount, learningRate, discount);
		m5trees = new HashSet<M5P>();
		// set up training data
		Attribute actionColumn = new Attribute("action");
		Attribute holdingColumn = new Attribute("holding");
		Attribute priceColumn = new Attribute("price");
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		trainingData = new Instances("trainingDataSet", attributes, trainingLength);
	}

	@Override
	protected Double getQ(StateActionPair sa) {
		
		// TODO Auto-generated method stub
		
		return null;
	}

}
