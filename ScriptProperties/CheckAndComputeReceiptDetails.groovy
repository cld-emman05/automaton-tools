import java.text.SimpleDateFormat
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Arrays;
import groovy.util.GroovyScriptEngine
import javax.imageio.ImageIO
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.text.DecimalFormat
import java.lang.Math

class init {

        File absFile = new File("")
        String absPath = absFile.getAbsolutePath()
        def binding = new Binding()
        File tmpDir = new File(absPath + File.separator + "ScriptProperties" + File.separator + "InitializeValues.groovy")
        def engine = new GroovyScriptEngine([tmpDir.toURI().toURL()] as URL[])
        def script = engine.run('InitializeValues.groovy', binding)

        String logsContent = "", receipt = "", productName = "", total = "", cash = "", change = "", vatable = "", vatableExempted = "",
            zeroRated = "", vatTax = "", scPwd12 = "", scPwd20 = "",
            sp12 = "", sp10 = "", discountAmount = "", temp = "", credit = "", giftCheck = "", disc = "", promoDisc = "";
        double totalValue = 0, vatableValue = 0, vatableExemptedValue = 0, zeroRatedValue = 0, vatTaxValue = 0, scPwd12Value = 0, scPwd20Value = 0,
        sp12Value = 0, sp10Value = 0,
        soloParentValue = 0, computedVatable = 0, computedVatTax = 0, cashValue = 0, changeValue = 0, ccValue = 0, gcValue = 0, discValue = 0, promoDiscValue = 0,
            totalDisc, totalPromoDisc;
        String[] tokenizeResult;
        int startIndex = 0;
        boolean isVatable = false, isNonVat = false, hasDiscount = false;

        public void initValues(){
            logsContent = ""; productName = ""; total = ""; cash = ""; change = ""; vatable = ""; vatableExempted = ""; zeroRated = "";
                vatTax = ""; scPwd12 = ""; scPwd20 = ""; sp12 = ""; sp10 = ""; discountAmount = ""; temp = ""; credit = ""; giftCheck = ""; disc = ""; promoDisc = "";
            totalValue = 0; vatableValue = 0; vatableExemptedValue = 0; zeroRatedValue = 0; vatTaxValue = 0; scPwd12Value = 0; scPwd20Value = 0; sp10Value = 0; sp12Value = 0;
                computedVatable = 0; computedVatTax = 0; cashValue = 0; changeValue = 0; ccValue = 0; gcValue = 0;
            startIndex = 0;
            isVatable = false; isNonVat = false; hasDiscount = false;

        }

