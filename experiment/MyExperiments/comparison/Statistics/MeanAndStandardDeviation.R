##############################################################
#		ExecuteJMetal 	::R functions::
#
#	Author: Lucas Prestes		lucas.prestes.lp@gmail.com
#
##############################################################

meanAndStandardDeviationTableIndicators <- function(OutputFile, algorithms, problems, indicators){
	roundingMEAN <- 5
	roundingSD <- 4
	

	tabularString <- "l"
	latexTableFirstLine <- "\\hline"
	

	for(algorithm in algorithms){
		tabularString <- paste(tabularString,"l",sep="")
		latexTableFirstLine <- paste(latexTableFirstLine," & ",algorithm,sep="")
	}

	latexTableFirstLine <- paste(latexTableFirstLine,"\\\\",sep="")
	latexTableHeader(OutputFile, "Mean and Standard Deviation","meanAndStDev.all", tabularString, latexTableFirstLine)

	for(indicator in indicators){
		
		write(paste("& \\textbf{\\small ",indicator,"}\\\\\n\\hline ",sep=""),OutputFile,append=TRUE)

		for(problem in problems){
			aux <- problem
			#	i<-1
			#	data <- matrix(nrow=50,ncol=length(algorithms))
			means <- c()
			sds <- c()

			for(algorithm in algorithms){
				file <- paste("../data/",algorithm,"/",problem,"/",indicator, sep="")
				#data[,i] <- c(scan(file))
				#i=i+1
				data <- c(scan(file))
				means <- c(means, mean(data))
				sds <- c(sds, mean(data))
			}

			rank <- rep(0,length(algorithms))
			if(identical(indicator, "HV")){
				rank <- rev(order(means)) #maxmize indicators		, decreasing=TRUE
	#			cat("Maximizer")
			}else{
				rank <- (order(means)) #to minimize indicators
	#			cat("Minimizer")
			}
	#		cat("\n",means)
	#		cat("\n",rank)
	#		cat("\n")

			for(i in 1:length(algorithms)){
				if(rank[1] == i){
					aux <- paste(aux, " & \\cellcolor{gray95}$",round(means[i] ,digits=roundingMEAN),"_{",round(sds[i],digits=roundingSD) , "}$",sep="")
				}else if(rank[2] == i){
					aux <- paste(aux, " & \\cellcolor{gray25}$",round(means[i] ,digits=roundingMEAN),"_{",round(sds[i],digits=roundingSD) , "}$",sep="")
				}else{
					aux <- paste(aux, " & $",round(means[i] ,digits=roundingMEAN),"_{",round(sds[i],digits=roundingSD) , "}$",sep="")
				}
			}
			aux<- paste(aux," \\\\",sep="")
			write(aux,OutputFile,append=TRUE)
		}
		write("\\hline",OutputFile,append=TRUE)
	}
	latexTableTail(OutputFile)
}



meanAndStandardDeviationTable <- function(OutputFile, algorithms, problems, indicator){
	roundingMEAN <- 5
	roundingSD <- 4
	

	tabularString <- "l"
	latexTableFirstLine <- "\\hline"
	

	for(algorithm in algorithms){
		tabularString <- paste(tabularString,"l",sep="")
		latexTableFirstLine <- paste(latexTableFirstLine," & ",algorithm,sep="")
	}

	latexTableFirstLine <- paste(latexTableFirstLine,"\\\\",sep="")
	latexTableHeader(OutputFile, indicator, "Mean and Standard Deviation", tabularString, latexTableFirstLine)

	for(problem in problems){
		aux <- problem
		#	i<-1
		#	data <- matrix(nrow=50,ncol=length(algorithms))
		means <- c()
		sds <- c()

		for(algorithm in algorithms){
			file <- paste("../data/",algorithm,"/",problem,"/",indicator, sep="")
			#data[,i] <- c(scan(file))
			#i=i+1
			data <- c(scan(file))
			means <- c(means, mean(data))
			sds <- c(sds, mean(data))
		}

		rank <- rep(0,length(algorithms))
		if(identical(indicator, "HV")){
			rank <- rev(order(means)) #maxmize indicators		, decreasing=TRUE
#			cat("Maximizer")
		}else{
			rank <- (order(means)) #to minimize indicators
#			cat("Minimizer")
		}
#		cat("\n",means)
#		cat("\n",rank)
#		cat("\n")

		for(i in 1:length(algorithms)){
			if(rank[1] == i){
				aux <- paste(aux, " & \\cellcolor{gray95}$",round(means[i] ,digits=roundingMEAN),"_{",round(sds[i],digits=roundingSD) , "}$",sep="")
			}else if(rank[2] == i){
				aux <- paste(aux, " & \\cellcolor{gray25}$",round(means[i] ,digits=roundingMEAN),"_{",round(sds[i],digits=roundingSD) , "}$",sep="")
			}else{
				aux <- paste(aux, " & $",round(means[i] ,digits=roundingMEAN),"_{",round(sds[i],digits=roundingSD) , "}$",sep="")
			}
		}
		aux<- paste(aux," \\\\",sep="")
		write(aux,OutputFile,append=TRUE)
	}
	latexTableTail(OutputFile)
}
