
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
        
        String tmp = "TestFromFourier.csv";
        
        //FourierTester.GenerateTestToFile(tmp, 8, 200);
        
        int n = 15;
        double ep = 0.12, del = 0.1;
        
        int numSample = (int) Math.ceil(2/(ep*ep)*((n+1)*Math.log(2.0) + Math.log(1/del)));
        
        double err = Math.pow(2, n)*ep*ep;
        
        System.out.println("Guanranteed error is: " + err);
        System.out.println("Total number of combinations is: " + Math.pow(2, n));
        System.out.println("Number of samples is: " + numSample);
        
        FourierTester.GenerateTestFromFourierToFile(tmp, n, numSample, 100);
        
        //FourierLearner K = new FourierLearner("TestSys", tmp);
        //FourierLearner K = new FourierLearner("Apache", tmp);
        
        TrivialLearner L = new TrivialLearner("TestSys", tmp);
        
        SimpleMatrix fCoefs = L.learn(L.numObs, 100, true);
        
        String tmp2 = "TestFourierEstimated.csv";
        try{
            fCoefs.saveToFileCSV(tmp2);
        }catch(IOException e){
            throw new RuntimeException(e);
        }
        
        FourierResult R = new FourierResult(fCoefs);
        
        R.estimateAllSample("TestEstimated.csv", tmp);
        
        fCoefs.print();
        
        
        // End timer:
        double duration = System.currentTimeMillis() - startTime;
        System.out.println("\nTime taken: " + duration/1000 + " seconds");
    }
    
}