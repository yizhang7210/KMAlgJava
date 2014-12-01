
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.IntStream;
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
public class RunKMAlg {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        // TODO code application logic here
        // Start timer:
        long startTime = System.currentTimeMillis();
        
        String [] sampleLocs = {
            "/home/yzhang/00ME/Education/UW/CS860/JavaImp/ApacheProcessed.csv",
            "/home/yzhang/00ME/Education/UW/CS860/JavaImp/X264Processed.csv",
            "/home/yzhang/00ME/Education/UW/CS860/JavaImp/LLVMProcessed.csv"};
        
        String tmp = "Test.csv";
        
        FourierTester.GenerateTestToFile(tmp, 8, 200);
        
        FourierLeaner K = new FourierLeaner("Test", sampleLocs[0]);
        //FourierLeaner K = new FourierLeaner("Apache", tmp);
        
 
        SimpleMatrix fCoefs = K.learnByKM(1, 0.1);
          
        FourierResult R = new FourierResult(fCoefs);
        
        R.estimateAllSample("Test", tmp);
        
        fCoefs.print();

        
        // End timer:
        double duration = System.currentTimeMillis() - startTime;
        System.out.println("\nTime taken: " + duration/1000 + " seconds");
    }
    
}
