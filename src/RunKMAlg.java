
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
        
        int n = 16;
        int t = 200;
        double theta = 200;
        double ep = 0.05, del = 0.25;
      
        int numSample = (int) Math.ceil(2/(ep*ep)*((n+1)*Math.log(2) + Math.log(1/del)));
        //System.out.println("Number of samples is: " + numSample);
        //int numSample = (int) Math.ceil(Math.pow(2, n));
        //numSample = Math.min(5000, numSample);
        
        double err = t*ep*ep;
        
        System.out.println("Guanranteed error is: " + err);
        System.out.println("Total number of combinations is: " + Math.pow(2, n));
        System.out.println("Number of samples is: " + numSample);

        FourierTester.GenerateTestFromFourierToFile(tmp, n, (int) Math.pow(2, n), t);
        
        //FourierLearner K = new FourierLearner("TestSys", tmp);
        //FourierLearner K = new FourierLearner("Apache", tmp);
        
        TrivialLearner L = new TrivialLearner("TestSys", tmp);
        
        //SimpleMatrix fCoefs = L.learn(numSample, theta, true);
        double[][] fCoefs = L.oldLearn(numSample, t, true);
        
        
        String tmp2 = "TestFourierEstimated.csv";
        
        Matrix.write(fCoefs, tmp2);
        
        FourierResult R = new FourierResult(fCoefs);
        
        R.estimateAllSample("TestEstimated.csv", tmp);
        
        Matrix.print(fCoefs);        
        
        
        // End timer:
        double duration = System.currentTimeMillis() - startTime;
        System.out.println("\nTime taken: " + duration/1000 + " seconds");
    }
    
}
