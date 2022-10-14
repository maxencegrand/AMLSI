CC=pdflatex
SRC=$(wildcard *.tex)
REP_PDF=../../graph/htn
OBJ=$(SRC:.tex=.pdf)
LOG=1
all : alert $(OBJ) clean

alert:
	@echo "Genrate HTN Graph"

%.pdf: %.tex
	@echo "Compile $*"
	@if [ $(LOG) = 1 ]; then \
		$(CC) $< > /dev/null ; \
		$(CC) $< > /dev/null ; \
	else \
		$(CC) $< ; \
		$(CC) $< ; \
	fi
	@mv $@ $(REP_PDF)

clean:
	rm -f *.log *.aux *.log *.tex

mrproper: clean
	rm -f ${REP_PDF}/*.pdf
