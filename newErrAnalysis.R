
systems <- c("ApacheX264", "LLVMX264", "X2642", "LLVM2");
sysNum <- 4;
sys <- systems[sysNum];

errPath <- paste(sys, '/Results.csv', sep='');

expErr <- as.matrix(read.csv(errPath, sep = "", header = F, skip = 1));

aves <- apply(expErr, 1, mean);
maxs <- apply(expErr, 1, max);

print("The average errors are:");
print(aves);

print("The maximum errors are:");
print(maxs);