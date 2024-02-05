import groovy.util.GroovyScriptEngine
import javax.imageio.ImageIO
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.text.DecimalFormat

int pauseXS = 300, pauseSmall = 400, pauseMedium = 2000, pauseLarge = 3000, pauseXL = 8000
boolean success = false

String fileContents = ''
File absFile = new File('')
String absPath = absFile.getAbsolutePath()
def binding = new Binding()
File tmpDir = new File(absPath + File.separator + 'ScriptProperties' + File.separator + 'InitializeValues.groovy')
def engine = new GroovyScriptEngine([tmpDir.toURI().toURL()] as URL[])
def script = engine.run('InitializeValues.groovy', binding)
script.Initialize()

def binding2 = new Binding()
File temp = new File(absPath + File.separator + 'ScriptProperties' + File.separator + 'CheckAndComputeReceiptDetails.groovy')
def computeEngine = new GroovyScriptEngine([temp.toURI().toURL()] as URL[])
def compute = computeEngine.run('CheckAndComputeReceiptDetails.groovy', binding2)
def logSniffer = computeEngine.run('PosLogSniffer.groovy', binding2)

def testTitle = 'Line Item - RegularToService'
String testResult = 'NOT TESTED'

File scrnShotDir = new File(absPath + File.separator + 'AutomatonLogs' + File.separator + 'screenshots' + File.separator + testTitle)
if (!scrnShotDir.exists()) scrnShotDir.mkdirs()

Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())
BufferedImage capture = new Robot().createScreenCapture(screenRect)

def subTestInfo = "- Adding Regular items with Services"

try {
    script.logger("INFO: Entering $testTitle $subTestInfo \n")

    clickOn 'text:SERVICES' pause(pauseMedium)
    clickOn fxer['button_0']; pause(pauseMedium) clickOn fxer['button_0']; pause(pauseMedium)

    script.logger("INFO: Taking a screenshot [" + testTitle + subTestInfo + ".bmp]...\n")
    capture = new Robot().createScreenCapture(screenRect)   
    ImageIO.write(capture, "bmp", new File(scrnShotDir.getAbsolutePath() + File.separator + testTitle  + subTestInfo +'.bmp'))
    
    pause(pauseMedium)
    testResult = logSniffer.testResult(true)
    script.checkLogger("[TEST | $testTitle $subTestInfo]: $testResult")
    clickOn 'text:OK' pause(pauseSmall)
    

    /* 5) Editing Sold To
    subTestInfo = " - On-screen keyobard should appear when editing 'Sold To' field"
    clickOn fxer['soldTo'] pause(pauseMedium)
    try{
        assertThat fxer['enterButton'], hasText('Enter')
        type soldToValue pause(pauseMedium)

        script.logger("INFO: Taking a screenshot [" + testTitle + subTestInfo + ".bmp]...\n")
        capture = new Robot().createScreenCapture(screenRect)   
        ImageIO.write(capture, "bmp", new File(scrnShotDir.getAbsolutePath() + File.separator + testTitle  + subTestInfo +'.bmp'))
        pause(pauseMedium)

        testResult = logSniffer.testResult(true)
        script.checkLogger("[TEST | $testTitle $subTestInfo]: $testResult")
        pause(pauseMedium)

        // Save entered value and check Sold To field
        subTestInfo = " - Editing Sold To"
        clickOn fxer['enterButton'] pause(pauseMedium)

        script.logger("INFO: Taking a screenshot [" + testTitle + subTestInfo + ".bmp]...\n")
        capture = new Robot().createScreenCapture(screenRect)   
        ImageIO.write(capture, "bmp", new File(scrnShotDir.getAbsolutePath() + File.separator + testTitle  + subTestInfo +'.bmp'))
        pause(pauseMedium)

        testResult = logSniffer.testResult(true)
        script.checkLogger("[TEST | $testTitle $subTestInfo]: $testResult")
        pause(pauseMedium)
    }

    catch(AssertionError ae) {
        subTestInfo = " - On-screen keyboard should appear when editing 'Sold To' field"
        script.logger("INFO: Taking a screenshot ['$testTitle $subTestInfo Error.bmp']...\n")
    capture = new Robot().createScreenCapture(screenRect)
    ImageIO.write(capture, 'bmp', new File(scrnShotDir.getAbsolutePath() + File.separator + testTitle + subTestInfo + ' Error.bmp'))

    e.printStackTrace()
    testResult = logSniffer.testResult(false)
    script.checkLogger("[$testTitle $subTestInfo]: $testResult with reason: $ae")
    } */

} catch (Exception e) {
    subTestInfo = "- Editing Sales Details"

    script.logger("INFO: Taking a screenshot ['$testTitle $subTestInfo Error.bmp']...\n")
    capture = new Robot().createScreenCapture(screenRect)
    ImageIO.write(capture, 'bmp', new File(scrnShotDir.getAbsolutePath() + File.separator + testTitle + ' Error.bmp'))

    e.printStackTrace()
    testResult = logSniffer.testResult(false)
    script.checkLogger("[$testTitle $subTestInfo]: $testResult with reason: $e")
}