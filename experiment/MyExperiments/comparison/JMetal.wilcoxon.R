
resultDirectory<-"../data"


latexHeader <- function(OutputFile) {
  write("\\documentclass{article}", OutputFile, append=TRUE)
  write("\\title{TESTE}", OutputFile, append=TRUE)
  write("\\usepackage{amssymb}", OutputFile, append=TRUE)
  write("\\author{A.J.Nebro}", OutputFile, append=TRUE)
  write("\\begin{document}", OutputFile, append=TRUE)
  write("\\maketitle", OutputFile, append=TRUE)
  write("\\section{Tables}\n", OutputFile, append=TRUE)
}

latexTableHeader <- function(OutputFile,indicator, problem, tabularString, latexTableFirstLine) {
  write("\\begin{table}", OutputFile, append=TRUE)
  write(paste("\\caption{",problem,".",indicator,".}",sep=""), OutputFile, append=TRUE)
  write(paste("\\label{Table:wilcoxon.",problem,".",indicator,"}",sep=""), OutputFile, append=TRUE)

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
        write("$\\blacktriangle$", OutputFile, append=TRUE)
      }
      else {
        write("$\\triangledown$", OutputFile, append=TRUE) 
      }
    }
    else {
      write("$-$", OutputFile, append=TRUE) 
    }
  }
  else {
    write(" ", OutputFile, append=TRUE)
  }
}

latexTableTail <- function(OutputFile) { 
  write("\\hline", OutputFile, append=TRUE)
  write("\\end{tabular}", OutputFile, append=TRUE)
  write("\\end{scriptsize}", OutputFile, append=TRUE)
  write("\\end{table}", OutputFile, append=TRUE)
}

latexTail <- function(OutputFile) { 
  write("\\end{document}", OutputFile, append=TRUE)
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

	# Step 3. Problem loop 
	latexProblems <- ""
	for(problem in problems){
		latexProblems <- paste(latexProblems,problem," ",sep="")
	}
	latexTableHeader(OutputFile, indicator,latexProblems, tabularString, latexTableFirstLine)

	indx = 0
	for (i in algorithmList) {
	  if (i != algorithmList[length(algorithmList)]) {
		 write(i , OutputFile, append=TRUE)
		 write(" & ", OutputFile, append=TRUE)

		 jndx = 0
		 for (j in algorithmList) {
		   for (problem in problemList) {
		     if (jndx != 0) {
		       if (i != j) {
		         printTableLine(OutputFile, indicator, i, j, indx, jndx, problem)
		       }
		       else {
		         write("  ", OutputFile, append=TRUE)
		       } 
		       if (problem == problems[length(problems)]) {
		         if (j == algorithmList[length(algorithmList)]) {
		           write(" \\\\ ", OutputFile, append=TRUE)
		         } 
		         else {
		           write(" & ", OutputFile, append=TRUE)
		         }
		       }
		  else {
		 write("&", OutputFile, append=TRUE)
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

