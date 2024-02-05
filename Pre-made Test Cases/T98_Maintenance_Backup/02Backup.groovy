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

def testTitle = "Maintenance Backup";
String testResult = "NOT TESTED";

File scrnShotDir = new File(absPath + File.separator + "AutomatonLogs" + File.separator + "screenshots" + File.separator + "Unreadable_Barcode");
if(!scrnShotDir.exists()) scrnShotDir.mkdirs();

Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())
BufferedImage capture = new Robot().createScreenCapture(screenRect)

try {
        String getCurrentDir = System.getProperty("user.dir");
        File backupDir = new File("getCurrentDir/posdb-backup");
        File backup = new File("$getCurrentDir/posdb.mv.db");

        if(backup.exists()){
                backupDir.deleteDir();
                println "Deleted the POS Database Backup: $backupDir";
        }
        script.logger("INFO: Entering $testTitle\n")

        clickOn 'text:MENU'
        pause(pauseLarge) 
        clickOn 'text:POS Maintenance'
        pause(pauseLarge)
        clickOn 'text:Back Up'
        pause(pauseLarge * 2)
        
        script.logger("INFO: Taking a screenshot [$testTitle' + '.bmp]...\n")
        capture = new Robot().createScreenCapture(screenRect)
        ImageIO.write(capture, "bmp", new File(scrnShotDir.getAbsolutePath() + File.separator + testTitle + '.bmp'))

        pause(pauseLarge)

        clickOn 'text:OK' pause(pauseMedium)

        // Check computation of VAT
        println "File location: " + backup.getAbsolutePath();

        def checkBackupExists = backup.exists();       

        testResult = logSniffer.testResult(checkBackupExists);
        script.checkLogger("[TEST | $testTitle]: $testResult")

        } catch (Exception | AssertionError e) {
                script.logger("INFO: Taking a screenshot ['$testTitle Error.bmp']...\n")
        capture = new Robot().createScreenCapture(screenRect)
        ImageIO.write(capture, "bmp", new File(scrnShotDir.getAbsolutePath() + File.separator + testTitle + ' Error.bmp'))

            e.printStackTrace();
            testResult = logSniffer.testResult(false)
            script.checkLogger("[$testTitle]: $testResult with reason: $e")
        }