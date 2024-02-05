import groovy.util.GroovyScriptEngine
import javax.imageio.ImageIO
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit;

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


File scrnShotDir = new File(absPath + File.separator + "AutomatonLogs" + File.separator + "screenshots" + File.separator + "2_Transaction");
if(!scrnShotDir.exists()) scrnShotDir.mkdirs();

Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())
BufferedImage capture = new Robot().createScreenCapture(screenRect)

def testTitle = "Logout";

// Change this based on the given value from POSX properties
def idleTime = 20;
def idleTimeInMillis = TimeUnit.MINUTES.toMillis(idleTime);

// LOGGING OUT
try {
    String testResult = "NOT TESTED";
    Calendar rawTime = Calendar.getInstance(); 
    rawTime.add(Calendar.MINUTE, idleTime);
    Date today = rawTime.getTime();

    script.checkLogger("[TEST | $testTitle - Idle: Expected time of logout (idle)]: $today")

    pause(idleTimeInMillis)

    try {
        clickOn 'text:OK' pause(pauseMedium)
        testResult = logSniffer.testResult(true)
        Calendar logOutTime = Calendar.getInstance();
        Date logoutTime = logoutTime.getTime();
        script.checkLogger("[TEST | $testTitle - Idle]: $testResult - $logoutTime");
    }
    catch (Exception e){
        testResult = logSniffer.testResult(false)
        script.checkLogger("[TEST | $testTitle - Idle]: $testResult")
    }

} catch (Exception | AssertionError e) {
            testResult = logSniffer.testResult(false)
            script.checkLogger("[TEST | $testTitle - Idle]: $testResult with reason: $e")
}

// compute.generateReceipt();
pause(pauseMedium)
