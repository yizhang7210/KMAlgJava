
isHome = T;

if(isHome){
  setwd('/home/yzhang/00ME/Education/UW/CS860/JavaImp/');
}else{
  setwd('/home/y825zhan/00ME/CS860/JavaImp/');
}

systems <- c("Apache", "X264", "LLVM", "BDBC", "BDBJ", "Test");
sysNum <- 4;
sys <- systems[sysNum];

derivPath <- paste(sys, '/AllDerivs.csv', sep='');

derivs <- as.matrix(read.csv(derivPath, sep = ","));

n = ncol(derivs);

posRates <- matrix(0, n, 1);

for(i in 1:n){
  thisDerivs <- derivs[,i];
  
  thisDerivs <- thisDerivs[!is.na(thisDerivs)];
  
  if(length(thisDerivs) > 0){
    
    print("Positive rate is:");
    posRates[i] <- length(thisDerivs[thisDerivs>0])/length(thisDerivs);
    print(posRates[i]);
    
    plot(thisDerivs, type='p', cex=0.4);
    
    title(sprintf("Derivatives of %s along with Direcdtion %d", sys, i));
    
  }

}

posRates <- ifelse(posRates>0, posRates, 1-posRates)

print("Average Positive Rate is:");
print(mean(posRates))









