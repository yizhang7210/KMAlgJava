/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author yzhang
 */
public class Processor {

    public static void getCoef(String origFileLoc, String coefLoc) {
        
        double[][] origFun = Matrix.read(origFileLoc);
        
        double[][] fCoefs = FourierLearner.estimateAllCoefs(origFun);

        Matrix.write(fCoefs, coefLoc);
    }

    public static void normalizeFun(String origFileLoc, String normalizedLoc) {

        double[][] origFun = Matrix.read(origFileLoc);

        double[][][] normalizedResult = Processor.normalizeSample(origFun);
        
        origFun = normalizedResult[1];

        Matrix.write(origFun, normalizedLoc);
    }

    
    public static double[][][] normalizeSample(double[][] origSample) {

        int m = origSample.length;
        int l = origSample[0].length;
        
        double[][][] result = new double[2][m][l];

        double[] vals = new double[m];
        for (int i = 0; i < m; ++i) {
            vals[i] = origSample[i][l - 1];
        }

        double ave = Matrix.mean(vals);
        for (int i = 0; i < m; ++i) {
            origSample[i][l - 1] = origSample[i][l - 1] - ave;
        }

        double max = Matrix.maxAbsCol(origSample, l - 1);
        for (int i = 0; i < m; ++i) {
            origSample[i][l - 1] = origSample[i][l - 1] / max;
        }

        result[0][0][0]= ave;
        result[0][0][1] = max;
        result[1] = origSample;
        
        return (result);

    }
    
    public static double[][] denormalizeSample(double[][] origSample,
            double scale, double shift){
        
        int m = origSample.length;
        int n = origSample[0].length - 1;
        
        for(int i = 0; i < m; ++i){
            origSample[i][n] = origSample[i][n] * scale + shift;
        }
        
        return(origSample);
        
    }

}
