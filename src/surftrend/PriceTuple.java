package surftrend;

public class PriceTuple {
	
	private int noOfStocks;
	private double amountInvested;
	
	public PriceTuple(int noOfStocks, double amountInvested) {
		
		this.noOfStocks= noOfStocks;
		this.amountInvested = amountInvested;
	}
	
	public int getNoOfStocks() {
		return noOfStocks;
	}
	public void setNoOfStocks(int noOfStocks) {
		this.noOfStocks = noOfStocks;
	}
	public double getAmountInvested() {
		return amountInvested;
	}
	public void setAmountInvested(double amountInvested) {
		this.amountInvested = amountInvested;
	}
	

}
