

numSys <- 5;
numRuns <- 20;

#ts <- c(163, 383, 689, 587, 51716);
#ts <- c(51, 138, 309, 474, 39846)
ns <- c(8,13,10,16,17)
ts <- 2^ns
sampleSizes <- c(29, 81, 62, 139, 48);
noObs <- c(192, 1152, 1024, 2560, 180);

#errPath <- paste(sys, '/expOneErr.csv', sep='');
errOnePath <- "expOneErr.good.csv"
errTwoPath <- "expTwoErr.csv"

expOneErrors <- as.matrix(read.csv(errOnePath, sep = "", header = F, skip = 1));
expTwoErrors <- as.matrix(read.csv(errTwoPath, sep = "", header = F, skip = 1))

# Error from Experiment 1 analysis:
for(i in 1:numSys){
  print(i)
  theoErr <- ((log(2)*(ns[i]+1) + log(10))/50 * ts[i])/(2^ns[i]);
  thisRow <- expOneErrors[i,];
  expOneErrors[i,] <- thisRow[order(thisRow)]
  rightPercent <- length(thisRow[thisRow < theoErr])/numRuns
  print(sprintf("theoretical error is %f", theoErr))
  print(sprintf("percentage of runs below error is %f", rightPercent))
  ave <- mean(thisRow);
  print(sprintf("average is %f", ave));
}

# # Error from Experiment 2 analysis:
# exeTime <- expTwoErrors[,(numRuns+1),drop=F];
# expTwoErrors <- expTwoErrors[,(1:numRuns),drop=F];
# 
# for(i in 1:numSys){
#   print(i)
#   theoErr <- 2*ts[i]*(log(2)*(ns[i]+1) + log(10))/noObs[i];
#   thisRow <- expTwoErrors[i,];
#   thisRow <- thisRow[order(thisRow)]
#   expTwoErrors[i,] <- thisRow
#   rightPercent <- length(thisRow[thisRow < theoErr])/numRuns
#   print(sprintf("theoretical error is %f", theoErr))
#   print(sprintf("percentage of runs below error is %f", rightPercent))
#   ave <- mean(thisRow[1:15]);
#   print(sprintf("average is %f", ave));
# }
# 



