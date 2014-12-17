
import java.util.Arrays;


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

        String [] systems = {"Apache", "X264", "LLVM", "BDBC", "BDBJ"};
        
        String sys = systems[1];
        
        //RunKMAlg.runOnTest(12, 0.05, 0.1, 50);
        
        int[] sampleSizes = {16, 32, 48, 81};
        double[] thetas = new double[30];
        
        for(int i = 0; i < thetas.length; ++i){
            thetas[i] = 0.02 + i*0.5/thetas.length;
        }
        
        System.out.println("The thetas are: " + Arrays.toString(thetas));
        
        double[][] allErrs;
        
        allErrs = RunKMAlg.multiRun(sys, sampleSizes, thetas, 15);
        
        Matrix.write(allErrs, sys+"/allErrors.csv");
        
        
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
    
    
    public static double runOnData(String sysName, String origFun, int numSample, double theta){
        
        double err;
        
        String estiCoef = sysName + "/estiCoef.csv";
        String estiFun = sysName + "/estiFun.csv";
        
        TrivialLearner L = new TrivialLearner(sysName, origFun);
        
        double[][] fCoefs = L.learn(numSample, theta, true);
        //double[][] fCoefs = L.oldLearn(numSample, (int) theta, true);
        
        Matrix.write(fCoefs, estiCoef);
        
        FourierResult R = new FourierResult(fCoefs);
        
        err = R.estimateAllSample(estiFun, origFun);
        //R.estimateAllSample(sysName+"/estiComplete.csv", sysName+"/completeFun.csv");
        
        //Matrix.print(fCoefs);
        
        return(err);
    }
    
    public static double[][] multiRun(String sysName, int[] sampleSizes, 
            double[] thetas, int repeat){
        
        int numSizes = sampleSizes.length;
        int numThetas = thetas.length;
        
        String origFun = sysName + "/origFun.csv";
        
        double[][][] allErrors = new double[numSizes][numThetas][repeat];
        double[][] meanErrors = new double[numSizes][numThetas];
        
        for(int i = 0; i < numSizes; ++i){
            for(int j = 0; j < numThetas; ++j){
                // Fill in the errors
                for(int k = 0; k < repeat; ++k){
                    allErrors[i][j][k] = RunKMAlg.runOnData(sysName, origFun,
                            sampleSizes[i], thetas[j]);
                }
                // Get the average
                meanErrors[i][j] = Matrix.mean(allErrors[i][j]);
            }
        }
        
        return(meanErrors);
    }
    
}
