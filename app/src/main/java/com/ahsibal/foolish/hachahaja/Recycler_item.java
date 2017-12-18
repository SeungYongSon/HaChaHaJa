package com.ahsibal.foolish.hachahaja;

/**
 * Created by 10210손승용 on 2017-10-13.
 */

public class Recycler_item {
    int BusStopNum;
    int image;
    String BusStopTitle;
    String BusStopLocation;

    int getNum(){
        return this.BusStopNum;
    }

    int getImage(){ return this.image; }

    String getTitle(){
        return this.BusStopTitle;
    }

    String getContent(){
        return  this.BusStopLocation;
    }

    Recycler_item(String title, String contetnt, int num, int image){
        this.BusStopNum=num;
        this.BusStopTitle=title;
        this.BusStopLocation=contetnt;
        this.image=image;
    }
}
