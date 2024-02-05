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

def testTitle = 'Line Item - Other Validations'
String testResult = 'NOT TESTED'

File scrnShotDir = new File(absPath + File.separator + 'AutomatonLogs' + File.separator + 'screenshots' + File.separator + testTitle)
if (!scrnShotDir.exists()) scrnShotDir.mkdirs()

Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())
BufferedImage capture = new Robot().createScreenCapture(screenRect)

def subTestInfo = "- Editing Sales Details"
def cardNoValue = "9902000012452"
def cashAmtValue = "10000" // PhP 100.00
def soldToValue = "apollotest"

def testInput = "12345"

try {
    script.logger("INFO: Entering $testTitle $subTestInfo \n")


    // 6) Test Non-editable fields
    // 1. Should not be able to edit fields, except "Card No." , "Cash"and "Sold To" fields

    // a) Discount Field
    subTestInfo = "- Editing Discount Field"
    clickOn fxer['discount'] pause(pauseMedium)
    type testInput pause(pauseMedium);
    try{
        assertNotEquals fxer['discount'], hasText(testInput)
        clickOn fxer['subtotal'] pause(pauseMedium)
        
        script.logger("INFO: Taking a screenshot [" + testTitle + subTestInfo + ".bmp]...\n")
        capture = new Robot().createScreenCapture(screenRect)   
        ImageIO.write(capture, "bmp", new File(scrnShotDir.getAbsolutePath() + File.separator + testTitle  + subTestInfo +'.bmp'))
        pause(pauseMedium)

        testResult = logSniffer.testResult(true)
        script.checkLogger("[TEST | $testTitle $subTestInfo]: $testResult")
        pause(pauseMedium)
    }
    catch (AssertionError ae){
        script.logger("INFO: Taking a screenshot ['$testTitle $subTestInfo Error.bmp']...\n")
        capture = new Robot().createScreenCapture(screenRect)
        ImageIO.write(capture, 'bmp', new File(scrnShotDir.getAbsolutePath() + File.separator + testTitle + subTestInfo + ' Error.bmp'))
        
        e.printStackTrace()
        testResult = logSniffer.testResult(false)
        script.checkLogger("[$testTitle $subTestInfo]: $testResult with reason: $ae")
    }

    
    // b) Total Field
    subTestInfo = "- Editing Total Field"
    clickOn fxer['total'] pause(pauseMedium)
    type testInput pause(pauseMedium);
    try{
        assertNotEquals fxer['discount'], hasText(testInput)
        clickOn fxer['subtotal'] pause(pauseMedium)
        
        script.logger("INFO: Taking a screenshot [" + testTitle + subTestInfo + ".bmp]...\n")
        capture = new Robot().createScreenCapture(screenRect)   
        ImageIO.write(capture, "bmp", new File(scrnShotDir.getAbsolutePath() + File.separator + testTitle  + subTestInfo +'.bmp'))
        pause(pauseMedium)

        testResult = logSniffer.testResult(true)
        script.checkLogger("[TEST | $testTitle $subTestInfo]: $testResult")
        pause(pauseMedium)
    }
    catch (AssertionError ae){
        script.logger("INFO: Taking a screenshot ['$testTitle $subTestInfo Error.bmp']...\n")
        capture = new Robot().createScreenCapture(screenRect)
        ImageIO.write(capture, 'bmp', new File(scrnShotDir.getAbsolutePath() + File.separator + testTitle + subTestInfo + ' Error.bmp'))
        
        e.printStackTrace()
        testResult = logSniffer.testResult(false)
        script.checkLogger("[$testTitle $subTestInfo]: $testResult with reason: $ae")
    }

    // c) Others Field
    subTestInfo = "- Editing Others Field"
    clickOn fxer['others'] pause(pauseMedium)
    type testInput pause(pauseMedium);
    try{
        assertNotEquals fxer['discount'], hasText(testInput)
        clickOn fxer['subtotal'] pause(pauseMedium)
        
        script.logger("INFO: Taking a screenshot [" + testTitle + subTestInfo + ".bmp]...\n")
        capture = new Robot().createScreenCapture(screenRect)   
        ImageIO.write(capture, "bmp", new File(scrnShotDir.getAbsolutePath() + File.separator + testTitle  + subTestInfo +'.bmp'))
        pause(pauseMedium)

        testResult = logSniffer.testResult(true)
        script.checkLogger("[TEST | $testTitle $subTestInfo]: $testResult")
        pause(pauseMedium)
    }
    catch (AssertionError ae){
        script.logger("INFO: Taking a screenshot ['$testTitle $subTestInfo Error.bmp']...\n")
        capture = new Robot().createScreenCapture(screenRect)
        ImageIO.write(capture, 'bmp', new File(scrnShotDir.getAbsolutePath() + File.separator + testTitle + subTestInfo + ' Error.bmp'))
        
        e.printStackTrace()
        testResult = logSniffer.testResult(false)
        script.checkLogger("[$testTitle $subTestInfo]: $testResult with reason: $ae")
    }

    // c) Balance Field
    subTestInfo = "- Editing Balance Field"
    clickOn fxer['balance'] pause(pauseMedium)
    type testInput pause(pauseMedium);
    try{
        assertNotEquals fxer['discount'], hasText(testInput)
        clickOn fxer['subtotal'] pause(pauseMedium)
        
        script.logger("INFO: Taking a screenshot [" + testTitle + subTestInfo + ".bmp]...\n")
        capture = new Robot().createScreenCapture(screenRect)   
        ImageIO.write(capture, "bmp", new File(scrnShotDir.getAbsolutePath() + File.separator + testTitle  + subTestInfo +'.bmp'))
        pause(pauseMedium)

        testResult = logSniffer.testResult(true)
        script.checkLogger("[TEST | $testTitle $subTestInfo]: $testResult")
        pause(pauseMedium)
    }
    catch (AssertionError ae){
        script.logger("INFO: Taking a screenshot ['$testTitle $subTestInfo Error.bmp']...\n")
        capture = new Robot().createScreenCapture(screenRect)
        ImageIO.write(capture, 'bmp', new File(scrnShotDir.getAbsolutePath() + File.separator + testTitle + subTestInfo + ' Error.bmp'))
        
        e.printStackTrace()
        testResult = logSniffer.testResult(false)
        script.checkLogger("[$testTitle $subTestInfo]: $testResult with reason: $ae")
    }

} catch (Exception e) {
    subTestInfo = "- Editing Sales Details"

    script.logger("INFO: Taking a screenshot ['$testTitle $subTestInfo Error.bmp']...\n")
    capture = new Robot().createScreenCapture(screenRect)
    ImageIO.write(capture, 'bmp', new File(scrnShotDir.getAbsolutePath() + File.separator + testTitle + ' Error.bmp'))

    e.printStackTrace()
    testResult = logSniffer.testResult(false)
    script.checkLogger("[$testTitle $subTestInfo]: $testResult with reason: $e")
}