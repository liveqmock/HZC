package com.haozan.caipiao.lotteryrecord;

import java.util.ArrayList;

import com.haozan.caipiao.types.Ball;

public class SSLRecord {
    private ArrayList<Ball> record=new ArrayList<Ball>(); 
    private String kind1=null;
  
    public ArrayList<Ball> getRecord() {
        return  record;
    }
    public void setRecord( String myBalls, Boolean open) {
        String[] rbballs=null;
        String[] ballsNum=null;
         
 //设置开奖记录    
       if(open){ 
              ballsNum= myBalls.split("\\,");
  
            for(int i=0;i<ballsNum.length;i++)
           {
                 Ball b=new Ball();
                 b.setNumber(ballsNum[i]);             
                 b.setColor("red");
                 b.setState(false);
                b.setGroupIndex(i); 
                record.add(b);
            }
      
      
     }
     
       
 //设置投注记录
     else{
         rbballs = myBalls.split("\\:");
         ballsNum=rbballs[0].split("\\,");  
         kind1=rbballs[1];
   
         for(int i=0;i<ballsNum.length;i++ ){
             for(int j=0;j<ballsNum[i].length();j++){
                     Ball b=new Ball();              
                      
                     b.setNumber(String.valueOf( (ballsNum[i].charAt(j))));                   
                     b.setColor("red");
                     b.setState(false);
                     b.setGroupIndex(i);
                     record.add(b);
                 
                 }
             }
 
         
         
 
           
         
     }        
 
 
  }
   
    public String getKind1() {
        return kind1;
    }
   

}
