package com.haozan.caipiao.matchrecord;

import java.util.ArrayList;

import com.haozan.caipiao.lotteryrecord.SSQRecord;
import com.haozan.caipiao.types.Ball;

public class DLTRecordMatch {

	public static ArrayList<Ball> isMatch(String betStr, String openStr) {
		SSQRecord bet = new SSQRecord();
		SSQRecord open = new SSQRecord();

		bet.setRecord(betStr, false);
		open.setRecord(openStr, true);

		ArrayList<Ball> betData = bet.getRecord();
		ArrayList<Ball> openData = open.getRecord();

		// 匹配红球
		for (int i = 0; i < open.getDivide(); i++) {
			for (int j = 0; j < bet.getDivide(); j++) {
				if (betData.get(j).getNumber().equals(
						openData.get(i).getNumber())) {
					betData.get(j).setState(true);
					break;
				}
			}

		}

		// 匹配篮球
		for (int i = open.getDivide(); i < openData.size(); i++) {
			for (int j = bet.getDivide(); j < betData.size(); j++) {

				if (betData.get(j).getNumber().equals(
						openData.get(i).getNumber())) {
					betData.get(j).setState(true);
					break;
				}
			}
		}
		return betData;
	}
}
