package environment;

import java.util.HashSet;
import java.util.Set;

import common.AssetConfig;
import stock.Stock;
import trader.SingleStockTrader;

public class SingleStockExchange {
	private static final double spreadFactor = 1;
	private static final double impactFactor = 1;
	
	private Set<SingleStockTrader> traders;
	private Stock stock;
	private AssetConfig config;
	
	private void notifyTraders() {
		for (SingleStockTrader trader: traders) {
			trader.getNotified(stock.getPrice());
		}
	}
	
	public SingleStockExchange(Stock stock, AssetConfig config) {
		this.stock = stock;
		this.config = config;
		this.traders = new HashSet<SingleStockTrader>();
	}
	
	public void registerTrader(SingleStockTrader trader) {
		traders.add(trader);
		trader.getNotified(stock.getPrice());
	}
	
	public int getMaxHolding() {
		return config.getMaxholding();
	}
	
	public Set<SingleStockTrader> getTraders() {
		return new HashSet<SingleStockTrader>(traders);
	}

	public double execute(int quantity) {
		double tick = Math.pow(10, -stock.getRounding());
		double spreadCost = Math.abs(quantity) * tick;
		double impactCost = Math.pow(quantity, 2) * tick / config.getLotsize();
		return spreadCost * spreadFactor + impactCost * impactFactor;
	}

	public void resetEpisode() {
		for (SingleStockTrader trader: traders) {
			trader.resetEpisode(this);
		}
	}

	public void simulate() {
		stock.simulate();
		notifyTraders();
	}
}
