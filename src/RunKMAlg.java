
import java.io.IOException;
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
        
        
        FourierLeaner K = new FourierLeaner("X264", sampleLocs[1]);
        
 
        SimpleMatrix fCoefs = K.learnByKM(1.2, 0.1);
          
        FourierResult R = new FourierResult(fCoefs);
        
        R.estimateAllSample("X264", sampleLocs[1]);
        
        fCoefs.print();

        
        // End timer:
        double duration = System.currentTimeMillis() - startTime;
        System.out.println("\nTime taken: " + duration/1000 + " seconds");
    }
    
}
