
import java.util.Arrays;
import java.util.Comparator;

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

    public Comparator<double[]> comp;

    public FourierEstimator(double[][] fCoefs, double scale, double shift) {
        this.fCoefs = fCoefs;
        this.t = fCoefs.length;
        this.n = fCoefs.length > 0 ? fCoefs[0].length - 1 : 0;
        this.scale = scale;
        this.shift = shift;
        this.comp = new Comparator<double[]>() {

            @Override
            public int compare(double[] a, double[] b) {

                for (int i = 0; i < b.length; ++i) {
                    if (a[i] < b[i]) {
                        return -1;
                    }
                }

                return 1;
            }
        };
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

        if (scale == -1) {
            scale = m;
        }

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
        double[] errs = new double[m];
        for (int i = 0; i < m; ++i) {
            oldVal = origFun[i][this.n];
            newVal = estiFun[i][this.n];
            errs[i] += (oldVal - newVal) * (oldVal - newVal);
        }

        return Matrix.mean(errs);

    }

    public double getDeriv(double[] vec) {

        double deriv = 0;

        for (int i = 0; i < this.t; ++i) {
            if (this.comp.compare(this.fCoefs[i], vec) > 0) {
                deriv += this.fCoefs[i][this.n];
            }
        }

        double multiplier = Math.pow(-2, Matrix.sum(vec));

        System.out.println(deriv);
        return deriv * multiplier;
    }

    public double[][] getAllDeriv() {

        double[][] allDeriv = new double[(int) Math.pow(2, this.n)][this.n + 1];
        
        for(int i = 0; i < allDeriv.length; ++i){
            
            double[] newVec = Matrix.intToVec(i, this.n);
            
            allDeriv[i] = Arrays.copyOf(newVec, n+1);
            allDeriv[i][n] = this.getDeriv(newVec);
        }
        
        return allDeriv;

    }

}
