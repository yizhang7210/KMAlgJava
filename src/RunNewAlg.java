/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author yzhang
 */
public class RunNewAlg {

    public static void runOnData(int sysNum, double desiredErr, double delta, double t0, int k) {

        String sysName = RunKMAlg.systems[sysNum];
        String origFunLoc = sysName + "/normedFun.csv";
        String estiFunLoc = sysName + "/estiNormedFun.csv";
        FourierLearner L = new FourierLearner(sysName, origFunLoc);
        int n = L.numFeatures;
        int noObs = L.numObs;

        double d = 8 * desiredErr / 9;
        double t = t0;
        double[][] params = new double[2][5];
        params[0][0] = t;

        while (true) {

            params = RunNewAlg.updateParams(n, d, delta, params);

            long m1 = (long) params[0][4];
            long m2 = (long) params[1][4];

            long numSamples = m1 + m2;

            if (numSamples > noObs) {
                System.out.println("Run out of samples.");
                System.out.println("Need " + numSamples + " samples. Only have: " + noObs);
                return;
            }

            System.out.println("Now t is: " + params[0][0]);
            System.out.println("And currently on " + numSamples + " samples");

            double[][] trainSamples = L.drawSamples(L.allSamples, (int) m1);
            double[][] testSamples = L.drawSamples(L.allSamples, (int) m2);

            String testSampleLoc = "tempTest.csv";
            String testEstimateLoc = "tempTestEstimate.csv";
            Matrix.write(testSamples, testSampleLoc);

            double theta1 = params[0][3];
            FourierEstimator E = L.newLearn(trainSamples, theta1, k);

            E.estimateSamples(testSampleLoc, testEstimateLoc, L.numObs);

            double estiErr = E.getError(testSampleLoc, testEstimateLoc);

            System.out.println("Current estimate error is about: " + estiErr);

            double theta2 = params[1][3];
            if (estiErr <= theta2) {
                System.out.println("Mission accomplished");
                System.out.println("Estimated error is: " + estiErr);
                System.out.println("Used " + numSamples + " samples.");

                E.estimateSamples(origFunLoc, estiFunLoc, -1);

                double realErr = E.getError(origFunLoc, estiFunLoc);

                System.out.println("The real error is: " + realErr);

                return;
            }

        }

    }

    /**
     * Params: t delta1 ep1 theta1 m1 :: 0 delta2 ep2 theta2 m2
     *
     * @param n
     * @param d
     * @param delta
     * @param params
     * @return
     */
    private static double[][] updateParams(int n, double d, double delta, double[][] params) {

        double t = params[0][0];

        t = 2 * t;

        double delta1 = 1 - Math.sqrt(1 - delta);
        double ep1 = d / (4 * t);
        double theta1 = 3 * ep1;
        int m1 = (int) Math.ceil(32 * t * t * (Math.log(2) * (n + 1) + Math.log(1 / delta1)) / (d * d));

        double delta2 = 1 - Math.sqrt(1 - delta);
        double ep2 = d / 8;
        double theta2 = 5 * d / 4;
        int m2 = (int) Math.ceil(32 * Math.log(2 / delta2) / (d * d));

        double[][] newParams = {{t, delta1, ep1, theta1, m1}, {0, delta2, ep2, theta2, m2}};

        return (newParams);
    }

}
