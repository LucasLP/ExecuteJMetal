##############################################################
#		ExecuteJMetal 	::R functions::
#
#	Author: Lucas Prestes		lucas.prestes.lp@gmail.com
#
#	https://github.com/LucasLP/ExecuteJMetal
#
#	#### Functions avaible in this file: ####
#
#	loadData(algorithm, instance) #Return the data file from instance ant indicator
#	setBenchmark(benchmark)
#	bestHV(algorithm, instance)
#	bestIGD(algorithm, instance)
#	bestEP(algorithm, instance)
#	bestIndicators(algorithm, instance)
	source("./Statistics/Counter.R")
#		countWinners(algorithms, instances)
#		countAll(algorithm, instance)
#		countAllinBenchmark(algorithm, benchmark)
#
#
#
	source("./Statistics/MeanAndStandardDeviation.R")
#		meanAndStandardDeviationTable(OutputFile, algorithms, problems, indicator) #tex
#
	source("./Tex/latexFunctions.R")
#	#### LATEX FUNCTIONS #### 
#		latexCreate(file)
#		latexHeader(file)
#		latexNewSection(file, section)
#		latexTail(file)
#  	latexTableHeader(OutputFile,indicator, caption, tabularString, latexTableFirstLine)
#		latexTableLine(file, line, best)
#		latexTableTail(file)
#		latexWinnersTable(file, problem, algorithms)
#		winnerTables(algorithms, benchmarks) #this files generate a tex file of winner algorithms 
#
	source("./Plots/ScatterPlot.R")
#	#### POINT & LINE CHART ####
#		objectivePoints(instanceName, algorithmsNames)
#		objectivePoints3D(instanceName, algorithmsNames)
	source("./Plots/LinePlot.R")
#		linePlotEvolution(instance, indicator, algorithmsNames)
#
#
#
#
	source("./Statistics/Kruskal.R")
#  #### Kruskall-Wallis ####
#		KruskallWallisTest(algorithms, instance,indicator) #this will print in terminal the comparison of all algorithms
#
#	#### R functions of JMetal (modified) ####
#	JMetalBoxplot(algorithms, indicator, problem)
	source("./Statistics/Wilcoxon.R")
	source("./Plots/BoxPlot.R")
#
##############################################################


loadData <- function(algorithm, instance, indicator){
	path <- paste("../data/",algorithm, "/",instance,"/",indicator, sep="")
	return (read.table(path, header=FALSE))
}

#return the index of max hypervolum of instance in algorithm
bestHV <- function(algorithm, instance){
	hv <- loadData(algorithm, instance,"HV")
	hvMax <- which(hv == max(hv))
	return (list(algorithm, instance, hvMax, max(hv)))
}

#return the index of min IGD of instance in algorithm
bestIGD <- function(algorithm, instance){
	igd <- loadData(algorithm, instance,"IGD")
	igdMin <- which(igd == min(igd))
	return (list(algorithm, instance, igdMin, min(igd)))
}

#return the index of min Epsilon of instance in algorithm
bestEP <- function(algorithm, instance){
	ep <- loadData(algorithm, instance,"EP")
	epMin <- which(ep == min(ep))
	return (list(algorithm, instance, epMin, min(ep)))
}


bestIndicators <- function(algorithm, instance){
	hv <- loadData(algorithm, instance,"HV")
	igd <- loadData(algorithm, instance,"IGD")
	ep <- loadData(algorithm, instance,"EP")
	hvMax <- which(hv == max(hv))
	igdMin <- which(igd == min(igd))
	epMin <- which(ep == min(ep))
	return (list(algorithm, max(hv),  min(igd),  min(ep)))
}






#return instances of a benchmark
setBenchmark <- function(benchmark){
	if(benchmark=="UF"){
		return (c("UF1", "UF2", "UF3", "UF4", "UF5", "UF6", "UF7", "UF8", "UF9", "UF10"))
	}else if(benchmark=="GLT"){
		return (c("GLT1", "GLT2", "GLT3", "GLT4", "GLT5"))
	}else if(benchmark=="LZ09"){
		return (c("LZ09F1", "LZ09F2", "LZ09F3", "LZ09F4", "LZ09F5", "LZ09F6", "LZ09F7", "LZ09F8", "LZ09F9"))
	}else if(benchmark=="WFG"){
		return (c("WFG1", "WFG2", "WFG3", "WFG4", "WFG5", "WFG6", "WFG7", "WFG8", "WFG9"))
	}else if(benchmark=="ZDT"){
		return (c("ZDT1", "ZDT2", "ZDT3", "ZDT4", "ZDT6"))#ZDT5
	}else if(benchmark=="DTLZ"){
		return (c("DTLZ1", "DTLZ2", "DTLZ3", "DTLZ4", "DTLZ6", "DTLZ7"))#DTLZ5
	}else if(benchmark=="MOP"){
		return (c("MOP1", "MOP2", "MOP3", "MOP4", "MOP5", "MOP6", "MOP7"))
	}
	return (c())
}






