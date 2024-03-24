package oy.tol.tra;

/**
 * An implementation of the QueueInterface.
 */
public class QueueImplementation<E> implements QueueInterface<E> {

	private Object[] itemArray;
	private int capacity;
	private int front;
	private int rear;

	private int tempElementSize = 0;

	private static final int DEFAULT_QUEUE_SIZE = 10;

	public QueueImplementation() throws QueueAllocationException {
		this(DEFAULT_QUEUE_SIZE);
	}

	public QueueImplementation(int capacity) throws QueueAllocationException {
		if (capacity < 1) {
			throw new QueueAllocationException("Invalid capacity");
		}
		this.capacity = capacity;
		itemArray = new Object[capacity];
		front = rear = -1;
	}

	@Override
	public int capacity() {
		return capacity;
	}

	@Override
	public void enqueue(E element) throws QueueAllocationException, NullPointerException {
		if (element == null) {
			throw new NullPointerException("Element cannot be NULL");
		}
		if (tempElementSize == capacity) {
			//todo capacity*2
			doubleCapacity();
		}
		if (isEmpty()) {
			front = rear = 0;
		} else {
			rear = (rear + 1) % capacity;
		}
		itemArray[rear] = element;
		tempElementSize++;
	}

	@Override
	@SuppressWarnings("unchecked")
	public E dequeue() throws QueueIsEmptyException {
		if (isEmpty()) {
			throw new QueueIsEmptyException("Queue is empty");
		}
		E element = (E) itemArray[front];
		tempElementSize--;
		if (front == rear) {
			front = rear = -1;
		} else {
			front = (front + 1) % capacity;
		}
		return element;
	}

	@Override
	@SuppressWarnings("unchecked")
	public E element() throws QueueIsEmptyException {
		if (isEmpty()) {
			throw new QueueIsEmptyException("Queue is empty");
		}
		return (E) itemArray[front];
	}

	@Override
	public int size() {
		if (isEmpty()) {
			return 0;
		}
		return tempElementSize;
	}

	@Override
	public boolean isEmpty() {
		return tempElementSize == 0;
	}

	@Override
	public void clear() {
		front = rear = -1;
		tempElementSize = 0;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder("[");
		if (!isEmpty()) {
			if (front <= rear) {
				for (int i = front; i <= rear; i++) {
					builder.append(itemArray[i]);
					if (i < rear) {
						builder.append(", ");
					}
				}
			} else {
				for (int i = front; i < capacity; i++) {
					builder.append(itemArray[i]);
					builder.append(", ");
				}
				for (int i = 0; i <= rear; i++) {
					builder.append(itemArray[i]);
					if (i < rear) {
						builder.append(", ");
					}
				}
			}
		}
		builder.append("]");
		return builder.toString();
	}

	private void doubleCapacity() throws QueueAllocationException {
		int newCapacity = capacity * 2;
		Object[] newArray = new Object[newCapacity];
		if (front <= rear) {
			for (int i = 0; i < capacity; i++) {
				newArray[i] = itemArray[i];
			}
		} else {
			// copy
			int j = 0;
			for (int i = front; i < capacity; i++) {
				newArray[j++] = itemArray[i];
			}
			for (int i = 0; i <= rear; i++) {
				newArray[j++] = itemArray[i];
			}
		}
		itemArray = newArray;
		front = 0;
		rear = capacity - 1;
		capacity = newCapacity;
	}
}
