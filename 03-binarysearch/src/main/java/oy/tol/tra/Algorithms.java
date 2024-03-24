package oy.tol.tra;

/**
 * A simple array of student grades to be used in testing
 * misbehaving algorithm for reversing the array.
 */

public class Algorithms {

	private Integer[] array = null;

	public Algorithms(Integer[] array) {
		this.array = new Integer[array.length];
		for (int counter = 0; counter < array.length; counter++) {
			this.array[counter] = array[counter];
		}
	}

	/**
	 * The method to reverse the internal Java int array.
	 */
	public static <T extends Comparable<T>> void reverse(T[] array) {
		int len = array.length;
		for (int i = 0; i < len / 2; i++) {
			T temp = array[i];
			array[i] = array[len - 1 - i];
			array[len - 1 - i] = temp;
		}
	}

	/**
	 * Sorts the array to ascending order.
	 */
	public static <T extends Comparable<T>> void sort(T[] array) {
		int len = array.length;
		for (int i = 0; i < len - 1; i++) {
			for (int j = i + 1; j < len; j++) {
				T x1 = array[i];
				T x2 = array[j];
				if (x1.compareTo(x2) > 0) {
					array[i] = x2;
					array[j] = x1;
				}
			}
		}
	}

	public static <E extends Comparable<E>> void fastSort(E[] array, int begin, int end) {
		quickSort(array, begin, end);
	}

	static class Stack {
		private int maxSize;
		private int[] stackArray;
		private int top;

		public Stack(int size) {
			maxSize = size;
			stackArray = new int[maxSize];
			top = -1;
		}

		public void push(int j) {
			stackArray[++top] = j;
		}

		public int pop() {
			return stackArray[top--];
		}

		public int peek() {
			return stackArray[top];
		}

		public boolean isEmpty() {
			return (top == -1);
		}
	}

	public static <E extends Comparable<E>> void quickSort(E[] array, int begin, int end) {
		if (begin >= end) return; // Base case

		// Create a stack
		int stackSize = end - begin + 1;
		Stack stack = new Stack(stackSize);

		// Push initial values of begin and end to the stack
		stack.push(begin);
		stack.push(end);

		// Keep popping from stack while it is not empty
		while (!stack.isEmpty()) {
			// Pop end and begin
			end = stack.pop();
			begin = stack.pop();

			// Set pivot element at its correct position in sorted array
			int partitionIndex = partition(array, begin, end);

			// If there are elements on left side of pivot, then push left side to stack
			if (partitionIndex - 1 > begin) {
				stack.push(begin);
				stack.push(partitionIndex - 1);
			}

			// If there are elements on right side of pivot, then push right side to stack
			if (partitionIndex + 1 < end) {
				stack.push(partitionIndex + 1);
				stack.push(end);
			}
		}
	}

	private static <E extends Comparable<E>> int partition(E[] array, int begin, int end) {
		E pivot = array[end];
		int i = begin - 1;
		for (int j = begin; j < end; j++) {
			if (array[j].compareTo(pivot) <= 0) {
				i++;
				E temp = array[i];
				array[i] = array[j];
				array[j] = temp;
			}
		}
		E temp = array[i + 1];
		array[i + 1] = array[end];
		array[end] = temp;
		return i + 1;
	}

	public static <T extends Comparable<T>> int binarySearch(T aValue, T[] fromArray, int fromIndex, int toIndex) {
		if (toIndex >= fromIndex) {
			int mid = fromIndex + (toIndex - fromIndex) / 2;
			if (fromArray[mid].equals(aValue)) {
				return mid;
			}
			if (fromArray[mid].compareTo(aValue) > 0) {
				return binarySearch(aValue, fromArray, fromIndex, mid - 1);
			}
			return binarySearch(aValue, fromArray, mid + 1, toIndex);
		}
		return -1;
	}

	/**
	 * Returns the plain Java int [] array for investigation.
	 *
	 * @return The int array.
	 */
	public Integer[] getArray() {
		return array;
	}


}
