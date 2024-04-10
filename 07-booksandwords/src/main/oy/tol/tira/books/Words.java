package oy.tol.tira.books;

public class Words implements Comparable<Words> {
	String word;
	int count;
	int hash;
	public Words(){
		this.word="";
		this.count=0;
		hash=hashCode();
	}
	public Words(String word) {
		this(word,1);
	}

	public Words(String word, int count) {
		this.word = word;
		this.count = count;
		hash=hashCode();
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
	public String toString() {
		return "word="+word+" count="+count;
	}
	@Override
	public boolean equals(Object other) {
		if (other instanceof Words) {
			return this.word.equals(((Words)other).word);
		}
		return false;
	}
	@Override
	public int compareTo(Words other) {
		return (other.count)-count;
	}
}
