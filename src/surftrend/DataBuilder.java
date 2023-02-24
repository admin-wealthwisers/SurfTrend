package surftrend;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DataBuilder {
	


	public static List<Date> getAllDates(List<NormalCandle> NormalCandles) {
        
        List<Date> allDates = new ArrayList<Date>();  
        for (int i = 0; i < NormalCandles.size(); i+=SurfTrendStrategy.numberOfStocks) {
            
        	if(i>NormalCandles.size()) break;
        	Date d1 = NormalCandles.get(i).getUtilDate();
			allDates.add(d1);
			//System.out.println(d1.toString());
			
        }
        return allDates;
	}
	
	public static List<NormalCandle> buildNormalCandles(List<String> inputData) {
        
        List<NormalCandle> NormalCandles = new ArrayList<NormalCandle>();
        for (int i = 0; i < inputData.size(); i++) {
            String[] values = inputData.get(i).split(",");
            double open = (Double.parseDouble(values[1]));
            double high = (Double.parseDouble(values[2]));
            double low = (Double.parseDouble(values[3]));
            double close = (Double.parseDouble(values[4]));
            NormalCandles.add(new NormalCandle(values[7],open,high,low,close,values[0]));
        }
        return sortNormalCandlesByDate(NormalCandles);
	}

	
	public static List<HeikenAshiCandle> buildHeikenAshiCandles(List<String> inputData) {
		
       // List<String> heikin_ashi_candles = new ArrayList<>();
        double prev_ha_open=0.0;
        double prev_ha_close=0.0;
        String nextSymbol = "";
        boolean newSymbol = true;
        
        List<HeikenAshiCandle> HeikenAshiCandles = new ArrayList<HeikenAshiCandle>();
        for (int i = 0; i < inputData.size(); i++) {
        	
            String[] values = inputData.get(i).split(",");
            //System.out.println(csv_values[0]+"\n");

            if(newSymbol) {
            	prev_ha_open = Double.parseDouble(values[1]);
            	prev_ha_close = Double.parseDouble(values[4]);
            	newSymbol = false;
            }
            double ha_open = (prev_ha_open + prev_ha_close) / 2; // Calculate Heikin-Ashi Open
            double ha_close = (Double.parseDouble(values[1]) + Double.parseDouble(values[2]) + Double.parseDouble(values[3]) + Double.parseDouble(values[4])) / 4; // Calculate Heikin-Ashi Close
            double ha_low = Math.min(Double.parseDouble(values[3]), Math.min(ha_open, ha_close)); // Calculate Heikin-Ashi Low
            double ha_high = Math.max(Double.parseDouble(values[2]), Math.max(ha_open, ha_close)); // Calculate Heikin-Ashi High
            HeikenAshiCandles.add(new HeikenAshiCandle(values[7],ha_open,ha_high,ha_low,ha_close, values[0]));
            
            if(i < inputData.size()-1) nextSymbol = inputData.get(i+1).split(",")[7];

            if(values[7].equals(nextSymbol)) {
            	prev_ha_open = ha_open;
            	prev_ha_close = ha_close;
            } else newSymbol = true;
            
           // String ha_csv_line = inputData.get(i) + "," + ha_open + "," + ha_high + "," + ha_low + "," + ha_close; // Combine original and Heikin-Ashi data
           // heikin_ashi_candles.add(ha_csv_line);
        }
        return sortHeikenAshiCandlesByDate(HeikenAshiCandles);
	}

    public static List<HeikenAshiCandle> sortHeikenAshiCandlesByDate(List<HeikenAshiCandle> list) {
 
        Comparator<HeikenAshiCandle> comparator = new Comparator<HeikenAshiCandle>() {
            public int compare(HeikenAshiCandle o1, HeikenAshiCandle o2) {
                Date date1 = o1.getUtilDate();
                Date date2 = o2.getUtilDate();
                return date1.compareTo(date2);
            }
        };
        return list.stream().sorted(comparator).collect(Collectors.toList());
    }

    
    public static List<NormalCandle> sortNormalCandlesByDate(List<NormalCandle> list) {
    	 
        Comparator<NormalCandle> comparator = new Comparator<NormalCandle>() {
            public int compare(NormalCandle o1, NormalCandle o2) {
                Date date1 = o1.getUtilDate();
                Date date2 = o2.getUtilDate();
                return date1.compareTo(date2);
            }
        };
        return list.stream().sorted(comparator).collect(Collectors.toList());
    }
    
    


}
