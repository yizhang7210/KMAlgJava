library('hash')

isHome = T;

if(isHome){
  setwd('/home/yzhang/00ME/Education/UW/CS860/JavaImp/');
}else{
  setwd('/home/y825zhan/00ME/CS860/JavaImp/');
}

systems <- c("Apache", "X264", "LLVM", "BDBC", "BDBJ", "Test");
sysNum <- 1;
sys <- systems[sysNum];

origPath <- paste(sys, '/rawFun.csv', sep='');

origFun <- as.matrix(read.csv(origPath, sep = "", header = F, skip = 1));

n <- ncol(origFun) - 1;
noObs <- nrow(origFun);

fHash <- hash(apply(origFun[,1:n],1,paste,collapse=""),origFun[,(n+1)]);

h <- function(x){
  # Convert to string first:
  x <- paste(x,collapse="");
  # Try and get the value from the hash structure:
  v <- fHash[[x]];
  # Return the value if exist, specialValue if not:
  if(!is.null(v)){
    return(v);
  }else{
    return(NaN);
  }
}


newDim <- n-1;

derivFun <- matrix(0, 2^newDim, newDim+1);

for(i in 1:2^newDim){
  
  inputVec <- rev(as.integer(intToBits(i-1))[1:newDim]);
  
  derivFun[i,1:newDim] <- inputVec
  derivFun[i,(newDim+1)] <- h(c(1,inputVec)) - h(c(0,inputVec))
}


