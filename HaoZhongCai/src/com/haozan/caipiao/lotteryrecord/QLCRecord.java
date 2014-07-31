package com.haozan.caipiao.lotteryrecord;

import java.util.ArrayList;

import com.haozan.caipiao.types.Ball;

public class QLCRecord {
   
    private ArrayList<Ball> record=new ArrayList<Ball>();     
    public ArrayList<Ball> getRecord() {
        return  record;
    }
    public void setRecord( String myBalls, Boolean open) {
        String[] rbballs=null;
        String[] ballsNum= null;
         
 //设置开奖记录    
       if(open){ 
               rbballs = myBalls.split("\\|");
               ballsNum = rbballs[0].split(",");
              // ballsNum[ballsNum.length] = rbballs[1];
      
  
        for(int i=0;i<ballsNum.length;i++)
        {
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
        Ball b=new Ball();
        if(rbballs[1].length()==1)
        {
            rbballs[1]="0"+ rbballs[1]; 
        }
        b.setNumber(rbballs[1]);           
        b.setColor("blue");
        b.setState(false);
        record.add(b);
      
      
      
     }
     
       
 //设置投注记录
     else{
         rbballs = myBalls.split("\\:");
         ballsNum=rbballs[0].split("\\,");
         
         
         for(int i=0;i<ballsNum.length;i++)
         {
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
        
}
    
 


