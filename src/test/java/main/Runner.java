package main;

import java.util.Arrays;

public class Runner {
    
    public static void main(String[] args) {
        final String[] dayTime = "9-11:30 am Wednesdays".split("[ ]");

        final String day;
        final String time;
        if (dayTime.length == 2) {
            day = dayTime[0];
            time = dayTime[1].split("[-]")[0];
        } else if (dayTime.length == 1){
            // Summer semester?
            day = "";
            time = dayTime[0].split("[-]")[0];
        } else {
            day = time = "";
        }

        System.out.println(Arrays.toString(dayTime));
        System.out.println(day);
        System.out.println(time);
    }

}
