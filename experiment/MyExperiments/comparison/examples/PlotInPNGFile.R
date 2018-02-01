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
#		ATTENTION! The function "linePlotEvolution" only can by plotter with the data files, 
#						see the algorithm in java /ExecuteJMetal/src/org.uma.jmetal.util.experiment.component/GenerateEvolutionChart.java
#	
#############################################################
#the file must be executed in same directory of main.R
source("main.R") #load these functions

par(mfrow=c(1, 3)) #set number of plot/page   #c(lines, columns)

algorithms <- c("MOEADDRA","NSGAII","IBEA")

instances3obj <- c("DTLZ1","DTLZ2","DTLZ3","DTLZ4","DTLZ7")


for(instance in instances3obj){
	png(paste("./PlotInPNGFile/PlotInPNGFile_",instance,".png",sep=""), height=800, width=1650, pointsize=11, res=230)

	par(mfrow=c(1, 3))

	linePlotEvolution(instance,"HV",algorithms)

	JMetalBoxplot(algorithms, "HV", instance)

	objectivePoints3D(instance, algorithms)	
}
