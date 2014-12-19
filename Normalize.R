systems <- c("Apache", "X264", "LLVM", "BDBC", "BDBJ");
sysNum <- 1;
sys <- systems[sysNum];


origPath <- paste(sys, "/rawFun.csv", sep='');
newFile <- paste(sys, "/origFun.csv", sep='');
estiFun <- paste(sys, "/estiFun.csv",sep='');
backPath <- paste(sys, "/estiRawFun.csv", sep='');

normalize <- function(origPath, newFile){
  origTable <- as.matrix(read.csv(origPath, sep = " ", header = F, skip = 1));
  
  n <- ncol(origTable) - 1;
  noObs <- nrow(origTable);
  
  ave <- mean(origTable[,n+1]);
  origTable[,n+1] <- origTable[,n+1]-ave;
  
  max <- max(abs(origTable[,n+1]));
  
  origTable[,n+1] <- origTable[,n+1]/max
  
  
  dims <- matrix(c(noObs, n+1), 1, 2);
  
  write.table(dims, newFile, sep =" ", row.names=F, col.names=F, eol='\n');
  
  write.table(origTable, newFile, append=T, sep = " ", row.names=F, col.names=F, eol='\n')
  
  print(sprintf("system: %s, mean: %f, max: %f", sys, ave, max));
  
  return(list(ave=ave, max=max));
}

denormalize <- function(estiFun, estiRawFun, params){
  
  origTable <- as.matrix(read.csv(estiFun, sep = " ", header = F, skip = 1));
  
  n <- ncol(origTable) - 1;
  noObs <- nrow(origTable);
  
  
  scale <- params$max;  
  origTable[,n+1] <- origTable[,n+1]*scale;
  
  shift <- params$ave;
  origTable[,n+1] <- origTable[,n+1]+shift;
  
  dims <- matrix(c(noObs, n+1), 1, 2);
  
  write.table(dims, estiRawFun, sep =" ", row.names=F, col.names=F, eol='\n');
  
  write.table(origTable, estiRawFun, append=T, sep = " ", row.names=F, col.names=F, eol='\n')
}

params <- normalize(origPath, newFile);

denormalize(estiFun, backPath, params);
