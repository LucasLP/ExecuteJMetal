
#return the index of max hypervolum of instance in algorithm
bestHV <- function(algorithm, instance){
	path <- paste("../data/",algorithm, "/",instance,"/HV", sep="")
	hv <- read.table(path, header=FALSE)
	hvMax <- which(hv == max(hv))
	return (list(algorithm, instance, hvMax, max(hv)))
}

#return the index of min IGD of instance in algorithm
bestIGD <- function(algorithm, instance){
	path <- paste("../data/",algorithm, "/",instance,"/IGD", sep="")
	igd <- read.table(path, header=FALSE)
	igdMin <- which(igd == min(igd))
	return (list(algorithm, instance, igdMin, min(igd)))
}

#return the index of min Epsilon of instance in algorithm
bestEP <- function(algorithm, instance){
	path <- paste("../data/",algorithm, "/",instance,"/EP", sep="")
	ep <- read.table(path, header=FALSE)
	epMin <- which(ep == min(ep))
	return (list(algorithm, instance, epMin, min(ep)))
}


bestIndicators <- function(algorithm, instance){
	pathHV <- paste("../data/",algorithm, "/",instance,"/HV", sep="")
	pathIGD <- paste("../data/",algorithm, "/",instance,"/IGD", sep="")
	pathEP <- paste("../data/",algorithm, "/",instance,"/EP", sep="")
	hv <- read.table(pathHV, header=FALSE)
	igd <- read.table(pathIGD, header=FALSE)
	ep <- read.table(pathEP, header=FALSE)
	hvMax <- which(hv == max(hv))
	igdMin <- which(igd == min(igd))
	epMin <- which(ep == min(ep))
	return (list(algorithm, max(hv),  min(igd),  min(ep)))
}





countWinnersHV <- function(algorithms, instances){
	winners <- data.frame(matrix(nrow=length(algorithms), ncol=2))
	colnames(winners) <- c("algorithm","wins")
	winners$algorithm <- algorithms
	winners$wins <- rep(0,length(algorithms)) #c(0,0,0,0)
	for(instance in instances){
		hvs <- list()
		df <- data.frame(matrix(nrow=length(algorithms), ncol=4))
		colnames(df) <- c("algorithm","instance","index","value")
		for (a in c(1:length(algorithms))){
			hv <- bestHV(algorithms[a],instance)
			df[a,]<-hv
		}
		index <- which(df$value == max(df$value))
		winners[index,2] <- winners[index,2]+1
	}

	return (winners)
}




countWinners <- function(algorithms, instances){
	winners <- data.frame(matrix(nrow=length(algorithms), ncol=4))
	colnames(winners) <- c("algorithm","hv","igd","ep")
	winners$algorithm <- algorithms
	winners$hv <- rep(0,length(algorithms)) #c(0,0,0,0)
	winners$igd <- rep(0,length(algorithms)) #c(0,0,0,0)
	winners$ep <- rep(0,length(algorithms)) #c(0,0,0,0)
	for(instance in instances){
		hvs <- list()
		df <- data.frame(matrix(nrow=length(algorithms), ncol=4))
		colnames(df) <- c("algorithm","hv","igd","ep")
		for (a in c(1:length(algorithms))){
			indicators <- bestIndicators(algorithms[a],instance)
			df[a,]<-indicators
		}
		index <- which(df$hv == max(df$hv))
		winners[index,2] <- winners[index,2]+1
		index <- which(df$igd == min(df$igd))
		winners[index,3] <- winners[index,3]+1
		index <- which(df$ep == min(df$ep))
		winners[index,4] <- winners[index,4]+1
	}

	return (winners)
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
		return (c("ZDT1", "ZDT2", "ZDT3", "ZDT4", "ZDT6"))
	}else if(benchmark=="DTLZ"){
		return (c("DTLZ1", "DTLZ2", "DTLZ3", "DTLZ4", "DTLZ6", "DTLZ7"))
	}
	return (c())
}





#################################################################
#			LATEX FUNCTIONS
#################################################################
latexCreate <- function(name){
	write("", name,append=FALSE)
	return (name)
}

latexHeader <- function(file) {
  write("\\documentclass{article}", file, append=TRUE)
  write("\\title{StandardStudy}", file, append=TRUE)
  write("\\usepackage{amssymb}", file, append=TRUE)
  write("\\usepackage{colortbl}", file, append=TRUE)
  write("\\usepackage[table*]{xcolor}", file, append=TRUE)
  write("\\xdefinecolor{gray95}{gray}{0.65}", file, append=TRUE)
  write("\\xdefinecolor{gray25}{gray}{0.8}", file, append=TRUE)
  write("\\author{name}\n", file, append=TRUE)
  write("\\begin{document}", file, append=TRUE)
  write("\\maketitle\n\n", file, append=TRUE)
}

latexNewSection <- function(file, section){
  write(paste("\\section{",section,"}\n",sep=""), file, append=TRUE)
}

latexTail <- function(file) { 
  write("\\end{document}", file, append=TRUE)
}

