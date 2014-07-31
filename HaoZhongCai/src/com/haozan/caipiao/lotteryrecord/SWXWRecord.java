package com.haozan.caipiao.lotteryrecord;

import java.util.ArrayList;

import com.haozan.caipiao.types.Ball;

public class SWXWRecord {
    
    private ArrayList<Ball>  record=new ArrayList<Ball>();
     
    public ArrayList<Ball> getRecord() {
        return record;
    }
    public void setRecord( String myBalls, Boolean open) {
        String[] rbballs=null;
        String[] ballsNum=null;       
        if(open){ 
            ballsNum=myBalls.split("\\,"); 
        }
     
        else{
            rbballs = myBalls.split("\\:");
            ballsNum=rbballs[0].split("\\,");
        }
        for(int i=0;i<ballsNum.length;i++){
            Ball b=new Ball();
            if(ballsNum[i].length()==1)
            {
              ballsNum[i]="0"+ ballsNum[i]; 
            }
            b.setNumber(ballsNum[i]);
            b.setColor("red");
            b.setState(false);
            record.add(b);
      }           
 
    }  
     
}
