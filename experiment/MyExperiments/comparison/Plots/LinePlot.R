#############################################################
#		ExecuteJMetal 	::R functions::
#
#	Author: Lucas Prestes		lucas.prestes.lp@gmail.com
#
#############################################################
#			LINE PLOT ::EVOLUTION:: FUNCTIONS
#		- you need to save the historic of some indicator(s) to generate this plot
#		- example of save:
#		/experiment/MyExperiment/history/NSGAII/data_HV_UF1.dat
#		each line contains a vector of a test, where, each value is a HV at some step in the search
#############################################################




linePlotEvolution <- function(instance, indicator, algorithmsNames){
	# Read data file
	algorithms <- c()
	xmax <- 100 #default #number of steps in evolution process data
	# Define colors to be used
	plot_colors <- c("blue","black","orange","green", "brown", "deepskyblue", "gray60","yellow")

	zoommin <- 1#40		#plot only from this
	zoommax <- 100#99		#at this value
	for(i in 1:length(algorithmsNames) ){
		algorithm <- read.table(paste("../history/",algorithmsNames[i],"/data_",indicator,"_",instance,".dat",sep=""), header=T, sep="\t") 
		algorithm <- algorithm[,(-1:-zoommin)]# "zoom"at the line
		algorithm <- algorithm[,(-(zoommax-zoommin):-100)]#
		xmax <- length(algorithm)
		algorithms[[i]] <- algorithm
	}
	# Compute the max and min y 
	max_y <- max(unlist(lapply(algorithms,FUN=max)))
	min_y <- min(unlist(lapply(algorithms,FUN=min)))


	algorithm1mean <- c()
	algorithm1Q <- c()
	algorithm <- algorithms[[1]]
	for(n in 1:xmax){ 	#calcules mean, 1o quartil and 3 quartil
		algorithm1mean<-append(algorithm1mean,mean(algorithm[,n]))
		algorithm1Q <-rbind(algorithm1Q,quantile(algorithm[,n], c(0.25,0.75),type=1))#rbind is to merge, or, add new line in data frame
	}

	#FIRST ALGORITHM
	plot(algorithm1mean, type="o", pch='.', lty=1, col=plot_colors[1],  
		xaxt='n',#this axis will be described later
		ylim=c(min_y,max_y), # Make y axis 
		ann=FALSE)

	l = seq(0,zoommax,10)# points where is to plot
	lab = l+zoommin		# labels 
	axis(1,at=l,labels=lab)

	lines(algorithm1Q[,1], type="o", pch='.', lty=2, col=plot_colors[1])
	lines(algorithm1Q[,2], type="o", pch='.', lty=2, col=plot_colors[1])

	#OTHER ALGORITHMS
	for(i in 2:length(algorithmsNames)){
		algorithmMean <- c()		
		algorithmQ <- c()
		algorithm <- algorithms[[i]]
		for(n in 1:xmax){ #calcules mean, 1o quartil and 3 quartil
			algorithmMean<-append(algorithmMean,mean(algorithm[,n]))
			algorithmQ <-rbind(algorithmQ,quantile(algorithm[,n], c(0.25,0.75),type=1))#rbind is to merge, or, add new line in data frame
		}
		lines(algorithmMean, type="o", pch='.', lty=1, col=plot_colors[i])
		lines(algorithmQ[,1], type="o", pch='.', lty=2, col=plot_colors[i])
		lines(algorithmQ[,2], type="o", pch='.', lty=2, col=plot_colors[i])
	}

	grid(col="black")
	# Create box around plot
	box()
	# Create a title bold/italic font
	title(main=paste("Quality Indicator ",instance," for ",indicator,sep=""), font.main=4)
	# Label the x and y axes
	title(xlab= "% of Evolutions")
	title(ylab= "Quality Indicator value")
	# Create a legend
	legend("bottomright", algorithmsNames, cex=0.6, col=plot_colors, pch=21:23, lty=1:3);
}


#pt-br version
linePlotEvolutionPT <- function(instance, indicator, algorithmsNames){
	# Read data file
	algorithms <- c()
	xmax <- 100 #default #number of steps in evolution process data
	# Define colors to be used
	plot_colors <- c("blue","black","orange","green", "brown", "deepskyblue", "gray60","yellow")

	zoommin <- 1#40		#plot only from this
	zoommax <- 100#99		#at this value
	for(i in 1:length(algorithmsNames) ){
		algorithm <- read.table(paste("../history/",algorithmsNames[i],"/data_",indicator,"_",instance,".dat",sep=""), header=T, sep="\t") 
		algorithm <- algorithm[,(-1:-zoommin)]# "zoom"at the line
		algorithm <- algorithm[,(-(zoommax-zoommin):-100)]#
		xmax <- length(algorithm)
		algorithms[[i]] <- algorithm
	}
	# Compute the max and min y 
	max_y <- max(unlist(lapply(algorithms,FUN=max)))
	min_y <- min(unlist(lapply(algorithms,FUN=min)))


	algorithm1mean <- c()
	algorithm1Q <- c()
	algorithm <- algorithms[[1]]
	for(n in 1:xmax){ 	#calcules mean, 1o quartil and 3 quartil
		algorithm1mean<-append(algorithm1mean,mean(algorithm[,n]))
		algorithm1Q <-rbind(algorithm1Q,quantile(algorithm[,n], c(0.25,0.75),type=1))#rbind is to merge, or, add new line in data frame
	}

	#FIRST ALGORITHM
	plot(algorithm1mean, type="o", pch='.', lty=1, col=plot_colors[1],  
		xaxt='n',#this axis will be described later
		ylim=c(min_y,max_y), # Make y axis 
		ann=FALSE)

	l = seq(0,zoommax,10)# points where is to plot
	lab = l+zoommin		# labels 
	axis(1,at=l,labels=lab)

	lines(algorithm1Q[,1], type="o", pch='.', lty=2, col=plot_colors[1])
	lines(algorithm1Q[,2], type="o", pch='.', lty=2, col=plot_colors[1])

	#OTHER ALGORITHMS
	for(i in 2:length(algorithmsNames)){
		algorithmMean <- c()		
		algorithmQ <- c()
		algorithm <- algorithms[[i]]
		for(n in 1:xmax){ #calcules mean, 1o quartil and 3 quartil
			algorithmMean<-append(algorithmMean,mean(algorithm[,n]))
			algorithmQ <-rbind(algorithmQ,quantile(algorithm[,n], c(0.25,0.75),type=1))#rbind is to merge, or, add new line in data frame
		}
		lines(algorithmMean, type="o", pch='.', lty=1, col=plot_colors[i])
		lines(algorithmQ[,1], type="o", pch='.', lty=2, col=plot_colors[i])
		lines(algorithmQ[,2], type="o", pch='.', lty=2, col=plot_colors[i])
	}

	grid(col="black")
	# Create box around plot
	box()
	# Create a title bold/italic font
	title(main=paste("Evolução do ",indicator," : ",instance,sep=""), font.main=4)
	# Label the x and y axes
	title(xlab= "% das avaliações")
	title(ylab= paste("Valor do indicador de qualidade ",indicator,sep=""))
	# Create a legend
	legend("bottomright", algorithmsNames, cex=0.6, col=plot_colors, pch=21:23, lty=1:3);
}

