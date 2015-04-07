
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
public class FourierEstimator {

    public final double[][] fCoefs;
    private final int t;
    private final int n;
    public double scale;
    public double shift;

    public FourierEstimator(double[][] fCoefs, double scale, double shift) {
        this.fCoefs = fCoefs;
        this.t = fCoefs.length;
        this.n = fCoefs.length > 0 ? fCoefs[0].length - 1 : 0;
        this.scale = scale;
        this.shift = shift;

    }

    private double h(double[] x) {

        double val = 0;

        for (int i = 0; i < this.t; ++i) {

            double[] z = Arrays.copyOfRange(this.fCoefs[i], 0, this.n);

            val = val + Matrix.character(z, x) * this.fCoefs[i][this.n];
        }

        return (val);
    }

    public void estimateSamples(String origFunLoc, String estiFunLoc, int scale) {

        double[][] testSet = Matrix.read(origFunLoc);
        int m = testSet.length;

        if (this.fCoefs.length == 0) {
            testSet[0][this.n] = Double.POSITIVE_INFINITY;
            Matrix.write(testSet, estiFunLoc);
            return;
        }

        for (int i = 0; i < m; ++i) {
            double[] input = Arrays.copyOfRange(testSet[i], 0, this.n);

            double newVal = this.h(input) * scale / Math.pow(2, this.n);

            testSet[i][this.n] = newVal;
        }

        Matrix.write(testSet, estiFunLoc);
    }

    public double getError(String origFunLoc, String estiFunLoc) {

        double[][] origFun = Matrix.read(origFunLoc);
        double[][] estiFun = Matrix.read(estiFunLoc);

        int m = origFun.length;

        double oldVal, newVal;
        double [] errs = new double[m];
        for (int i = 0; i < m; ++i) {
            oldVal = origFun[i][this.n];
            newVal = estiFun[i][this.n];
            errs[i] += (oldVal - newVal) * (oldVal - newVal);
        }

        return Matrix.mean(errs);

    }

}
