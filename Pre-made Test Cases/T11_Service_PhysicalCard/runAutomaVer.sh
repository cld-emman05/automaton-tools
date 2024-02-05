POS2_DIR=/opt/pos2
TEST_TITLE="Service Sales - Physical Card"
NUMBER_OF_ITEMS_PROCESSED=1

# Alter Table TBL_PROMO_TRANSACTION_COUNT Add USE_PROMO_SOLO_COUNT
echo "SELECT RECEIPT_NUMBER FROM PUBLIC.TBL_TRANSACTION ORDER BY CREATED_DATE DESC LIMIT $NUMBER_OF_ITEMS_PROCESSED" > result.sql
java -cp $POS2_DIR/maven-lib/h2-1.4.190.jar org.h2.tools.RunScript -url jdbc:h2:"/opt/pos2/posdb" -script result.sql -user sa -showResults | grep -oE "[0-9]+" > output.txt

# Read the output file content into an array
readarray -t receipt_numbers < output.txt

# Print the receipt numbers
for receipt in "${receipt_numbers[@]}"; do
   echo "$receipt"
done

rm -fv result.sql output.txt

# Input amount for each respective expected value
BASE_TOTAL_AMOUNT=300
PAYMENT_TENDER=300


echo "Total Amount: $BASE_TOTAL_AMOUNT";

echo Running AutomaVer for validating the matching data between the target value and the database value

java -jar ./AutomaVer.jar "$TEST_TITLE - Total Amount Due" $BASE_TOTAL_AMOUNT "SELECT computed_amount_value FROM TBL_TRANSACTION ORDER BY CREATED_DATE DESC LIMIT $NUMBER_OF_ITEMS_PROCESSED"

java -jar ./AutomaVer.jar "$TEST_TITLE - Base Total" $BASE_TOTAL_AMOUNT "SELECT transaction_amount_value FROM TBL_TRANSACTION ORDER BY CREATED_DATE DESC LIMIT $NUMBER_OF_ITEMS_PROCESSED"

java -jar ./AutomaVer.jar "$TEST_TITLE - Cash" $PAYMENT_TENDER "SELECT paid_amount_value FROM TBL_TRANSACTION ORDER BY CREATED_DATE DESC LIMIT $NUMBER_OF_ITEMS_PROCESSED"

java -jar ./AutomaVer.jar "$TEST_TITLE - Change" 0.00 "SELECT change_value FROM TBL_TRANSACTION ORDER BY CREATED_DATE DESC LIMIT $NUMBER_OF_ITEMS_PROCESSED"

java -jar ./AutomaVer.jar "$TEST_TITLE - Vatable" 0.00 "SELECT discounted_vatable_amount_value FROM TBL_TRANSACTION ORDER BY CREATED_DATE DESC LIMIT $NUMBER_OF_ITEMS_PROCESSED"

java -jar ./AutomaVer.jar "$TEST_TITLE - Vat Amount" 0.00 "SELECT tax_value FROM TBL_TRANSACTION ORDER BY CREATED_DATE DESC LIMIT $NUMBER_OF_ITEMS_PROCESSED"


# OR PROMO_ID = [Promo Code]

# Validate Product Ordered
COL_NAME=TBL_PRODUCT.SHORT_NAME # TBL_PRODUCT.PRODUCT_NAME, #TBL_BARCODE.CODE
ITEM_NAME="GLOBEPRPDCCP300" # Full Name of the Product, Barcode/GTIN of the Item Scanned

java -jar ./AutomaVer.jar "$TEST_TITLE - Product Name" "$ITEM_NAME" "SELECT $COL_NAME
FROM TBL_TRANSACTION
JOIN TBL_LINE_ITEM on TBL_LINE_ITEM.TRANSACTION_ID = TBL_TRANSACTION.id
JOIN TBL_PRODUCT ON TBL_PRODUCT.ID = TBL_LINE_ITEM.PRODUCT_ID
JOIN TBL_BARCODE ON TBL_PRODUCT.ID = TBL_BARCODE.CODE
ORDER BY TBL_TRANSACTION.CREATED_DATE DESC
LIMIT $NUMBER_OF_ITEMS_PROCESSED"