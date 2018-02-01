#############################################################
#		ExecuteJMetal 	::R functions::
#
#	Author: Lucas Prestes		lucas.prestes.lp@gmail.com
#
#############################################################
#the file must be executed in same directory of main.R

source("main.R")

algorithms <- c("MOEADDRA","NSGAII","IBEA")

latexMain(algorithms, "UF")


#output file is: ComparisonMain.tex