latexTableHeader <- function(file, problem, caption, label, tabularString, latexTableFirstLine) {
  write("\\begin{table}[!h]", file, append=TRUE)
  write(paste("\\caption{",caption,"} \\label{",label,"}", sep=""), file, append=TRUE)
  write("\\centering", file, append=TRUE)
  write("\\begin{scriptsize}", file, append=TRUE)
  write(paste("\\begin{tabular}{",tabularString,"}\n\\hline",sep=""), file, append=TRUE)
  write(latexTableFirstLine, file, append=TRUE)
  write("\\hline ", file, append=TRUE)
}

latexTableLine <- function(file, line, best){
	str <- paste(line[1]," & ",sep="")
	for(i in 1:(length(best)-1)){
		if(best[i]){
			str <- paste(str, "\\cellcolor{gray95}", line[i+1], " & ",sep="")
		}else{
			str <- paste(str, line[i+1], " & ",sep="")
		}
	}
	if(best[length(best)]){
		str <- paste(str, "\\cellcolor{gray95}", line[length(best)+1], "\\\\")
   }else{
		str <- paste(str, line[length(best)+1], "\\\\",sep="")
	}
	write(str, file, append=TRUE)
}

latexTableTail <- function(file) { 
  write("\\hline", file, append=TRUE)
  write("\\end{tabular}", file, append=TRUE)
  write("\\end{scriptsize}", file, append=TRUE)
  write("\\end{table}", file, append=TRUE)
}


latexWinnersTable <- function(file, problem, algorithms){
	instances <- setBenchmark(problem)
	winners <- countWinners(algorithms,instances)
	caption <- paste("Winners in \\emph{benchmark} ",problem,sep="")
	label <- paste("Table:winners.",problem, sep="")
	latexTableHeader(file, problem, caption, label, "llll","Algorithm & HV & IGD & $\\epsilon$\\\\")
	for(w in 1:ncol(winners)){
		best <- c(winners[w,2]==max(winners[,2]),winners[w,3]==max(winners[,3]),winners[w,4]==max(winners[,4]))
		latexTableLine(file,winners[w,], best)
	}
	latexTableTail(file)
}






#################################################################
#			SCATTER PLOT FUNCTIONS
#################################################################
library(scatterplot3d)
#install.packages("scatterplot3d", repos="http://R-Forge.R-project.org")

objectivePoints <- function(instanceName, algorithmsNames){
	pareto <- paste("../pareto_fronts/",instanceName,sep="")
	instance<- paste(instanceName,"/FUN0.tsv",sep="")
	algorithms <- c()
	for(i in 1:length(algorithmsNames) ){
		algorithms[[i]] <- read.table(paste("../data/",algorithmsNames[i],"/",instance,sep=""), header=FALSE, sep="")
	}

	pf <- read.table(paste(pareto,".pf",sep=""), header=FALSE, sep="")

	max_x <- max(pf[,1])
	min_x <- min(pf[,1])
	max_y <- max(pf[,2])
	min_y <- min(pf[,2])

	colors <- c("red", "blue","black","orange","green", "brown", "deepskyblue", "gray60","yellow")

	plot(pf[,1],pf[,2] ,col=colors[1], pch='.' , 		
		ylim=c(min_y,max_y), 
		xlim=c(min_x,max_x), 
		ann=FALSE)
	for(i in 1:length(algorithmsNames)){
		algorithm <- algorithms[[i]]
		points(algorithm[,1],algorithm[,2], col=colors[i+1], pch='.')
	}
	# Create a title bold/italic font
	title(main=paste("Valores dos objetivos para ", instanceName,sep=""), font.main=4)

	# Label the x and y axes
	title(xlab= "Objetivo 1")
	title(ylab= "Objetivo 2")
	# Create a legend
	legend("topright", 
			c("Pareto set", algorithmsNames),
			cex=0.5, 
			col=colors, 
			pch='.',
			lty=1);
}


# 3D plot - using scatterplot3d
objectivePoints3D <- function(instanceName, algorithmsNames){
	pareto <- paste("../pareto_fronts/",instanceName,sep="")
	instance<- paste(instanceName,"/FUN0.tsv",sep="")

	algorithms <- c()
	for(i in 1:length(algorithmsNames) ){
		algorithms[[i]] <- read.table(paste("../data/",algorithmsNames[i],"/",instance,sep=""), header=FALSE, sep="")
	}
	pf <- read.table(paste(pareto,".pf",sep=""), header=FALSE, sep="")

	max_y <- max(pf[,2])
	min_y <- min(pf[,2])

	max_x <- max(pf[,1])
	min_x <- min(pf[,1])

	max_z <- max(pf[,3])
	min_z <- min(pf[,3])

	colors <- c("red", "blue","black","orange","green", "brown", "deepskyblue", "gray60","yellow")

	s3d <- scatterplot3d(pf[,1],pf[,2],pf[,3], pch='.', angle=45, color=colors[1] ,# axes=FALSE, ann=FALSE  ,col=colors[1]
		main=paste("Valores dos objetivos para ", instanceName,sep=""), font.main=4, 	# Create a title bold/italic font
		xlab= "Objetivo 1",	# Label the x  y z axes
		ylab= "Objetivo 2",
		zlab= "Objetivo 3",
		ylim=c(min_y,max_y),
		xlim=c(min_x,max_x),
		zlim=c(min_z,max_z),
		box=FALSE);

	for(i in 1:length(algorithmsNames)){
		algorithm <- algorithms[[i]]
		s3d$points3d(algorithm[,1],algorithm[,2],algorithm[,3], col=colors[i+1], pch='.')
	}

	# Create a legend
	legend("topright", 
			c("Pareto set", algorithmsNames),
			cex = 0.5,
			col=colors, 
			pch='.',
			lty=1);
}

