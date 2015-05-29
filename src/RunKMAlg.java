
import java.util.Arrays;

/**
 *
 * @author yzhang
 */
public class RunKMAlg {

    public static final int numSys = 5;
    public static final String[] systems = {"Apache", "X264", "LLVM", "BDBC", "BDBJ", "Test"};
    //public static final String[] systems = {"ApacheX264", "LLVMX264", "X2642", "LLVM2", "Test2"};
    public static final int[] dims = {9, 16, 11, 18, 26};
    public static final int[] ks = {8, 10, 13, 10, 10};
    public static final int[] realDims = {8, 13, 10, 16, 17};
    public static final int[] sampleSizes = {29, 81, 62, 139, 48};
    public static final int[] noObs = {192, 1152, 1024, 2560, 180};
    public static final int[] ts = {163, 383, 689, 587, 51716};

    public static void main(String[] args) {

        // Start timer:
        long startTime = System.currentTimeMillis();

        // Run Performance Prediction:
        //RunKMAlg.runPerformancePrediction();
        RunKMAlg.runOnDataFI(0, 1000, 0);

        // End timer:
        double duration = System.currentTimeMillis() - startTime;
        System.out.println("\nTime taken: " + duration / 1000 + " seconds");
    }

    public static void runPerformancePrediction() {
        for (int sysNum = 0; sysNum < RunKMAlg.numSys; sysNum++) {
            //PreProcess:
            RunKMAlg.preProcess(sysNum);
            RunKMAlg.getSparseFun(sysNum);

            //Run Experiments:
            RunNewAlg.runOnData(sysNum, 0.1, 0.2, 0.5, RunKMAlg.ks[sysNum]);
        }
    }

    public static void runFeatureInteraction() {

    }

    public static void runOnDataFI(int sysNum, int numSample, double theta) {

        String sysName = RunKMAlg.systems[sysNum];
        String origFunLoc = sysName + "/normedFun.csv";
        String derivLoc = sysName + "/estiDerivs.csv";
        String estiNormedFunLoc = sysName + "/estiNormedFun.csv";
        String estiNormedCoefLoc = sysName + "/estiNormedCoef.csv";
        FourierLearner L = new FourierLearner(sysName, origFunLoc);

        // Learn
        double[][] sample = L.drawSamples(L.allSamples, numSample);
        FourierEstimator E = L.learn(sample, theta);

        // Performance Prediction
        boolean debug = false;
        if (debug) {
            Matrix.write(E.fCoefs, estiNormedCoefLoc);
            E.estimateSamples(origFunLoc, estiNormedFunLoc, L.numObs);
            double errNormed = E.getError(origFunLoc, estiNormedFunLoc);
            System.out.println("The Estimated Error is: " + errNormed);
        }
        
        // Derivatives
        double[][] allDerivs = E.getAllDeriv();
        Matrix.write(allDerivs, derivLoc);

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
        FourierEstimator E = L.learn(samples, 1.0 / t);

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

    public static void getSparseFun(int sysNum) {
        String sysName = RunKMAlg.systems[sysNum];

        String coefLoc = sysName + "/sparseCoef.csv";
        String origFunLoc = sysName + "/normedFun.csv";
        String sparseFunLoc = sysName + "/sparseFun.csv";

        double[][] sparseCoef = Matrix.read(coefLoc);

        FourierEstimator E = new FourierEstimator(sparseCoef, 1, 0);

        E.estimateSamples(origFunLoc, sparseFunLoc, RunKMAlg.noObs[sysNum]);

    }

}
