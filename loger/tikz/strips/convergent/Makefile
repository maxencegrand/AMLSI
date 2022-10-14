CC=pdflatex
SRC=$(wildcard *.tex)
REP_PDF=../../../graph/strips/convergent
OBJ=$(SRC:.tex=.pdf)
LOG=1
all : $(OBJ) clean

alert:
	@echo "Genrate STRIPS -- Convergent Graph"
	@python3 extractAllDomains.py
	@python3 tex_generator.py

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
