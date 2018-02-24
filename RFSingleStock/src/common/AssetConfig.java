package common;

//the parameters associated with an asset when the asset is on an exchange
public class AssetConfig {
	private int lotsize;
	private int maxholding;

	public int getLotsize() {
		return lotsize;
	}

	public int getMaxholding() {
		return maxholding;
	}

	public AssetConfig(int lotsize, int maxholding) {
		assert lotsize > 0 && maxholding > 0;
		this.lotsize = lotsize;
		this.maxholding = maxholding;
	}	
}
