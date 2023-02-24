package surftrend;

public class Trade {

	private double entryPrice;
	private double exitPrice;
	private double stopLoss;
	private double pnl;
	private String entryDate;
	private String exitDate;
	private boolean inTrade;
	private int quantity;

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public boolean isInTrade() {
		return inTrade;
	}

	public void setInTrade(boolean inTrade) {
		this.inTrade = inTrade;
	}

	public Trade(String entryDate, double entryPrice, double stopLoss, int quantity, boolean inTrade) {

		this.entryDate = entryDate;
		this.entryPrice = entryPrice;
		this.stopLoss = stopLoss;
		this.quantity = quantity;
		this.inTrade = inTrade;
	}
	
	public double getPnl() {
		return pnl;
	}

	public void setPnl(double pnl) {
		this.pnl = pnl;
	}

	public void exitTrade() {
		this.pnl = this.stopLoss-this.entryPrice;
	}

	public String printTrade() {
		return (entryDate + "," + exitDate + "," + entryPrice + "," + exitPrice + "," + pnl);
	}

	public double getEntryPrice() {
		return entryPrice;
	}

	public void setEntryPrice(double entryPrice) {
		this.entryPrice = entryPrice;
	}

	public double getExitPrice() {
		return exitPrice;
	}

	public void setExitPrice(double exitPrice) {
		this.exitPrice = exitPrice;
	}

	public double getStopLoss() {
		return stopLoss;
	}

	public void setStopLoss(double stopLoss) {
		this.stopLoss = stopLoss;
	}

	public String getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(String entryDate) {
		this.entryDate = entryDate;
	}

	public String getExitDate() {
		return exitDate;
	}

	public void setExitDate(String exitDate) {
		this.exitDate = exitDate;
	}
}
