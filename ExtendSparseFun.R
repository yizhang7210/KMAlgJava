isHome = T;

if(isHome){
  setwd('/home/yzhang/00ME/Education/UW/CS860/JavaImp/');
}else{
  setwd('/home/y825zhan/00ME/CS860/JavaImp/');
}

systems <- c("Apache", "X264", "LLVM", "BDBC", "BDBJ", "Test", "LLVM2");
sysNum <- 7;
sys <- systems[sysNum];

origPath <- paste(sys, '/origRawFun.csv', sep='');

origTable <- as.matrix(read.csv(origPath, sep = "", header = F, skip = 1));

newTable <- cbind(origTable[rep(1:1024, 1024),], origTable[rep(1:1024, each=1024),]);

newVals <- newTable[,11]+newTable[,22]

newTable <- cbind(newTable[,c(-11,-22)], newVals)

write.table(newTable, "rawFun.csv", row.names=F, col.names=F, quote=F, sep=" ")