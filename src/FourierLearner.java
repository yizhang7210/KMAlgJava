
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import org.ejml.simple.SimpleMatrix;

/**
 * Fourier Learner: implemented the Kushilevitz-Mansour algorithm as well as the
 * low degree algorithm for learning Boolean functions f: {0,1}^n --> R.
 *
 */

public class FourierLearner {
    
    // Useful parameters
    private final int numObs;
    private final int numFeatures;
    private final String sysName;
    private final String sampleLoc;
    private final HashMap<String, Double> allSampleMap;
    private double[][][][] currentSample;
    public HashSet<String> uniqueSample;
    private int firstTime;
    
    /**
     * Constructor: provide the name of the system and the location of samples
     * @param sysName
     * @param sampleLoc
     */
    public FourierLearner(String sysName, String sampleLoc){
        this.sysName = sysName;
        this.sampleLoc = sampleLoc;
        this.numObs = FourierLearner.getSampleDim(sampleLoc)[0];
        this.numFeatures = FourierLearner.getSampleDim(sampleLoc)[1] - 1;
        this.allSampleMap = this.readSampleToMap(sampleLoc);
        this.currentSample = null;
        this.uniqueSample = new HashSet<>();
        this.firstTime = 0;
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
     * Get the dimension of the collected sample, namely, number of features
     * and number of observations.
     * @param sampleLoc
     * @return [numFeatures, numObservations]
     */
    public static int[] getSampleDim(String sampleLoc){
        try{
            FileReader r = new FileReader(sampleLoc);
            BufferedReader in = new BufferedReader(r);
            String firstLine = in.readLine();
            String[] dims = firstLine.split(" ");
            int numOb = Integer.parseInt(dims[0]);
            int numFeature = Integer.parseInt(dims[1]);
            
            int [] sampleDim = {numOb, numFeature};
            
            return(sampleDim);
        }catch(IOException | NumberFormatException e){
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Directly read the samples line by line into a HashMap for lookup.
     * @param sampleLoc
     * @return A HashMap with Strings as keys and double as values.
     */
    private HashMap<String, Double> readSampleToMap(String sampleLoc){
    
        HashMap<String, Double> f = new HashMap<>();
        
        try{
            FileReader r = new FileReader(sampleLoc);
            BufferedReader in = new BufferedReader(r);
            
            in.readLine();
            
            for(int i = 0; i < this.numObs; ++i){
                String s = in.readLine();
                s = s.trim();
                s = s.replace(".0 ", " ");
                
                int split = s.lastIndexOf(" ");
                
                String key = s.substring(0, split).replaceAll(" ", "");
                double val = Double.parseDouble(s.substring(split));
                
                f.put(key, val);
                
            }
              
        }catch(IOException e){
            throw new RuntimeException(e);
        }
        
        return(f);
    }
        
    /**
     * Lookup the value of the input vec in the HashMap, return 0 if not found.
     * @param vec
     * @return f(vec) or 0 if not sampled.
     */
    private double lookup(double[] vec){
    
        String key = "";
        
        for(int i = 0; i < vec.length ;++i){
            key += Integer.toString((int) vec[i]);
        }
    
        double val = this.allSampleMap.getOrDefault(key, 0.0);
        
        return val;
    }
    
            
    /**
     * Generate the n pairs of samples for both the prefix and suffix.
     * @param m1
     * @param m2
     * @return An n by 2 array of SimpleMatrix. Each row corresponds to a set
     * of samples for a prefix of length k. First column is the sample of
     * prefixes and the second column is the sample of the suffixes.
     */
    private double[][][][] generateSample(int m1, int m2){
        
        int n = this.numFeatures;
    
        double [][][][] theSample = new double [n][2][][];
        
        Random gen = new Random();
        
        // k index the length of alpha, n rounds
        for(int k = 1; k <= n; ++k){
            int mm1 = (int) Math.min(m1, 20*Math.pow(2, n - k));
            int mm2 = (int) Math.min(m2, 20*Math.pow(2, k));
            
            double [][] thisX = new double[m1][n - k];
            double [][] thisY = new double[m2][k];
            
            // initialize thisX
            for(int i = 0; i < thisX.length; ++i){
                thisX[i] = Matrix.intToVec(gen.nextInt((int) Math.pow(2, n-k)), n - k);
            }
            
            // initialize thisY
            for(int i = 0; i < thisY.length; ++i){
                thisY[i] = Matrix.intToVec(gen.nextInt((int) Math.pow(2, k)), k);
            }
            
            theSample[k-1][0] = thisX;
            theSample[k-1][1] = thisY;
            
        }
        
        System.out.println("Done generating Samples");
        return(theSample);
    }
        
    /**
     * Approximate the sum of the squares of the Fourier coefficients starting
     * with the prefix alpha.
     * @param alpha
     * @return The sum of the squares.
     */
    private double approx(double[] alpha){
        
        int k = alpha.length;
        
        if(k == 0){
            return(Double.POSITIVE_INFINITY);
        }
        
        // sampleX has m1 rows and n-k columns
        // sampleY has m2 rows and k columns
        
        if(k < this.numFeatures){
            double[][] sampleX = this.currentSample[k-1][0];
            double[][] sampleY = this.currentSample[k-1][1];
            
            int m1 = sampleX.length;
            int m2 = sampleY.length;
            
            int [] charYs = new int[m2];
            
            for(int i = 0; i < m2; ++i){
                charYs[i] = Matrix.character(alpha, sampleY[i]);
            }
            
            double A = 0;
            double B = 0;
            
            for(int i = 0; i < m1;++i){
            
                double[] thisX = sampleX[i];
                
                for(int j = 0; j < m2; ++j){
                    double[] thisY = sampleY[j];
                    
                    double[] thisSample = Matrix.concat(thisY, thisX);
                    
                    A += this.lookup(thisSample)*charYs[j];
                    
                    this.uniqueSample.add(Arrays.toString(thisSample));
                }
                
                B += (A/m2)*(A/m2);
            }
            
            return(B/m1);
        }else{// k = n
            
            double[][] sampleY = this.currentSample[k-1][1];
            
            int m2 = sampleY.length;
            int [] charYs = new int[m2];
            
            for(int i = 0; i < m2; ++i){
                charYs[i] = Matrix.character(alpha, sampleY[i]);
            }
            
            double A = 0;
            for(int j = 0; j < m2; ++j){
                double[] thisY = sampleY[j];
                A += this.lookup(thisY)*charYs[j];
                
                this.uniqueSample.add(Arrays.toString(thisY));
                
            }
                double B = A/m2;
                return(Math.signum(B)*B*B);
            }    
            
        }
        
    
    /**
     * Section 2: User required functions.
     */
    
    /**
     * Estimate all Fourier coefficients above theta.
     * @param theta
     * @param delta
     * @return A SimpleMatrix listing all large Fourier coefficients.
     */
    public SimpleMatrix learnByKM(double theta, double delta){

        SimpleMatrix fCoefs = new SimpleMatrix(0, this.numFeatures + 1);
        
        int m1 = (int) Math.max(1, Math.ceil(48/(theta*theta)*
               Math.log(2*this.numFeatures/(delta*theta*theta))));
        int m2 = (int) Math.max(1, Math.ceil(512/Math.pow(theta, 4)*
               Math.log(2*this.numFeatures*m1/(delta*theta*theta))));
        
        System.out.println("m1 is: "+ m1 + " and m2 is: " + m2);
        
        this.currentSample = this.generateSample(m1, m2);
               
        double[] emptyAlpha = new double [0];
        
        fCoefs = this.learnByKMImp(theta, delta, emptyAlpha, fCoefs);
               
        return(fCoefs);
    }
    
    /**
     * Recursive core of the learnByKM function.
     * @param theta
     * @param delta
     * @param alpha
     * @param fCoefs
     * @return Recursively return the intermediate list of Fourier coefficients.
     */
    private SimpleMatrix learnByKMImp(double theta, double delta, 
                    double[] alpha, SimpleMatrix fCoefs){
    
        double bucketWeight = this.approx(alpha);
        
        double [] bucketWeightMat = {Math.signum(bucketWeight)*Math.sqrt(Math.abs(bucketWeight))};
        
        if(Math.abs(bucketWeight) >= theta*theta/2){
            if(alpha.length == this.numFeatures){
                
                if(this.firstTime == 0){
                    System.out.println("The sample size is: " + this.uniqueSample.size());
                    this.firstTime = 1;
                }
                
                
                double[][] newRow = {Matrix.concat(alpha, bucketWeightMat)};
                
                SimpleMatrix newRowMat = new SimpleMatrix(newRow);
                
                fCoefs = fCoefs.combine(fCoefs.numRows(), 0, newRowMat);
            }else{
                
                double [] zero = {0};
                double [] one = {1};
                double[] newAlpha1 = Matrix.concat(alpha, zero);
                double[] newAlpha2 = Matrix.concat(alpha, one);
                
                SimpleMatrix table1 = this.learnByKMImp(theta, delta, newAlpha1, fCoefs);
                SimpleMatrix table2 = this.learnByKMImp(theta, delta, newAlpha2, fCoefs);
                
                fCoefs = table1.combine(table1.numRows(), 0, table2);
            }
            
        }
        
        return(fCoefs);

    }

    /**
     * The low degree algorithm in implementation.
     */
    //public SimpleMatrix learnByLowLevel(int k){
        
        
    //}
    
}
