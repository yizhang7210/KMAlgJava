origPath <- '/home/y825zhan/00ME/CS860/JavaImp/X264Original.csv';
newFile  <- 'X264Processed.csv';


origTable <- as.matrix(read.csv(origPath, sep = "", header = F, skip = 1));

n <- ncol(origTable) - 1;
noObs <- nrow(origTable);

max <- max(origTable[,n+1])

origTable[,n+1] <- origTable[,n+1]/max


dims <- matrix(c(noObs, n+1), 1, 2);

write.table(dims, newFile, sep =" ", row.names=F, col.names=F, eol='\n');

write.table(origTable, newFile, append=T, sep = " ", row.names=F, col.names=F, eol='\n')