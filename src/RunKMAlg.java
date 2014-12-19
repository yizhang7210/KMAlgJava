
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
        
        String sys = systems[0];
        
        /*
        int sampleSize = 81;
        int[] maxLevels = {0,1,2,3,4,5,6,7,8,9,10};
        double[] thetas = new double[50];
        
        for(int i = 0; i < thetas.length; ++i){
            thetas[i] = i*0.5/thetas.length;
        }
        
        System.out.println("The thetas are: " + Arrays.toString(thetas));
        
        double[][] allErrs;
        
        allErrs = RunKMAlg.multiRun(sys, sampleSize, maxLevels, thetas, 15);
        
        Matrix.write(allErrs, sys+"/allErrors.csv");
        */
        
        double err = RunKMAlg.runOnData(sys, sys+"/rawFun.csv", 3, 30, 0.22);
        
        System.out.println(err);
        
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
        
        L.learn(numSample, 1.0/t, (int) Math.ceil(n/2));
        //double[][] fCoefs = L.oldLearn(numSample, t, true);
        
        Matrix.write(L.fCoefs, estiCoef);
        
        L.estimateSample(estiFun, L.testSamples);
        
        Matrix.print(L.fCoefs);      
    }
    
    
    public static double runOnData(String sysName, String origFun, 
            int maxLevel, int numSample, double theta){
        
        double err;
        
        String estiCoef = sysName + "/estiCoef.csv";
        String estiFun = sysName + "/estiRawFun.csv";
        
        TrivialLearner L = new TrivialLearner(sysName, origFun);
        
        L.learn(numSample, theta, maxLevel);
        //double[][] fCoefs = L.oldLearn(numSample, (int) theta, true);
        
        Matrix.write(L.fCoefs, estiCoef);
        
        err = L.estimateSample(estiFun, L.allSamples);
        
        //Matrix.print(fCoefs);
        
        return(err);
    }
    
    public static double[][] multiRun(String sysName, int sampleSize, int[] maxLevel,
            double[] thetas, int repeat){
        
        int numLevels = maxLevel.length;
        int numThetas = thetas.length;
        
        String origFun = sysName + "/rawFun.csv";
        
        double[][][] allErrors = new double[numLevels][numThetas][repeat];
        double[][] meanErrors = new double[numLevels][numThetas];
        
        for(int i = 0; i < numLevels; ++i){
            for(int j = 0; j < numThetas; ++j){
                // Fill in the errors
                for(int k = 0; k < repeat; ++k){
                    allErrors[i][j][k] = RunKMAlg.runOnData(sysName, origFun,
                            maxLevel[i], sampleSize, thetas[j]);
                }
                // Get the average
                meanErrors[i][j] = Matrix.mean(allErrors[i][j]);
            }
        }
        
        return(meanErrors);
    }
    
}
