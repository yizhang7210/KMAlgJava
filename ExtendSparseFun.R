isHome = T;

if(isHome){
  setwd('/home/yzhang/00ME/Education/UW/CS860/JavaImp/');
}else{
  setwd('/home/y825zhan/00ME/CS860/JavaImp/');
}

systems <- c("Apache", "X264", "LLVM", "BDBC", "BDBJ", "Test")
#systems <- c("LLVM2", "LLVMX264", "Test2");

sys1 <- systems[2];
sys2 <- systems[2];
newSys <- "X2642";

origPath1 <- paste(sys1, '/rawFun.csv', sep='');
origPath2 <- paste(sys2, '/rawFun.csv', sep='');
writePath <- paste(newSys, '/rawFun.csv', sep='');

origTable1 <- as.matrix(read.csv(origPath1, sep = "", header = F, skip = 1));
origTable2 <- as.matrix(read.csv(origPath2, sep = "", header = F, skip = 1));

newTable <- cbind(origTable1[rep(1:nrow(origTable1), nrow(origTable2)),], 
                  origTable2[rep(1:nrow(origTable2), each=nrow(origTable1)),]);

newVals <- newTable[,ncol(origTable1)]+newTable[,ncol(newTable)];

newTable <- cbind(newTable[,c(-ncol(origTable1),-ncol(newTable))], newVals)
colnames(newTable) <- NULL

write.table(matrix(dim(newTable),1,2), writePath, row.names=F, col.names=F, quote=F, sep=" ");
write.table(newTable, writePath, row.names=F, col.names=F, quote=F, sep=" ", append=T);














