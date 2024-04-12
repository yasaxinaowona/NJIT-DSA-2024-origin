package oy.tol.tira.books;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class HashImplementation implements Book {
	public class WordCount implements Comparable<WordCount> {
		String word;
		int count;
		public WordCount(){
			this.word="";
			this.count=0;
		}
		public WordCount(String word) {
			this(word,1);
		}

		public WordCount(String word, int count) {
			this.word = word;
			this.count = count;
		}

		@Override
		public int hashCode() {
			int hash=5381;
			String hashString=word;
			for (int i = 0; i < hashString.length(); i++) {
				hash=31*hash+hashString.charAt(i);
			}
			return hash;
		}


		@Override
		public int compareTo(WordCount other) {
			return (other.count)-count;
		}
	}
	private static final int MAX_WORDS = 100000;
	private static final int MAX_WORD_LEN = 100;
	private static final double LOAD_FACTOR = 0.45;

	private WordCount[] units = null;
	private String bookFile = null;
	private String wordsToIgnoreFile = null;
	private WordFilter filter = null;

	private int uniqueWordCount = 0;
	private int totalWordCount = 0;
	private int ignoredWordsTotal = 0;

	private long collisionCount = 0;
	private long reallocationCount = 0;
	private int maxProbingSteps = 0;
	@Override
	public void setSource(String fileName, String ignoreWordsFile) throws FileNotFoundException {
		// Check if both files exist. If not, throw an exception.
		boolean success = false;
		if (checkFile(fileName)) {
			bookFile = fileName;
			if (checkFile(ignoreWordsFile)) {
				wordsToIgnoreFile = ignoreWordsFile;
				success = true;
			}
		}
		if (!success) {
			throw new FileNotFoundException("Cannot find the specified files");
		}
	}


	@Override
	public void countUniqueWords() throws IOException, OutOfMemoryError {
		if (bookFile == null || wordsToIgnoreFile == null) {
			throw new IOException("No file(s) specified");
		}
		// Reset the counters
		uniqueWordCount = 0;
		totalWordCount = 0;
		collisionCount = 0;
		ignoredWordsTotal = 0;
		// Create an array for the words.
		units =new WordCount[MAX_WORDS];
		// Create the filter class to handle filtering.
		filter = new WordFilter();
		// Read the words to filter.
		filter.readFile(wordsToIgnoreFile);

		// Start reading from the book file using UTF-8.
		FileReader reader = new FileReader(bookFile, StandardCharsets.UTF_8);
		int c;
		// Array holds the code points of the UTF-8 encoded chars.
		int[] array = new int[MAX_WORD_LEN];
		int currentIndex = 0;
		while ((c = reader.read()) != -1) {
			// If the char is a letter, then add it to the array...
			if (Character.isLetter(c)) {
				array[currentIndex] = c;
				currentIndex++;
			} else {
				// ...otherwise a word break was met, so handle the word just read.
				if (currentIndex > 0) {
					// If a word was actually read, then create a string out of the codepoints,
					// normalizing the word to lowercase.
					String word = new String(array, 0, currentIndex).toLowerCase(Locale.ROOT);
					// Reset the counter for the next word read.
					currentIndex = 0;
					addToWords(new WordCount(word));
				}
			}
		}
		// Must check the last word in the file too. There may be chars in the array
		// not yet handled, when read() returns -1 to indicate EOF.
		if (currentIndex > 1) {
			String word = new String(array, 0, currentIndex).toLowerCase(Locale.ROOT);
			addToWords(new WordCount(word));
		}
		// Close the file reader.
		reader.close();
	}
	private void addToWords(WordCount Word) throws OutOfMemoryError {
		// Filter out too short words or words in filter list.
		if (!filter.shouldFilter(Word.word) && Word.word.length() >= 2) {
			// Checks if the LOAD_FACTOR has been exceeded --> if so, reallocates to a bigger hashtable.
			if (((double)uniqueWordCount * (1.0 + LOAD_FACTOR)) >= units.length) {
				ensureCap((int)((double)(units.length) * (1.0 / LOAD_FACTOR)));
			}

			int hash=Word.hashCode();
			int index=hash%units.length;
			if(index<0){
				index+=units.length;
			}
			// if index was taken by different WordCount (collision), get new hash and index,
			int tmpIndex;
			for(int i=0;;i++){
				tmpIndex=(index+i*i)%units.length;
				if(units[tmpIndex]==null){
					// insert into table when the index has a null in it,
					units[tmpIndex]=Word;
					uniqueWordCount++;
					break;
				}else if(units[tmpIndex].word.equals(Word.word)){
					//find the same word,count++
					units[tmpIndex].count+=1;
					break;
				}
				collisionCount++;
				if(i>maxProbingSteps){
					maxProbingSteps=i;
				}
			}
		} else {
			ignoredWordsTotal++;
		}
	}

	@Override
	public void report() {
		if (units == null) {
			System.out.println("*** No words to report! ***");
			return;
		}
		System.out.println("Listing words from a file: " + bookFile);
		System.out.println("Ignoring words from a file: " + wordsToIgnoreFile);
		System.out.println("Sorting the results...");
		// First sort the array
		int length=Algorithms.partitionByRule(units, units.length, element->element==null);
		reallocateArray(length);
		Algorithms.fastSort(units);
		System.out.println("...sorted.");

		for (int index = 0; index < 100; index++) {
			if (index>=units.length) {
				break;
			}
			String word = String.format("%-20s", units[index].word).replace(' ', '.');
			System.out.format("%4d. %s %6d%n", index + 1, word, units[index].count);
		}
		System.out.println("Count of words in total: " + totalWordCount);
		System.out.println("Count of unique words:    " + uniqueWordCount);
		System.out.println("Count of words to ignore:    " + filter.ignoreWordCount());
		System.out.println("Ignored words count:      " + ignoredWordsTotal);
		System.out.println("Data of the HashTable: ");
		System.out.println("Count of collision: " + collisionCount);
		System.out.println("Count of reallocation: " + reallocationCount);
		System.out.println("Max ProbingSteps: " + maxProbingSteps);

	}
	private void reallocateArray(int newSize) throws OutOfMemoryError {
		WordCount[] newWordArray = new WordCount[newSize];
		int newTotalWordCount = 0;
		for (int i = 0; i < newSize && i < units.length; i++) {
			newWordArray[i] = units[i];
			newTotalWordCount += units[i].count;
		}
		units = newWordArray;
		totalWordCount = newTotalWordCount;
	}

	@Override
	public int getUniqueWordCount() {
		return uniqueWordCount;
	}
	@Override
	public int getTotalWordCount() {
		return totalWordCount;
	}
	@Override
	public String getWordInListAt(int position) {
		if (units != null && position >= 0 && position < uniqueWordCount) {
			return units[position].word;
		}
		return null;
	}
	@Override
	public int getWordCountInListAt(int position) {
		if (units != null && position >= 0 && position < uniqueWordCount) {
			return units[position].count;
		}
		return -1;
	}
	private void ensureCap(int newSize) throws OutOfMemoryError {
		if (newSize < MAX_WORDS) {
			newSize = MAX_WORDS;
		}
		reallocationCount++;
		WordCount[] oldWordList = units;
		this.units = new WordCount[(int) (newSize * (1.0 + LOAD_FACTOR))];

		uniqueWordCount = 0;
		collisionCount = 0;
		maxProbingSteps = 0;
		for (WordCount oldWord : oldWordList) {
			if (oldWord != null) {
				addToWords(oldWord);
			}
		}
	}
	private boolean checkFile(String fileName) {
		if (fileName != null) {
			File file = new File(fileName);
			if (file.exists() && !file.isDirectory()) {
				return true;
			}
		}
		return false;
	}
	@Override
	public void close() {
		bookFile = null;
		wordsToIgnoreFile = null;
		units = null;
		if (filter != null) {
			filter.close();
		}
		filter = null;
	}
}
