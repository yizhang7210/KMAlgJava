isTest = F;
isHome = T;

if(isHome){
  setwd('/home/yzhang/00ME/Education/UW/CS860/JavaImp/');
}else{
  setwd('/home/y825zhan/00ME/CS860/JavaImp/');
}



if(isTest){
  origPath <- 'OrigTestFun.csv';
  estiPath <- 'TestEstimated.csv';
  
}else{
  
  origPath <- 'X264Processed.csv';
  estiPath <- 'X264Estimated.csv';
  
  origAllPath <- 'X264Complete.csv';
  estiAllPath <- 'X264CompleteEstimated.csv';
}

origCoefPath <- 'OrigCoefs.csv';
estiCoefPath <- 'TestCoefsEstimated.csv';

#==============================================================================
origTable <- as.matrix(read.csv(origPath, sep = "", header = F, skip = 1));
estiTable <- as.matrix(read.csv(estiPath, sep = "", header = F, skip = 1));

n <- ncol(origTable) - 1;
noObs <- nrow(origTable);


#=============================================================================
# Checking how it does outside of the sample
origAll <- as.matrix(read.csv(origAllPath, sep = "", header = F, skip = 1));
estiAll <- as.matrix(read.csv(estiAllPath, sep = "", header = F, skip = 1));

origNon <- rbind(origTable[,1:n], origAll[,1:n]);
estiNon <- rbind(estiTable[,1:n], estiAll[,1:n]);
nonOrd <- !(duplicated(origNon,fromLast = T) | duplicated(origNon));

origNon <- rbind(origTable, origAll)[nonOrd,];
estiNon <- rbind(estiTable, estiAll)[nonOrd,];

plot(estiNon[,n+1], type='l');




#==========================================================
# Checking how it does inside the sample
range = 1:noObs;

ord = order(origTable[,n+1], decreasing=T);
origVals = origTable[,n+1][ord];
estiVals = estiTable[,n+1][ord];


# The estimation, h(x):

minVal = min(estiVals[range], origVals[range]);
maxVal = max(estiVals[range], origVals[range]);

plot(estiVals[range]*noObs/2^n, type = 'l', xlab = 'x', ylab = 'h(x)', col = 4,
     ylim = c(minVal*0.95,maxVal*1.05));

# The real, f(x):
lines(origVals[range], col = 2);

# Titles and legends and others:
legend('topright', legend = c("Estimated h(x)", "Real f(x)"), 
       lwd = c(2.5, 2.5), col = c(4,2));
errors <- as.matrix(abs(origTable[, n+1] - estiTable[, n+1]*noObs/2^n)/abs(origTable[,n+1]));
maxerror <- max(errors);
minerror <- min(errors);
error <- mean(errors);
error2 <- sum((origTable[, n+1] - estiTable[, n+1])^2)/(2^n);

plot(errors[range], type='l', xlab = 'x', ylab = 'error at x', col = 4);


#=====================================================================
# Comparing Fourier coefficients

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


