package environment;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.math3.util.Precision;

import common.AssetConfig;
import stock.Stock;

public class SingleStockExchange {
	private Set<SingleStockTrader> traders;
	private Stock stock;
	private AssetConfig config;
	
	private double roundPrice(double price) {
		return Precision.round(price, config.getRounding());
		/*
		if (rounding == 0) {
			return Math.round(price);
		} else if (rounding == 1) {
			return Math.round(price * 10.0) / 10.0;
		}
		return Math.round(price * 100.0) / 100.0;
		*/
	}
	
	private void notifyOneTrader(SingleStockTrader trader) {
		double price = roundPrice(stock.getPrice());
		trader.getNotified(price);
	}
	
	private void notifyTraders() {
		for (SingleStockTrader trader: traders) {
			notifyOneTrader(trader);
		}
	}
	
	public SingleStockExchange(Stock stock, AssetConfig config) {
		this.stock = stock;
		this.config = config;
		this.traders = new HashSet<SingleStockTrader>();
	}
	
	public void registerTrader(SingleStockTrader trader) {
		traders.add(trader);
		notifyOneTrader(trader);
	}
	
	public int getMaxHolding() {
		return config.getMaxholding();
	}
	
	public Set<SingleStockTrader> getTraders() {
		return new HashSet<SingleStockTrader>(traders);
	}

	public double execute(int quantity) {
		double numLots = Math.abs(quantity) * 1.0 / config.getLotsize();
		double spreadCost = numLots * config.getTick();
		double impactCost = Math.pow(numLots, 2) * config.getTick();
		return spreadCost + impactCost;
	}

	public void resetEpisode() {
		for (SingleStockTrader trader: traders) {
			trader.resetEpisode();
		}
	}

	public void simulate() {
		stock.simulate();
		notifyTraders();
	}
}
