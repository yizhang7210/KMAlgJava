systems <- c("Apache", "X264", "LLVM", "BDBC");

sysNum <- 1;

sys <- systems[sysNum];

sampleSizes <- rbind(c(9, 18, 27, 29), c(16, 32, 48, 81),
                     c(11, 22, 33, 62), c(18, 36, 54, 139));
thetas <- seq(from=0.02,by=1/60, length=30)

errFile <- paste(sys, "/allErrors.csv", sep='');

allErr <- as.matrix(read.csv(errFile, sep=" ", header=F, skip = 1))

minErr <- min(allErr[is.finite(allErr)]);
maxErr <- max(allErr[is.finite(allErr)]);

#levels <- seq(minErr, maxErr-0.1, by=0.8);

filled.contour(sampleSizes[sysNum,], thetas, allErr,xlab='Number of samples', ylab='theta');
title(paste(sys, ": Error with different parameters"))