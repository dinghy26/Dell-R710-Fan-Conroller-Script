/**
 * Program: DellFanController
 * Author: Brian Miranda Perez
 * Date: 05/AUG/2020
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.sql.Timestamp;    
import java.util.Date;    
import java.text.SimpleDateFormat;  


public class DellFanController {
    
    /**
     * Program variables that setup the main environmental
     * variables to set the IPMI commands 
     * to set the dellR710 servers fans speeds. this program can be use
     * with only Dell R710.
     */

    public static int serverTemp;
    public static int fan3RPM;
    public final static int MAX_TEMP = 32;
    public final static int MIN_TEMP = 24;
    public final static String REF_FILE = "dellR710.txt";
    public final static String SERVER_LOGIN = "ipmitool -I lanplus -H SERVER_IP -U SERVER_USERNAME -P SERVER_PASSWORD ";
    public final static String SERVER_GET_DATA = "sdr entity 7";
    public final static String MANUAL_MODE = "raw 0x30 0x30 0x01 0x00";
    public final static String AUTO_MODE = "raw 0x30 0x30 0x01 0x01";
    public final static String FAN_SPEED_3000 = "raw 0x30 0x30 0x02 0xff 0x10";
    public final static String FAN_SPEED_2280 = "raw 0x30 0x30 0x02 0xff 0x0a";
    public final static String FAN_SPEED_2160 = "raw 0x30 0x30 0x02 0xff 0x09";
    
    

    /**
     * @see 
     * this Creates the file to store the outputs.
     */
    public static void createFile() {

        try {
            File myObj = new File(REF_FILE);

            if (myObj.createNewFile()) {
              System.out.println("File created: " + myObj.getName());
             } else {
              System.out.println("File already exists.");
            }
          } catch (IOException file) {
            System.out.println("An error occurred creating the file.");
            file.printStackTrace();

          }
    }

    /**
     * 
     * @param message use for write a message to the file created.
     * @see this method is use to add data to the file created. 
     */
    public static void WriteToFile(String message) {

        try {
            FileWriter myWriter = new FileWriter(REF_FILE, true);
            myWriter.write(message + "\n");
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
          } catch (IOException e) {
            System.out.println("An error occurred writing to file.");
            e.printStackTrace();
          }

    }

    /**
     * 
     * @return the timestamp formatted to y-m-d H:M:S
     * @see this method create a timestamp use for the data in the file.
     */
    public static String timeStamp() {
        Date date = new Date();  
        Timestamp ts=new Timestamp(date.getTime());  
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(ts);   
    }


    public static void main(String[] args) {

        // Local Variables for store the data retrieved from the IMPI sensor.
        Process rt;
        ArrayList<String> ipmiOutputList = new ArrayList<>();
        String [] value;
        String [] fanRPM;

        // Create the Storage File
        createFile();
        
        // Try block to get the data From the IPMI sensor
        try {
            
            rt = Runtime.getRuntime().exec(SERVER_LOGIN + SERVER_GET_DATA);

            String s;
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(rt.getInputStream()));

            while ((s = stdInput.readLine()) != null) {
                
                ipmiOutputList.add(s);    
            }

            value = ipmiOutputList.get(0).split(" ");
            serverTemp = Integer.parseInt(value[15]);

            fanRPM = ipmiOutputList.get(23).split(" ");
            fan3RPM = Integer.parseInt(fanRPM[19]);


        } catch (IOException e) {
            e.printStackTrace();
        }
        

        /**
         * Try block use to set the speed of the fans. If the server temperature
         * Exceed the designated Max temp it set the server to auto mode (IDRAC take control of the fans).
         * also if the server goes below the min Temp this block sets the server to manual mode
         * and set the fan speed to 2160 RPM's.
         */

        try {

        if (serverTemp > MAX_TEMP ){

            rt = Runtime.getRuntime().exec(SERVER_LOGIN + AUTO_MODE);
            WriteToFile(timeStamp() + " Server is above the max temp: " + serverTemp + "\u2103");
    
        } else if (serverTemp <= MIN_TEMP && fan3RPM < 2280) {

            WriteToFile(timeStamp() + " Server temp is normal: " + serverTemp + "\u2103" + " RPM = " + fan3RPM);
            System.exit(1);

        } else if (serverTemp < MIN_TEMP){

            rt = Runtime.getRuntime().exec(SERVER_LOGIN + MANUAL_MODE);
            rt = Runtime.getRuntime().exec(SERVER_LOGIN + FAN_SPEED_2160);
            WriteToFile(timeStamp() + "Server is bellow " + serverTemp + ". Activating Manual Mode");

        } else {
            WriteToFile(timeStamp() + " Server temp is normal: " + serverTemp + "\u2103");
        }
            
        } catch (IOException check) {
            check.printStackTrace();
        }


    }
    
}