#################################################################
#			LINE PLOT ::EVOLUTION:: FUNCTIONS
#		- you need to save the historic of some indicator(s) to generate this plot
#		- example of save:
#		/experiment/MyExperiment/history/NSGAII/data_HV_UF1.dat
#		each line contains a vector of a test, where, each value is a HV at some step in the search
#################################################################
linePlotEvolution <- function(instance, indicator, algorithmsNames){
	# Read data file
	algorithms <- c()
	xmax <- 100 #default #number of steps in evolution process data
	# Define colors to be used
	plot_colors <- c("blue","black","orange","green", "brown", "deepskyblue", "gray60","yellow")

	zoommin <- 0		#plot only from this
	zoommax <- 99		#at this value
	for(i in 1:length(algorithmsNames) ){
		algorithm <- read.table(paste("../history/",algorithmsNames[i],"/data_",indicator,"_",instance,".dat",sep=""), header=T, sep="\t") 
		algorithm <- algorithm[,(-1:-zoommin)]# "zoom"at the line
		algorithm <- algorithm[,(-(zoommax-zoommin):-100)]#
		xmax <- length(algorithm)
		algorithms[[i]] <- algorithm
	}
	# Compute the max and min y 
	max_y <- max(unlist(lapply(algorithms,FUN=max)))
	min_y <- min(unlist(lapply(algorithms,FUN=min)))


	algorithm1mean <- c()
	algorithm1Q <- c()
	algorithm <- algorithms[[1]]
	for(n in 1:xmax){ 	#calcules mean, 1o quartil and 3 quartil
		algorithm1mean<-append(algorithm1mean,mean(algorithm[,n]))
		algorithm1Q <-rbind(algorithm1Q,quantile(algorithm[,n], c(0.25,0.75),type=1))#rbind is to merge, or, add new line in data frame
	}

	#FIRST ALGORITHM
	plot(algorithm1mean, type="o", pch='.', lty=1, col=plot_colors[1],  
		xaxt='n',#this axis will be described later
		ylim=c(min_y,max_y), # Make y axis 
		ann=FALSE)

	l = seq(0,zoommax,10)# points where is to plot
	lab = l+zoommin		# labels 
	axis(1,at=l,labels=lab)

	lines(algorithm1Q[,1], type="o", pch='.', lty=2, col=plot_colors[1])
	lines(algorithm1Q[,2], type="o", pch='.', lty=2, col=plot_colors[1])

	#OTHER ALGORITHMS
	for(i in 2:length(algorithmsNames)){
		algorithmMean <- c()		
		algorithmQ <- c()
		algorithm <- algorithms[[i]]
		for(n in 1:xmax){ #calcules mean, 1o quartil and 3 quartil
			algorithmMean<-append(algorithmMean,mean(algorithm[,n]))
			algorithmQ <-rbind(algorithmQ,quantile(algorithm[,n], c(0.25,0.75),type=1))#rbind is to merge, or, add new line in data frame
		}
		lines(algorithmMean, type="o", pch='.', lty=1, col=plot_colors[i])
		lines(algorithmQ[,1], type="o", pch='.', lty=2, col=plot_colors[i])
		lines(algorithmQ[,2], type="o", pch='.', lty=2, col=plot_colors[i])
	}

	grid(col="black")
	# Create box around plot
	box()
	# Create a title bold/italic font
	title(main=paste("Quality Indicator ",instance," for ",indicator,sep=""), font.main=4)
	# Label the x and y axes
	title(xlab= "% of Evolutions")
	title(ylab= "Quality Indicator value")
	# Create a legend
	legend("bottomright", algorithmsNames, cex=0.8, col=plot_colors, pch=21:23, lty=1:3);
}





# Pass the algorithms and benchmarks to put in the tables
winnerTables <- function(algorithms, benchmarks){
	file <- latexCreate("result_winner_tables.tex")
	latexHeader(file)
	latexNewSection(file, "Winner Tables")
	for(benchmark in benchmarks){
		latexWinnersTable(file, benchmark, algorithms)
	}
	latexTail(file)
}





#################################################################
#			SCRIPT START HERE
#################################################################

#source("functions.R") #load these functions


#par(mfrow=c(2,2))
algorithms <- c("MOEADDRA","UCBIrace","UCBIraceNew")
benchmark <- setBenchmark("ZDT")
#par(mfrow=c(2,2))
linePlotEvolution("ZDT1","HV",algorithms)
#for(instance in benchmark){
#	linePlotEvolution(instance,"HV",algorithms)
#	objectivePoints(instance, algorithms)	
#}







if(FALSE){
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



