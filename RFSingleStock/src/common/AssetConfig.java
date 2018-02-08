package common;

//the parameters associated with an asset when the asset is on an exchange
public class AssetConfig {
	private int lotsize;
	private int rounding; // rounding of price to how many decimals
	private int maxholding;
	private double tick;

	public int getLotsize() {
		return lotsize;
	}

	public int getRounding() {
		return rounding;
	}

	public int getMaxholding() {
		return maxholding;
	}
	
	public double getTick() {
		return tick;
	}

	public AssetConfig(int lotsize, int rounding, int maxholding) {
		assert lotsize > 0 && maxholding > 0;
		assert rounding == 0 || rounding == 1 || rounding == 2;
		this.lotsize = lotsize;
		this.rounding = rounding;
		this.maxholding = maxholding;
		tick = Math.pow(10, -rounding);
	}	
}
