#############################################################
#		ExecuteJMetal 	::R functions::
#
#	Author: Lucas Prestes		lucas.prestes.lp@gmail.com
#
#
#
# 	#### R functions of JMetal (modified)	####
#
#
#############################################################


JMetalBoxplot <- function(algorithms, indicator, problem){
	data <- matrix(nrow=50,ncol=length(algorithms))
	i<-1
	for(algorithm in algorithms){
		file <- paste("../data/",algorithm,"/",problem,"/",indicator, sep="")
		data[,i] <- c(scan(file))
		i=i+1
	}

	boxplot(data,names=algorithms, 
					notch = TRUE, 
					use.cols=TRUE,#use boxplot with matrix
					cex.axis=0.5)#,xaxt='n',xlab="")
	#axis(1, at=seq(1, length(algorithms), by=1), labels = FALSE)
	#text(seq(1, length(algorithms), by=1), par("usr")[4] - 0.27, labels = algorithms, srt = 45, pos = 1, xpd = TRUE,cex=0.7)

	titulo <-paste(indicator, problem, sep=":")
	title(main=titulo)

}


