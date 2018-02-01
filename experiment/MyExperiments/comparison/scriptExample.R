

#################################################################
#			SCRIPT START HERE
#
#	These are examples of how to call the methods
#
#################################################################

source("main.R") #load these functions

par(mfrow=c(1, 3)) #set number of plot/page   #c(lines, columns)
#pdf("Rplot.pdf", width=15,height=11)#paper="A4")

#png("name.png", height=1800, width=1650, pointsize=11, res=230)

algorithms <- c("MOEADDRA","NSGAII","IBEA","UCBHybrid")
algorithms2 <- c("MOEADDRA","NSGAII","UCBHybrid")
instances2obj <- c("WFG1","WFG2","WFG4","WFG7","ZDT1","ZDT2","ZDT3","ZDT6")#setBenchmark("DTLZ")
instances3obj <- c("DTLZ1","DTLZ2","DTLZ3","DTLZ4","DTLZ7")#setBenchmark("DTLZ")

#latexMain(algorithms, "UF")



for(instance in instances2obj){
	png(paste(instance,".png",sep=""), height=800, width=1650, pointsize=11, res=230)
	par(mfrow=c(1, 3))

	linePlotEvolutionPT(instance,"HV",algorithms)
	#linePlotEvolution(instance,"Spread",algorithms)
	#objectivePoints(instance, algorithms)	
	JMetalBoxplot(algorithms, "HV", instance)
	JMetalBoxplot(algorithms2, "HV", instance)
}



for(instance in instances3obj){
	png(paste(instance,".png",sep=""), height=800, width=1650, pointsize=11, res=230)
	par(mfrow=c(1, 3))

	linePlotEvolutionPT(instance,"HV",algorithms)
	#linePlotEvolution(instance,"Spread",algorithms)
	#objectivePoints3D(instance, algorithms)	
	JMetalBoxplot(algorithms, "HV", instance)
	JMetalBoxplot(algorithms2, "HV", instance)
}




if(FALSE){
	instance <- "WFG2"
	linePlotEvolution(instance,"HV",algorithms)
	linePlotEvolution(instance,"Epsilon",algorithms)
	linePlotEvolution(instance,"IGD",algorithms)
	linePlotEvolution(instance,"Spread",algorithms)
	par(mfrow=c(1,1))
	objectivePoints(instance, algorithms)	
	#3D

	benchmark <- setBenchmark("ZDT")
	for(instance in benchmark){
	#	linePlotEvolution(instance,"HV",algorithms)
	}

	#benchmarks <- c("UF","LZ09","GLT","WFG","ZDT","DTLZ")
	#winnerTables(algorithms,benchmarks)

	par(mfrow=c(2,2)) #each page has 2x2 plots
	objectivePoints("WFG1", algorithms)	
	objectivePoints("WFG2", algorithms)

	objectivePoints("UF1", algorithms)
	objectivePoints("UF2", algorithms)
	objectivePoints("UF3", algorithms)
	objectivePoints("UF4", algorithms)
	objectivePoints("UF5", algorithms)
	objectivePoints("UF6", algorithms)
	objectivePoints("UF7", algorithms)
	objectivePoints3D("UF8", algorithms)
	objectivePoints3D("UF9", algorithms)
	objectivePoints3D("UF10", algorithms)

}


