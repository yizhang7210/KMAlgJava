
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
public class FourierTester {
    
    public FourierTester(){
        
    }
    
    public static void GenerateTestToFile(String fileName, int dim, int noObs){
        
        int total = (int) Math.pow(2, dim);
        
        if(noObs > total){
            noObs = total;
        }
        
        List<Integer> numList = new ArrayList<>(total);
        for(int i = 0; i < total;++i){
            numList.add(i, i);
        }
        
        Collections.shuffle(numList);
        
        try{
            PrintWriter writer = new PrintWriter(fileName);
            writer.print(noObs + " ");
            writer.println(dim + 1);
            for(int i = 0; i < noObs; ++i){
                double [] thisVec = FourierLearner.intToVec(numList.get(i), dim);
                for(int j = 0; j < dim; ++j){
                    writer.print((int) thisVec[j] + " ");
                }
                writer.println(2*Math.random()-1);
            }
            
            writer.close();

        }catch(IOException e){
            throw new RuntimeException(e);
        }
        
    }
    
    public static void GenerateTestFromFourierToFile(String fileName,
            int dim, int noObs, int sparcity){
        
        String origCoefs = "origFouriers.csv";
        
        FourierTester.GenerateTestToFile(origCoefs, dim, sparcity);
        FourierTester.GenerateTestToFile(fileName, dim, noObs);
        
        try{
            SimpleMatrix fCoefs = SimpleMatrix.loadCSV(origCoefs);
            FourierResult R = new FourierResult(fCoefs);
            
            R.estimateAllSample(fileName, fileName);
            
            SimpleMatrix fun = SimpleMatrix.loadCSV(fileName);
            
            double max = fun.extractVector(false, dim).elementMaxAbs();
            
            for(int i = 0; i < fun.numRows(); ++i){
                fun.set(i, dim, fun.get(i, dim)/max);
            }
            
            fun.saveToFileCSV(fileName);
            
            
            SimpleMatrix coefs = SimpleMatrix.loadCSV(origCoefs);
            
            for(int i = 0; i < coefs.numRows(); ++i){
                coefs.set(i, dim, coefs.get(i, dim)/max);
            }
            
            coefs.saveToFileCSV(origCoefs);

            
        }catch(IOException e){
            throw new RuntimeException(e);
        }
        

        
    }
    
}
