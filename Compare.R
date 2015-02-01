isTest = F;
isHome = T;

if(isHome){
  setwd('/home/yzhang/00ME/Education/UW/CS860/JavaImp/');
}else{
  setwd('/home/y825zhan/00ME/CS860/JavaImp/');
}

systems <- c("Apache", "X264", "LLVM", "BDBC", "BDBJ");
sysNum <- 4;
sys <- systems[sysNum];

if(isTest){
  origPath <- paste(sys, '/origFun.csv', sep='');
  estiPath <- paste(sys, '/estiFun.csv', sep='');
  
}else{
  
  origPath <- paste(sys, '/rawFun.csv', sep='');
  estiPath <- paste(sys, '/estiRawFun.csv', sep='');
  
}

origCoefPath <- paste(sys, '/normedCoef.csv', sep='');
estiCoefPath <- paste(sys, '/estiCoef.csv', sep='');


#==============================================================================
origTable <- as.matrix(read.csv(origPath, sep = "", header = F, skip = 1));
estiTable <- as.matrix(read.csv(estiPath, sep = "", header = F, skip = 1));

n <- ncol(origTable) - 1;
noObs <- nrow(origTable);


#==========================================================
# Checking how it does inside the sample
range = 1:noObs;

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
title('Original and Estimated Function Comparison')
legend('topright', legend = c("Estimated h(x)", "Real f(x)"), 
       lwd = c(2.5, 2.5), col = c(4,2));
#errors <- as.matrix(abs(origTable[, n+1] - estiTable[, n+1])/abs(origTable[,n+1]));
errors <- as.matrix(abs((origVals - estiVals)/origVals));
maxerror <- max(errors);
minerror <- min(errors);
error <- mean(errors);
error2 <- sum((origTable[, n+1] - estiTable[, n+1])^2)/sum(origTable[,n+1]^2);

plot(errors[range], type='l', xlab = 'x', ylab = 'error at x', col = 4);
title('Error at all points');

#=====================================================================
#Comparing Fourier coefficients

origCoefs<- as.matrix(read.csv(origCoefPath, sep = "", header = F, skip = 1));
estiCoefs <- as.matrix(read.csv(estiCoefPath, sep = "", header = F, skip = 1));

origCoefs <- origCoefs[do.call(order, as.data.frame(origCoefs)),]
if(nrow(estiCoefs) > 1){
  estiCoefs <- estiCoefs[do.call(order, as.data.frame(estiCoefs)),]
}

coefOrd <- order(abs(origCoefs[,n+1]), decreasing=T);
orderedOrigCoefs <- origCoefs[coefOrd,n+1];
#orderedEstiCoefs <- estiCoefs[coefOrd,];
absCoefs <- abs(orderedOrigCoefs);

mark <- sum(absCoefs)*0.95;
#mark <- 0.005;

i <- 1;
while(sum(absCoefs[1:i]) < mark){
#while(absCoefs[i] > mark){
  i <- i+1;
}

print(i)
print(i/(2^n));

print("Percentage");
print(sum(absCoefs[1:600])/sum(absCoefs));


#================================================
# Plot
plot(origCoefs[,n+1], type = 'l', xlab = 'x', ylab = 'h(x)', col = 2);

# The real, f(x):
points(estiCoefs[,n+1], col = 4, cex=0.8);

# Titles and legends and others:
title('Original and Estimated Coefficients Comparison')
legend('topright', legend = c("Estimated h(x)", "Real f(x)"), 
       lwd = c(2.5, 2.5), col = c(4,2));

#=================================================
# Plot the ordered ones

# Original
plot(abs(orderedOrigCoefs), type='p',xlab='z',ylab='Fourier Coefficient of z', cex=0.8);

title(sprintf('%s: Actual Fourier Coefficients\n (absolute value in decreasing order)', sys))



# Estimate
#points(orderedEstiCoefs[,n+1], col=4,cex=0.8);

#title('Original and Estimated Coefficients in Decreasing Order');
#legend('topright', legend=c("Estimated", "Real"), lwd=c(2.5, 2.5), col=c(4,2));



# #=========================================================
# # Look at the original coefs ordered by weight
# coefOrd <- order(rowSums(origCoefs[,1:n]));
# orderedOrigCoefs <- origCoefs[coefOrd,];
# 
# coefRange <- 1:2^n;
# plot(orderedOrigCoefs[coefRange,n+1]^2, type='p',col=2,xlab='z',ylab='coef(z)',cex=0.4);
# title(paste(sys, ': Distribution of Fourier Coefficients', sep=""))
# 
# #===============================================================
# # Coefficients grouped by weight
# 
# sumCoef <- matrix(0,1,n+1);
# cut <- choose(n, 0:n);
# cuts <- cut;
# 
# #orderedCoefVals <- orderedOrigCoefs[,n+1];
# orderedCoefVals <- orderedOrigCoefs[,n+1]^2;
# 
# sumCoef[1] <- orderedCoefVals[1];
# 
# for(i in 2:(n+1)){
#   cuts[i] <- sum(cut[1:i]);
#   sumCoef[i] <- sum(orderedCoefVals[1:cuts[i]]) - sum(sumCoef[1:(i-1)]);
# }
# 
# plot(0:n, sumCoef, type='l', xlab='Coefficients at level', ylab='Sum of coefficients squared');
# title(paste(sys, ': Distribution of Fourier Coefficients by level', sep=""));

print(sprintf("error is: %f", error));

