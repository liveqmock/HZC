package com.haozan.caipiao.matchrecord;

import java.util.ArrayList;

import com.haozan.caipiao.lotteryrecord.SWXWRecord;
import com.haozan.caipiao.types.Ball;

public class SWXWRecordMatch {
	public static ArrayList<Ball> isMatch(String betStr, String openStr) {
		SWXWRecord bet = new SWXWRecord();
		SWXWRecord open = new SWXWRecord();

		bet.setRecord(betStr, false);
		open.setRecord(openStr, true);

		ArrayList<Ball> betData = bet.getRecord();
		ArrayList<Ball> openData = open.getRecord();

		for (int i = 0; i < openData.size(); i++) {
			for (int j = 0; j < betData.size(); j++) {
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
