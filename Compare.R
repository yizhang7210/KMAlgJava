
isHome = T;

if(isHome){
  setwd('/home/yzhang/00ME/Education/UW/Projects/Performance-Prediction/JavaImp/');
}else{
  setwd('/home/y825zhan/00ME/CS860/JavaImp/');
}

systems <- c("Apache", "X264", "LLVM", "BDBC", "BDBJ", "Test");
systems <- c("LLVM2", "LLVMX264", "X2642", "Test2");


sysNum <- 1;
sys <- systems[sysNum];

origPath <- paste(sys, '/normedFun.csv', sep='');
estiPath <- paste(sys, '/estiNormedFun.csv', sep='');
#estiPath <- paste(sys, '/sparseFun.csv', sep='');

origCoefPath <- paste(sys, '/normedCoef.csv', sep='');
estiCoefPath <- paste(sys, '/estiRawCoef.csv', sep='');
#estiCoefPath <- paste(sys, '/sparseCoef.csv', sep='');

#==============================================================================
origTable <- as.matrix(read.csv(origPath, sep = "", header = F, skip = 1));
estiTable <- as.matrix(read.csv(estiPath, sep = "", header = F, skip = 1));

n <- ncol(origTable) - 1;
noObs <- nrow(origTable);


#==========================================================
# Compare raw function values:
range <- seq(1, noObs, by=500);

ord <- order(origTable[,n+1], decreasing=T);
origVals <- origTable[,n+1][ord];
estiVals <- estiTable[,n+1][ord];


# The estimation, h(x):

minVal <- min(estiVals[range], origVals[range]);
maxVal <- max(estiVals[range], origVals[range]);

# Plot and compare, the estimated first:
plot(estiVals[range], type = 'l', xlab = 'x', ylab = 'h(x)', col = 4,
     ylim = c(minVal*0.95,maxVal*1.05));

# The real, f(x):
lines(origVals[range], col = 2);

# Titles and legends and others:
title('Original and Estimated Function Comparison')
legend('topright', legend = c("Estimated h(x)", "Real f(x)"), 
       lwd = c(2.5, 2.5), col = c(4,2));
#errors <- as.matrix(abs(origTable[, n+1] - estiTable[, n+1])/abs(origTable[,n+1]));
errors <- as.matrix(abs((origVals - estiVals)/origVals));
maxerror <- max(errors);
minerror <- min(errors);
error <- mean(errors);
error2 <- mean((origTable[, n+1] - estiTable[, n+1])^2)#/sum(origTable[,n+1]^2);

plot(errors[range], type='l', xlab = 'x', ylab = 'error at x', col = 4);
title('Error at all points');

print(sprintf("relative error is: %f", error));
print(sprintf("L2 error is: %f", error2));

#=====================================================================
# Determine sparsity:

origCoefs<- as.matrix(read.csv(origCoefPath, sep = "", header = F, skip = 1));
estiCoefs <- as.matrix(read.csv(estiCoefPath, sep = "", header = F, skip = 1));

origCoefs <- origCoefs[do.call(order, as.data.frame(origCoefs)),]
if(nrow(estiCoefs) > 1){
  estiCoefs <- estiCoefs[do.call(order, as.data.frame(estiCoefs)),]
}

coefOrd <- order((origCoefs[,n+1])^2, decreasing=T);
orderedOrigCoefs <- origCoefs[coefOrd,n+1];
#orderedEstiCoefs <- estiCoefs[coefOrd,];
squaredCoefs <- orderedOrigCoefs^2;

mark <- sum(squaredCoefs)*0.99;
#mark <- 0.005;

i <- 1;
while(sum(squaredCoefs[1:i]) < mark){
#while(absCoefs[i] > mark){
  i <- i+1;
}

print("Number of coefficients having 99% weight:")
print(i)
print(i/(2^n));

print("Percentage");
print(sum(squaredCoefs[1:i])/sum(squaredCoefs));


#================================================
# Compare Fourier Coefficients:

# Real:
plot(origCoefs[,n+1], type = 'p', xlab = 'x', ylab = 'Coef(x)', col = 2);

# Estimate:
points(estiCoefs[,n+1], col = 4, cex=0.8);

# Titles and legends and others:
title('Original and Estimated Coefficients Comparison')
legend('topright', legend = c("Estimated h(x)", "Real f(x)"), 
       lwd = c(2.5, 2.5), col = c(4,2));

#=================================================
#Plot the ordered ones

# Original
plot(squaredCoefs, type='p',xlab='z',ylab='Fourier Coefficient of z squared', cex=0.8);

title(sprintf('%s: Actual Fourier Coefficients\n (squared in decreasing order)', sys))

 
#===============================================================
# Coefficients grouped by weight

coefByLevel <- matrix(0, 1, n+1);
for(i in 1:nrow(origCoefs)){
  coefByLevel[sum(origCoefs[i,1:n])] <- coefByLevel[sum(origCoefs[i,1:n])] + origCoefs[i,n+1]^2
}

plot(0:n, coefByLevel, type='o', xlab='Coefficients at level', ylab='Sum of coefficients squared');
title(paste(sys, ': Distribution of Fourier Coefficients by level', sep=""));


# 
# l <- rep(list(0:1), n)
# completeInput <- cbind(as.matrix(expand.grid(l)), -1);
# write.table(completeInput, "completeFun.csv", row.names=F, col.names=F, quote=F, sep=" ")
# 
# 








