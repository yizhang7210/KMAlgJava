

f <- function(d,t){
  x = d^2/(16*t) + d + d^2/t + 2*d*sqrt(d+(d^2)/t)/(4*sqrt(t))
  
  return(x - 4*d/3)
}



ds <- seq(from=0,to=4,by=0.1);
ts <- 1:100

x <- outer(ds,ts,f)

print(x)