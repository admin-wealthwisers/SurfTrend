package surftrend;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;



public class SurfTrendStrategy {

	public static double totalCapital = 0.0;
	public static double freeCapital = 0.0;

	public static double positionSizePercentage = 0.0;
	public static int deploymentRatio = 0;

	public static int numberOfStocks = 0;
	private static HashMap<String,Double> pnls = new HashMap<String, Double>();
	private static HashMap<String,Trade> currentTrades = new HashMap<String, Trade>();
	private static HashMap<String,HeikenAshiCandle> comparativeCandle = new HashMap<String, HeikenAshiCandle>();
	private static StringBuffer logMsgs = new StringBuffer("Logging started\n");
	
	private static HashMap<String,PriceTuple> capitalDeployedByStock = new HashMap<String, PriceTuple>();


	
	public static void main(String[] args) {

				
		List<String> allData = Utilities.loadData();
		List<String> allNiftyData = Utilities.loadNiftyData();
		
		logMsgs.append("Data loading completed successfully\n");
		
		List<HeikenAshiCandle> HeikenAshiCandles = DataBuilder.buildHeikenAshiCandles(allData);

		List<NormalCandle> NormalCandles = DataBuilder.buildNormalCandles(allData);
		List<NormalCandle> NormalNiftyCandles = DataBuilder.buildNormalCandles(allNiftyData);


		List<Date> allDates = DataBuilder.getAllDates(NormalCandles);

		List<HeikenAshiCandle> HeikenAshiCandlesByDate = new ArrayList<HeikenAshiCandle>();
		List<HeikenAshiCandle> PrevHeikenAshiCandlesByDate = new ArrayList<HeikenAshiCandle>();
		List<NormalCandle> NormalCandlesByDate = new ArrayList<NormalCandle>();
		
	/*	
		for (int k=0; k < Utilities.symbolsStrings.length; k++) {
			List<NormalCandle> NormalCandlesBySymbol = NormalCandles.stream().filter(s -> s.getSymbol().compareTo(Utilities.symbolsStrings[k]) == 0).collect(Collectors.toList());
		}
			*/	

		
		
		int prev = 0;
		freeCapital = (totalCapital*deploymentRatio)/100;
		double maxAllocationPerStock = (freeCapital*positionSizePercentage)/100;
		

		for (int i = 0; i < allDates.size(); i++) {
						
			if (i != 0)
				prev = i - 1;

			Date d = allDates.get(i);
			Date pd = allDates.get(prev);

			HeikenAshiCandlesByDate = HeikenAshiCandles.stream().filter(p -> p.getUtilDate().compareTo(d) == 0)
					.collect(Collectors.toList());

			PrevHeikenAshiCandlesByDate = HeikenAshiCandles.stream().filter(p -> p.getUtilDate().compareTo(pd) == 0)
					.collect(Collectors.toList());

			NormalCandlesByDate = NormalCandles.stream().filter(p -> p.getUtilDate().compareTo(d) == 0)
					.collect(Collectors.toList());

			for (int j = 0; j < HeikenAshiCandlesByDate.size(); j++) {

				HeikenAshiCandle hc_current = HeikenAshiCandlesByDate.get(j);
				HeikenAshiCandle hc_prev = PrevHeikenAshiCandlesByDate.get(j);
				NormalCandle nc = NormalCandlesByDate.get(j);

				boolean conditions = false;

				HeikenAshiCandle compCandle = comparativeCandle.get(nc.getSymbol());
				
				Trade trade = currentTrades.get(nc.getSymbol());

				if (hc_current.getOpen() == hc_current.getLow() && hc_current.isGreenCandle()
						&& hc_current.getHigh() > hc_prev.getHigh()) {
					
					
					if (compCandle != null && hc_current.getClose() > compCandle.getClose() && compCandle.isGreenCandle()) conditions = true;
					if (compCandle != null && hc_current.getClose() > compCandle.getOpen() && !compCandle.isGreenCandle()) conditions = true;
					if (compCandle == null) conditions = true; 

					if (conditions && (trade == null || !trade.isInTrade())) {

						if(compCandle == null || hc_current.getClose() > compCandle.getHigh()) {
													
							
							if(freeCapital > maxAllocationPerStock) {
								int quantity = (int)(maxAllocationPerStock/nc.getClose());
								currentTrades.put(hc_current.getSymbol(),new Trade(hc_current.getDate(), nc.getClose(), hc_current.getOpen(), quantity, true));
								double invInTrade = quantity*nc.getClose();
								freeCapital -= invInTrade;
								//maxAllocationPerStock = (freeCapital*positionSizePercentage)/100;
								capitalDeployedByStock.put(nc.getSymbol(), new PriceTuple(quantity, invInTrade));
								String msg = "Trade initiated :: " + nc.getSymbol() + " , Date : " + nc.getDate()
								+ " , Entry : Rs. " + roundoff(nc.getClose()) + " , StopLoss : Rs. " + roundoff(hc_current.getOpen()) + " , Quantity : " + quantity
								+ " , Free Capital : Rs." + roundoff(freeCapital)
								+ " , Position Size : Rs." + roundoff(maxAllocationPerStock);
								logMsgs.append(msg).append("\n");
								System.out.println(msg);
								comparativeCandle.put(nc.getSymbol(), hc_current);
								conditions = false;
							} else {
								
								String msg = "Could not take trade due to inadequate free capital :: "+nc.getSymbol()+" , Date : "+nc.getDate()+", Free Capital : "+roundoff(freeCapital);
								System.out.println(msg);
								logMsgs.append(msg).append("\n");
							}
				
							
						}
					} else if (conditions && trade != null && trade.isInTrade()) {
						
						if(compCandle == null || hc_current.getClose() > compCandle.getHigh()) {
							trade.setStopLoss(hc_current.getOpen());
							currentTrades.put(nc.getSymbol(), trade);
							String msg = "Stop Loss updated :: " + nc.getSymbol() + " , Date : " + nc.getDate() + " , Rs. " + roundoff(hc_current.getOpen());
							logMsgs.append(msg).append("\n");
							System.out.println(msg);
							comparativeCandle.put(nc.getSymbol(), hc_current);
							conditions = false;
						}
					}
				}
				else if (trade != null && trade.isInTrade() && hc_current.getClose() < trade.getStopLoss()) {
					trade.setInTrade(false);
					trade.setExitDate(hc_current.getDate());
					trade.setExitPrice(nc.getClose());
					trade.exitTrade();
					int quantity = capitalDeployedByStock.get(nc.getSymbol()).getNoOfStocks();
					double capitalFreed = nc.getClose() * quantity;
					double currentpnl = (nc.getClose() - trade.getEntryPrice())*quantity;
					freeCapital += capitalFreed;
					freeCapital += currentpnl;
					maxAllocationPerStock = (freeCapital*positionSizePercentage)/100;
					capitalDeployedByStock.remove(nc.getSymbol());
					String msg = "Trade exited :: " + nc.getSymbol()+ " , Date : " + nc.getDate() + " , Quantity : " + quantity + " , Exit Price : Rs. "+roundoff(nc.getClose())+"  , PnL : Rs."+ roundoff(currentpnl) +" , Free Capital : Rs."+roundoff(freeCapital);
					logMsgs.append(msg).append("\n");
					System.out.println(msg);
					comparativeCandle.put(nc.getSymbol(), hc_current);
					addToPnL(nc.getSymbol(), currentpnl);
				}
				
				
				if((compCandle != null && compCandle.isGreenCandle() && hc_current.getClose() < compCandle.getOpen() && hc_current.getOpen() == hc_current.getHigh()) ||
				   (compCandle != null && !compCandle.isGreenCandle() && hc_current.getClose() < compCandle.getClose() && hc_current.getOpen() == hc_current.getHigh() && hc_current.getLow() < compCandle.getLow()) 
					)
				{
					String msg = "Comparative candle updated :: "+nc.getSymbol()+" , Date : "+nc.getDate()+" , HC Close : Rs."+roundoff(hc_current.getClose());
					logMsgs.append(msg).append("\n");
					//System.out.println(msg);
					comparativeCandle.put(nc.getSymbol(), hc_current);
				}
			}
		}
		
		
		System.out.println("\nMark to Market PnLs by symbol : ");
		logMsgs.append("\nMark to Market PnLs by symbol : \n");
		double totalmtm = 0.0;
				
		for(String symbol : currentTrades.keySet()) {		
			Trade runningTrade = currentTrades.get(symbol);
			for(int i=0;i< NormalCandlesByDate.size();i++) {
				NormalCandle nc = NormalCandlesByDate.get(i);
				if(nc.getSymbol().equals(symbol)) {
					double mtmval = (nc.getClose() - runningTrade.getEntryPrice())*runningTrade.getQuantity();
					totalmtm += mtmval;
					logMsgs.append("\n").append(symbol).append(" : Rs.").append(roundoff(mtmval));
					System.out.println(symbol+" : Rs."+roundoff(mtmval));
				}
			}
		}
		logMsgs.append("\n\nTotal MTM : Rs.").append(roundoff(totalmtm));
		System.out.println("\nTotal MTM : Rs."+roundoff(totalmtm));
		
		printpnls();

		try {
			FileWriter csv_file = new FileWriter("./all_data.csv");
			FileWriter output_file = new FileWriter("./tradelog.txt");
			csv_file.write("Symbol,Date,HCOpen,HCHigh,HCLow,HCClose,NCOpen,NCHigh,NCLow,NCClose,\n");
			for (int i = 0; i < NormalCandles.size(); i++) {
				csv_file.write(HeikenAshiCandles.get(i).printCandle() + NormalCandles.get(i).printCandle() + "\n");
			}
			csv_file.close();
			output_file.write(logMsgs.toString());
			output_file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	
/*	private static double getRS(List<NormalCandle> baseSymbol, List<NormalCandle> compSymbol, Date startDate, int period) {
		
		
		for(int i=baseSymbol.size();i>)
		
		
		return 0.0;
		
	}
	*/
	
	
	private static double roundoff(double d) {
		
		//return d;
		return Math.round(d * 100.0) / 100.0 ;
		//if(d<0) return Math.round(d * 100.0) / 100.0  * -1;
		//return 0.0;

	}
	
	private static void addToPnL(String key, Double pnl) {
	
	// Check if the key is already in the map
	if (pnls.containsKey(key)) {
	  // If the key exists, add the new value to the existing value
	  Double existingValue = pnls.get(key);
	  Double newValue = existingValue + pnl;
	  pnls.put(key, newValue);
	} else {
	  // If the key doesn't exist, put the new value in the map
	  pnls.put(key, pnl);
	}
	
	}
	
	
	private static void printpnls() {
		
		String msg = "\n\n"+"Booked Pnls by symbol : \n";
		System.out.println(msg);
		logMsgs.append(msg).append("\n");
		double totalbookedpnl = 0.0;
		for(String symbol:pnls.keySet()) {
			
			msg = symbol +" : Rs."+roundoff(pnls.get(symbol));
			totalbookedpnl += pnls.get(symbol);
			logMsgs.append(msg).append("\n");
			System.out.println(msg);
		}
		System.out.println("\nTotal Booked PnL : Rs. " + roundoff(totalbookedpnl));
		logMsgs.append("\nTotal Booked PnL : Rs. " + roundoff(totalbookedpnl)).append("\n");		

		System.out.println("\nFree Capital : Rs. " + roundoff(freeCapital));
		logMsgs.append("\nFree Capital : Rs. " + roundoff(freeCapital)).append("\n");		
	}
	
}
