
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
#			SCRIPT START HERE
#################################################################

#source("functions.R") #load these functions
algorithms <- c("MOEADDRA","NSGAII","IBEA","UCBHybrid")

file <- latexCreate("result.tex")
latexHeader(file)
latexNewSection(file, "Winner Tables")

latexWinnersTable(file, "UF", algorithms)
latexWinnersTable(file, "LZ09", algorithms)
latexWinnersTable(file, "GLT", algorithms)
latexWinnersTable(file, "WFG", algorithms)
latexWinnersTable(file, "ZDT", algorithms)
latexWinnersTable(file, "DTLZ", algorithms)

latexTail(file)