    public void getDetails(){
        initValues();
        logsContent = script.posLogs();
        receipt = "";
        boolean isOnline = false, isCard = false;
        totalDisc = Double.parseDouble("0.00");
        totalPromoDisc = Double.parseDouble("0.00");

        tokenizeResult = logsContent.split("[ \t\\x0B\f\r]+|(?=\n)");
        for (int a = tokenizeResult.length - 1; a >= 0; a--) {
            if (tokenizeResult[a].equals("Philippine")) {
                System.out.println(tokenizeResult[a])
                startIndex = a;
                break;
            }
            if (tokenizeResult[a].equals("\nAcct#")){       // Check if ecpay
                script.logger("INFO: Transaction product: BillsCollctionPLDT")
                isOnline = true;
            }
            if (tokenizeResult[a].equals("\n7-CONNECT")){       // Check if 7 connect
                script.logger("INFO: Transaction product: 7-CONNECT")
                isOnline = true;
            }
            if(!isOnline && tokenizeResult[a].equals("Acknowledgement")){
                isCard = true;
            }
        }

        for(int a = startIndex; a<tokenizeResult.length; a++) {
            receipt += tokenizeResult[a] + " ";

            // println("$a: " + tokenizeResult[a]);

            if(tokenizeResult[a].equals("Acknowledgement") && isCard)  productName += tokenizeResult[a + 5];    // Check if physical cards
            else if (tokenizeResult[a].equals("\nMIN") && !isOnline && !isCard) productName += tokenizeResult[a + 6];  // Check if regular transaction

            if(tokenizeResult[a].equals("\nNET")){
                discountAmount += tokenizeResult[a - 1];
                temp +=" " + tokenizeResult[a - 1];
                promoDisc = tokenizeResult[a - 1];
                promoDiscValue = Double.parseDouble(promoDisc);
                totalPromoDisc += promoDiscValue;
                hasDiscount = true;
            }
            if(tokenizeResult[a].equals("Credit_Card")){
                credit += tokenizeResult[a + 1];
                ccValue = Double.parseDouble(credit);
            }
            if(tokenizeResult[a].equals("Gift_Check")){
                giftCheck += tokenizeResult[a + 1];
                gcValue = Double.parseDouble(giftCheck);
            }
            if (tokenizeResult[a].equals("\nTotal")) {
                total += tokenizeResult[a + 4];
                totalValue = Double.parseDouble(total);
                if(tokenizeResult[a - 2].contains("V")) isVatable = true;
                if(tokenizeResult[a - 2].contains("N")) isNonVat = true;
            }
            if (tokenizeResult[a].equals("CASH")) {
                cash += tokenizeResult[a + 1];
                cashValue = Double.parseDouble(cash);
            }
            if (tokenizeResult[a].equals("CHANGE")) {
                change += tokenizeResult[a + 1];
                changeValue = Double.parseDouble(change);
            }
            if (tokenizeResult[a].equals("Vatable")) {
                vatable += tokenizeResult[a + 1];
                vatableValue = Double.parseDouble(vatable);
            }
            if (tokenizeResult[a].equals("VAT_Amt")) {
                vatTax += tokenizeResult[a + 1];
                vatTaxValue = Double.parseDouble(vatTax);
            }
            if (tokenizeResult[a].equals("Zero_Rated")) {
                zeroRated += tokenizeResult[a + 1];
                zeroRatedValue = Double.parseDouble(zeroRated);
            }
            // System.out.println("$a " + tokenizeResult[a]);
            if (tokenizerCheck(a, "VAT")
                && tokenizerCheck(a+1, "Exempt")
                && tokenizerCheck(a+2, "Sales")
                ) {
                vatableExempted += tokenizeResult[a + 3];
                System.out.println("VAT Exempt Sales: $vatableExempted");
                vatableExemptedValue = Double.parseDouble(vatableExempted);
            }
            if (tokenizeResult[a].equals("SC12") || tokenizeResult[a].equals("PWD12")) {
                scPwd12 += tokenizeResult[a + 1];
                scPwd12Value = Math.abs(Double.parseDouble(scPwd12));
            }
            if (tokenizeResult[a].equals("SC20") || tokenizeResult[a].equals("PWD20")) {
                scPwd20 += tokenizeResult[a + 1];
                scPwd20Value = Math.abs(Double.parseDouble(scPwd20));
            }
            if (tokenizeResult[a].equals("SP12")) {
                sp12 += tokenizeResult[a + 1];
                sp12Value = Math.abs(Double.parseDouble(sp12));
            }
            if (tokenizeResult[a].equals("SP10")) {
                sp10 += tokenizeResult[a + 1];
                sp10Value = Math.abs(Double.parseDouble(sp10));
            }
            if ( ( tokenizerCheck(a, "Total") && tokenizerCheck(a+1, "SC/PWD") && tokenizerCheck(a+2, "Disc") )
                || 
                ( ( tokenizerCheck(a, "Total") && tokenizerCheck(a+1, "SP") && tokenizerCheck(a+2, "Disc") )
                )
            ) {
                disc = tokenizeResult[a + 1];
                discValue = Math.abs(Double.parseDouble(disc));
                totalDisc += discValue;
            }
        }
        if(hasDiscount && (scPwd20 == "" || sp10 == "")){
            script.logger("INFO: Promo/Discount amount:" + temp)
            script.logger("INFO: TOTAL Promo Discount =  " + Math.abs(totalPromoDisc))
        }
        script.logger("INFO: Transaction product: " + productName.replace("\n", ""))
        String msg = "INFO: TOTAL: "+total+" | CASH: "+cash+" | CHANGE: "+change;
        String info = msg+" | Vatable: "+vatable+
            " | VAT_Amt: "+vatTax+" | VAT_Exempt: " +vatableExempted;

        if(scPwd20 != "")
            info += " | Total SC/PWD Disc: "+scPwd20;
        if(credit != "")
            info += " | Credit_Card: "+credit;
        if(giftCheck != "")
            info += " | Gift_Check: "+giftCheck;

        if(isCard || isOnline) script.logger(msg)
        else script.logger(info)

        System.out.println("Cash: $cash");
        System.out.println("Change: $change");
        System.out.println("Total: $total");
        if(!vatable.equals(""))
            System.out.println("Vatable: " + vatable);
        if(!vatTax.equals(""))
            System.out.println("VAT_Amt: " + vatTax);
        if(!zeroRated.equals(""))
            System.out.println("Zero_Rated: " + zeroRated);
        if(!vatableExempted.equals(""))
            System.out.println("VAT Exempt Sales: " + vatableExempted);
        if(!scPwd12.equals(""))
            System.out.println("SC/PWD 5: " + scPwd12);
        if(!scPwd20.equals(""))
            System.out.println("SC/PWD 20: " + scPwd20);
        if(!sp12.equals(""))
            System.out.println("SP 5: " + sp12);
        if(!sp10.equals(""))
            System.out.println("SP10: " + sp10);
        if(totalValue != 0)
            System.out.println("Total Value: " + totalValue);
        if(vatableValue != 0)
            System.out.println("Total Value: " + vatableValue);
        if(vatTaxValue != 0)
            System.out.println("VAT Tax: " + vatTaxValue);
        if(zeroRatedValue != 0)
            System.out.println("Zero Rated: " + zeroRatedValue);
        if(vatableExemptedValue != 0)
            System.out.println("Vatable Exempted: " + vatableExemptedValue);
        if(scPwd12Value != 0)
            System.out.println("SC/PWD 12: " + scPwd12Value);
        if(scPwd20Value != 0)
            System.out.println("SC/PWD 20: " + scPwd20Value);
        if(sp12Value != 0)
            System.out.println("SP 12: " + sp12Value);
        if(sp10Value != 0)
            System.out.println("SP 10: " + sp10Value);

    }

