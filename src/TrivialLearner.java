
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Trivial Learner: implemented the low-level algorithm for learning Boolean
 * functions f: {0,1}^n --> R.
 *
 */

public class TrivialLearner {
    
    // Useful parameters
    public final int numObs;
    public final int numFeatures;
    private final String sysName;
    private final String sampleLoc;
    private final double[][] allSamples;
    public double[] transformParam;
    
    /**
     * Constructor: provide the name of the system and the location of samples
     * @param sysName
     * @param sampleLoc
     */
    public TrivialLearner(String sysName, String sampleLoc){
        this.sysName = sysName;
        this.sampleLoc = sampleLoc;
        this.allSamples = this.readSample(sampleLoc);
        this.numObs = this.allSamples.length;
        this.numFeatures = this.allSamples[0].length - 1;
        this.transformParam = new double[2];
    }
    
    
    /**
     * Section 1: Utility functions.
     */
    
    /**
     * Print the information about this FourierLearner.
     */
    public void print(){
        System.out.println(" This FourierLearner:\n System: " + this.sysName + 
                "\n Sample Location: " + this.sampleLoc + 
                "\n Number of Features: " + this.numFeatures + 
                "\n Total number of Observations: " + this.numObs);
    }
    

    /**
     * Directly read the samples line by line into a HashMap for lookup.
     * @param sampleLoc
     * @return A HashMap with Strings as keys and double as values.
     */
    private double[][] readSample(String sampleLoc){
        
        try{
            FileReader r = new FileReader(sampleLoc);
            BufferedReader in = new BufferedReader(r);
            
            String firstLine = in.readLine();
            String[] splitFirstLine = firstLine.split(" ");
            int numRow = Integer.parseInt(splitFirstLine[0]);
            int numCol = Integer.parseInt(splitFirstLine[1]);
            
            double[][] allSample = new double[numRow][numCol];
            
            for(int i = 0; i < numRow; ++i){
                String line = in.readLine();
                
                String [] splitLine = line.split(" ");
        
                for(int j = 0; j < numCol; ++j){
                    allSample[i][j] = Double.parseDouble(splitLine[j]);
                }
                
            }
            
            return(allSample);
            
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }
                    
    /**
     * Approximate the sum of the squares of the Fourier coefficients starting
     * with the prefix alpha.
     * @param alpha
     * @return The sum of the squares.
     */
    private double approx(double[] alpha, double[][] sample){
        
        int m = sample.length;
        int n = this.numFeatures;
        
        double A = 0;
        for(int i = 0; i < m; ++i){
            A += sample[i][n]*Matrix.character(alpha, sample[i]);
        }
        
            return(A/m);
    }  
    

    public double[][] learn(int numSamples, double theta, int maxLevel){
        
        int n = this.numFeatures;
        
        // Warn is there aren't enough samples
        if(numSamples > this.numObs){
            System.out.println("Warning: Don't have that many samples.");
            numSamples = this.numObs;
        }
        
        // Shuffle the samples
        List<Integer> numList = new ArrayList<>(this.numObs);
        for(int i = 0; i < this.numObs;++i){
            numList.add(i, i);
        }
        
        Collections.shuffle(numList);
        
        double[][] sampleToUse = new double [numSamples][n +1];
        
        for(int i = 0; i < numSamples; ++i){
            sampleToUse[i] = this.allSamples[numList.get(i)];
        }
        
        sampleToUse = this.normalizeSample(sampleToUse);
        
        // Initialize the Fourier coefficients.
        double[][] allCoefs = new double [(int) Math.pow(2, n)][n + 1];
        
        Comparator<double[]> comp = new Comparator<double[]>(){
            @Override
            public int compare(double[] a, double [] b){
                return Double.compare(Math.abs(b[b.length-1]), Math.abs(a[a.length-1]));
            }
        };

        int i = 0;
        int nonZeroCount = 0;
        while(i < allCoefs.length){
            double [] vec = Matrix.intToVec(i, n);
            
            if(Matrix.sum(vec) <= maxLevel){
                double val = this.approx(vec, sampleToUse);

                if(Math.abs(val) > theta){
                    allCoefs[i] = Arrays.copyOf(vec, n+1);
                    allCoefs[i][n] = val;
                    ++nonZeroCount;
                }
            }

            ++i;
        }
        
        Arrays.sort(allCoefs, comp);
        
        allCoefs = Arrays.copyOfRange(allCoefs, 0, nonZeroCount);

        return(allCoefs);
    }
    
    public double[][] oldLearn(int numSamples, int numCoefs, Boolean sorted){
        
        int n = this.numFeatures;
        
        if(numSamples > this.numObs){
            System.out.println("Warning: Don't have that many samples.");
            numSamples = this.numObs;
        }
        
        List<Integer> numList = new ArrayList<>(this.numObs);
        for(int i = 0; i < this.numObs;++i){
            numList.add(i, i);
        }
        
        Collections.shuffle(numList);
        
        double[][] sampleToUse = new double [numSamples][n +1];
        
        for(int i = 0; i < numSamples; ++i){
            sampleToUse[i] = this.allSamples[numList.get(i)];
        }
        
        double[][] allCoefs = new double [(int) Math.pow(2, n)][n + 1];
        
        if(sorted){
            
            Comparator<double[]> comp = new Comparator<double[]>(){
                @Override
                public int compare(double[] a, double [] b){
                    return Double.compare(Math.abs(b[b.length-1]), Math.abs(a[a.length-1]));
                }
            };
            
            for(int i = 0; i < allCoefs.length; ++i){
                double [] vec = Matrix.intToVec(i, n);
                allCoefs[i] = Arrays.copyOf(vec, n+1);
                allCoefs[i][n] = this.approx(vec, sampleToUse);
            }
            
            Arrays.sort(allCoefs, comp);
            
            allCoefs = Arrays.copyOfRange(allCoefs, 0, numCoefs);
            
        }else{
            for(int i = 0; i < allCoefs.length; ++i){

                double[] vec = Matrix.intToVec(i, n);

                allCoefs[i] = Arrays.copyOf(vec, n+1);

                if(i < numCoefs){
                    allCoefs[i][n] = this.approx(vec, sampleToUse);
                    }

                }
        }
               
        return(allCoefs);
    }
    
    private double[][] normalizeSample(double[][] origSample){
        
        int m = origSample.length;
        int n = origSample[0].length;
        
        double[] vals = new double[m];        
        for(int i = 0; i < m; ++i){
            vals[i] = origSample[i][n-1];
        }
        
        double ave = Matrix.mean(vals);
        for(int i = 0; i < m; ++i){
            origSample[i][n-1] = origSample[i][n-1] - ave;
        }
        
        double max = Matrix.maxAbsCol(origSample, n-1);
        for(int i = 0; i < m; ++i){
            origSample[i][n-1] = origSample[i][n-1]/(max);
        }
        
        this.transformParam[0] = ave;
        this.transformParam[1] = max;
        
        return(origSample);
    }
    
    
}
