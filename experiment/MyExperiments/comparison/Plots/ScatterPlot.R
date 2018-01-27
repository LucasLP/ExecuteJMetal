#############################################################
#		ExecuteJMetal 	::R functions::
#
#	Author: Lucas Prestes		lucas.prestes.lp@gmail.com
#
#############################################################
#			SCATTER PLOT FUNCTIONS
#############################################################


library(scatterplot3d)
#install.packages("scatterplot3d", repos="http://R-Forge.R-project.org")




objectivePoints <- function(instanceName, algorithmsNames){
	pareto <- paste("../pareto_fronts/",instanceName,sep="")
	instance<- paste(instanceName,"/FUN0.tsv",sep="")
	algorithms <- c()
	for(i in 1:length(algorithmsNames) ){
		algorithms[[i]] <- read.table(paste("../data/",algorithmsNames[i],"/",instance,sep=""), header=FALSE, sep="")
	}

	pf <- read.table(paste(pareto,".pf",sep=""), header=FALSE, sep="")

	max_x <- max(pf[,1])
	min_x <- min(pf[,1])
	max_y <- max(pf[,2])
	min_y <- min(pf[,2])

	colors <- c("red", "blue","black","orange","green", "brown", "deepskyblue", "gray60","yellow")

	plot(pf[,1],pf[,2] ,col=colors[1], pch='.' , 		
		ylim=c(min_y,max_y), 
		xlim=c(min_x,max_x), 
		ann=FALSE)
	for(i in 1:length(algorithmsNames)){
		algorithm <- algorithms[[i]]
		points(algorithm[,1],algorithm[,2], col=colors[i+1], pch='.')
	}
	# Create a title bold/italic font
	title(main=paste("Valores dos objetivos para ", instanceName,sep=""), font.main=4)

	# Label the x and y axes
	title(xlab= "Objetivo 1")
	title(ylab= "Objetivo 2")
	# Create a legend
	legend("topright", 
			c("Pareto set", algorithmsNames),
			cex=0.5, 
			col=colors, 
			pch='.',
			lty=1);
}


# 3D plot - using scatterplot3d
objectivePoints3D <- function(instanceName, algorithmsNames){
	pareto <- paste("../pareto_fronts/",instanceName,sep="")
	instance<- paste(instanceName,"/FUN0.tsv",sep="")

	algorithms <- c()
	for(i in 1:length(algorithmsNames) ){
		algorithms[[i]] <- read.table(paste("../data/",algorithmsNames[i],"/",instance,sep=""), header=FALSE, sep="")
	}
	pf <- read.table(paste(pareto,".pf",sep=""), header=FALSE, sep="")

	max_y <- max(pf[,2])
	min_y <- min(pf[,2])

	max_x <- max(pf[,1])
	min_x <- min(pf[,1])

	max_z <- max(pf[,3])
	min_z <- min(pf[,3])

	colors <- c("red", "blue","black","orange","green", "brown", "deepskyblue", "gray60","yellow")

	s3d <- scatterplot3d(pf[,1],pf[,2],pf[,3], pch='.', angle=45, color=colors[1] ,# axes=FALSE, ann=FALSE  ,col=colors[1]
		main=paste("Valores dos objetivos para ", instanceName,sep=""), font.main=4, 	# Create a title bold/italic font
		xlab= "Objetivo 1",	# Label the x  y z axes
		ylab= "Objetivo 2",
		zlab= "Objetivo 3",
		ylim=c(min_y,max_y),
		xlim=c(min_x,max_x),
		zlim=c(min_z,max_z),
		box=FALSE);

	for(i in 1:length(algorithmsNames)){
		algorithm <- algorithms[[i]]
		s3d$points3d(algorithm[,1],algorithm[,2],algorithm[,3], col=colors[i+1], pch='.')
	}

	# Create a legend
	legend("topright", 
			c("Pareto set", algorithmsNames),
			cex = 0.5,
			col=colors, 
			pch='.',
			lty=1);
}
