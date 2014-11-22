
import org.ejml.simple.SimpleMatrix;

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
    
    public SimpleMatrix fCoefs;
    public SimpleMatrix derivatives;
    
    
    public FourierResult(SimpleMatrix coefs){
        this.fCoefs = coefs;
        this.derivatives = this.getDerivatives(coefs);
    }
    
    private SimpleMatrix getDerivatives(SimpleMatrix fCoefs){
        
        return(fCoefs);
    }
    
    public double h(double[] x){
        
        double val = 0;
        
        int m = this.fCoefs.numRows();
        int n = this.fCoefs.numCols() - 1;
        
        
        for(int i = 0; i < m;++i){
            
            double [] input = new double[n];
            for(int j = 0; j < n; ++j){
                input[j] = this.fCoefs.get(i, j);
            }
            
            val = val + FourierLeaner.character(input, x)*this.fCoefs.get(i, n);
        }
        
        
        return(val);
    }
    
}
