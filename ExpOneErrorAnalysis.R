isTest = F;
isHome = T;

if(isHome){
  setwd('/home/yzhang/00ME/Education/UW/CS860/JavaImp/');
}else{
  setwd('/home/y825zhan/00ME/CS860/JavaImp/');
}

numSys <- 5;
numRuns <- 20;

ts <- c(163, 383, 689, 587, 51716);
ns <- c(8,13,10,16,17)

#errPath <- paste(sys, '/expOneErr.csv', sep='');
errPath <- "ExpOneErrs/expOneErr.csv"

expOneErrors <- as.matrix(read.csv(errPath, sep = "", header = F, skip = 1));

for(i in 1:numSys){
  print(i)
  theoErr <- (log(2)*(ns[i]+1) + log(10))/50 * ts[i];
  thisRow <- expOneErrors[i,];
  expOneErrors[i,] <- thisRow[order(thisRow)]
  rightPercent <- length(thisRow[thisRow < theoErr])/numRuns
  print(sprintf("theoretical error is %f", theoErr))
  print(sprintf("percentage of runs below error is %f", rightPercent))
  ave <- mean(thisRow[1:15]);
  print(sprintf("average is %f", ave));
}




