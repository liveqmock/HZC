package com.haozan.caipiao.util;

import com.haozan.caipiao.types.BallPre;

public class CreateBallPre {
    public BallPre[] getBallPre(int lotteryKind) {
        BallPre[] ballPre;
        switch (lotteryKind) {
            case 1:
                ballPre = new BallPre[2];
                ballPre[0] = getOneBall(0, 0, 0, 1, 33);
                ballPre[1] = getOneBall(0, 0, 1, 1, 16);
                return ballPre;
            case 2:
                ballPre = new BallPre[3];
                ballPre[0] = getOneBall(0, 0, 0, 0, 10);
                ballPre[1] = getOneBall(0, 0, 0, 0, 10);
                ballPre[2] = getOneBall(0, 0, 0, 0, 10);
                return ballPre;
            case 3:
                ballPre = new BallPre[1];
                ballPre[0] = getOneBall(0, 0, 0, 0, 10);
                return ballPre;
            case 4:
                ballPre = new BallPre[1];
                ballPre[0] = getOneBall(0, 0, 0, 0, 10);
                return ballPre;
            case 5:
                ballPre = new BallPre[1];
                ballPre[0] = getOneBall(0, 0, 0, 1, 30);
                return ballPre;
            case 6:
                ballPre = new BallPre[7];
                ballPre[0] = getOneBall(0, 0, 0, 0, 10);
                ballPre[1] = getOneBall(0, 0, 0, 0, 10);
                ballPre[2] = getOneBall(0, 0, 0, 0, 10);
                ballPre[3] = getOneBall(0, 0, 0, 0, 10);
                ballPre[4] = getOneBall(0, 0, 0, 0, 10);
                ballPre[5] = getOneBall(0, 0, 0, 0, 10);
                ballPre[6] = getOneBall(1, 0, 1, 0, 12);

                return ballPre;
            case 7:
                ballPre = new BallPre[1];
                ballPre[0] = getOneBall(0, 0, 0, 1, 15);

                return ballPre;
            case 8:
                ballPre = new BallPre[3];
                ballPre[0] = getOneBall(0, 0, 0, 0, 10);
                ballPre[1] = getOneBall(0, 0, 0, 0, 10);
                ballPre[2] = getOneBall(0, 0, 0, 0, 10);
                return ballPre;
            case 9:
                ballPre = new BallPre[1];
                ballPre[0] = getOneBall(0, 0, 0, 1, 21);

                return ballPre;
            case 10:
                ballPre = new BallPre[1];
                ballPre[0] = getOneBall(0, 0, 0, 1, 24);

                return ballPre;
            case 11:
                ballPre = new BallPre[5];
                ballPre[0] = getOneBall(0, 0, 0, 1, 24);
                ballPre[1] = getOneBall(0, 0, 0, 1, 24);
                ballPre[2] = getOneBall(0, 0, 0, 1, 24);
                ballPre[3] = getOneBall(0, 0, 0, 1, 24);
                ballPre[4] = getOneBall(0, 0, 0, 1, 24);
                return ballPre;
            case 12:
                ballPre = new BallPre[2];
                ballPre[0] = getOneBall(0, 0, 0, 1, 35);
                ballPre[1] = getOneBall(0, 0, 1, 1, 12);
                return ballPre;
            default:
                return null;
        }
    }

    private BallPre getOneBall(int kind, int color, int initColor, int startNumber, int count) {
        // TODO Auto-generated method stub
        BallPre bp = new BallPre();
        bp.setKind(kind);
        bp.setColor(color);
        bp.setInitColor(initColor);
        bp.setStartNumber(startNumber);
        bp.setCount(count);
        return bp;
    }
}
