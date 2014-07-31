package com.haozan.caipiao.types;

public class ShengFuBetHistoryDetailItemData {

	private String game_id;
	
	private String term;
	
	private String match_id;
	
 	private String match_name;
	
	private String home_team;
	
	private String guest_team;
	
	private String sellout_time;
	
	private String remark;
	
	//赔率
	private String sp1;
	
	String sp2;
	
	String result;
	
    public String getGame_id() {
		return game_id;
	}

	public void setGame_id(String game_id) {
		this.game_id = game_id;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getMatch_id() {
		return match_id;
	}

	public void setMatch_id(String match_id) {
		this.match_id = match_id;
	}

	public String getMatch_name() {
		return match_name;
	}

	public void setMatch_name(String match_name) {
		this.match_name = match_name;
	}

	public String getHome_team() {
		return home_team;
	}

	public void setHome_team(String home_team) {
		this.home_team = home_team;
	}

	public String getGuest_team() {
		return guest_team;
	}

	public void setGuest_team(String guest_team) {
		this.guest_team = guest_team;
	}

	public String getSellout_time() {
		return sellout_time;
	}

	public void setSellout_time(String sellout_time) {
		this.sellout_time = sellout_time;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getSp1() {
		return sp1;
	}

	public void setSp1(String sp1) {
		this.sp1 = sp1;
	}

	public String getSp2() {
		return sp2;
	}

	public void setSp2(String sp2) {
		this.sp2 = sp2;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getGuset() {
		return guset;
	}

	public void setGuset(String guset) {
		this.guset = guset;
	}

	public String[] getSetScore() {
		return setScore;
	}

	public void setSetScore(String[] setScore) {
		this.setScore = setScore;
	}

	public String[] getBetResultGG() {
		return betResultGG;
	}

	public void setBetResultGG(String[] betResultGG) {
		this.betResultGG = betResultGG;
	}

	
	
	private String betTerm = "";
    private String master= "";
    private String guset= "";
    private String gameResult= "";
    private int concedePoints=-1;
    private String betResultWin= "";
    private String betResultEqual= "";
    private String betResultLost= "";
    private String halfMatchScore="";
    private String fullMatchScore="";
    private int splitNum;
    private String betGoal;
    private String[] letScore;
    private String[] setScore;
    private String[] betResultGG;

    public void setBetTerm(String betTerm) {
        this.betTerm = betTerm;
    }

    public String getBetTerm() {
        return betTerm;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public String getMaster() {
        return master;
    }

    public void setGuest(String guset) {
        this.guset = guset;
    }

    public String getGuest() {
        return guset;
    }

    public void setGameResult(String gameResult) {
        this.gameResult = gameResult;
    }

    public String getGameResult() {
        return gameResult;
    }

    public void setConcedePoints(int concedePoints) {
        this.concedePoints = concedePoints;
    }

    public int getConcedePoints() {
        return concedePoints;
    }

    public void setBetResultWin(String betResultWin) {
        this.betResultWin = betResultWin;
    }

    public String getBetResultWin() {
        return betResultWin;
    }

    public void setBetResultEqual(String betResultEqual) {
        this.betResultEqual = betResultEqual;
    }

    public String getBetResultEqual() {
        return betResultEqual;
    }

    public void setBetResultLost(String betResultLost) {
        this.betResultLost = betResultLost;
    }

    public String getBetResultLost() {
        return betResultLost;
    }

    public void setHalfMatchScore(String halfMatchScore) {
        this.halfMatchScore = halfMatchScore;
    }

    public String getHalfMatchScore() {
        return halfMatchScore;
    }

    public void setFullMatchScore(String fullMatchScore) {
        this.fullMatchScore = fullMatchScore;
    }

    public String getFullMatchScore() {
        return fullMatchScore;
    }

    public void setSplitNum(int splitNum) {
        this.splitNum = splitNum;
    }

    public int getSplitNum() {
        return splitNum;
    }
    
    public void setBetGoal(String betGoal) {
        this.betGoal = betGoal;
    }

    public String getBetGoal() {
        return betGoal;
    }
    public void setLetScore(String[] letScore) {
        this.letScore = letScore;
    }

    public String[] getLetScore() {
        return letScore;
    }
    public void setScore(String[] setScore) {
        this.setScore = setScore;
    }

    public String[] getScore() {
        return setScore;
    }
    
    public void setResultGG(String[] betResultGG) {
        this.betResultGG = betResultGG;
    }

    public String[] getResultGG() {
        return betResultGG;
    }
}
