# Read car and truck values from tab-delimited autos.dat
algorithms <- read.table("data_UF2.dat", header=T, sep="\t") 

# Compute the max and min y 
max_y <- max(algorithms)
min_y <- min(algorithms)

# Define colors to be used for alg1, alg2, alg3
plot_colors <- c("blue","red","forestgreen", "black","yellow", "brown", "darkorange")
plot(algorithms$UCBv4, type="o", col=plot_colors[1],  ylim=c(min_y,max_y), axes=FALSE, ann=FALSE)

# Make x,y axis 
axis(1, at=1:10,lab=c(10,20,30,40,50,60,70,80,90,100))
axis(2, at=seq(min_y, max_y, 0.05))

# Create box around plot
box()

lines(algorithms$UCBv1, type="o", pch=1, lty=2, col=plot_colors[2])
lines(algorithms$UCBv3, type="o", pch=2, lty=2, col=plot_colors[3])

# Create a title bold/italic font
title(main="Quality Indicator Evolution for UF2", font.main=4)

# Label the x and y axes
title(xlab= "% of Evolutions")
title(ylab= "Indicator value")

# Create a legend
legend("bottomright", names(algorithms), cex=0.8, col=plot_colors, pch=21:23, lty=1:3);

