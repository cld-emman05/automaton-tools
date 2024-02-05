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

def testTitle = 'Price Reference'
String testResult = 'NOT TESTED'

File scrnShotDir = new File(absPath + File.separator + 'AutomatonLogs' + File.separator + 'screenshots' + File.separator + testTitle)
if (!scrnShotDir.exists()) scrnShotDir.mkdirs()

Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())
BufferedImage capture = new Robot().createScreenCapture(screenRect)

def subTestInfo = ""

try {
    subTestInfo = "- User shall be able to input - Product Barcode"
    script.logger("INFO: Entering $testTitle...")
    clickOn 'text:PRICE REF' pause(pauseSmall)

    /** OPTION 1: Manually type the GTIN/Barcode ofthe Item (for scanning tests)
        */
        String[] items = ['4806511010028']; // Supply as many barcodes as needed within the array
        for(int i = 0; i < items.length; i++) {
            clickOn fxer['barcode'] pause(pauseMedium)
                type items[i] pause(pauseMedium)
                clickOn 'text:SEARCH'
                pause(pauseLarge)
        }

    subTestInfo = "- Display Product Name"    
    try {
        assertNotNull fxer['productName'].getText()
        // assertThat fxer['productName'], is(notNullValue())

        script.logger("INFO: Taking a screenshot [" + testTitle + ".bmp]...\n")
        ImageIO.write(capture, "bmp", new File(scrnShotDir.getAbsolutePath() + File.separator + testTitle + '.bmp'))
        testResult = logSniffer.testResult(true)
        script.checkLogger("[$testTitle $subTestInfo]: $testResult")
    } catch (AssertionError ae) {
        script.logger("INFO: Taking a screenshot ['$testTitle $subTestInfo Error.bmp']...\n")
        capture = new Robot().createScreenCapture(screenRect)
        ImageIO.write(capture, 'bmp', new File(scrnShotDir.getAbsolutePath() + File.separator + testTitle + ' Error.bmp'))
        
        testResult = logSniffer.testResult(false)
        script.checkLogger("[$testTitle $subTestInfo]: $testResult with reason: $ae")
    }

    subTestInfo = "- User shall be able to input - Product Price"
    try {
        assertThat fxer['productPrice'], is(notNullValue())

        script.logger("INFO: Taking a screenshot [" + testTitle + ".bmp]...\n")
        ImageIO.write(capture, "bmp", new File(scrnShotDir.getAbsolutePath() + File.separator + testTitle + '.bmp'))
        testResult = logSniffer.testResult(true)
        script.checkLogger("[$testTitle $subTestInfo]: $testResult")
    } catch (AssertionError ae) {
        script.logger("INFO: Taking a screenshot ['$testTitle $subTestInfo Error.bmp']...\n")
        capture = new Robot().createScreenCapture(screenRect)
        ImageIO.write(capture, 'bmp', new File(scrnShotDir.getAbsolutePath() + File.separator + testTitle + ' Error.bmp'))
        
        testResult = logSniffer.testResult(false)
        script.checkLogger("[$testTitle $subTestInfo]: $testResult with reason: $ae")
    }

    clickOn 'text:BACK'
    pause(pauseMedium)
} catch (Exception e) {
    script.logger("INFO: Taking a screenshot ['$testTitle Error.bmp']...\n")
    capture = new Robot().createScreenCapture(screenRect)
    ImageIO.write(capture, 'bmp', new File(scrnShotDir.getAbsolutePath() + File.separator + testTitle + ' Error.bmp'))

    e.printStackTrace()
    testResult = logSniffer.testResult(false)
    script.checkLogger("[$testTitle]: $testResult with reason: $e")
}
