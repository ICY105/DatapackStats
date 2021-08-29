package datapack_stats;

public enum EExecuteArg {
	AS			(0),
	AT			(1),
	POSITIONED	(2),
	ALIGN		(3),
	FACING		(4),
	ROTATED		(5),
	IN			(6),
	ANCHORED	(7),
	IF			(8),
	UNLESS		(9),
	STORE		(10);
	
	private final int INDEX;
	
	EExecuteArg(int i) {
		INDEX = i;
	}
	
	public int getIndex() {
		return INDEX;
	}
}
