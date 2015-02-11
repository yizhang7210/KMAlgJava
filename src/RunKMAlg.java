
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

    private static final int numSys = 5;
    private static final String[] systems = {"Apache", "X264", "LLVM", "BDBC", "BDBJ", "Test"};
    private static final int[] dims = {9, 16, 11, 18, 26};
    private static final int[] realDims = {8, 13, 10, 16, 17};
    private static final int[] sampleSizes = {29, 81, 62, 139, 48};
    private static final int[] ts = {163, 383, 689, 587, 51716};

    public static void main(String[] args) {

        // Start timer:
        long startTime = System.currentTimeMillis();

        /*
         int sysNum = 3;
        
         String sysName = RunKMAlg.systems[sysNum];
         int numSamples = RunKMAlg.sampleSizes[sysNum];
        
         String origFun = sysName + "/rawFun.csv";
        
         double err = RunKMAlg.runOnData(sysNum, origFun, numSamples, 0.12);
        
         System.out.println(sysName + " has error: " + err);
         */
        //RunKMAlg.runOnTest(13, 0.1, 0.1, 50);
        double err = RunKMAlg.runOnData(0, "Apache/rawFun.csv", 50, 0.16);
        System.out.println(err);
        
        
        
        
        // Standard suite.------------------------------------
        //PreProcess:
        //for(int sysNum = 0; sysNum < 5; sysNum ++){
        //    RunKMAlg.preProcess(sysNum);
        //}
        //Experiment 0: Parameter Tuning:
        //double[][][] tuneParamErrors = new double[RunKMAlg.numSys][4][30];
        //for (int sysNum = 0; sysNum < 1; ++sysNum) {
        //tuneParamErrors[sysNum] = RunKMAlg.tuneParam(3);
        //}
        //Experiment 1: Verifying Theoretical Guarantee:
        //double[][] expOneErr = RunKMAlg.expOneRun();
        //Matrix.print(expOneErr);
        // Experiment 2: Comparing to other methods
        //double[][] expTwoErr = RunKMAlg.expTwoRun();
        //Matrix.print(expTwoErr);
        //System.out.println(expTwoErr[0][0]);
        // End timer:
        double duration = System.currentTimeMillis() - startTime;
        System.out.println("\nTime taken: " + duration / 1000 + " seconds");
    }

    public static void runOnTest(int n, double ep, double del, int t) {

        String origFunLoc = "Test/rawFun.csv";
        String origCoefLoc = "Test/rawCoef.csv";
        String normedFunLoc = "Test/normedFun.csv";
        String normedCoefLoc = "Test/normedCoef.csv";

        String estiNormedFunLoc = "Test/estiNormedFun.csv";
        String estiNormedCoefLoc = "Test/estiNormedCoef.csv";
        String estiRawFunLoc = "Test/estiRawFun.csv";
        String estiRawCoefLoc = "Test/estiRawCoef.csv";

        int numSample = (int) Math.ceil(2.0 / (ep * ep) * ((n + 1) * Math.log(2) + Math.log(1.0 / del)));

        double err = t * ep * ep;

        System.out.println("Guanranteed error is: " + err);
        System.out.println("Total number of combinations is: " + Math.pow(2, n));
        System.out.println("Number of requried samples is: " + numSample);

        FourierTester.GenerateTestFromFourierToFile(origCoefLoc, origFunLoc, n, (int) Math.pow(2, n), t);

        Processor.normalizeFun(origFunLoc, normedFunLoc);
        Processor.getCoef(normedFunLoc, normedCoefLoc, t);

        FourierLearner L = new FourierLearner("Test", origFunLoc);

        //L.learn(numSample, 0.01);//, (int) Math.ceil(n/2));
        //double[][] fCoefs = L.oldLearn(numSample, t, true);
        FourierEstimator E = L.learn(numSample, 0.02);

        Matrix.write(E.fCoefs, estiNormedCoefLoc);
        E.estimateSamples(origFunLoc, estiNormedFunLoc);
        err = E.getError(origFunLoc, estiNormedFunLoc);

        double[][] estiNormedFun = Matrix.read(estiNormedFunLoc);
        double[][] estiRawFun = Processor.denormalizeSample(estiNormedFun, E.scale, E.shift);
        Matrix.write(estiRawFun, estiRawFunLoc);
        Processor.getCoef(estiRawFunLoc, estiRawCoefLoc, E.fCoefs.length);

        System.out.println("Real error is: " + err);

        //Matrix.print(L.fCoefs);
    }

    public static double runOnData(int sysNum, String origFun, //int maxLevel, 
            int numSample, double theta) {

        double err;

        String sysName = RunKMAlg.systems[sysNum];

        String estiNormedCoefLoc = sysName + "/estiNormedCoef.csv";
        String estiNormedFunLoc = sysName + "/estiNormedFun.csv";
        String estiRawFunLoc = sysName + "/estiRawFun.csv";
        String estiRawCoefLoc = sysName + "/estiRawCoef.csv";

        FourierLearner L = new FourierLearner(sysName, origFun);

        FourierEstimator E = L.learn(numSample, theta);

        Matrix.write(E.fCoefs, estiNormedCoefLoc);
        E.estimateSamples(origFun, estiNormedFunLoc);
        err = E.getError(origFun, estiNormedFunLoc);

        double[][] estiNormedFun = Matrix.read(estiNormedFunLoc);
        double[][] estiRawFun = Processor.denormalizeSample(estiNormedFun, E.scale, E.shift);
        Matrix.write(estiRawFun, estiRawFunLoc);
        Processor.getCoef(estiRawFunLoc, estiRawCoefLoc, E.fCoefs.length);

        return (err);
    }

    public static double[][] tuneParam(int sysNum) {

        // Setup
        String sysName = RunKMAlg.systems[sysNum];
        int n = RunKMAlg.realDims[sysNum];
        int[] numSamples = {n, 2 * n, 3 * n, RunKMAlg.sampleSizes[sysNum]};
        int repeat = 5;

        double[] thetas = new double[30];
        for (int i = 0; i < thetas.length; ++i) {
            thetas[i] = i * 0.5 / thetas.length;
        }

        System.out.println("The thetas are: " + Arrays.toString(thetas));

        int numSizes = numSamples.length;
        int numThetas = thetas.length;

        String origFun = sysName + "/rawFun.csv";
        String allErrFile = sysName + "/allErrors.csv";

        // Run algorithm on various parameters
        double[][][] allErrors = new double[numSizes][numThetas][repeat];
        double[][] meanErrors = new double[numSizes][numThetas];

        for (int i = 0; i < numSizes; ++i) {
            for (int j = 0; j < numThetas; ++j) {
                // Fill in the errors
                for (int k = 0; k < repeat; ++k) {
                    allErrors[i][j][k] = RunKMAlg.runOnData(sysNum, origFun,
                            sampleSizes[i], thetas[j]);
                }
                // Get the average
                meanErrors[i][j] = Matrix.mean(allErrors[i][j]);
            }
        }

        Matrix.write(meanErrors, allErrFile);
        return (meanErrors);
    }

    public static double[][] expOneRun() {

        int repeat = 20;
        int sampleSize = 100;
        String expOneErr = "expOneErr.csv";

        double[][] errors = new double[RunKMAlg.numSys][repeat];

        for (int sysNum = 0; sysNum < RunKMAlg.numSys; ++sysNum) {

            String sysName = RunKMAlg.systems[sysNum];
            int n = RunKMAlg.realDims[sysNum];
            int t = RunKMAlg.ts[sysNum];

            String origFun = sysName + "/rawFun.csv";

            for (int i = 0; i < repeat; ++i) {
                errors[sysNum][i] = RunKMAlg.runOnData(sysNum, origFun, sampleSize, 2.5 / n);
            }

            double theoErr = (Math.log(2) * (n + 1) + Math.log(10)) / 50 * t;
            System.out.println("Error should be within " + theoErr);
        }

        Matrix.write(errors, expOneErr);

        return (errors);
    }

    public static double[][] expTwoRun() {

        int repeat = 20;
        String expTwoErr = "expTwoErr.csv";

        double[][] errors = new double[RunKMAlg.numSys][repeat + 1];

        for (int sysNum = 0; sysNum < RunKMAlg.numSys; ++sysNum) {

            String sysName = RunKMAlg.systems[sysNum];
            int n = RunKMAlg.realDims[sysNum];
            int t = RunKMAlg.ts[sysNum];
            int sampleSize = RunKMAlg.sampleSizes[sysNum];

            String origFun = sysName + "/rawFun.csv";

            long startTime = System.currentTimeMillis();
            for (int i = 0; i < repeat; ++i) {
                errors[sysNum][i] = RunKMAlg.runOnData(sysNum, origFun, sampleSize, 0.1);
            }
            errors[sysNum][repeat] = System.currentTimeMillis() - startTime;

            double theoErr = (2.0 * t * (Math.log(2) * (n + 1) + Math.log(10)) / sampleSize);// /RunKMAlg.fNorms[sysNum];

            System.out.println("90% confidence interval is: " + theoErr);
        }

        Matrix.write(errors, expTwoErr);

        return (errors);
    }

    public static void preProcess(int sysNum) {

        String sysName = RunKMAlg.systems[sysNum];

        String origFun = sysName + "/rawFun.csv";
        String origCoef = sysName + "/rawCoef.csv";

        String normedFun = sysName + "/normedFun.csv";
        String normedCoef = sysName + "/normedCoef.csv";

        Processor.getCoef(origFun, origCoef, Integer.MAX_VALUE);

        Processor.normalizeFun(origFun, normedFun);
        Processor.getCoef(normedFun, normedCoef, Integer.MAX_VALUE);

    }

}
