
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
public class FeatureInteraction {

    public static boolean gt(double[] a, double[] b) {

        for (int i = 0; i < Math.min(a.length, b.length); ++i) {
            if (a[i] < b[i]) {
                return false;
            }
        }

        return true;

    }

    public static double getSingleCoef(double[] vec, double[][] funVals) {

        int m = funVals.length;
        int n = funVals[0].length - 1;

        double coef = 0;

        for (int i = 0; i < m; ++i) {

            double[] funInput = Arrays.copyOfRange(funVals[i], 0, n);

            coef = coef + Matrix.character(vec, funInput) * funVals[i][n];
        }

        return (coef / Math.pow(2, n));

    }

    public static double[][] funValsToCoef(double[][] funVals) {

        int n = funVals[0].length - 1;
        int m = (int) Math.pow(2, n);

        double[][] allCoefs = new double[m][n + 1];

        for (int i = 0; i < m; ++i) {

            double[] inputVec = Matrix.intToVec(i, n);

            allCoefs[i] = Arrays.copyOf(inputVec, n + 1);
            allCoefs[i][n] = FeatureInteraction.getSingleCoef(inputVec, funVals);
        }

        return (allCoefs);

    }

    public static double[][] funValsToDeriv(double[][] funVals) {

        double[][] coefs = FeatureInteraction.funValsToCoef(funVals);
        double[][] derivs = FeatureInteraction.coefToDeriv(coefs);

        return (derivs);
    }

    public static double getSingleDeriv(double[] vec, double[][] coefs) {

        int n = coefs[0].length - 1;
        int m = coefs.length;

        double deriv = 0;

        for (int i = 0; i < m; ++i) {

            if (gt(coefs[i], vec)) {
                deriv = deriv + coefs[i][n];
            }

        }

        return (deriv * Math.pow(-2, Matrix.sum(vec)));

    }

    public static double[][] coefToDeriv(double[][] coefs) {

        int n = coefs[0].length - 1;
        int m = (int) Math.pow(2, n);

        double[][] allDerivs = new double[m][n + 1];

        for (int i = 0; i < m; ++i) {

            double[] inputVec = Matrix.intToVec(i, n);

            allDerivs[i] = Arrays.copyOf(inputVec, n + 1);
            allDerivs[i][n] = FeatureInteraction.getSingleDeriv(inputVec, coefs);
        }

        return (allDerivs);
    }
    
    public static void runOnSystem(String sysName){
        
        String dir = "/home/yzhang/00ME/Education/UW/Projects/Feature-Interaction/Experiment/";
        
        String estiFunLoc = dir +sysName + "/estiFun.csv";
        String origCoefLoc = dir + sysName + "/rawCoef.csv";
        String estiDerivLoc = dir +sysName + "/estiDeriv.csv";
        String origDerivLoc = dir + sysName + "/origDeriv.csv";
        
        double[][] funVals = Matrix.read(estiFunLoc);
        double[][] estiDeriv = FeatureInteraction.funValsToDeriv(funVals);
        Matrix.write(estiDeriv, estiDerivLoc);
        
        //double[][] origCoef = Matrix.read(origCoefLoc);
        //double[][] origDeriv = FeatureInteraction.coefToDeriv(origCoef);
        //Matrix.write(origDeriv, origDerivLoc);
        
        
    }

}
