

JMetalWilcoxon <- function(algorithms,problems,indicator){
	wilcoxonMain(algorithms,problems,indicator)
}

resultDirectory<-"../data"


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
			cat("$\\blacktriangle$", file=OutputFile, append=TRUE)
      }
      else {
			cat("$\\triangledown$", file=OutputFile, append=TRUE)  
      }
    }
    else {
      cat("$-$", file=OutputFile, append=TRUE) 
    }
  }
  else {
    cat(" ", file=OutputFile, append=TRUE)
  }
}



##############################################################
#		ExecuteJMetal 	::R functions::
#
#	Author: Lucas Prestes		lucas.prestes.lp@gmail.com
#
##############################################################







wilcoxonMain <- function(algorithms,problems,indicator){
	### START OF SCRIPT 
	# Constants
	problemList <- problems
	algorithmList <- algorithms
	OutputFile <- paste(	"Wilcoxon.",indicator ,".tex",sep="")
	write("", OutputFile,append=FALSE)


	tabularString <-"| l | " 
	latexTableFirstLine <- "\\hline \\multicolumn{1}{|c|}{} "

	for(i in 1:(length(algorithms)-1)){
		for(p in 1:length(problems)){
			tabularString <- paste(tabularString,"p{0.03cm }",sep="")
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
		 cat(i," & ",file=OutputFile,append=TRUE)
		 jndx = 0
		 for (j in algorithmList) {
		   for (problem in problemList) {
		     if (jndx != 0) {
		       if (i != j) {
		         printTableLine(OutputFile, indicator, i, j, indx, jndx, problem)
		       } else {
					cat("  ", file=OutputFile, append=TRUE)
		       } 
		       if (problem == problems[length(problems)]) {
		         if (j == algorithmList[length(algorithmList)]) {
		           write(" \\\\ ", OutputFile, append=TRUE)
		         }else {
						cat(" & ", file=OutputFile, append=TRUE)
		         }
		       } else {
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





