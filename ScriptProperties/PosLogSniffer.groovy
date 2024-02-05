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

    public boolean successfulPOSInit () {
        logsContent = script.posLogs()
        return logsContent.contains("Obtaining new instance of Application Start");
    }

    public String testResult(boolean result){
        if (result == true){
            return "PASS";
        }
        else {
            return "FAIL";
        }
    }



}
new init()
