package learner;

import common.SingleStockState;

public interface Learner {

	public void resetEpisode();
	
	// return an int as the action in a given state
	public int act(SingleStockState state);

	// first use the reward and state to learn internally, then return an action
	public int learnThenAct(double reward, SingleStockState state);

}
