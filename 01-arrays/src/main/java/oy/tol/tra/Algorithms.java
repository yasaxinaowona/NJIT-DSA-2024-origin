package oy.tol.tra;

/**
 * A simple array of student grades to be used in testing
 * misbehaving algorithm for reversing the array.
 */

public class Algorithms {

    private Integer [] array = null;

    public Algorithms(Integer [] array) {
        this.array = new Integer [array.length];
        for (int counter = 0; counter < array.length; counter++) {
            this.array[counter] = array[counter];
        }
    }

    /**
     * The method to reverse the internal Java int array.
     */
    public static <T extends Comparable<T>> void reverse(T[] array) {
        int len = array.length;
        for(int i = 0; i < len/2; i++){
            T temp = array[i];
            array[i] = array[len-1-i];
            array[len-1-i] = temp;
        }
    }

    /**
     * Sorts the array to ascending order.
     */
    public static <T extends Comparable<T>> void sort(T[] array) {
        int len = array.length;
        for(int i = 0; i < len-1; i++){
            for(int j = i+1; j < len; j++){
                T x1 = array[i];
                T x2 = array[j];
                if(x1.compareTo(x2) > 0){
                    array[i] = x2;
                    array[j] = x1;
                }
            }
        }
    }

    /**
     * Returns the plain Java int [] array for investigation.
     * @return The int array.
     */
    public Integer [] getArray() {
        return array;
    }


}
