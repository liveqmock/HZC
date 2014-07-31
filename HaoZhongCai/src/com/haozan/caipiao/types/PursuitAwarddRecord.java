package com.haozan.caipiao.types;

public class PursuitAwarddRecord {
	private String lot_id;
	private String term;
	private double money;
	private String codes;
	private String time;
	private int pursuit_id;
	private String Order_id;
	private String prize;

	public void setLottId(String lot_id) {
		this.lot_id = lot_id;
	}

	public String getLottId() {
		return lot_id;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getTerm() {
		return term;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public double getMoney() {
		return money;
	}

	public void setCodes(String codes) {
		this.codes = codes;
	}

	public String getCodes() {
		return codes;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTime() {
		return time;
	}

	public void setPursuitId(int pursuit_id) {
		this.pursuit_id = pursuit_id;
	}

	public int getPursuitId() {
		return pursuit_id;
	}

	public void setOderId(String Order_id) {
		this.Order_id = Order_id;
	}

	public String getOderId() {
		return Order_id;
	}

	public void setPrize(String prize) {
		this.prize = prize;
	}

	public String getPrize() {
		return prize;
	}
}
