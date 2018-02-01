package common;

public class SingleStockState {
	private int holding;
	private double price;
	
	public SingleStockState(int holding, double price) {
		this.holding = holding;
		this.price = price;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + holding;
		long temp;
		temp = Double.doubleToLongBits(price);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SingleStockState other = (SingleStockState) obj;
		if (holding != other.holding) {
			return false;
		}
		if (Double.doubleToLongBits(price) != Double.doubleToLongBits(other.price)) {
			return false;
		}
		return true;
	}
	
	public double[] toArray() {
		return new double[] {holding, price};
	}
}
