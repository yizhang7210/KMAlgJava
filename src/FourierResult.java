
import java.util.Arrays;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author yzhang
 */
public class FourierResult {
    
    public double[][] fCoefs;
    public double[][] derivatives;
    
    
    public FourierResult(double[][] coefs){
        this.fCoefs = coefs;
        this.derivatives = this.getDerivatives(coefs);
    }
    
    private double[][] getDerivatives(double[][] fCoefs){
        
        return(fCoefs);
    }
    
    public double h(double[] x){
        
        double val = 0;
        
        int m = this.fCoefs.length;
        int n = this.fCoefs[0].length- 1;
        
        
        for(int i = 0; i < m;++i){
            
            double[] z = Arrays.copyOfRange(this.fCoefs[i], 0, n);
            
            val = val + Matrix.character(z, x)*this.fCoefs[i][n];
        }

        return(val);
    }
    
    public double estimateAllSample(String newName, String origFun){
        
        if(this.fCoefs.length == 0){
            return(Double.POSITIVE_INFINITY);
        }

        double[][] allSample = Matrix.read(origFun);

        int m = allSample.length;
        int n = allSample[0].length - 1;
        
        double[] errors = new double [m];

        for(int i = 0; i < m;++i){
            
            double [] input = Arrays.copyOfRange(allSample[i], 0, n);
            
            double oldVal = allSample[i][n];
            double newVal = this.h(input)*m/Math.pow(2, n);
            
            errors[i] = Math.abs(newVal - oldVal)/Math.abs(oldVal);
            
            allSample[i][n] = newVal;
        }

        Matrix.write(allSample, newName);

        return(Matrix.mean(errors));
    }
    
    
}
