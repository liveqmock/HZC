package com.haozan.caipiao.types;

public class LotteryAwardRank {
    private String phone_num;
    private String lottery_prize;
    private int userId;
    private String lotteryAwardTime;

    public void setPhone(String phone_num) {
        this.phone_num = phone_num;
    }

    public String getPhone() {
        return phone_num;
    }

    public void setLotteryPrize(String lottery_prize) {
        this.lottery_prize = lottery_prize;
    }

    public String getLotteryPrize() {
        return lottery_prize;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public void setAwardTime(String lotteryAwardTime) {
        this.lotteryAwardTime = lotteryAwardTime;
    }

    public String getAwardTime() {
        return lotteryAwardTime;
    }
}
