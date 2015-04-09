
import java.util.Arrays;

/**
 *
 * @author yzhang
 */
public class RunKMAlg {

    public static final int numSys = 5;
    //public static final String[] systems = {"Apache", "X264", "LLVM", "BDBC", "BDBJ", "Test"};
    public static final String[] systems = {"LLVM2", "LLVMX264"};
    public static final int[] dims = {9, 16, 11, 18, 26};
    public static final int[] realDims = {8, 13, 10, 16, 17};
    public static final int[] sampleSizes = {29, 81, 62, 139, 48};
    public static final int[] noObs = {192, 1152, 1024, 2560, 180};
    public static final int[] ts = {163, 383, 689, 587, 51716};

    public static void main(String[] args) {

        // Start timer:
        long startTime = System.currentTimeMillis();

        //RunKMAlg.tuneParam(sysNum);
        
        //RunKMAlg.runOnTest(13, 0.1, 0.1, 50);
        //double err = RunKMAlg.runOnData(0, 200, 0.0333);
        //System.out.println(err);

        RunNewAlg.runOnData(0, 0.3, 0.3, 4, 10);
        
        // Standard suite.------------------------------------
        //PreProcess:
        //for(int sysNum = 0; sysNum < 5; sysNum ++){
        //    RunKMAlg.preProcess(sysNum);
        //    RunKMAlg.getSparseFun(sysNum);
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

        double err = Math.pow(2, n) * ep * ep;

        System.out.println("Guanranteed error is: " + err);
        System.out.println("Total number of combinations is: " + Math.pow(2, n));
        System.out.println("Number of requried samples is: " + numSample);

        FourierTester.GenerateTestFromFourierToFile(origCoefLoc, origFunLoc, n, (int) Math.pow(2, n), t);

        Processor.normalizeFun(origFunLoc, normedFunLoc);
        Processor.getCoef(normedFunLoc, normedCoefLoc, t);

        FourierLearner L = new FourierLearner("Test", origFunLoc);

        //L.learn(numSample, 0.01);//, (int) Math.ceil(n/2));
        //double[][] fCoefs = L.oldLearn(numSample, t, true);
        
        double[][] samples = L.drawSamples(L.allSamples, numSample);
        FourierEstimator E = L.learn(samples, 1.0/t);

        Matrix.write(E.fCoefs, estiNormedCoefLoc);
        E.estimateSamples(origFunLoc, estiNormedFunLoc, L.numObs);

        double[][] estiNormedFun = Matrix.read(estiNormedFunLoc);
        double[][] estiRawFun = Processor.denormalizeSample(estiNormedFun, E.scale, E.shift);
        Matrix.write(estiRawFun, estiRawFunLoc);
        Processor.getCoef(estiRawFunLoc, estiRawCoefLoc, E.fCoefs.length);

        double errNormed = E.getError(normedFunLoc, estiNormedFunLoc);
        double errRaw = E.getError(origFunLoc, estiRawFunLoc);

        System.out.println("Normalized Error is: " + errNormed);
        System.out.println("Raw Error is: " + errRaw);
        //Matrix.print(L.fCoefs);
    }

    public static double runOnData(int sysNum, int numSample, double theta) {

        String sysName = RunKMAlg.systems[sysNum];

        String origFunLoc = sysName + "/rawFun.csv";
        //String origCoefLoc = sysName + "/rawCoef.csv";
        String normedFunLoc = sysName + "/normedFun.csv";
        //String normedCoefLoc = sysName + "/normedCoef.csv";

        String estiNormedCoefLoc = sysName + "/estiNormedCoef.csv";
        String estiNormedFunLoc = sysName + "/estiNormedFun.csv";
        String estiRawFunLoc = sysName + "/estiRawFun.csv";
        String estiRawCoefLoc = sysName + "/estiRawCoef.csv";

        FourierLearner L = new FourierLearner(sysName, origFunLoc);

        
        double[][] sample = L.drawSamples(L.allSamples, numSample);
        FourierEstimator E = L.learn(sample, theta);

        Matrix.write(E.fCoefs, estiNormedCoefLoc);
        E.estimateSamples(origFunLoc, estiNormedFunLoc, L.numObs);
        //E.estimateSamples(sysName+"/completeFun.csv", sysName+"/completeFun.csv");

        double[][] estiNormedFun = Matrix.read(estiNormedFunLoc);
        double[][] estiRawFun = Processor.denormalizeSample(estiNormedFun, E.scale, E.shift);
        Matrix.write(estiRawFun, estiRawFunLoc);
        Processor.getCoef(estiRawFunLoc, estiRawCoefLoc, E.fCoefs.length);

        double errNormed = E.getError(normedFunLoc, estiNormedFunLoc);
        double errRaw = E.getError(origFunLoc, estiRawFunLoc);

        //System.out.println("Normalized Error is: " + errNormed);
        //System.out.println("Raw Error is: " + errRaw);
        //System.out.println(E.scale);
        
        return (errNormed);
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

        String allErrFile = sysName + "/allErrors.csv";

        // Run algorithm on various parameters
        double[][][] allErrors = new double[numSizes][numThetas][repeat];
        double[][] meanErrors = new double[numSizes][numThetas];

        for (int i = 0; i < numSizes; ++i) {
            for (int j = 0; j < numThetas; ++j) {
                // Fill in the errors
                for (int k = 0; k < repeat; ++k) {
                    allErrors[i][j][k] = RunKMAlg.runOnData(sysNum, 
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

            //String sysName = RunKMAlg.systems[sysNum];
            int n = RunKMAlg.realDims[sysNum];
            int t = RunKMAlg.ts[sysNum];
            int m = RunKMAlg.noObs[sysNum];

            for (int i = 0; i < repeat; ++i) {
                errors[sysNum][i] = RunKMAlg.runOnData(sysNum, sampleSize, 2.0 / n);
            }

            double theoErr = (Math.log(2) * (n + 1) + Math.log(10)) / 50 * t;
            System.out.println("Error should be within " + theoErr/m);
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

            long startTime = System.currentTimeMillis();
            for (int i = 0; i < repeat; ++i) {
                errors[sysNum][i] = RunKMAlg.runOnData(sysNum, sampleSize, 0.1);
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

        //Processor.getCoef(origFun, origCoef, Integer.MAX_VALUE);

        Processor.normalizeFun(origFun, normedFun);
        //Processor.getCoef(normedFun, normedCoef, Integer.MAX_VALUE);

    }
    
    public static void getSparseFun(int sysNum){
        String sysName = RunKMAlg.systems[sysNum];
        
        String coefLoc = sysName + "/sparseCoef.csv";
        String origFunLoc = sysName + "/normedFun.csv";
        String sparseFunLoc = sysName + "/sparseFun.csv";
        
        double[][] sparseCoef = Matrix.read(coefLoc);
        
        FourierEstimator E = new FourierEstimator(sparseCoef, 1, 0);
        
        E.estimateSamples(origFunLoc, sparseFunLoc, RunKMAlg.noObs[sysNum]);
        
    }

}
