
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import org.ejml.simple.SimpleMatrix;

/**
 * Fourier Learner: implemented the Kushilevitz-Mansour algorithm as well as the
 * low degree algorithm for learning Boolean functions f: {0,1}^n --> R.
 *
 */

public class FourierLeaner {
    
    // Useful parameters
    private int count = 0;
    private final int numObs;
    private final int numFeatures;
    private final String sysName;
    private final String sampleLoc;
    private final HashMap<String, Double> allSampleMap;
    private SimpleMatrix[][] currentSample;
    
    /**
     * Constructor: provide the name of the system and the location of samples
     * @param sysName
     * @param sampleLoc
     */
    public FourierLeaner(String sysName, String sampleLoc){
        this.sysName = sysName;
        this.sampleLoc = sampleLoc;
        this.numObs = this.getSampleDim(sampleLoc)[0];
        this.numFeatures = this.getSampleDim(sampleLoc)[1] - 1;
        this.allSampleMap = this.readSampleToMap(sampleLoc);
        this.currentSample = null;
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
    private int[] getSampleDim(String sampleLoc){
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
     * Calculate the character of z and alpha.
     * @param z
     * @param alpha
     * @return char(z, alpha)
     */
    private int character(SimpleMatrix z, SimpleMatrix alpha){
        return(int) (Math.pow(-1, z.dot(alpha)));
    }
    
    /**
     * Lookup the value of the input vec in the HashMap, return 0 if not found.
     * @param vec
     * @return f(vec) or 0 if not sampled.
     */
    private double lookup(SimpleMatrix vec){
    
        String key = "";
        
        for(int i = 0; i < vec.numCols();++i){
            key += Integer.toString((int) vec.get(0, i));
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
    private SimpleMatrix[][] generateSample(int m1, int m2){
    
        SimpleMatrix [][] theSample = new SimpleMatrix[this.numFeatures][2];
        
        Random gen = new Random();
        
        // k index the length of alpha, n rounds
        for(int k = 1; k <= this.numFeatures;++k){
            
            int mm1 = (int) Math.min(m1, 2*Math.pow(2, this.numFeatures - k));
            int mm2 = (int) Math.min(m2, 2*Math.pow(2, k));
            
            SimpleMatrix thisX = new SimpleMatrix(mm1, this.numFeatures - k);
            SimpleMatrix thisY = new SimpleMatrix(mm2, k);
            
            // initialize thisX
            for(int i = 0; i < thisX.numRows(); ++i){
                for(int j = 0; j < this.numFeatures - k; ++j){
                    thisX.set(i, j, gen.nextInt(2));
                }
            }
            
            // initialize thisY
            for(int i = 0; i < thisY.numRows(); ++i){
                for(int j = 0; j < k; ++j){
                    thisY.set(i, j, gen.nextInt(2));
                }
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
    private double approx(SimpleMatrix alpha){
        
        int k = alpha.numCols();
        
        if(k == 0){
            return(Double.POSITIVE_INFINITY);
        }
        
        // sampleX has m1 rows and n-k columns
        // sampleY has m2 rows and k columns
        
        if(k < this.numFeatures){
            SimpleMatrix sampleX = this.currentSample[k-1][0];
            SimpleMatrix sampleY = this.currentSample[k-1][1];
            
            int m1 = sampleX.numRows();
            int m2 = sampleY.numRows();
            
            int [] charYs = new int[m2];
            
            for(int i = 0; i < m2; ++i){
                charYs[i] = this.character(alpha, sampleY.extractVector(true, i));
            }
            
            double A = 0;
            double B = 0;
            
            for(int i = 0; i < m1;++i){
            
                SimpleMatrix thisX = sampleX.extractVector(true, i);
                
                for(int j = 0; j < m2; ++j){
                    SimpleMatrix thisY = sampleY.extractVector(true, j);
                    A += this.lookup(thisY.combine(0, thisY.numCols(), thisX))*charYs[j];
                }
            
                B += (A/m2)*(A/m2);
            }
            
            return(B/m1);
        }else{// k = n
            this.count += 1;
            System.out.println(this.count + "th coef");
            
            SimpleMatrix sampleY = this.currentSample[k-1][1];
            
            int m2 = sampleY.numRows();
            int [] charYs = new int[m2];
            
            for(int i = 0; i < m2; ++i){
                charYs[i] = this.character(alpha, sampleY.extractVector(true, i));
            }
            
            double A = 0;
            for(int j = 0; j < m2; ++j){
                SimpleMatrix thisY = sampleY.extractVector(true, j);
                A += this.lookup(thisY)*charYs[j];
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
        
        SimpleMatrix nullMat = new SimpleMatrix(0, 0);
        
        fCoefs = this.learnByKMImp(theta, delta, nullMat, fCoefs);
        
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
                    SimpleMatrix alpha, SimpleMatrix fCoefs){
    
        double bucketWeight = this.approx(alpha);
        
        SimpleMatrix bucketWeightMat = SimpleMatrix.diag(
                Math.signum(bucketWeight)*Math.sqrt(Math.abs(bucketWeight)));
        
        if(Math.abs(bucketWeight) >= theta*theta/2){
            if(alpha.numCols() == this.numFeatures){
                SimpleMatrix newRow = alpha.combine(0, this.numFeatures, bucketWeightMat);
                
                fCoefs = fCoefs.combine(fCoefs.numRows(), 0, newRow);
            }else{
                
                SimpleMatrix newAlpha1 = alpha.combine(0, alpha.numCols(), SimpleMatrix.diag(0));
                SimpleMatrix newAlpha2 = alpha.combine(0, alpha.numCols(), SimpleMatrix.diag(1));
                
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
