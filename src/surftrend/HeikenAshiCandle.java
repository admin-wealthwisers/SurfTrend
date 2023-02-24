package surftrend;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class HeikenAshiCandle {
	
	private String symbol;
	private double open;
	private double high;
	private double low;
	private double close;
	private Date date;
	
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public Date getUtilDate() {
		return this.date;
	}
	
		public String getDate() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDateTime localDateTime = LocalDateTime.ofInstant(this.date.toInstant(), ZoneId.systemDefault());
		return localDateTime.format(formatter);
	}
	public void setDate(String inputDate) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate localDate = LocalDate.parse(inputDate, formatter);
		this.date = Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}
	public double getOpen() {
		return open;
	}
	public void setOpen(double open) {
		this.open = open;
	}
	public double getHigh() {
		return high;
	}
	public void setHigh(double high) {
		this.high = high;
	}
	public double getLow() {
		return low;
	}
	public void setLow(double low) {
		this.low = low;
	}
	public double getClose() {
		return close;
	}
	public void setClose(double close) {
		this.close = close;
	}
	
	HeikenAshiCandle(String symbol, double open,double high,double low,double close, String date) {
		this.symbol=symbol;
		this.open=open;
		this.high=high;
		this.low=low;
		this.close=close;
		this.setDate(date);
	}
	
	public boolean isGreenCandle() {
		if(close>=open) return true;
		else return false;
	}
	
	public String printCandle() {
		return (symbol+","+getDate()+","+open+","+high+","+low+","+close+",");
	}

}
