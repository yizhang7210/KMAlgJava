
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

        String [] systems = {"Apache", "X264", "LLVM"};
        String [] sampleLocs = {
            "/home/yzhang/00ME/Education/UW/CS860/JavaImp/ApacheProcessed.csv",
            "/home/yzhang/00ME/Education/UW/CS860/JavaImp/X264Processed.csv",
            "/home/yzhang/00ME/Education/UW/CS860/JavaImp/LLVMProcessed.csv"};
        
        
        RunKMAlg.runOnTest(16, 0.05, 0.1, 40);
        
        //RunKMAlg.runOnData(systems[0], sampleLocs[0], 50, 5);
        
        
        // End timer:
        double duration = System.currentTimeMillis() - startTime;
        System.out.println("\nTime taken: " + duration/1000 + " seconds");
    }
    
    
    
    
    public static void runOnTest(int n, double ep, double del, int t){
        
        String origFun = "OrigTestFun.csv";
        String origCoef = "OrigCoefs.csv";
        String estiFun = "TestEstimated.csv";
        String estiCoef = "TestCoefsEstimated.csv";
      
        int numSample = (int) Math.ceil(2/(ep*ep)*((n+1)*Math.log(2) + Math.log(1/del)));
        
        double err = t*ep*ep;
        
        System.out.println("Guanranteed error is: " + err);
        System.out.println("Total number of combinations is: " + Math.pow(2, n));
        System.out.println("Number of requried samples is: " + numSample);

        FourierTester.GenerateTestFromFourierToFile(origCoef, origFun, n, (int) Math.pow(2, n), t);

        TrivialLearner L = new TrivialLearner("TestSys", origFun);
        
        //double[][] fCoefs = L.learn(numSample, theta, true);
        double[][] fCoefs = L.oldLearn(numSample, t+3, true);
        
        Matrix.write(fCoefs, estiCoef);
        
        FourierResult R = new FourierResult(fCoefs);
        
        R.estimateAllSample(estiFun, origFun);
        
        Matrix.print(fCoefs);      
    }
    
    
    public static void runOnData(String sysName, String sampleLoc, int numSample, double theta){
        
        String estiCoef = sysName + "CoefsEstimated.csv";
        String estiFun = sysName + "Estimated.csv";

        TrivialLearner L = new TrivialLearner(sysName, sampleLoc);
        
        double[][] fCoefs = L.learn(numSample, theta, true);
        //double[][] fCoefs = L.oldLearn(numSample, t, true);
        
        Matrix.write(fCoefs, estiCoef);
        
        FourierResult R = new FourierResult(fCoefs);
        
        R.estimateAllSample(estiFun, sampleLoc);
        
        Matrix.print(fCoefs);      
    }
    
}
