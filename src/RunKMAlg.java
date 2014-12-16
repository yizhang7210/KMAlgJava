
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

        String [] systems = {"Apache", "X264", "LLVM", "BDBC"};
        
        //RunKMAlg.runOnTest(12, 0.05, 0.1, 50);
        
        double[][] allErrs = new double[15][9];
        
        for(int i = 0; i < 15; ++i){
            for(int j = 0; j < 9; ++j){
                allErrs[i][j] = RunKMAlg.runOnData(systems[3], systems[3]+"/origFun.csv",
                        i*30+10, j*0.05);
            }   
        }
        
        Matrix.write(allErrs, "allErrors.csv");
        
        
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
    
}
