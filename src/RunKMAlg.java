
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
        
        String [] sampleLocs = {"ApacheProcessed.csv", 
            "X264Processed.csv", "LLVMProcessed.csv"};
        
        
        //RunKMAlg.runOnTest(12, 0.05, 0.1, 50);
        
        RunKMAlg.runOnData(systems[0], systems[0]+"/origFun.csv", 80, 0.165);
        
        // End timer:
        double duration = System.currentTimeMillis() - startTime;
        System.out.println("\nTime taken: " + duration/1000 + " seconds");
    }
    
    
    
    
    public static void runOnTest(int n, double ep, double del, int t){
        
        String origFun = "Test/origFun.csv";
        String origCoef = "Test/origCoef.csv";
        String estiFun = "Test/estiFun.csv";
        String estiCoef = "Test/estiCoef.csv";
      
        int numSample = (int) Math.ceil(2/(ep*ep)*((n+1)*Math.log(2) + Math.log(1/del)));
        
        double err = t*ep*ep;
        
        System.out.println("Guanranteed error is: " + err);
        System.out.println("Total number of combinations is: " + Math.pow(2, n));
        System.out.println("Number of requried samples is: " + numSample);

        FourierTester.GenerateTestFromFourierToFile(origCoef, origFun, n, (int) Math.pow(2, n), t);

        TrivialLearner L = new TrivialLearner("TestSys", origFun);
        
        double[][] fCoefs = L.learn(numSample, 1.0/t, true);
        //double[][] fCoefs = L.oldLearn(numSample, t, true);
        
        Matrix.write(fCoefs, estiCoef);
        
        FourierResult R = new FourierResult(fCoefs);
        
        R.estimateAllSample(estiFun, origFun);
        
        Matrix.print(fCoefs);      
    }
    
    
    public static void runOnData(String sysName, String sampleLoc, int numSample, double theta){
        
        String estiCoef = sysName + "/estiCoef.csv";
        String estiFun = sysName + "/estiFun.csv";

        TrivialLearner L = new TrivialLearner(sysName, sampleLoc);
        
        double[][] fCoefs = L.learn(numSample, theta, true);
        //double[][] fCoefs = L.oldLearn(numSample, (int) theta, true);
        
        Matrix.write(fCoefs, estiCoef);
        
        FourierResult R = new FourierResult(fCoefs);
        
        R.estimateAllSample(estiFun, sampleLoc);
        R.estimateAllSample(sysName+"/estiComplete.csv", sysName+"/completeFun.csv");
        
        Matrix.print(fCoefs);      
    }
    
}
