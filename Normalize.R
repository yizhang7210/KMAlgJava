origPath <- 'X264/rawFun.csv';
newFile  <- 'X264/origFun.csv';

normalize <- function(origPath, newFile){
  origTable <- as.matrix(read.csv(origPath, sep = "", header = F, skip = 1));
  
  n <- ncol(origTable) - 1;
  noObs <- nrow(origTable);
  
  max <- max(origTable[,n+1])
  
  origTable[,n+1] <- origTable[,n+1]/max
  
  
  dims <- matrix(c(noObs, n+1), 1, 2);
  
  write.table(dims, newFile, sep =" ", row.names=F, col.names=F, eol='\n');
  
  write.table(origTable, newFile, append=T, sep = " ", row.names=F, col.names=F, eol='\n')
}

normalize(origPath, newFile);
