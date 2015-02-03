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
nns <- c(9,16,11, 18, 26);
sizes <- c(29, 81, 62, 139, 48);

errPath <- 'expTwoErr.csv'
#errPath <- "ExpOneErrs/expTwoErr.csv"

expTwoErrors <- as.matrix(read.csv(errPath, sep = "", header = F, skip = 1));

exeTime <- expTwoErrors[,(numRuns+1),drop=F];
expTwoErrors <- expTwoErrors[,(1:numRuns),drop=F];

for(i in 1:numSys){
  print(i)
  theoErr <- 2*ts[i]*(log(2)*(ns[i]+1) + log(10))/sizes[i];
  thisRow <- expTwoErrors[i,];
  thisRow <- thisRow[order(thisRow)]
  expTwoErrors[i,] <- thisRow
  rightPercent <- length(thisRow[thisRow < theoErr])/numRuns
  print(sprintf("theoretical error is %f", theoErr))
  print(sprintf("percentage of runs below error is %f", rightPercent))
  ave <- mean(thisRow[1:15]);
  print(sprintf("average is %f", ave));
}






























# #errPath <- paste(sys, '/expOneErr.csv', sep='');
# errPath <- "expTwoErr.csv"
# 
# expTwoErrors <- as.matrix(read.csv(errPath, sep = "", header = F, skip = 1));
# 
# #exeTime <- expTwoErrors[,(numRuns+1),drop=F];
# expTwoErrors <- expTwoErrors[,(1:numRuns),drop=F];
# 
# for(i in 1:numSys){
#   thisRow <- expTwoErrors[i,];
#   expTwoErrors[i,] <- thisRow[order(thisRow)]
# }
# 
# for(i in 1:numSys){
#   print(i)
#   theoErr <- (log(2)*(ns[i]+1) + log(10))/50 * ts[i];
#   thisRow <- expOneErrors[i,];
#   expOneErrors[i,] <- thisRow[order(thisRow)]
#   rightPercent <- length(thisRow[thisRow < theoErr])/numRuns
#   print(sprintf("theoretical error is %f", theoErr))
#   print(sprintf("percentage of runs below error is %f", rightPercent))
#   ave <- mean(thisRow[!is.infinite(thisRow)]);
#   print(sprintf("average is %f", ave));
# }
# 
# aveErrs <- as.matrix(apply(expTwoErrors[,1:15], 1, mean), numSys, 1);
# 
# 
# print("The average is:")
# print(aveErrs)




