# Read data file
algorithm1 <- read.table("../history/MOEADDRA/data_IGD_MOEADDRA_UF1.dat", header=T, sep="\t") 
algorithm2 <- read.table("../history/MOEADDRAUCB/data_IGD_MOEADDRAUCB_UF1.dat", header=T, sep="\t") 
algorithm3 <- read.table("../history/MOEADDRAUCBIrace/data_IGD_MOEADDRAUCBIrace_UF1.dat", header=T, sep="\t") 
# Compute the max and min y 
max_y <- max(algorithm1, algorithm2, algorithm3)
min_y <- min(algorithm1, algorithm2, algorithm3)

algorithm1mean <- c()
algorithm1Q <- c()

#calcules mean, 1o quartil and 3 quartil
for(n in 1:100){ 
	algorithm1mean<-append(algorithm1mean,mean(algorithm1[,n]))
	algorithm1Q <-rbind(algorithm1Q,quantile(algorithm1[,n], c(0.25,0.75),type=1))#rbind is to merge, or, add new line in data frame
}

algorithm2mean <- c()
algorithm2Q <- c()

#calcules mean, 1o quartil and 3 quartil
for(n in 1:100){ 
	algorithm2mean<-append(algorithm2mean,mean(algorithm2[,n]))
	algorithm2Q <-rbind(algorithm2Q,quantile(algorithm2[,n], c(0.25,0.75),type=1))#rbind is to merge, or, add new line in data frame
}

algorithm3mean <- c()
algorithm3Q <- c()

#calcules mean, 1o quartil and 3 quartil
for(n in 1:100){ 
	algorithm3mean<-append(algorithm3mean,mean(algorithm3[,n]))
	algorithm3Q <-rbind(algorithm3Q,quantile(algorithm3[,n], c(0.25,0.75),type=1))#rbind is to merge, or, add new line in data frame
}

# Define colors to be used
plot_colors <- c("blue","red","forestgreen", "gray60","yellow", "brown", "darkorange", "deepskyblue")

plot(algorithm1mean, type="o", col=plot_colors[1],  ylim=c(min_y,max_y), axes=FALSE, ann=FALSE, pch='.', lty=1)

lines(algorithm1Q[,1], type="o", pch='.', lty=2, col=plot_colors[1])
lines(algorithm1Q[,2], type="o", pch='.', lty=2, col=plot_colors[1])

lines(algorithm2mean, type="o", pch='.', lty=1, col=plot_colors[2])
lines(algorithm2Q[,1], type="o", pch='.', lty=2, col=plot_colors[2])
lines(algorithm2Q[,2], type="o", pch='.', lty=2, col=plot_colors[2])

lines(algorithm3mean, type="o", pch='.', lty=1, col=plot_colors[3])
lines(algorithm3Q[,1], type="o", pch='.', lty=2, col=plot_colors[3])
lines(algorithm3Q[,2], type="o", pch='.', lty=2, col=plot_colors[3])


# Make x,y axis 
axis(1, at=seq(0,100,5))
axis(2, at=seq(min_y, max_y, 0.05))

# Create box around plot
box()

# Create a title bold/italic font
title(main="Quality Indicator IGD for UF1", font.main=4)

# Label the x and y axes
title(xlab= "% of Evolutions")
title(ylab= "IGD value")

# Create a legend
legend("bottomright", c("MOEADDRA", "MOEADDRAUCB", "MOEADDRAUCBIrace"), cex=0.8, col=plot_colors, pch=21:23, lty=1:3);