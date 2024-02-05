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

def testTitle = "Service Sales - 7-CONNECT";
String testResult = "NOT TESTED";

File scrnShotDir = new File(absPath + File.separator + "AutomatonLogs" + File.separator + "screenshots" + File.separator + "$testTitle");
if(!scrnShotDir.exists()) scrnShotDir.mkdirs();

Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())
BufferedImage capture = new Robot().createScreenCapture(screenRect)

def subTestInfo = " - Add 7-Connect "
//---------------------TS 3.5.7 TRANSACT 7CONNECT SALES------------------------------------------------------------------------
try{
    script.logger("INFO: Processing $testTitle $subTestInfo...\n")
    //TODO: 2. Error message shall be displayed when adding another 7-connect transaction.

    script.logger("INFO: Processing $testTitle...\n")        
    clickOn 'text:SERVICES' pause(pauseSmall)
    clickOn fxer['button_11'] pause(pauseSmall) // clickOn 'text:7-Connect' 
    pause(pauseSmall)
    
    script.logger("INFO: Taking a screenshot [" + testTitle + ".bmp]...\n")
    capture = new Robot().createScreenCapture(screenRect)
    ImageIO.write(capture, "bmp", new File(scrnShotDir.getAbsolutePath() + File.separator + testTitle +'.bmp'))
    script.logger("INFO: SENDING 7 connect transaction\n")
    pause(pauseMedium)

    clickOn "text:OK" pause(pauseLarge)
    testResult = logSniffer.testResult(true)
    script.checkLogger("[$testTitle $subTestInfo]: $testResult")
    pause(pauseMedium)
} catch (Exception e) {
    script.logger("INFO: Taking a screenshot ['$testTitle Error.bmp']...\n")
    capture = new Robot().createScreenCapture(screenRect)
    ImageIO.write(capture, "bmp", new File(scrnShotDir.getAbsolutePath() + File.separator + testTitle + " Error.bmp"))


        e.printStackTrace();
        testResult = logSniffer.testResult(false)
        script.checkLogger("[$testTitle $subTestInfo]: $testResult with reason: $e")
}