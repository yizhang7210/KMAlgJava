
isHome = T;

if(isHome){
  setwd('/home/yzhang/00ME/Education/UW/CS860/JavaImp/');
}else{
  setwd('/home/y825zhan/00ME/CS860/JavaImp/');
}

systems <- c("Apache", "X264", "LLVM", "BDBC", "BDBJ", "Test");
sysNum <- 6;
sys <- systems[sysNum];

derivPath <- paste(sys, '/AllDerivs.csv', sep='');

derivs <- as.matrix(read.csv(derivPath, sep = ","));

n = ncol(derivs);

for(i in 1:n){
  thisDerivs <- derivs[,i];
  
  thisDerivs <- thisDerivs[!is.na(thisDerivs)];
  
  if(length(thisDerivs) > 0){
    
    plot(thisDerivs, type='p', cex=0.4);
    
    title(sprintf("Derivatives of %s along with Direcdtion %d", sys, i));
    
  }

}









