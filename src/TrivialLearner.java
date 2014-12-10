
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.ejml.simple.SimpleMatrix;

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
     * Calculate the character of z and alpha.
     * @param z
     * @param alpha
     * @return char(z, alpha)
     */
    private int character(double[] z, double[] alpha){
        
        double sum = 0;
        
        for(int i = 0; i < z.length; ++i){
            sum = sum + z[i]*alpha[i];
        }
        
        
        return(-2*((int)sum%2)+1);
    }
     
    public static double[] concat(double[] a, double[] b){
        int length = a.length + b.length;
        double[] result = new double[length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
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
            A += sample[i][n]*this.character(alpha, sample[i]);
        }
        
            return(A/m);
    }  
    

    public SimpleMatrix learn(int numSamples, double theta, Boolean sorted){
        
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
            double [] vec = FourierLearner.intToVec(i, n);

            double val = this.approx(vec, sampleToUse);

            if(Math.abs(val) > theta){
                allCoefs[i] = Arrays.copyOf(vec, n+1);
                allCoefs[i][n] = val;
                ++nonZeroCount;
            }

            ++i;
        }
        
        if(sorted){
            Arrays.sort(allCoefs, comp);
        }
        
        SimpleMatrix resultCoefs = new SimpleMatrix(allCoefs);
        
        resultCoefs = resultCoefs.extractMatrix(0, nonZeroCount, 0, n+1);

        return(resultCoefs);
    }
    
    public SimpleMatrix oldLearn(int numSamples, int numCoefs, Boolean sorted){
        
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
                double [] vec = FourierLearner.intToVec(i, n);
                allCoefs[i] = Arrays.copyOf(vec, n+1);
                allCoefs[i][n] = this.approx(vec, sampleToUse);
            }
            
            Arrays.sort(allCoefs, comp);
            
            allCoefs = Arrays.copyOfRange(allCoefs, 0, numCoefs);
            
            
        }else{
            for(int i = 0; i < allCoefs.length; ++i){

                double[] vec = FourierLearner.intToVec(i, n);

                allCoefs[i] = Arrays.copyOf(vec, n+1);

                if(i < numCoefs){
                    allCoefs[i][n] = this.approx(vec, sampleToUse);
                    }

                }
        }
               
        return(new SimpleMatrix(allCoefs));
    }
    
    
}
