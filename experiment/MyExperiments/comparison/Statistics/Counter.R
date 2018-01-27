#############################################################
#		ExecuteJMetal 	::R functions::
#
#	Author: Lucas Prestes		lucas.prestes.lp@gmail.com
#
#############################################################









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






##############################################################
#Operator history functions
##############################################################
countAll <- function(algorithm, instance){
	values <- read.table(paste("../ucbHistory/",algorithm,"/operators_",instance,".dat",sep=""), header=FALSE) 
	allvalues <- table(t(values[1,])) 
	for(i in 2:50){
		allvalues <- allvalues+table(t(values[i,]))
#		print(table(t(values[i,])))
	}
#	print(allvalues)
	return (allvalues)
}

countAllinBenchmark <- function(algorithm, benchmark){
	instances <- setBenchmark(benchmark)
	allvalues <- c(length=11)
	for(instance in instances){
		allvalues <- allvalues + countAll(algorithm, instance)	
	}
	print(allvalues)
	return (allvalues)
}

