
isHome = T;

if(isHome){
  setwd('/home/yzhang/00ME/Education/UW/CS860/');
}else{
  setwd('/home/y825zhan/00ME/CS860/');
}

systems <- c("Apache", "X264", "LLVM", "BDBC", "BDBJ", "Test");
systems <- c("LLVM2", "LLVMX264");
sysNum <- 1;
sys <- systems[sysNum];

srcPath <- paste('JavaImp/',sys,'/rawFun.csv',sep='');
destPath <- paste('RImp/',sys,'TestAll.csv',sep='');

origFun <- as.matrix(read.csv(srcPath, sep = "", header = F, skip = 1));
n <- dim(origFun)[2] - 1;

inputs <- origFun[,1:n];
Performance <- origFun[,n+1];

newInputs <- ifelse(inputs==1, "Y", "N")
newFun <- cbind(newInputs, Performance)

write.csv(newFun, destPath, row.names=F, quote=F)


