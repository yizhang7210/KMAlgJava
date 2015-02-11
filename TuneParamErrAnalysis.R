
isHome = T;

if(isHome){
  setwd('/home/yzhang/00ME/Education/UW/CS860/JavaImp/');
}else{
  setwd('/home/y825zhan/00ME/CS860/JavaImp/');
}

#setwd('/home/yzhang/00ME/Education/UW/CS860/JavaImp/L2Error');

systems <- c("Apache", "X264", "LLVM", "BDBC", "BDBJ");
sysNum <- 4;
sys <- systems[sysNum];

sampleSizes <- rbind(c(9, 18, 27, 29), c(16, 32, 48, 81),
                     c(11, 22, 33, 62), c(18, 36, 54, 139),
                     c(26, 48, 52, 78));

#levels <- c(0,1,2,3,4,5,6,7,8,9,10);

l <- 30;

thetas <- seq(from=0,by=1/(2*l), length=l)

errFile <- paste(sys, "/allErrors.csv", sep='');
#errFile <- paste(sys,".csv",sep='');

allErr <- as.matrix(read.csv(errFile, sep=" ", header=F, skip = 1))
allErrMod <- allErr;

if(sysNum == 5){
  allErrMod[2,] <- allErr[4,]
  allErrMod[3,] <- allErr[2,]
  allErrMod[4,] <- allErr[3,]
  allErr <- allErrMod
}

minErr <- min(allErr[is.finite(allErr)]);
maxErr <- max(allErr[is.finite(allErr)]);
rowMinErr <- matrix(apply(allErr,1,min),nrow(allErr),1);
print("Minimum error for each sample size is:");
print(rowMinErr);

minIndex <- matrix(0,4,1)
for(i in 1:4){
  minIndex[i] <- match(rowMinErr[i], allErr[i,])
}

print("best theta")
print(thetas[minIndex[4]])

print(allErr[4,minIndex[4]])