    public void getReceipt(){
        receipt = "";
        logsContent = script.posLogs();

        tokenizeResult = logsContent.split("[ \t\\x0B\f\r]+|(?=\n)");
        for (int a = tokenizeResult.length - 1; a >= 0; a--) {
            if (tokenizeResult[a].equals("Cash")) {
                startIndex = a;
                break;
            }
        }
        for(int a = startIndex; a<tokenizeResult.length; a++) {
            receipt += tokenizeResult[a] + " ";
        }
    }

    public void generateReceipt(){
        DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        Date today = Calendar.getInstance().getTime();
        String reportDate = df.format(today);
        String folder = absPath + File.separator + "ReceiptLogs";
        File rcptDir = new File(folder)
        if(!rcptDir.exists()) rcptDir.mkdir();

        String rcptNumber = getReceiptNumber();
        String filePath = folder + File.separator + "RCPT $rcptNumber";

        try{
            File logFile = new File (filePath)
            if (!logFile.exists())
            {
                logFile.createNewFile()
            }

            receipt = "";
            logsContent = script.posLogs();

            tokenizeResult = logsContent.split("[ \t\\x0B\f\r]+|(?=\n)");
            for (int a = tokenizeResult.length - 1; a >= 0; a--) {
                if (tokenizeResult[a].equals("Philippine")) {
                    startIndex = a
                    break;
                }
            }

            // System.out.println("Initial Token: " + tokenizeResult[0])
            // System.out.println("Terminal Token: " + tokenizeResult[tokenizeResult.length - 2])
            for(int a = startIndex; a<tokenizeResult.length; a++) {
                // println tokenizeResult[a];
                receipt += tokenizeResult[a] + " ";
                if(tokenizeResult[a].equals("INVOICE") && tokenizeResult[a + 1].equals("-")){
                    receipt += tokenizeResult[a + 1] + " ";
                    break;
                }
            }
            
            System.out.println(filePath)
             File file = new File (filePath)
             FileWriter fileWritter = new FileWriter(file,true);
             BufferedWriter bufferWritter = new BufferedWriter(fileWritter);

             bufferWritter.write(receipt+ "\n");
             bufferWritter.write("\n");
             bufferWritter.close();

             System.out.println("Successfully generated receipt: $rcptNumber");
             //receipt)
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


    public String getReceiptNumber(){
        logsContent = script.posLogs()

        tokenizeResult = logsContent.split("[ \t\\x0B\f\r]+|(?=\n)");
        String receiptNumber = "";
        startIndex = 0;

        for (int a = tokenizeResult.length - 1; a >= 0; a--) {
            if (tokenizeResult[a].equals("Philippine")) {
                startIndex = a;
                break;
            }
        }

        for(int a = startIndex; a<tokenizeResult.length; a++) {
            if (tokenizeResult[a].equals("\nINVOICE")) {
                receiptNumber += tokenizeResult[a + 1];
                break;
            }
        }
        return receiptNumber;
    }

    public boolean checkVatableDetails(){
        getDetails();
        computedVatable = totalValue / Double.parseDouble("1.12");
        computedVatTax = computedVatable * 0.12;

        /* System.out.println("Total Value: $totalValue");
        System.out.println("Computed Vatable: $computedVatable");
        System.out.println("Computed VAT Tax: $computedVatTax");

        System.out.println(new DecimalFormat("#.##").format(computedVatable));
        System.out.println(vatable);
        System.out.println(new DecimalFormat("#.##").format(computedVatTax));
        System.out.println(vatTax); */

        if (Double.parseDouble(new DecimalFormat("#.##").format(computedVatable)) == vatableValue &&
            Double.parseDouble(new DecimalFormat("#.##").format(computedVatTax)) == vatTaxValue && isVatable) return true;
        else return false;
    }

    public boolean checkNonVatableAmountDetails(){
        getDetails();

        /* System.out.println("Vatable Exempted: $vatableExemptedValue");
        System.out.println("Total: $total");
        System.out.println("isNonVat: $isNonVat");
        System.out.println("Change: $changeValue");
        System.out.println("Cash: $cashValue");
        System.out.println("Total Value: $totalValue"); */

        if(vatableExempted == total && isNonVat && changeValue == cashValue - totalValue) return true;
        else return false;
    }

    public boolean checkPromoVatable(){
        getDetails();
        computedVatable = (totalValue - vatableExemptedValue)  / Double.parseDouble("1.12");
        computedVatTax = computedVatable * 0.12;

        // System.out.println(totalValue);
        System.out.println("Total Vatable = " + (totalValue - vatableExemptedValue));
        System.out.println("Vatable = "+computedVatable);
        System.out.println("Vat_Tax = "+computedVatTax);

        // System.out.println(new DecimalFormat("#.##").format(computedVatable));
        // System.out.println(vatable);
        // System.out.println(new DecimalFormat("#.##").format(computedVatTax));
        // System.out.println(vatTax);

        if (Double.parseDouble(new DecimalFormat("#.##").format(computedVatable)) == vatableValue &&
            Double.parseDouble(new DecimalFormat("#.##").format(computedVatTax)) == vatTaxValue && isVatable) return true;
        else return false;
    }

    public boolean checkPromoDetails(){

        if(hasDiscount) return true;
        else return false;
    }

    public boolean checkDiscoutDetails(){

        if(Math.abs(scPwd20Value) == Math.abs(Double.parseDouble(discountAmount))) return true;
        else return false;
    }

    public boolean checkSC5(){
        getDetails();

        if(scPwd12 == scPwd12) return true;
        else return false;

    }

    public boolean checkSC20(){
        getDetails();

        if(scPwd20 == scPwd20) return true;
        else return false;

    }

    public boolean checkPWD5(){
        getDetails();

        if(scPwd12 == scPwd12) return true;
        else return false;
    }

    public boolean checkPWD20(){
        getDetails();

        if(scPwd20 == scPwd20) return true;
        else return false;
    }

    public boolean checkZero(){
        getDetails();

        if(zeroRated == total) return true;
        else return false;

    }

    public boolean check_SC_PWD_Disc(){
        println("SC/PWD 20: $scPwd20Value");
        println("Total Discount: $totalDisc");

        if(scPwd20Value == Math.abs(totalDisc)) return true;
        else return false;
    }

    public boolean check_SP_Disc(){
        println("SP 10: $sp10Value");
        println("Total Discount: $totalDisc");

        if(sp10Value == Math.abs(totalDisc)) return true;
        else return false;
    }

    public boolean checkPhysicalCards(){
        getDetails();

        if(receipt.contains("Acknowledgement Receipt")) {
            return true;
        }
        else return false;
    }

    public boolean checkECPay(){
        getDetails();

        if(receipt.contains("Acknowledgement Receipt") && receipt.contains("Acct#")) return true;
        else return false;

    }

    public boolean check7Connect(){
        getDetails();

        if(receipt.contains("Acknowledgement Receipt") && receipt.contains("7-CONNECT")) return true;
        else return false;
    }

    public boolean checkCashDrop(){
        getReceipt();
        if(receipt.contains("CASH DROP")) return true;
        else return false;

    }

    public boolean checkVoid(){
        getReceipt();

        if(receipt.contains("VOID")) return true;
        else return false;
    }

    public boolean checkAddCash(){
        getReceipt();
        if(receipt.contains("CASH ADDED")) return true;
        else return false;
    }

    public boolean checkCancel(){
        getReceipt();

        if(receipt.contains("CANCEL")) return true;
        else return false;
    }

    public boolean checkRefund(){
        if(receipt.contains("Refund")) return true;
        else return false;
    }

    public boolean checkNonCash(){
        if(receipt.contains("Credit_Card") || receipt.contains("Gift_Check")) return true;
        else return false;
    }

    public boolean checkReprint(){
        getReceipt();
        if(receipt.contains("Reprint")) return true;
        else return false;
    }

    public boolean checkTrainingTag(){
        getReceipt();
        if(receipt.contains("Training")) return true;
        else return false;
    }

    public boolean isOfficial(){
        getReceipt();
        if(receipt.contains("THIS IS AN OFFICIAL RECEIPT")) return true;
        else return false;
    }

    public boolean checkARLabels(){
        getReceipt();
        if(receipt.contains("THIS IS AN OFFICIAL RECEIPT") && receipt.contains("THIS DOCUMENT IS NOT VALID")) return true;
        else return false;
    }

    private boolean tokenizerCheck(int index, String entry){
        return tokenizeResult[index].equals(entry)
    }
}
new init()