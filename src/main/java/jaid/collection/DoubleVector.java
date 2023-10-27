package jaid.collection;

import com.google.common.base.Preconditions;

import java.util.Arrays;

public class DoubleVector implements IVector {

    public double[] contents;

    public DoubleVector(double[] contents) {
        this.contents = Preconditions.checkNotNull(contents);
    }

    public double meanSquaredError(final DoubleVector comparedTo) {
        return distance(contents, comparedTo.contents);
    }

    /**
     * Calculate the mean squared error.
     * for vectors v1 and v2 of length I calculate
     * MSE = 1/I * Sum[ ( v1_i - v2_i )^2 ]
     */
    public static double distance(final double[] vector1, final double[] vector2) {
        if (vector1.length != vector2.length) {
            throw new IllegalArgumentException();
        }
        double sum = 0;
        for(int i = 0; i < vector1.length; i++) {
            sum += Math.pow(vector1[i] - vector2[i] , 2 );
        }
        return (sum / vector1.length);
    }

    @Override
    public double dotProduct(final IVector comparedTo) {
        if (!(comparedTo instanceof DoubleVector) || contents.length != ((FloatVector)comparedTo).contents.length) {
            throw new IllegalArgumentException();
        }
        double sum = 0;
        for (int i = 0; i < contents.length; ++i) {
            sum = Math.fma(contents[i], ((DoubleVector)comparedTo).contents[i], sum);
        }
        return sum;
        // TODO when panama is no longer incubating, the following should provide a large speedup
//        var sum = YMM_DOUBLE.zero();
//        for (int i = 0; i < size; i += YMM_DOUBLE.length()) {
//            var l = YMM_DOUBLE.fromArray(left, i);
//            var r = YMM_DOUBLE.fromArray(right, i);
//            sum = l.fma(r, sum);
//        }
//        return sum.addAll();
    }

    @Override
    public int simHash() {
        int[] accum = new int[32];
        for (int i = 0; i < contents.length; i++) {
            long hash = Double.doubleToLongBits(contents[i]);
            for (int j = 0; j < 32; j++) {
                if ((hash & (1 << j)) != 0) {
                    accum[j]++;
                } else {
                    accum[j]--;
                }
            }
        }
        int finalHash = 0;
        for (int j = 0; j < 32; j++) {
            if (accum[j] > 0) {
                finalHash |= (1 << j);
            }
        }
        return finalHash;
    }

    @Override
    public String toString() {
        return Arrays.toString(contents);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DoubleVector that = (DoubleVector) o;
        return Arrays.equals(contents, that.contents);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(contents);
    }
}
