isTest = F;
isHome = T;

if(isHome){
  setwd('/home/yzhang/00ME/Education/UW/CS860/JavaImp/');
}else{
  setwd('/home/y825zhan/00ME/CS860/JavaImp/');
}

systems <- c("Apache", "X264", "LLVM", "BDBC", "BDBJ");
dims <- c(8, 13, 10, 16, 17)
sparsities <- c(163, 383, 689, 587, 51716);

sysNum <- 5;
numRuns <- 10;

n <- dims[sysNum];
sys <- systems[sysNum];
t <- sparsities[sysNum];

#errPath <- paste(sys, '/expOneErr.csv', sep='');
errPath <- paste('ExpTwoErrs/',sys,'.csv', sep='');

expTwoErrors <- as.matrix(read.csv(errPath, sep = "", header = F, skip = 1));

exeTime <- expTwoErrors[2,1];
expTwoErrors <- expTwoErrors[1,,drop=F];

expTwoErrors <- expTwoErrors[,order(expTwoErrors), drop=F]

ave <- mean(expTwoErrors[!is.infinite(expTwoErrors)])

#theoErr <- (log(2)*(n+1) + log(10))/50 * t


#print("90 percentile is:")
#print(1 - length(expOneErrors[expOneErrors>theoErr])/numRuns)

print("The average is:")
print(ave)

#print("Error should be below:")
#print(theoErr)




