outputDir=.
texlive
pdflatex --output-directory=$outputDir $1
#rm $outputDir/*.aux
#rm $outputDir/*.log
#rm $outputDir/*.out

cp doc.pdf ../dist
