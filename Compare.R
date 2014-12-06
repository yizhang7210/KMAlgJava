origPath <- '/home/yzhang/00ME/Education/UW/CS860/JavaImp/TestFromFourier.csv';
estiPath <- '/home/yzhang/00ME/Education/UW/CS860/JavaImp/TestEstimated.csv';

#origPath <- '/home/y825zhan/00ME/CS860/JavaImp/Test.csv';
#estiPath <- '/home/y825zhan/00ME/CS860/JavaImp/TestEstimated.csv';

origTable <- as.matrix(read.csv(origPath, sep = "", header = F, skip = 1));
estiTable <- as.matrix(read.csv(estiPath, sep = "", header = F, skip = 1));

n <- ncol(origTable) - 1;
noObs <- nrow(origTable);

range = 1:300;

# The estimation, h(x):
plot(estiTable[range,n+1], type = 'l', xlab = 'x', ylab = 'h(x)', col = 4);

# The real, f(x):
lines(origTable[range,n+1], col = 2);

# Titles and legends and others:
legend('bottomright', legend = c("Estimated h(x)", "Real f(x)"), 
       lwd = c(2.5, 2.5), col = c(4,2));
error <- mean(abs((origTable[, n+1] - estiTable[, n+1])/origTable[ ,n+1]));


#=====================================================================
# Comparing Fourier coefficients

origCoefPath <- '/home/yzhang/00ME/Education/UW/CS860/JavaImp/origFouriers.csv';
estiCoefPath <- '/home/yzhang/00ME/Education/UW/CS860/JavaImp/TestFourierEstimated.csv';

origCoefs<- as.matrix(read.csv(origCoefPath, sep = "", header = F, skip = 1));
estiCoefs <- as.matrix(read.csv(estiCoefPath, sep = "", header = F, skip = 1));

origCoefs <- origCoefs[order(origCoefs[,n+1],decreasing=T),]
estiCoefs <- estiCoefs[order(estiCoefs[,n+1],decreasing=T),]


#================================================
# Plot
plot(estiCoefs[,n+1], type = 'l', xlab = 'x', ylab = 'h(x)', col = 4, );

# The real, f(x):
lines(origCoefs[,n+1], col = 2);

# Titles and legends and others:
legend('bottomright', legend = c("Estimated h(x)", "Real f(x)"), 
       lwd = c(2.5, 2.5), col = c(4,2));


