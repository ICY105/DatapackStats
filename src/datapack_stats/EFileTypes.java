package datapack_stats;

public enum EFileTypes {
	DIRECTORIES		(0,  null, null, 			"Directories"),
	FUNCTIONS		(1,  null, ".mcfunction", 	"Functions"),
	ADVANCEMENTS	(2,  null, ".json", 		"Advancements"),
	STRUCTURES		(3,  null, ".nbt", 			"Structures"),
	LOOT_TABLES		(4,  null, ".json", 		"Loot Tables"),
	RECIPES			(5,  null, ".json", 		"Recipes"),
	ITEM_MOD		(25, null, ".json", 		"Item Modifiers"),
	TAGS			(6,  null, ".json", 		"Tags"),
	TAGS_BLOCK		(7,  TAGS, ".json", 		"Blocks"),
	TAGS_ENTITY		(8,  TAGS, ".json", 		"Entity Types"),
	TAGS_FLUID		(9,  TAGS, ".json", 		"Fluids"),
	TAGS_FUNCTION	(10, TAGS, ".json", 		"Functions"),
	TAGS_EVENT		(11, TAGS, ".json", 		"Game Events"),
	TAGS_ITEM		(12, TAGS, ".json", 		"Items"),
	WORLDGEN		(13, null, ".json", 		"Worldgen"),
	WORLDGEN_DIM	(14, WORLDGEN, ".json", 	"Dimensions"),
	WORLDGEN_TYPE	(15, WORLDGEN, ".json", 	"Dimension Types"),
	WORLDGEN_BIOME	(16, WORLDGEN, ".json", 	"Biomes"),
	WORLDGEN_CARVER	(17, WORLDGEN, ".json", 	"Carvers"),
	WORLDGEN_FEATURE		(18, WORLDGEN, ".json", 	"Features"),
	WORLDGEN_STRUCT_FEATURE	(19, WORLDGEN, ".json", 	"Structure Feature"),
	WORLDGEN_STRUCT_BUILDER	(20, WORLDGEN, ".json", 	"Surface Builder"),
	WORLDGEN_NOISE			(21, WORLDGEN, ".json", 	"Noise Settings"),
	WORLDGEN_PROCESSOR		(22, WORLDGEN, ".json", 	"Processor List"),
	WORLDGEN_TEMPLATE		(23, WORLDGEN, ".json", 	"Template Pool"),
	OTHER					(24, null, 	   null, 		"Other");
	
	private final int index;
	private final EFileTypes parent;
	private final String extension;
	private final String title;
	
	EFileTypes(int index, EFileTypes parent, String extension, String title) {
		this.index = index;
		this.parent = parent;
		this.extension = extension;
		this.title = title;
	}
	
	public int getIndex() {
		return index;
	}
	
	public EFileTypes getParent() {
		return parent;
	}
	
	public boolean matchesExtension(String s) {
		if(extension == null)
			return true;
		else
			return s.endsWith(extension);
	}
	
	public String getTitle() {
		return title;
	}
}
