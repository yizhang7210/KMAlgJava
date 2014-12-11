
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author yzhang
 */
public class Matrix {
    
    public static double[] intToVec(int val, int len){
        
        if(len == 0){
            return(new double[0]);
        }
        
        double [] vec = new double[len];
        
        String s = Integer.toBinaryString(val);
        char [] chars = s.toCharArray();
        
        int charlen = chars.length;
        
        for(int i = 0; i< charlen; ++i){
            vec[i + len - charlen] = Character.getNumericValue(chars[i]);
        }
        
        return(vec);
    }
    
    public static int character(double[] z, double[] alpha){
        
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
    
    public static double[][] read(String file){
        
        try{
            FileReader r = new FileReader(file);
            BufferedReader in = new BufferedReader(r);
            
            String firstLine = in.readLine();
            String[] dims = firstLine.split(" ");
            int m = Integer.parseInt(dims[0]);
            int n = Integer.parseInt(dims[1]);
            
            double[][] mat = new double[m][n];
            
            for(int i = 0; i < m; ++i){
                String s = in.readLine();
                s = s.trim();
                s = s.replace(".0 ", " ");
                
                String [] line = s.split(" ");
                
                for(int j = 0; j < n; ++j){
                    mat[i][j] = Double.parseDouble(line[j]);
                } 
            }
            
            return(mat);
              
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }
    
    public static void print(double [][] mat){
        int m = mat.length;
        int n = mat[0].length;
        
        for(int i = 0; i < m; ++i){
            for(int j = 0; j < n-1; ++j){
                System.out.print(mat[i][j]+" ");
            }
            System.out.println(mat[i][n-1]);
        }
        
        System.out.println("\nThe dimension is: " + m + " by " + n + ".");
    }
    
    
    public static void write(double[][] mat, String file){
        
        try{
            PrintWriter w = new PrintWriter(file);
            
            int m = mat.length;
            int n = mat[0].length;
            
            w.println(m + " " +n);
            
            for(int i = 0; i < m; ++i){
                for(int j = 0; j < n-1; ++j){
                    w.print(mat[i][j] + " ");
                }
                w.println(mat[i][n-1]);
            }
            
            w.close();
              
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }
    
    public static double maxAbsCol(double[][] mat, int col){
        
        int m = mat.length;
        double max = -1;
        
        for(int i = 0; i < m; ++i){
            if(mat[i][col] > max){
                max = mat[i][col];
            }
        }
        
        return(max);
    }
    
}
