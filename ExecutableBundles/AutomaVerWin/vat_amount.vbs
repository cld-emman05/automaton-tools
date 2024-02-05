Dim BASE_TOTAL_AMOUNT
BASE_TOTAL_AMOUNT = CDbl(WScript.Arguments(0))
VATABLE = CDbl(WScript.Arguments(1))

Dim VAT_AMT
VAT_AMT = CDbl(BASE_TOTAL_AMOUNT - VATABLE)

' Round to 2 decimal places
VAT_AMT = Round(VAT_AMT, 2)

WScript.StdOut.WriteLine VAT_AMT