package datapack_stats;

public enum ESelectors {
	P (0),
	E (1),
	S (2),
	A (3),
	R (4);
	
	private final int INDEX;
	
	ESelectors(int i) {
		INDEX = i;
	}
	
	public int getIndex() {
		return INDEX;
	}
}
