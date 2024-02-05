import groovy.util.GroovyScriptEngine
import javax.imageio.ImageIO
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.text.DecimalFormat

def parentFolder = "T_Script_Test" // Change based on the name of the script folder

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

String propertiesPath = absPath + File.separator + parentFolder + File.separator + "localProperties.txt"
def testTitle = script.extractLocalProperty(propertiesPath, "TEST_TITLE");

String testResult = "NOT TESTED";

File scrnShotDir = new File(absPath + File.separator + "AutomatonLogs" + File.separator + "screenshots" + File.separator + "Unreadable_Barcode");
if(!scrnShotDir.exists()) scrnShotDir.mkdirs();

Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())
BufferedImage capture = new Robot().createScreenCapture(screenRect)

try {   
        // SCREENSHOT / SCREEN CAPTURE - Uncomment / duplicate this script for generating an image capture of the screen
        /* 
        script.logger("INFO: Taking a screenshot [$testTitle' + '.bmp]...\n") // Use or duplicate this snippet to create a log for debugging purposes
        capture = new Robot().createScreenCapture(screenRect)
        ImageIO.write(capture, "bmp", new File(scrnShotDir.getAbsolutePath() + File.separator + testTitle + '.bmp'))
        */

        // Use this snippet to apply delay/pause interval in between actions
        /* pause(pauseLarge)
        */
        
        // Use this snippet to simulate a clickable action
        /*      clickOn 'text:OK' pause(pauseMedium) // Use this snippet to click an UI with a given text element
        *       clickOn fxer['number'] // Use this snippet to click on a JavaFX element with a definite ID assignment
        */

        // Use this snippet to simulate a clickable action
        /*      type 123456 pause(pauseMedium) // Use this snippet to type/encode a numerical entry to the selected FXer
        *       type 'This is a text entry!' // Use this snippet to type/encode a text/String entry to the selected FXer
        *       type variable (or '$variable') // Use this snippet to type/encode a value from a defined variable
        */

        // Use this snippet to generate a PASS/FAIL logs based on the test
        /*
        testResult = logSniffer.testResult(checkBackupExists);
        script.checkLogger("[TEST | $testTitle]: $testResult")
        */

        } catch (Exception | AssertionError e) {
                script.logger("INFO: Taking a screenshot ['$testTitle Error.bmp']...\n")
                capture = new Robot().createScreenCapture(screenRect)
                ImageIO.write(capture, "bmp", new File(scrnShotDir.getAbsolutePath() + File.separator + testTitle + ' Error.bmp'))
                
                e.printStackTrace();
                testResult = logSniffer.testResult(false)
                script.checkLogger("[$testTitle]: $testResult with reason: $e")
        }