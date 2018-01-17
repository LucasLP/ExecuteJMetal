require(PMCMR) 




loadData <- function(algorithm, instance){
	path <- paste("../data/",algorithm, "/",instance,"/HV", sep="")#  
	return (read.table(path, header=FALSE))
}


KruskallWallisTest <- function(algorithms, instance){
	values <- c()
	group <- c()
	for(algorithm in algorithms){
		values <- c(values, loadData(algorithm,instance))
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
	for(c in 1:3){
		index <- index + (c-1)
		for(i in c:3){
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
}



source("functions.R")

algorithms <- c("UCBHybrid","NSGAII","MOEADDRA","IBEA")
instances <- setBenchmark("MOP")

#KruskallWallisTest(algorithms,"WFG1")
for(instance in instances){
	KruskallWallisTest(algorithms,instance)
}

