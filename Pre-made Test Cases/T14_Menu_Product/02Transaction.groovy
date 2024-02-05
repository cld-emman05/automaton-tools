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

def testTitle = "Transact Menu Product";
String testResult = "NOT TESTED";

File scrnShotDir = new File(absPath + File.separator + "AutomatonLogs" + File.separator + "screenshots" + File.separator + "Unreadable_Barcode");
if(!scrnShotDir.exists()) scrnShotDir.mkdirs();

Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())
BufferedImage capture = new Robot().createScreenCapture(screenRect)

try {
        script.logger("INFO: Entering $testTitle\n")

        clickOn fxer['button_0'] pause(pauseSmall) clickOn fxer['button_0'] pause(pauseSmall)
        // Define the quantity
        clickOn fxer['number'] pause(pauseMedium)
        type '1' pause(pauseMedium) // enter quantity
        clickOn 'text:QTY' pause(pauseMedium)
        
        script.logger("INFO: Taking a screenshot [$testTitle' + '.bmp]...\n")
        capture = new Robot().createScreenCapture(screenRect)
        ImageIO.write(capture, "bmp", new File(scrnShotDir.getAbsolutePath() + File.separator + testTitle + '.bmp'))

        clickOn 'text:PAY' pause(pauseMedium)
        script.logger("INFO: Testing Quick Cash buttons\n")
        clickOn fxer ['cash'] pause(pauseMedium)
        clickOn 'text:CHECK\nOUT' pause(pauseMedium)

        // Check computation of VAT
        boolean successVatDetails = compute.checkVatableDetails()
        testResult = logSniffer.testResult(successVatDetails)
        script.checkLogger("[TEST | $testTitle]: $testResult")

        } catch (Exception | AssertionError e) {
                script.logger("INFO: Taking a screenshot ['$testTitle Error.bmp']...\n")
        capture = new Robot().createScreenCapture(screenRect)
        ImageIO.write(capture, "bmp", new File(scrnShotDir.getAbsolutePath() + File.separator + testTitle + ' Error.bmp'))

            e.printStackTrace();
            testResult = logSniffer.testResult(false)
            script.checkLogger("[$testTitle]: $testResult with reason: $e")
        }