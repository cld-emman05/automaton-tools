import groovy.util.GroovyScriptEngine
import javax.imageio.ImageIO
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.text.DecimalFormat

int pauseXS = 300, pauseSmall = 400, pauseMedium = 2000, pauseLarge = 3000, pauseXL = 8000
boolean success = false

def testTitle = "Login";
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

String notTestedWord = 123456;

def subTestInfo = ""
/////// LOGGING IN
try {
    script.logger("INFO: Entering Login credentials\n")

    // TO DO: Add random button tapping before logging in

    // Subtest 1: Username Login with Wrong Username
    subTestInfo = " - Username Login with Wrong Username"
    clickOn fxer['userId'] pause(pauseMedium)
    type notTestedWord pause(pauseMedium)

    try{
        assertThat fxer['name'], hasText('')
        clickOn fxer['password'] pause(pauseMedium)
        type script.getUsername2() pause(pauseMedium)
        clickOn 'text:OK' pause(pauseMedium)
        script.logger("INFO: Taking a screenshot [" + testTitle + ".bmp]...\n")
        capture = new Robot().createScreenCapture(screenRect)
        ImageIO.write(capture, "bmp", new File(scrnShotDir.getAbsolutePath() + File.separator + testTitle + subTestInfo + '.bmp'))
        clickOn 'text:OK' pause(pauseMedium)
        testResult = logSniffer.testResult(true)
        script.checkLogger("[$testTitle $subTestInfo]: $testResult")
    } catch (Exception | AssertionError e) {
            testResult = logSniffer.testResult(false)
            script.checkLogger("[$testTitle $subTestInfo]: $testResult with reason: $e")
    }

    // Subtest 2: Username Login with Wrong Password
    subTestInfo = " - Username Login with Wrong Password"
    clickOn fxer['userId'] pause(pauseMedium)
    type script.getUsername2() pause(pauseMedium)

    try{
        assertThat fxer['name'], hasText(script.getName2())
        clickOn fxer['password'] pause(pauseMedium)
        type notTestedWord pause(pauseMedium)
        clickOn 'text:OK' pause(pauseMedium)
    
        script.checkLogger("[$testTitle $subTestInfo]: $testResult")
        script.logger("INFO: Taking a screenshot [" + testTitle + subTestInfo + ".bmp]...\n")
        capture = new Robot().createScreenCapture(screenRect)
        ImageIO.write(capture, "bmp", new File(scrnShotDir.getAbsolutePath() + File.separator + testTitle + subTestInfo + '.bmp'))
        
        clickOn 'text:OK' pause(pauseMedium)
        testResult = logSniffer.testResult(true)
        script.checkLogger("[$testTitle $subTestInfo]: $testResult")
    } catch (Exception | AssertionError e) {
        testResult = logSniffer.testResult(false)
        script.checkLogger("[$testTitle $subTestInfo]: $testResult with reason: $e")
    }


    // Subtest 5: Successful Login! User shall be able to input the following detail/s
    subTestInfo = " - Successful Login (Valid User Login)"   
    clickOn fxer['userId'] pause(pauseMedium)
    type script.getUsername2() pause(pauseMedium)
    try {
        assertThat fxer['name'], hasText(script.getName2())
        clickOn fxer['password'] pause(pauseMedium)
        type script.getPassword2() pause(pauseMedium)

        testResult = logSniffer.testResult(true)
        script.checkLogger("[TEST | $testTitle $subTestInfo]: $testResult")
        } catch (Exception | AssertionError e) {
            e.printStackTrace();
            testResult = logSniffer.testResult(false)
            script.checkLogger("[TEST | $testTitle $subTestInfo]: $testResult")
        }

        script.logger("INFO: Taking a screenshot [Login.bmp]...\n")
        capture = new Robot().createScreenCapture(screenRect)
        ImageIO.write(capture, "bmp", new File(scrnShotDir.getAbsolutePath() + File.separator + testTitle + subTestInfo + '.bmp'))
        clickOn 'text:OK' pause(pauseMedium)   

        // Subtest 5: Upon successful login, if the previous shift/business date has ended, START SHIFT screen shall be displayed and Sync from SBS occurs
        subTestInfo = " - Successful Login (Start of Shift_Day)"
        try {
           assertThat fxer['startOfShiftOk'], hasText("OK")
           clickOn fxer['startOfShiftOk']
           script.logger("Waiting for 2 minutes and 30 seconds for SBS to finish.")
           pause(pauseLarge * 50)
           clickOn 'text:OK' pause(pauseMedium)
           testResult = logSniffer.testResult(true)
           script.checkLogger("[TEST | $testTitle $subTestInfo]: $testResult")
           } catch (AssertionError | Exception e) {
               testResult = logSniffer.testResult(true)
               script.checkLogger("[TEST | $testTitle $subTestInfo]: $testResult with reason: Already started shift!")
           }
           
           pause(pauseMedium)
        } catch (Exception e) {
            testResult = logSniffer.testResult(false)
            script.checkLogger("[TEST | $testTitle]: $testResult with reason: $e")
System.exit(1)
               pause(pauseMedium);
        }

        //TODO: Add Login Cancellation
        
        // compute.generateReceipt();
        pause(pauseMedium)
