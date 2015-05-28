library('rpart')

isHome = T;

if(isHome){
  setwd('/home/yzhang/00ME/Education/UW/CS860/JavaImp');
}else{
  setwd('/home/y825zhan/00ME/CS860/JavaImp');
}

systems <- c("Apache", "X264", "LLVM", "BDBC", "BDBJ", "Test");
numSamples <- c(150, 81, 62, 139, 48, 2402);

#systems <- c("LLVM2", "LLVMX264", "X2642", "Test2");
#numSamples <- c(25000, 18838, 20351);

sysNum <- 4;
sys <- systems[sysNum];
numSample <- numSamples[sysNum];

origPath <- paste(sys, '/normedFun.csv', sep='');

allSample <- as.matrix(read.csv(origPath, sep = "", header = F, skip = 1));

n <- ncol(allSample) - 1
numObs <- nrow(allSample)

allSample <- allSample[sample(numObs),];

trainSet <- allSample[1:numSample, 1:n];
trainVals <- allSample[1:numSample, (n+1)];

testSet <- allSample[(numSample+1):numObs, 1:n];
testVals <- allSample[(numSample+1):numObs,(n+1)];

control <- rpart.control(cp=0);
cartModel <- rpart(trainVals ~., as.data.frame(trainSet), control=control);

estiVals <- predict(cartModel, as.data.frame(testSet), type="vector");
errors <- estiVals - testVals

error <- mean(errors^2);
print(sprintf("Mean square error is: %f", error));


# Visualization ===========================================

#range = seq(1, length(testVals), by=1000);
range=1:length(testVals)


ord <- order(testVals, decreasing=T);
testVals <- testVals[ord];
estiVals <- estiVals[ord];

minVal = min(estiVals[range], testVals[range]);
maxVal = max(estiVals[range], testVals[range]);


plot(testVals[range], type = 'l', xlab = 'x', ylab = 'f(x)', col = 2,
     ylim = c(minVal*0.95,maxVal*1.05));

# The real, f(x):
lines(estiVals[range], col = 4);

# Titles and legends and others:
title('Original and Estimated Functions, CART')
legend('topright', legend = c("Estimated h(x)", "Real f(x)"), 
       lwd = c(2.5, 2.5), col = c(4,2));








