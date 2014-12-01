
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
    
    public double h(SimpleMatrix x){
        
        double val = 0;
        
        int m = this.fCoefs.numRows();
        int n = this.fCoefs.numCols() - 1;
        
        
        for(int i = 0; i < m;++i){
            
            SimpleMatrix input = this.fCoefs.extractMatrix(i, i+1, 0, n);
            val = val + FourierLearner.character(input, x)*this.fCoefs.get(i, n);
        }

        return(val);
    }
    
    public void estimateAllSample(String newName, String sampleLoc){
        
        try{
            SimpleMatrix allSample = SimpleMatrix.loadCSV(sampleLoc);
            
            int m = allSample.numRows();
            int n = allSample.numCols() - 1;
            
            for(int i = 0; i < m;++i){
                SimpleMatrix input = allSample.extractMatrix(i, i+1, 0, n);
                
                allSample.set(i, n , this.h(input));
            }

            allSample.saveToFileCSV(newName);
            
        }catch(IOException e){
            throw new RuntimeException(e);
        }


    }
    
    
}
