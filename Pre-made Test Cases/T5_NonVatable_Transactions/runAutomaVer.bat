@echo off

set TEST_TITLE=Transact Menu Product

set POS2_DIR=/opt/pos2
set NUMBER_OF_ITEMS_PROCESSED=1

:: Input amount for each respective expected value
set BASE_TOTAL_AMOUNT=45.00
set PAYMENT_TENDER=45.00

:: Formula provided for the computation of discount applications for SC20
for /f "delims=" %%a in ('cscript //NoLogo ExecutableBundles/AutomaVerWin/vatable.vbs "%BASE_TOTAL_AMOUNT%"') do set "result=%%a"
echo Vatable: %result%
set VATABLE=%result%

for /f "delims=" %%a in ('cscript //NoLogo ExecutableBundles/AutomaVerWin/vat_amount.vbs "%BASE_TOTAL_AMOUNT%" "%VATABLE%"') do set "result=%%a"
echo Vat_Amount=%result%
set VAT_AMT=%result%


echo Vat Amt: %VAT_AMT%

echo Running AutomaVer for validating the matching data between the target value and the database value

java -jar ./AutomaVer.jar "%TEST_TITLE% - Total Amount Due" %BASE_TOTAL_AMOUNT% "SELECT computed_amount_value FROM TBL_TRANSACTION ORDER BY CREATED_DATE DESC LIMIT %NUMBER_OF_ITEMS_PROCESSED%"

echo "SELECT computed_amount_value FROM TBL_TRANSACTION ORDER BY CREATED_DATE DESC LIMIT %NUMBER_OF_ITEMS_PROCESSED%"

java -jar ./AutomaVer.jar "%TEST_TITLE% - Base Total" %BASE_TOTAL_AMOUNT% "SELECT transaction_amount_value FROM TBL_TRANSACTION ORDER BY CREATED_DATE DESC LIMIT %NUMBER_OF_ITEMS_PROCESSED%"

java -jar ./AutomaVer.jar "%TEST_TITLE% - Cash" %PAYMENT_TENDER% "SELECT paid_amount_value FROM TBL_TRANSACTION ORDER BY CREATED_DATE DESC LIMIT %NUMBER_OF_ITEMS_PROCESSED%"

java -jar ./AutomaVer.jar "%TEST_TITLE% - Change" 0.00 "SELECT change_value FROM TBL_TRANSACTION ORDER BY CREATED_DATE DESC LIMIT %NUMBER_OF_ITEMS_PROCESSED%"

java -jar ./AutomaVer.jar "%TEST_TITLE% - Vatable" %VATABLE% "SELECT discounted_vatable_amount_value FROM TBL_TRANSACTION ORDER BY CREATED_DATE DESC LIMIT %NUMBER_OF_ITEMS_PROCESSED%"

java -jar ./AutomaVer.jar "%TEST_TITLE% - Vat Amount" %VAT_AMT% "SELECT tax_value FROM TBL_TRANSACTION ORDER BY CREATED_DATE DESC LIMIT %NUMBER_OF_ITEMS_PROCESSED%"


:: OR PROMO_ID = [Promo Code]

:: Validate Product Ordered
set COL_NAME=TBL_PRODUCT.SHORT_NAME 
:: TBL_PRODUCT.PRODUCT_NAME, ::TBL_BARCODE.CODE
set ITEM_NAME=BBHOTDOGSPICY 
:: Full Name of the Product, Barcode/GTIN of the Item Scanned

set QUERY="SELECT %COL_NAME% FROM TBL_TRANSACTION JOIN TBL_LINE_ITEM on TBL_LINE_ITEM.TRANSACTION_ID = TBL_TRANSACTION.id JOIN TBL_PRODUCT ON TBL_PRODUCT.ID = TBL_LINE_ITEM.PRODUCT_ID ORDER BY TBL_TRANSACTION.CREATED_DATE DESC LIMIT %NUMBER_OF_ITEMS_PROCESSED%"
echo %QUERY%

java -jar ./AutomaVer.jar "%TEST_TITLE% - Product Name" "%ITEM_NAME%" %QUERY%

:: Use this script when using Barcode
:: JOIN TBL_BARCODE ON TBL_PRODUCT.ID = TBL_BARCODE.CODE