Dim BASE_TOTAL_AMOUNT
BASE_TOTAL_AMOUNT = CDbl(WScript.Arguments(0))

Dim VATABLE
VATABLE = CDbl(BASE_TOTAL_AMOUNT / 1.12)

Dim VAT_AMT
VAT_AMT = CDbl(BASE_TOTAL_AMOUNT - VATABLE)

' Round to 2 decimal places
VATABLE = Round(VATABLE, 2)
VAT_AMT = Round(VAT_AMT, 2)

WScript.StdOut.WriteLine VATABLE
