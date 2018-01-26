
resultDirectory<-"../data"


latexHeader <- function(OutputFile) {
  write("\\documentclass{article}\n", OutputFile, append=TRUE)
  write("\\usepackage{amssymb}", OutputFile, append=TRUE)
  write("\\usepackage{colortbl}", OutputFile, append=TRUE)
  write("\\usepackage[table*]{xcolor}", OutputFile, append=TRUE)
  write("\\xdefinecolor{gray95}{gray}{0.65}", OutputFile, append=TRUE)
  write("\\xdefinecolor{gray25}{gray}{0.8}\n", OutputFile, append=TRUE)
  write("\\title{Wilcoxon Tests}", OutputFile, append=TRUE)
  write("\\author{A.J.Nebro and Lucas Prestes}", OutputFile, append=TRUE)
  write("\\begin{document}", OutputFile, append=TRUE)
  write("\\maketitle\n", OutputFile, append=TRUE)
  write("\\section{Tables}\n", OutputFile, append=TRUE)
}

latexTableHeader <- function(OutputFile,indicator, caption, tabularString, latexTableFirstLine) {
  write("\\begin{table}[!h]", OutputFile, append=TRUE)
  write(paste("\\caption{",caption,".",indicator,".}",sep=""), OutputFile, append=TRUE)
  write(paste("\\label{Table:wilcoxon.",indicator,"}",sep=""), OutputFile, append=TRUE)

  write("\\centering", OutputFile, append=TRUE)
  write("\\begin{scriptsize}", OutputFile, append=TRUE)
  write(paste("\\begin{tabular}{",tabularString,"}",sep=""), OutputFile, append=TRUE)
  write(latexTableFirstLine, OutputFile, append=TRUE)
  write("\\hline ", OutputFile, append=TRUE)
}

printTableLine <- function(OutputFile, indicator, algorithm1, algorithm2, i, j, problem) { 
  file1<-paste(resultDirectory, algorithm1, problem, indicator, sep="/")
  data1<-scan(file1)
  file2<-paste(resultDirectory, algorithm2, problem, indicator, sep="/")
  data2<-scan(file2)
  if (i == j) {
    write("--", OutputFile, append=TRUE)
  }
  else if (i < j) {
    if (is.finite(wilcox.test(data1, data2)$p.value) & wilcox.test(data1, data2)$p.value <= 0.05) {
      if (median(data1) >= median(data2)) {
        #write("$\\blacktriangle$", OutputFile, append=TRUE)
			cat("$\\blacktriangle$", file=OutputFile, append=TRUE)
      }
      else {
        #write("$\\triangledown$", OutputFile, append=TRUE)
			cat("$\\triangledown$", file=OutputFile, append=TRUE)  
      }
    }
    else {
      #write("$-$", OutputFile, append=TRUE) 
      cat("$-$", file=OutputFile, append=TRUE) 
    }
  }
  else {
    #write(" ", OutputFile, append=TRUE)
    cat(" ", file=OutputFile, append=TRUE)
  }
}

latexTableTail <- function(OutputFile) { 
  write("\\hline", OutputFile, append=TRUE)
  write("\\end{tabular}", OutputFile, append=TRUE)
  write("\\end{scriptsize}", OutputFile, append=TRUE)
  write("\\end{table}\n", OutputFile, append=TRUE)
}

latexTail <- function(OutputFile) { 
  write("\\end{document}", OutputFile, append=TRUE)
}





meanAndStandardDeviationTable2 <- function(OutputFile,algorithms, problems, indicator){
	roundingMEAN <- 4
	roundingSD <- 6	
	
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
		for(algorithm in algorithms){
			file<-paste(resultDirectory, algorithm, problem, indicator, sep="/")
			data<-scan(file)
			aux <- paste(aux, " & $",round(mean(data) ,digits=roundingMEAN),"_{",round(sd(data),digits=roundingSD) , "}$",sep="")
		}
		aux<- paste(aux," \\\\",sep="")
		write(aux,OutputFile,append=TRUE)
	}
	latexTableTail(OutputFile)
}





meanAndStandardDeviationTable <- function(OutputFile,algorithms, problems, indicator){
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
#	problem <- problems[1]
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
			cat("Maximizer")
		}else{
			rank <- (order(means)) #to minimize indicators
			cat("Minimizer")
		}
		cat("\n",means)
		cat("\n",rank)
		cat("\n")

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







wilcoxonMain <- function(algorithms,problems,indicator){
	### START OF SCRIPT 
	# Constants
	problemList <- problems
	algorithmList <- algorithms
	OutputFile <- "Wilcoxon"
	#for(algorithm in algorithms){
	#	OutputFile <- paste(	OutputFile, ".", algorithm,sep="")
	#}
	OutputFile <- paste(	OutputFile,".",indicator ,".tex",sep="")
	write("", OutputFile,append=FALSE)


	tabularString <-"| l | " 
	latexTableFirstLine <- "\\hline \\multicolumn{1}{|c|}{} "

	for(i in 1:(length(algorithms)-1)){
		for(p in 1:length(problems)){
			tabularString <- paste(tabularString,"p{0.05cm }",sep="")
		}
		tabularString <- paste(tabularString," | ",sep="")
		latexTableFirstLine <- paste(latexTableFirstLine, "& \\multicolumn{",length(problems),"}{c|}{",algorithms[i+1],"}",sep="")
	}

	latexTableFirstLine <- paste(latexTableFirstLine,"\\\\",sep="")


	 # Step 1.  Writes the latex header
	latexHeader(OutputFile)

	meanAndStandardDeviationTable(OutputFile,algorithms,problems,indicator)
	# Step 3. Problem loop 
	latexProblems <- ""
	for(problem in problems){
		latexProblems <- paste(latexProblems,problem," ",sep="")
	}
	latexTableHeader(OutputFile, indicator,latexProblems, tabularString, latexTableFirstLine)

	indx = 0
	for (i in algorithmList) {
	  if (i != algorithmList[length(algorithmList)]) {
		 #write(i , OutputFile, append=TRUE)
		 #write(" & ", OutputFile, append=TRUE)
		 cat(i," & ",file=OutputFile,append=TRUE)
		 jndx = 0
		 for (j in algorithmList) {
		   for (problem in problemList) {
		     if (jndx != 0) {
		       if (i != j) {
		         printTableLine(OutputFile, indicator, i, j, indx, jndx, problem)
		       }
		       else {
		         #write("  ", OutputFile, append=TRUE)
					cat("  ", file=OutputFile, append=TRUE)
		       } 
		       if (problem == problems[length(problems)]) {
		         if (j == algorithmList[length(algorithmList)]) {
		           write(" \\\\ ", OutputFile, append=TRUE)
		         } 
		         else {
		           #write(" & ", OutputFile, append=TRUE)
						cat(" & ", file=OutputFile, append=TRUE)
		         }
		       }
		  else {
#		 write("&", OutputFile, append=TRUE)
  		 cat("&", file=OutputFile, append=TRUE)
		  }
		     }
		   }
		   jndx = jndx + 1
		 }
		 indx = indx + 1
	  }
	} # for algorithm
   latexTableTail(OutputFile)

	#Step 3. Writes the end of latex file 
	latexTail(OutputFile)

}#end main

