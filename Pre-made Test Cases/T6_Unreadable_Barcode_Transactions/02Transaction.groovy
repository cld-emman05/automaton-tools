import groovy.util.GroovyScriptEngine
import javax.imageio.ImageIO
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.text.DecimalFormat

int pauseXS = 300, pauseSmall = 400, pauseMedium = 2000, pauseLarge = 3000, pauseXL = 8000
boolean success = false

String fileContents = ""
File absFile = new File("")
String absPath = absFile.getAbsolutePath()
def binding = new Binding()
File tmpDir = new File(absPath + File.separator + "ScriptProperties" + File.separator + "InitializeValues.groovy")
def engine = new GroovyScriptEngine([tmpDir.toURI().toURL()] as URL[])
def script = engine.run('InitializeValues.groovy', binding)
script.Initialize()

def binding2 = new Binding()
File temp = new File(absPath + File.separator + "ScriptProperties" + File.separator + "CheckAndComputeReceiptDetails.groovy")
def computeEngine = new GroovyScriptEngine([temp.toURI().toURL()] as URL[])
def compute = computeEngine.run('CheckAndComputeReceiptDetails.groovy', binding2)
def logSniffer = computeEngine.run('PosLogSniffer.groovy', binding2)

def testTitle = "Unreadable Transaction";
String testResult = "NOT TESTED";

File scrnShotDir = new File(absPath + File.separator + "AutomatonLogs" + File.separator + "screenshots" + File.separator + "$testTitle");
if(!scrnShotDir.exists()) scrnShotDir.mkdirs();

Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())
BufferedImage capture = new Robot().createScreenCapture(screenRect)

try {
        script.logger("INFO: Entering $testTitle\n")

        /** OPTION 1: Manually type the GTIN/Barcode ofthe Item (for scanning tests)
        */

        String[] items = ['4806511010028']; // Supply as many barcodes as needed within the array
        clickOn fxer['number'] pause(pauseMedium) // Clicks the "Barcode Entry" input
        for(int i = 0; i < items.length; i++) {
                // Queue items from the array
                type items[i] pause(pauseMedium)
                clickOn 'text:PLU' pause(pauseMedium) // For product look up / queueing of items to Line Items
                
                // Define the quantity
                clickOn fxer['number'] pause(pauseMedium) 
                type '1' pause(pauseMedium) // enter quantity
                clickOn 'text:QTY' pause(pauseMedium) 
        }

        // End of OPTION 1

        /* OPTION 2: User shall transact using MENU ITEMS (clickable items)
        * 
        * clickOn fxer['button_1'] pause(pauseSmall) clickOn fxer['button_2'] pause(pauseSmall)
        */
        // End of OPTION 2

        script.logger("INFO: Taking a screenshot ['$testTitle' + '.bmp']...\n")
        capture = new Robot().createScreenCapture(screenRect)
        ImageIO.write(capture, "bmp", new File(scrnShotDir.getAbsolutePath() + File.separator + testTitle + '.bmp'))

         clickOn 'text:PAY' pause(pauseMedium)
         clickOn fxer ['cash'] pause(pauseMedium)
         clickOn 'text:CHECK\nOUT' pause(pauseMedium)

        // Check computation of VAT
        boolean successVatDetails = compute.checkVatableDetails()
        testResult = logSniffer.testResult(successVatDetails)
        script.checkLogger("[TEST | $testTitle Computation]: $testResult")

} catch (Exception | AssertionError e) {
        script.logger("INFO: Taking a screenshot ['$testTitle Error.bmp']...\n")
        capture = new Robot().createScreenCapture(screenRect)
        ImageIO.write(capture, "bmp", new File(scrnShotDir.getAbsolutePath() + File.separator + testTitle + ' Error.bmp'))

            e.printStackTrace();
            testResult = logSniffer.testResult(false)
            script.checkLogger("[$testTitle]: $testResult with reason: $e")
}