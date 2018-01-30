##############################################################
#		ExecuteJMetal 	::R functions::
#
#	Author: Lucas Prestes		lucas.prestes.lp@gmail.com
#
##############################################################



latexMain <- function(algorithms, benchmark){
	indicators <- c("HV","EP","IGD")#,"SPREAD")
	problems <- setBenchmark(benchmark)
	OutputFile <- latexCreate("ComparisonMain.tex")

	latexHeader(OutputFile)

	latexWinnersTable(OutputFile, benchmark, algorithms)

	meanAndStandardDeviationTableIndicators(OutputFile,algorithms,problems,indicators)

	for(indicator in indicators){
		latexNewSection(OutputFile, indicator)
		#meanAndStandardDeviationTable(OutputFile,algorithms,problems,indicator)
		wilcoxonTable(OutputFile,algorithms,problems,indicator)
		kruskalTable(OutputFile,algorithms,problems,indicator)
	}

	latexTail(OutputFile)	
}

latexMainOld <- function(algorithms, benchmark){
	indicators <- c("HV","EP","IGD","SPREAD")
	problems <- setBenchmark(benchmark)
	OutputFile <- latexCreate("ComparisonMain.tex")

	latexHeader(OutputFile)

	latexWinnersTable(OutputFile, benchmark, algorithms)

	for(indicator in indicators){
		latexNewSection(OutputFile, indicator)
		meanAndStandardDeviationTable(OutputFile,algorithms,problems,indicator)
		wilcoxonTable(OutputFile,algorithms,problems,indicator)
		kruskalTable(OutputFile,algorithms,problems,indicator)
	}

	latexTail(OutputFile)	
}
