package surftrend;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Utilities {

	
	private static String symbols = "";
	private static String start_date = "";
	private static String end_date = "";
	public static String[] symbolsStrings;
	
	public static List<String> loadData() {
		
        String output = "";     
        String niftyOutput = "";
        List<String> allData = new ArrayList<String>(); 

        loadProperties();

        symbolsStrings = symbols.split(",");
        SurfTrendStrategy.numberOfStocks = symbolsStrings.length;
        String niftyurl = "https://query1.finance.yahoo.com/v7/finance/download/NIFTY_NS"+"?period1="+dateToUnixTimestamp(start_date)+"&period2="+dateToUnixTimestamp(end_date)+"&interval=1d&events=history&includeAdjustedClose=true";

        for(int i=0;i<symbolsStrings.length;i++) {
            String url = "https://query1.finance.yahoo.com/v7/finance/download/"+symbolsStrings[i]+"?period1="+dateToUnixTimestamp(start_date)+"&period2="+dateToUnixTimestamp(end_date)+"&interval=1d&events=history&includeAdjustedClose=true";
            try {
            	URL url1 = new URL(url);
                // System.out.print(url);
            	HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
            	conn.setRequestMethod("GET");

            	if (conn.getResponseCode() != 200) {
            		throw new RuntimeException("Failed : HTTP error code : "+ conn.getResponseCode());
            	}
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                while ((output = br.readLine()) != null) {
                    //System.out.println(output);
                	if(!output.startsWith("Date")) {
                		allData.add(output+","+symbolsStrings[i]);
                	}
                }
                conn.disconnect();

            /*	URL niftyurl1 = new URL(niftyurl);
            	HttpURLConnection conn1 = (HttpURLConnection) niftyurl1.openConnection();
            	conn1.setRequestMethod("GET");

            	if (conn1.getResponseCode() != 200) {
            		throw new RuntimeException("Failed : HTTP error code : "+ conn1.getResponseCode());
            	}
                BufferedReader br1 = new BufferedReader(new InputStreamReader(conn1.getInputStream()));

                while ((niftyOutput = br1.readLine()) != null) {
                	if(!niftyOutput.startsWith("Date")) {
                		allData.add(output+","+symbolsStrings[i]);
                	}
                }
                conn1.disconnect();*/
            
            } catch(Exception e) {
            	e.printStackTrace();            
            }
        }
		return allData;
	}
	
	public static List<String> loadNiftyData() {
		
        String output = "";     
        List<String> allData = new ArrayList<String>(); 

        loadProperties();

        String niftyurl = "https://query1.finance.yahoo.com/v7/finance/download/NIFTY.NS"+"?period1="+dateToUnixTimestamp(start_date)+"&period2="+dateToUnixTimestamp(end_date)+"&interval=1d&events=history&includeAdjustedClose=true";

            try {
            	URL url1 = new URL(niftyurl);
            	HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
            	conn.setRequestMethod("GET");

            	if (conn.getResponseCode() != 200) {
            		throw new RuntimeException("Failed : HTTP error code : "+ conn.getResponseCode());
            	}
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                while ((output = br.readLine()) != null) {
                    //System.out.println(output);
                	if(!output.startsWith("Date")) {
                		allData.add(output+",NIFTY");
                	}
                }
                conn.disconnect();
                
            } catch (Exception e) {
            	e.printStackTrace(); 
            }
    		return allData;
	}

	
	
    private static long dateToUnixTimestamp(String date) {
        return java.time.LocalDate.parse(date).atStartOfDay(java.time.ZoneId.systemDefault()).toEpochSecond();
    }
    
    
    
    private static void loadProperties() {
    	 Properties prop = new Properties();
         FileInputStream input = null;
         try {
             input = new FileInputStream("./config.txt");
             // load a properties file
             prop.load(input);

             Utilities.symbols = prop.getProperty("SYMBOLS");
             Utilities.start_date = prop.getProperty("START_DATE");
             Utilities.end_date = prop.getProperty("END_DATE");
             SurfTrendStrategy.totalCapital = Integer.parseInt(prop.getProperty("TOTAL_CAPITAL"));
             SurfTrendStrategy.deploymentRatio = Integer.parseInt(prop.getProperty("DEPLOYMENT_RATIO"));
             SurfTrendStrategy.positionSizePercentage = Double.parseDouble(prop.getProperty("POSITION_SIZE_PERCENTAGE"));


         } catch(Exception e) {
        	 e.printStackTrace();
        	 System.exit(0);
         }
    }

}
