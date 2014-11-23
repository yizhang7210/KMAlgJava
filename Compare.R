origPath <- '/home/yzhang/00ME/Education/UW/CS860/JavaImp/LLVMProcessed.csv';
estiPath <- '/home/yzhang/00ME/Education/UW/CS860/JavaImp/LLVMEstimated.csv';

origTable <- as.matrix(read.csv(origPath, sep = "", header = F, skip = 1));
estiTable <- as.matrix(read.csv(estiPath, sep = "", header = F, skip = 1));

n <- ncol(origTable) - 1;
noObs <- nrow(origTable);

# The estimation, h(x):
plot(estiTable[,n+1], type = 'l', xlab = 'x', ylab = 'h(x)', col = 4, );

# The real, f(x):
lines(origTable[,n+1], col = 2);

# Titles and legends and others:
legend('bottomright', legend = c("Estimated h(x)", "Real f(x)"), 
       lwd = c(2.5, 2.5), col = c(4,2));
error <- mean(abs((origTable[, n+1] - estiTable[, n+1])/origTable[ ,n+1]));
