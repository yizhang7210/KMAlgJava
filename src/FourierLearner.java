
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author yzhang
 */
public class FourierLearner {

    private final String sysName;
    private final String sampleLoc;
    public double[][] allSamples;
    public final int numObs;
    public final int numFeatures;

    public FourierLearner(String sysName, String sampleLoc) {
        this.sysName = sysName;
        this.sampleLoc = sampleLoc;
        this.allSamples = this.readSample(sampleLoc);
        this.numObs = this.allSamples.length;
        this.numFeatures = this.allSamples[0].length - 1;
    }

    public void print() {
        System.out.println(" This FourierLearner:\n System: " + this.sysName
                + "\n Sample Location: " + this.sampleLoc
                + "\n Number of Features: " + this.numFeatures
                + "\n Total number of Observations: " + this.numObs);
    }

    private double[][] readSample(String sampleLoc) {

        try {
            FileReader r = new FileReader(sampleLoc);
            BufferedReader in = new BufferedReader(r);

            String firstLine = in.readLine();
            String[] splitFirstLine = firstLine.split(" ");
            int numRow = Integer.parseInt(splitFirstLine[0]);
            int numCol = Integer.parseInt(splitFirstLine[1]);

            double[][] allSample = new double[numRow][numCol];

            for (int i = 0; i < numRow; ++i) {
                String line = in.readLine();

                String[] splitLine = line.split(" ");

                for (int j = 0; j < numCol; ++j) {
                    allSample[i][j] = Double.parseDouble(splitLine[j]);
                }

            }

            return (allSample);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FourierEstimator learn(double[][] trainingSamples, double theta) {

        double[][][] normedResult = Processor.normalizeSample(trainingSamples);

        double shift = normedResult[0][0][0];
        double scale = normedResult[0][0][1];
        double[][] normedSamples = normedResult[1];

        double[][] estiCoefs = FourierLearner.estimateAllCoefs(normedSamples, theta);

        return (new FourierEstimator(estiCoefs, scale, shift));

    }

    public FourierEstimator newLearn(double[][] trainingSamples, double theta, int k) {

        double[][][] normedResult = Processor.normalizeSample(trainingSamples);

        double shift = normedResult[0][0][0];
        double scale = normedResult[0][0][1];
        double[][] normedSamples = normedResult[1];

        double[][] estiCoefs = FourierLearner.estimatePartCoefs(normedSamples, theta, k);

        return (new FourierEstimator(estiCoefs, scale, shift));

    }

    public double[][] drawSamples(double[][] theSamples, int numSamples) {

        int m = theSamples.length;
        int n = theSamples[0].length - 1;

        Random rn = new Random();

        // Warn if there aren't enough samples
        if (numSamples > m) {
            System.out.println("Warning: Don't have that many samples.");
            numSamples = m;
        }

        // Shuffle the samples
        int[] randomInds = new int[numSamples];

        for (int i = 0; i < numSamples; ++i) {
            randomInds[i] = rn.nextInt(m);
        }

        double[][] trainingSamples = new double[numSamples][n + 1];
        for (int i = 0; i < numSamples; ++i) {
            trainingSamples[i] = Arrays.copyOf(theSamples[randomInds[i]], n + 1);
        }

        return (trainingSamples);

    }

    public static double[][] estimateAllCoefs(double[][] trainingSamples, double theta) {

        int n = trainingSamples[0].length - 1;

        double[][] allCoefs = new double[(int) Math.pow(2, n)][n + 1];

        int counter = 0;
        for (int i = 0; i < allCoefs.length; ++i) {
            double[] vec = Matrix.intToVec(i, n);

            double val = FourierLearner.approx(vec, trainingSamples);

            if (Math.abs(val) >= theta) {
                allCoefs[counter] = Arrays.copyOf(vec, n + 1);
                allCoefs[counter][n] = val;
                counter++;
            }

        }

        allCoefs = Arrays.copyOfRange(allCoefs, 0, counter);

        Comparator<double[]> comp = (double[] a, double[] b)
                -> Double.compare(Math.abs(b[b.length - 1]), Math.abs(a[a.length - 1]));

        Arrays.sort(allCoefs, comp);

        return (allCoefs);
    }

    public static double[][] estimatePartCoefs(double[][] trainingSamples, double theta, int k) {

        int n = trainingSamples[0].length - 1;

        int numCoef = (int) (Math.pow(2, k) + Math.pow(2, n-k));
        
        double[][] allCoefs = new double[numCoef][n + 1];

        int counter = 0;
        for (int i = 0; i < allCoefs.length; ++i) {
            double[] vec = Matrix.intToVec(i, n);

            if (Matrix.sum(Arrays.copyOfRange(vec, 0, k)) > 0
                    && Matrix.sum(Arrays.copyOfRange(vec, k, vec.length)) > 0) {
                continue;
            }

            double val = FourierLearner.approx(vec, trainingSamples);

            if (Math.abs(val) >= theta) {
                allCoefs[counter] = Arrays.copyOf(vec, n + 1);
                allCoefs[counter][n] = val;
                counter++;
            }

        }

        allCoefs = Arrays.copyOfRange(allCoefs, 0, counter);

        Comparator<double[]> comp = (double[] a, double[] b)
                -> Double.compare(Math.abs(b[b.length - 1]), Math.abs(a[a.length - 1]));

        Arrays.sort(allCoefs, comp);

        return (allCoefs);
    }

    public static double approx(double[] alpha, double[][] samples) {

        int n = samples[0].length - 1;

        double A = 0;
        for (double[] sample : samples) {
            A += sample[n] * Matrix.character(alpha, sample);
        }

        return (A / samples.length);
    }
}
