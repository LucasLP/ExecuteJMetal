# Read data file
# Compute the max and min y 
max_y <- max()
min_y <- min()

# Define colors to be used
plot_colors <- c("blue","red","forestgreen", "gray60","yellow", "brown", "darkorange", "deepskyblue")

plot(algorithm1mean, type="o", col=plot_colors[1],  ylim=c(min_y,max_y), axes=FALSE, ann=FALSE, pch='.', lty=1)

lines(algorithm1Q[,1], type="o", pch='.', lty=2, col=plot_colors[1])
lines(algorithm1Q[,2], type="o", pch='.', lty=2, col=plot_colors[1])


# Make x,y axis 
axis(1, at=seq(0,100,5))
axis(2, at=seq(min_y, max_y, 0.05))

# Create box around plot
box()

# Create a title bold/italic font
title(main="Quality Indicator IGD for UF2", font.main=4)

# Label the x and y axes
title(xlab= "% of Evolutions")
title(ylab= "IGD value")

# Create a legend
legend("bottomright", c(), cex=0.8, col=plot_colors, pch=21:23, lty=1:3);