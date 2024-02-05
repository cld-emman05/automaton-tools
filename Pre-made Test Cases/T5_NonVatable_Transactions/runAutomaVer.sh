TEST_TITLE="NonVatable Transactions"

echo Running AutomaVer for validating the matching data between the target value and the database value

java -jar ./AutomaVer.jar "$TEST_TITLE - Total" 30.00 "SELECT computed_amount_value FROM TBL_TRANSACTION ORDER BY CREATED_DATE DESC LIMIT 1"

java -jar ./AutomaVer.jar "$TEST_TITLE - Subtotal" 30.00 "SELECT transaction_amount_value FROM TBL_TRANSACTION ORDER BY CREATED_DATE DESC LIMIT 1"

java -jar ./AutomaVer.jar "$TEST_TITLE - Cash" 30.00 "SELECT paid_amount_value FROM TBL_TRANSACTION ORDER BY CREATED_DATE DESC LIMIT 1"

java -jar ./AutomaVer.jar "$TEST_TITLE - Change" 0.00 "SELECT change_value FROM TBL_TRANSACTION ORDER BY CREATED_DATE DESC LIMIT 1"

java -jar ./AutomaVer.jar "$TEST_TITLE - Vatable" 26.79 "SELECT discounted_vatable_amount_value FROM TBL_TRANSACTION ORDER BY CREATED_DATE DESC LIMIT 1"

java -jar ./AutomaVer.jar "$TEST_TITLE - Vat Amount" 3.21 "SELECT tax_value FROM TBL_TRANSACTION ORDER BY CREATED_DATE DESC LIMIT 1"

java -jar ./AutomaVer.jar "$TEST_TITLE - Product Name" "Creamy Cheese Jumbo" "SELECT TBL_PRODUCT.PRODUCT_NAME
FROM TBL_TRANSACTION
JOIN TBL_LINE_ITEM on TBL_LINE_ITEM.TRANSACTION_ID = TBL_TRANSACTION.id
JOIN TBL_PRODUCT ON TBL_PRODUCT.ID = TBL_LINE_ITEM.PRODUCT_ID
ORDER BY TBL_TRANSACTION.CREATED_DATE DESC
LIMIT 1;"