origPath <- '/home/yzhang/00ME/Education/UW/CS860/JavaImp/OrigTestFun.csv';
estiPath <- '/home/yzhang/00ME/Education/UW/CS860/JavaImp/TestEstimated.csv';

#origPath <- '/home/y825zhan/00ME/CS860/JavaImp/TestFromFourier.csv';
#estiPath <- '/home/y825zhan/00ME/CS860/JavaImp/TestEstimated.csv';

origTable <- as.matrix(read.csv(origPath, sep = "", header = F, skip = 1));
estiTable <- as.matrix(read.csv(estiPath, sep = "", header = F, skip = 1));

n <- ncol(origTable) - 1;
noObs <- nrow(origTable);

range = 1:300;

ord = order(origTable[,n+1], decreasing=T);
origVals = origTable[,n+1][ord];
estiVals = estiTable[,n+1][ord];


# The estimation, h(x):

minVal = min(estiVals[range], origVals[range]);
maxVal = max(estiVals[range], origVals[range]);

plot(estiVals[range], type = 'l', xlab = 'x', ylab = 'h(x)', col = 4, 
     ylim = c(minVal*0.95,maxVal*1.05));

# The real, f(x):
lines(origVals[range], col = 2);

# Titles and legends and others:
legend('topright', legend = c("Estimated h(x)", "Real f(x)"), 
       lwd = c(2.5, 2.5), col = c(4,2));
errors <- as.matrix(abs(origTable[, n+1] - estiTable[, n+1])/abs(origTable[,n+1]));
maxerror <- max(errors);
minerror <- min(errors);
error <- mean(errors);
error2 <- sum((origTable[, n+1] - estiTable[, n+1])^2)/(2^n);

plot(errors[range], type='l', xlab = 'x', ylab = 'error at x', col = 4);


#=====================================================================
# Comparing Fourier coefficients

origCoefPath <- '/home/yzhang/00ME/Education/UW/CS860/JavaImp/OrigCoefs.csv';
estiCoefPath <- '/home/yzhang/00ME/Education/UW/CS860/JavaImp/TestCoefsEstimated.csv';

#origCoefPath <- '/home/y825zhan/00ME/CS860/JavaImp/origFouriers.csv';
#estiCoefPath <- '/home/y825zhan/00ME/CS860/JavaImp/TestFourierEstimated.csv';


origCoefs<- as.matrix(read.csv(origCoefPath, sep = "", header = F, skip = 1));
estiCoefs <- as.matrix(read.csv(estiCoefPath, sep = "", header = F, skip = 1));

origCoefs <- origCoefs[do.call(order, as.data.frame(origCoefs)),]
estiCoefs <- estiCoefs[do.call(order, as.data.frame(estiCoefs)),]

# # Order coefs may not make sense
# coefOrd <- order(origCoefs[,n+1],decreasing = T);
# origCoefs <- origCoefs[coefOrd,]
# estiCoefs <- estiCoefs[coefOrd,]


#================================================
# Plot
plot(estiCoefs[,n+1], type = 'l', xlab = 'x', ylab = 'h(x)', col = 4);

# The real, f(x):
lines(origCoefs[,n+1], col = 2);

# Titles and legends and others:
legend('bottomleft', legend = c("Estimated h(x)", "Real f(x)"), 
       lwd = c(2.5, 2.5), col = c(4,2));


