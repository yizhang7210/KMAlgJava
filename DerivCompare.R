
isHome = T;

if(isHome){
  setwd('/home/yzhang/00ME/Education/UW/Projects/Performance-Prediction/JavaImp/');
}else{
  setwd('/home/y825zhan/00ME/CS860/JavaImp/');
}

systems <- c("Apache", "X264", "LLVM", "BDBC", "BDBJ", "Test");
#systems <- c("LLVM2", "LLVMX264", "X2642", "Test2");


sysNum <- 3;
sys <- systems[sysNum];

origPath <- paste(sys, '/origDerivs.csv', sep='');
estiPath <- paste(sys, '/estiDerivs.csv', sep='');

#==============================================================================
origTable <- as.matrix(read.csv(origPath, sep = "", header = F, skip = 1));
estiTable <- as.matrix(read.csv(estiPath, sep = "", header = F, skip = 1));

n <- ncol(origTable) - 1;
noObs <- nrow(origTable);


#==========================================================
# Compare derivatives:
#range <- seq(1, noObs, by=500);
range <- 1:noObs;

ord <- order(origTable[,n+1], decreasing=T);
origVals <- origTable[,n+1][ord];
estiVals <- estiTable[,n+1][ord];

#estiVals <- estiVals/3.5


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

