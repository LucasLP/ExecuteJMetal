#############################################################
#		ExecuteJMetal 	::R functions::
#
#	Author: Lucas Prestes		lucas.prestes.lp@gmail.com
#
#############################################################
#			SCRIPT START HERE
#
#	These are examples of how to call the methods
#
#############################################################

source("../main.R") #load these functions

par(mfrow=c(2,4)) #set number of plot/page
#pdf("Rplot.pdf", width=15,height=11)#paper="A4")

algorithms <- c("MOEADDRA","NSGAII","IBEA")
instances2obj <- c("WFG1","WFG2","WFG4","WFG7","ZDT1","ZDT2","ZDT3","ZDT6")#c(setBenchmark("WFG"),setBenchmark("ZDT"))
instances3obj <- c("DTLZ1","DTLZ2","DTLZ3","DTLZ4","DTLZ7")#setBenchmark("DTLZ")



for(instance in instances2obj){
	linePlotEvolution(instance,"HV",algorithms)
	linePlotEvolution(instance,"Spread",algorithms)
	objectivePoints(instance, algorithms)	
	JMetalBoxplot(algorithms, "HV", instance)
}



for(instance in instances3obj){
	linePlotEvolution(instance,"HV",algorithms)
	linePlotEvolution(instance,"Spread",algorithms)
	objectivePoints3D(instance, algorithms)	
	JMetalBoxplot(algorithms, "HV", instance)
}



