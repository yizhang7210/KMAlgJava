
isHome = T;

if(isHome){
  setwd('/home/yzhang/00ME/Education/UW/CS860/JavaImp');
}else{
  setwd('/home/y825zhan/00ME/CS860/JavaImp');
}

systems <- c("Apache", "X264", "LLVM", "BDBC", "BDBJ");
ts <- c(51, 138, 309, 474, 39846);

sysNum <- 2;
sys <- systems[sysNum];
sp <- ts[sysNum];

origPath <- paste(sys, '/normedFun.csv', sep='');

origCoefPath <- paste(sys, '/normedCoef.csv', sep='');
sparsePath <- paste(sys, '/sparseCoef.csv', sep='');

origCoefs<- as.matrix(read.csv(origCoefPath, sep = "", header = F, skip = 1));

sparseCoefs <- origCoefs[1:sp,]

writeSparse <- function(){
  write.table(matrix(dim(sparseCoefs),1,2), sparsePath, row.names=F, col.names=F, quote=F, sep=" ");
  write.table(sparseCoefs, sparsePath, row.names=F, col.names=F, quote=F, sep=" ", append=T);
}

#writeSparse()










