import java.text.SimpleDateFormat
import java.text.DateFormat;
import java.nio.channels.FileChannel
import java.io.*
import java.lang.*
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;

class init {
    int pauseXS = 200, pauseSmall = 500, pauseMedium = 1000, pauseLarge = 2000, pauseXL = 7000


    String buildName = "";
    File absFile = new File(buildName)
    String absPath = absFile.getAbsolutePath()
    String logPath = absPath + File.separator + "logs" + File.separator + "pos2.log"
    String posLogs = new File(logPath).text
    String propertiesPath = absPath + File.separator + "ScriptProperties" + File.separator + "properties.txt"
    String fileContents = new File(propertiesPath).text
    String[] tokenizeResult = fileContents.split("\n");
    String cashUsername , cashPassword , cashName, manUsername, manPassword , manName,techUsername, techPassword, techName,
    regularItem, scItem, pwdItem, spItem;
    //Logger logger = null;
    DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
    DateFormat tsDf = new SimpleDateFormat("MM-dd-yyyy");
    Date today = Calendar.getInstance().getTime();
    String reportDate = df.format(today);
    String timestamp = tsDf.format(today);
    String logFilePath = absPath + File.separator + "AutomatonLogs" + File.separator + reportDate + "_LOG_FILE"
    String checkerFilePath = absPath + File.separator + "AutomatonLogs" + File.separator + reportDate + "_CHECKER_LOG_FILE"

    public void createAutomatonDir()
    {
        String AutomatonLogs = absPath + File.separator + "AutomatonLogs"
        File automatonFile = new File(AutomatonLogs)
        if (!automatonFile.exists()) {
            automatonFile.mkdir()
        }


    File logFile = new File (logFilePath)
    if (!logFile.exists())
    {
        logFile.createNewFile()
    }

    File checkerFile = new File (checkerFilePath)
    if (!checkerFile.exists())
    {
        checkerFile.createNewFile()
    }

    }

   public void Initialize() {

        for (int a = 0; a < tokenizeResult.length; a++) {
            if (tokenizeResult[a].contains("MANAGER_CASHIER_USERNAME="))
                manUsername = tokenizeResult[a].replace("MANAGER_CASHIER_USERNAME=", "")
            else if (tokenizeResult[a].contains("MANAGER_CASHIER_PASSWORD="))
                manPassword = tokenizeResult[a].replace("MANAGER_CASHIER_PASSWORD=", "")
            else if (tokenizeResult[a].contains("MANAGER_CASHIER_NAME="))
                manName = tokenizeResult[a].replace("MANAGER_CASHIER_NAME=", "")
            else if (tokenizeResult[a].contains("CASHIER2_USERNAME="))
                cashUsername = tokenizeResult[a].replace("CASHIER2_USERNAME=", "")
            else if (tokenizeResult[a].contains("CASHIER2_PASSWORD="))
                cashPassword = tokenizeResult[a].replace("CASHIER2_PASSWORD=", "")
            else if (tokenizeResult[a].contains("CASHIER2_NAME="))
                cashName = tokenizeResult[a].replace("CASHIER2_NAME=", "")
            else if(tokenizeResult[a].contains("POSMAINTENANCE_USERNAME"))
                techUsername = tokenizeResult[a].replace("POSMAINTENANCE_USERNAME=", "")
            else if(tokenizeResult[a].contains("POSMAINTENANCE_PASSWORD"))
                techPassword = tokenizeResult[a].replace("POSMAINTENANCE_PASSWORD=", "")
            else if(tokenizeResult[a].contains("POSMAINTENACE_NAME"))
                techName = tokenizeResult[a].replace("POSMAINTENACE_NAME=", "")
            else if(tokenizeResult[a].contains("REGULAR_ITEM_BARCODE"))
                regularItem = tokenizeResult[a].replace("REGULAR_ITEM_BARCODE=", "")
            else if(tokenizeResult[a].contains("SC_ITEM_BARCODE"))
                scItem = tokenizeResult[a].replace("SC_ITEM_BARCODE=", "")
            else if(tokenizeResult[a].contains("PWD_ITEM_BARCODE"))
                pwdItem = tokenizeResult[a].replace("PWD_ITEM_BARCODE=", "")
            else if(tokenizeResult[a].contains("SP_ITEM_BARCODE"))
                spItem = tokenizeResult[a].replace("SP_ITEM_BARCODE=", "")      
        }

    }
    public String getUsername() {
        return manUsername
    }

    public String getPassword() {
        return manPassword
    }

    public String getName() {
        return manName
    }

     public String getUsername2() {
        return cashUsername
    }

    public String getPassword2() {
        return cashPassword
    }

    public String getName2() {
        return cashName
    }

    public String[] getRegularItemBarcode() {
        return regularItem.split(",")
    }

    public String[] getSeniorItemBarcode() {
        return scItem.split(",")
    }

    public String[] getPWDItemBarcode() {
        return pwdItem.split(",")
    }

    public String[] getSoloParentItemBarcode() {
        return spItem.split(",")
    }

    public String posLogs()
    {
        fileContents = new File(logPath).text
        return fileContents
    }

   public void logger(String log) {
        try{

             DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
             Date today = Calendar.getInstance().getTime();
             String date = df.format(today);
             System.out.println(logFilePath)
             File file = new File (logFilePath)
             FileWriter fileWritter = new FileWriter(file,true);
             BufferedWriter bufferWritter = new BufferedWriter(fileWritter);

             bufferWritter.write(date + " java_util_logging_Logger info 1 call\n");
             bufferWritter.write(log + "\n");
             bufferWritter.write("\n");
             bufferWritter.close();

             System.out.println(log)

         }catch(IOException e){
            e.printStackTrace();
         }

    }

    public void checkLogger(String log) {
        try{

             DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
             Date today = Calendar.getInstance().getTime();
             String date = df.format(today);
             System.out.println(checkerFilePath)
             File file = new File (checkerFilePath)
             FileWriter fileWritter = new FileWriter(file,true);
             BufferedWriter bufferWritter = new BufferedWriter(fileWritter);

             bufferWritter.write(date + " java_util_logging_Logger info 1 call\n");
             bufferWritter.write(log + "\n");
             bufferWritter.write("\n");
             bufferWritter.close();

             System.out.println(log)

         }catch(IOException e){
            e.printStackTrace();
         }

    }

}

new init()

