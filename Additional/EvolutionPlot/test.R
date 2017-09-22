# Read car and truck values from tab-delimited autos.dat
algorithms <- read.table("data.dat", header=T, sep="\t") 

# Compute the largest y value used in the data (or we could
# just use range again)
max_y <- max(algorithms)
min_y <- min(algorithms)

# Define colors to be used for alg1, alg2, alg3
plot_colors <- c("blue","red","forestgreen")

plot(algorithms$alg1, type="o", col=plot_colors[1],  ylim=c(min_y,max_y), axes=FALSE, ann=FALSE)

# Make x,y axis 
axis(1, at=1:11,lab=c(0,10,20,30,40,50,60,70,80,90,100))
axis(2, las=1, at=4*0:max_y)

# Create box around plot
box()

# Graph alg2 with red dashed line and square points
lines(algorithms$alg2, type="o", pch=22, lty=2, col=plot_colors[2])

# Graph alg3 with green dotted line and diamond points
lines(algorithms$alg3, type="o", pch=23, lty=3, col=plot_colors[3])

# Create a title bold/italic font
title(main="Quality Indicator Evolution", font.main=4)

# Label the x and y axes with dark green text
title(xlab= "Nº Evolutions")
title(ylab= "Indicator value")

# Create a legend
legend(1, max_y, names(algorithms), cex=0.8, col=plot_colors, pch=21:23, lty=1:3);
   

