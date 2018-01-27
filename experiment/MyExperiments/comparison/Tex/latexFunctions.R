#############################################################
#		ExecuteJMetal 	::R functions::
#
#	Author: Lucas Prestes		lucas.prestes.lp@gmail.com
#
#############################################################
#			LATEX FUNCTIONS
#############################################################

latexCreate <- function(name){
	write("", name,append=FALSE)
	return (name)
}


latexHeader <- function(OutputFile) {
  write("\\documentclass{article}\n", OutputFile, append=TRUE)
  write("\\usepackage{amssymb}", OutputFile, append=TRUE)
  write("\\usepackage{colortbl}", OutputFile, append=TRUE)
  write("\\usepackage[table*]{xcolor}", OutputFile, append=TRUE)
  write("\\xdefinecolor{gray95}{gray}{0.65}", OutputFile, append=TRUE)
  write("\\xdefinecolor{gray25}{gray}{0.8}\n", OutputFile, append=TRUE)
  write("\\title{Tests}", OutputFile, append=TRUE)
  write("\\author{A.J.Nebro and Lucas Prestes}", OutputFile, append=TRUE)
  write("\\begin{document}", OutputFile, append=TRUE)
  write("\\maketitle\n", OutputFile, append=TRUE)
  write("\\section{Tables}\n", OutputFile, append=TRUE)
}



latexNewSection <- function(file, section){
  write(paste("\\section{",section,"}\n",sep=""), file, append=TRUE)
}


latexTail <- function(file) { 
  write("\\end{document}", file, append=TRUE)
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
  write("\\end{table}\n\n", file, append=TRUE)
}


latexWinnersTable <- function(file, problem, algorithms){
	instances <- setBenchmark(problem)
	winners <- countWinners(algorithms,instances)
	caption <- paste("Winners in \\emph{benchmark} ",problem,sep="")
	label <- paste("Table:winners.",problem, sep="")
	#latexTableHeader(file, problem, caption, label, "llll","Algorithm & HV & IGD & $\\epsilon$\\\\")
	latexTableHeader(file,"HV IGD and EP", caption, label, "llll","Algorithm & HV & IGD & $\\epsilon$\\\\")
	for(w in 1:ncol(winners)){
		best <- c(winners[w,2]==max(winners[,2]),winners[w,3]==max(winners[,3]),winners[w,4]==max(winners[,4]))
		latexTableLine(file,winners[w,], best)
	}
	latexTableTail(file)
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


