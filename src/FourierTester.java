
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
        
        List<Integer> numList = new ArrayList<>((int) Math.pow(2, dim));
        for(int i = 0; i < (int) Math.pow(2, dim);++i){
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
                writer.println(200*Math.random());
            }
            
            writer.close();

        }catch(IOException e){
            throw new RuntimeException(e);
        }
        
    }
    
    public static void GenerateTestFromFourierToFile(String fileName,
            int dim, int noObs, int sparcity){
        
        FourierTester.GenerateTestToFile("origFouriers.csv", dim, sparcity);
        FourierTester.GenerateTestToFile(fileName, dim, noObs);
        
        try{
            SimpleMatrix fCoefs = SimpleMatrix.loadCSV("origFouriers.csv");
            FourierResult R = new FourierResult(fCoefs);
            
            R.estimateAllSample(fileName, fileName);
            
        }catch(IOException e){
            throw new RuntimeException(e);
        }
        

        
    }
    
}
