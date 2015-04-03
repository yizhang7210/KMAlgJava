x <- list(0:1)
fun = expand.grid(rep(x, 6))

fun <- t(apply(fun, 1, rev))

vals1 <- c(4,-2,4,-2,4,-2,4,-2)
vals2 <- c(7,7,3,3,-3,-3,-7,-7)

vals <- rep(vals1, each=8) + rep(vals2, 8)

funVal <- cbind(fun, vals)

char <- function(z, x){
  return(1 - (sum(z*x) %% 2)*2)
}

getCoef <- function(z){
  sum <- 0
  for(i in 1:nrow(funVal)){
    
    input <- funVal[i,1:6]
    val <- funVal[i,7]
    
    sum <- sum + val*char(input, z)
  }
  
  return( sum/64)
}

coef <- apply(fun, 1, getCoef)

coefVal <- cbind(fun, coef)


f <- function(x){
  
  sum <- 0
  
  for(i in 1:nrow(coefVal)){
    
    add <- char(coefVal[i, 1:6], x)*coefVal[i,7]
    print(add)
    sum <- sum + add
  }
  
  return(sum)
  
}

x <- c(0,0,0,0,0,1)
f(x)

newVals <- apply(fun, 1, f)














