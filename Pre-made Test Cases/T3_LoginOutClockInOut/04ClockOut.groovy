import groovy.util.GroovyScriptEngine
import javax.imageio.ImageIO
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.text.DecimalFormat

int pauseXS = 300, pauseSmall = 400, pauseMedium = 2000, pauseLarge = 3000, pauseXL = 8000
boolean success = false

def testTitle = "Clock Out";
String testResult = "NOT TESTED";

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

File scrnShotDir = new File(absPath + File.separator + "AutomatonLogs" + File.separator + "screenshots" + File.separator + "$testTitle");
if(!scrnShotDir.exists()) scrnShotDir.mkdirs();

Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())
BufferedImage capture = new Robot().createScreenCapture(screenRect)
def subTestInfo = ""

    // CLOCK OUT
    // Scenario 1: The user has not yet clocked out
    subTestInfo = " - The user has not yet clocked out"

    try {
        try {
            clickOn 'CLOCK IN/OUT' pause(pauseMedium)
            clickOn fxer['staffId'] pause(pauseMedium)
            clickOn fxer['userId'] pause(pauseMedium)
            type script.getUsername2() pause(pauseMedium)
            clickOn fxer['password'] pause(pauseMedium)
            type script.getPassword2() pause(pauseMedium)
            clickOn 'Time Out' pause(pauseMedium)

            capture = new Robot().createScreenCapture(screenRect)
            ImageIO.write(capture, "bmp", new File(scrnShotDir.getAbsolutePath() + File.separator + testTitle + subTestInfo + '.bmp'))
            clickOn 'text:OK' pause(pauseMedium)
            
            testResult = logSniffer.testResult(true)
            script.checkLogger("[TEST | $testTitle $subTestInfo]: $testResult")
           } catch (AssertionError | Exception e) {
               testResult = logSniffer.testResult(true)
               script.checkLogger("[TEST | $testTitle $subTestInfo]: $testResult with reason: Already started shift!")
        }

        // Scenario 2: If user has already Clocked in or Clocked out for the said date, a prompt will appear.

        subTestInfo = " - Already clocked out"
        clickOn 'text:CLOCK IN/OUT' pause(pauseSmall)
        clickOn fxer['staffId'] pause(pauseMedium)
        clickOn fxer['userId'] pause (pauseMedium)
        type script.getUsername() pause(pauseMedium)
        clickOn fxer['password']
        type script.getPassword() pause(pauseSmall)
        clickOn fxer['loginOk'] pause(pauseSmall)
        clickOn 'text:Time Out' pause(pauseSmall)
        clickOn fxer['clockOk'] pause(pauseMedium)

        script.logger("INFO: Taking a screenshot [AlreadyClockedOut.bmp]...\n")
        capture = new Robot().createScreenCapture(screenRect)
        ImageIO.write(capture, "bmp", new File(scrnShotDir.getAbsolutePath() + File.separator + testTitle + subTestInfo + '.bmp'))
        testResult = logSniffer.testResult(true)
        script.checkLogger("[TEST | $testTitle $subTestInfo]: $testResult")
    } catch (Exception | AssertionError e) {
            e.printStackTrace();
            testResult = logSniffer.testResult(false)
            script.checkLogger("$testTitle $subTestInfo: $testResult")
    }
        
        
    // compute.generateReceipt();
    pause(pauseMedium)
