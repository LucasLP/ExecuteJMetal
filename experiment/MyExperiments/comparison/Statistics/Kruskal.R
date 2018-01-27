##############################################################
#		ExecuteJMetal 	::R functions::
#
#	Author: Lucas Prestes		lucas.prestes.lp@gmail.com
#
# 			#### Kruskal-Wallis Test	####
#
##############################################################
require(PMCMR) 


KruskallWallisTest <- function(algorithms, instance, indicator){
	values <- c()
	group <- c()
	for(algorithm in algorithms){
		values <- c(values, loadData(algorithm,instance,indicator))
		group <- c(group, rep(algorithm,50))# 50 is number of runs, number of elements in each sample
	}
	values <- unlist(values)
	group <- as.factor(group)

	kruskal.test(values, group)	

	out <- posthoc.kruskal.dunn.test(x=values,  g=group ,p.adjust.method="bonferroni")
	#out <- posthoc.kruskal.dunn.test(x=values, g=group, p.adjust.method="none")

	#out<- posthoc.kruskal.conover.test(x=values,g=group,p.adjust.method="bonferroni")
	#out <- posthoc.kruskal.conover.test(x=values, g=group, p.adjust.method="none")

	#out <- posthoc.kruskal.nemenyi.test(x=values, g=group, dist="Tukey")

	
	result <- c()
	index <- 1
	for(c in 1:(length(algorithms)-1) ){
		index <- index + (c-1)
		for(i in c:(length(algorithms)-1)){
			result <- c(result, paste(
				#"DEBUG[",c,",",i,"  ",index,"] ", #debug info
				colnames(out$p.value)[c]," = ",rownames(out$p.value)[i],
				"    ", out$statistic[index] < out$p.value[index], 
				#" ",out$statistic[index]," ", out$p.value[index],#debug info
				sep="")
			)
			index <- index+1
		}
	}
	
	#print(summary.lm(aov(values ~ group)))
	#print(summary(out))
	#print(out$statistic)

	#summary(out)[]#debug info
	cat("\nProblem: ",instance,"\n")
	cat(result,sep="\n")
	return (out)
}


printTableLineKruskal <- function(OutputFile,indicator, algorithm1, algorithm2, i, j, problem) { 
	#	values <- matrix(nrow=50,ncol=2)
	group <- c()
	#		values <- c(loadData(algorithm1,problem,indicator),loadData(algorithm2,problem,indicator))
	values1 <- scan(paste("../data/",algorithm1,"/",problem,"/",indicator, sep=""))#loadData(algorithm1,problem,indicator)
	values2 <- scan(paste("../data/",algorithm2,"/",problem,"/",indicator, sep=""))#loadData(algorithm2,problem,indicator)
	values <- unlist(c(values1,values2))
	cat("v1",mean(values1))
	cat("v2",mean(values2))

	group <- c(rep(algorithm1,50),rep(algorithm2,50))# 50 is number of runs, number of elements in each sample
	cat(algorithm1,"\t",algorithm2,"\n")
	#cat("value",	length(values)	,"\n")
	#cat("group",	length(group)	,"\n")

  if (i == j) {
    write("--", OutputFile, append=TRUE)
  }
  else if (i < j) {
	kruskal <- posthoc.kruskal.dunn.test(x=as.factor(values),  g=as.factor(group) ,p.adjust.method="bonferroni")
	#print(summary(kruskal)[])
   # if (is.finite(wilcox.test(data1, data2)$p.value) & wilcox.test(data1, data2)$p.value <= 0.05) {
	 if ( kruskal$statistic[1,1]<kruskal$p.value[1,1]){ 
#wilcox.test(data1, data2)$p.value <= 0.05) {
		cat("$-$", file=OutputFile, append=TRUE) 
    } else {
      #if (median(values1) >= median(values2)) {
		if (mean(values1) >= mean(values2)) {
			cat("$\\blacktriangle$", file=OutputFile, append=TRUE)
      } else {
			cat("$\\triangledown$", file=OutputFile, append=TRUE)  
      }
    }
  } else {
    cat(" ", file=OutputFile, append=TRUE)
  }
}
 
KruskallWallisMain <- function(algorithms,problems,indicator){
	### START OF SCRIPT 
	# Constants
	problemList <- problems
	algorithmList <- algorithms
	OutputFile <- paste( "KruskalWallis.",indicator ,".tex",sep="")
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
	caption <- "Kruskal-Wallis"
	for(problem in problems){
		caption <- paste(caption,problem," ",sep="")
	}
	latexTableHeader(OutputFile, indicator,caption, tabularString, latexTableFirstLine)

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
		         printTableLineKruskal(OutputFile,indicator, i, j, indx, jndx, problem)
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

}

