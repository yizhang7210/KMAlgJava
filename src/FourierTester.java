
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author yzhang
 */
public class FourierTester {

    public static void GenerateTestToFile(String fileName, int dim, int noObs) {

        int total = (int) Math.pow(2, dim);

        if (noObs > total) {
            noObs = total;
        }

        List<Integer> numList = new ArrayList<>(total);
        for (int i = 0; i < total; ++i) {
            numList.add(i, i);
        }

        Collections.shuffle(numList);

        try {
            PrintWriter writer = new PrintWriter(fileName);
            writer.print(noObs + " ");
            writer.println(dim + 1);
            for (int i = 0; i < noObs; ++i) {
                double[] thisVec = Matrix.intToVec(numList.get(i), dim);
                for (int j = 0; j < dim; ++j) {
                    writer.print((int) thisVec[j] + " ");
                }

                //double coef = 2*Math.random() - 1;
                double coef = Math.random() * 20;
                //double coef = Math.signum(Math.random()-0.5)*(Math.random()*0.8+0.2);

                writer.println(coef);
            }

            writer.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void GenerateTestFromFourierToFile(String origCoefs,
            String origFun, int dim, int noObs, int sparcity) {

        FourierTester.GenerateTestToFile(origCoefs, dim, sparcity);
        FourierTester.GenerateTestToFile(origFun, dim, noObs);

        double[][] fCoefs = Matrix.read(origCoefs);

        FourierEstimator E = new FourierEstimator(fCoefs, 3, 20);

        E.estimateSamples(origFun, origFun, (int) Math.pow(2, dim));

    }

}
