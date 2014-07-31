package com.haozan.caipiao.lotteryrecord;

import java.util.ArrayList;

import com.haozan.caipiao.types.Ball;

public class DFLJYRecord {
    private int divide=0; // 基本号码和生肖号码的分界
    private ArrayList<Ball>  record=new ArrayList<Ball>();
     
    public ArrayList<Ball> getRecord() {
        return record;
    }
    public void setRecord( String myBalls, Boolean open) {
        String[] rbballs=null;
        String[] baseBalls =null;
        String[] shengxiaoBalls=null;
        
        
     if(open){ 
       rbballs = myBalls.split("\\|");
       baseBalls = rbballs[0].split(",");
       shengxiaoBalls= rbballs[1].split(",");
     }
     
     else{
         rbballs = myBalls.split("\\:");
         String[] record=rbballs[0].split("\\|");
         baseBalls = record[0].split(",");
        shengxiaoBalls = record[1].split(",");
         
     }
      
        
   divide=baseBalls.length;      
        
 //初始化基本球       
       
        for(int i=0;i<baseBalls.length;i++ ){
            for(int j=0;j<baseBalls[i].length();j++){
                    Ball b=new Ball();
                    b.setNumber(String.valueOf( (baseBalls[i].charAt(j))));
                    b.setColor("red");
                    b.setState(false);
                    b.setGroupIndex(i);
                    record.add(b);
                
                }
            }
     
        
 //初始化生肖球      
        for( int i= 0;i<shengxiaoBalls.length;i++){
            
            Ball b=new Ball();
            if((int)(shengxiaoBalls[i].charAt(0))==48)
            {
                shengxiaoBalls[i]=String.valueOf(shengxiaoBalls[i].charAt(1));
            }
            b.setNumber(shengxiaoBalls[i]);
            b.setColor("shengxiao");
            b.setState(false);
            b.setGroupIndex(divide);
            record.add(b);
            
            
 
        }
        
            
           
    }
    public int getDivide() {
        return divide;
    }
    
    
}
