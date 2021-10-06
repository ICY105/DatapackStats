package datapack_stats;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class DatapackStats {

	public static void main(String[] args) {		
		final DatapackStats stats = new DatapackStats();
		final File data = new File("data");
		
		if(data.exists()) {
			stats.getTopFiles(data);
			new StatsFrame(stats);
		} else {
			int out = JOptionPane.showConfirmDialog(null, "No datapack file system detected.\nWould you like to select it manually?");
			if(out == 0) {
				final File currentDir = new File(System.getProperty("user.dir"));
				final JFileChooser chooser = new JFileChooser(currentDir);
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				out = chooser.showOpenDialog(null);
				if(out == 0) {
					File selected = chooser.getSelectedFile();
					stats.getTopFiles(selected);
					new StatsFrame(stats);
				}
			}
		}
	}
	
	private DatapackStats() {
		fileCount = new int[EFileTypes.values().length];
		selectorStats = new int[ESelectors.values().length];
		executeStats = new int[EExecuteArg.values().length];
		functionStats = new int[3];
		commandStats = new HashMap<String,Integer>();
	}
	
	private final int[] fileCount;
	private final int[] selectorStats;
	private final int[] executeStats;

	//0 = lines, 1 = lines of code, 2 = comments
	private final int[] functionStats;
	
	//Dynamically generated from the first word of a line and after run
	private final HashMap<String,Integer> commandStats;
	
	public int[] getFileCount() {
		return fileCount;
	}

	public int[] getSelectorStats() {
		return selectorStats;
	}

	public int[] getExecuteStats() {
		return executeStats;
	}

	public int[] getFunctionStats() {
		return functionStats;
	}

	public HashMap<String, Integer> getCommandStats() {
		return commandStats;
	}

	private void getTopFiles(File file) {
		
		for(File e: file.listFiles()) {
			
			if(e.isDirectory()) {
				fileCount[EFileTypes.DIRECTORIES.getIndex()]++;
				
				switch(e.getName()) {
				case "functions":
					getSubFiles(e,EFileTypes.FUNCTIONS);
					break;
				case "advancements":
					getSubFiles(e,EFileTypes.ADVANCEMENTS);
					break;
				case "structures":
					getSubFiles(e,EFileTypes.STRUCTURES);
					break;
				case "loot_tables":
					getSubFiles(e,EFileTypes.LOOT_TABLES);
					break;
				case "recipes":
					getSubFiles(e,EFileTypes.RECIPES);
					break;
				case "tags":
					getSubFiles(e,EFileTypes.TAGS);
					break;
				case "dimension":
					getSubFiles(e,EFileTypes.WORLDGEN_DIM);
					break;
				case "dimension_type":
					getSubFiles(e,EFileTypes.WORLDGEN_TYPE);
					break;
				case "worldgen":
					getSubFiles(e,EFileTypes.WORLDGEN);
					break;
				case "item_modifiers":
					getSubFiles(e,EFileTypes.ITEM_MOD);
					break;
				default:
					getTopFiles(e);
					break;
				}
			} else {
				fileCount[EFileTypes.OTHER.getIndex()]++;
			}
		}
	}
	
	private void getSubFiles(File file, EFileTypes type) {
		if(file.isDirectory()) {
			fileCount[EFileTypes.DIRECTORIES.getIndex()]++;
			
			File[] list = file.listFiles();
			for(File e: list) {
				switch(type) {
				case WORLDGEN:
					switch(e.getName()) {
					case "biome":
						getSubFiles(e, EFileTypes.WORLDGEN_BIOME);
						break;
					case "configured_carver":
						getSubFiles(e, EFileTypes.WORLDGEN_CARVER);
						break;
					case "configured_feature":
						getSubFiles(e, EFileTypes.WORLDGEN_FEATURE);
						break;
					case "configured_structure_feature":
						getSubFiles(e, EFileTypes.WORLDGEN_STRUCT_FEATURE);
						break;
					case "configured_surface_builder":
						getSubFiles(e, EFileTypes.WORLDGEN_STRUCT_BUILDER);
						break;
					case "noise_settings":
						getSubFiles(e, EFileTypes.WORLDGEN_NOISE);
						break;
					case "processor_list":
						getSubFiles(e, EFileTypes.WORLDGEN_PROCESSOR);
						break;
					case "template_pool":
						getSubFiles(e, EFileTypes.WORLDGEN_TEMPLATE);
						break;
					default:
						getSubFiles(e, type);
						break;
					}
					break;
				case TAGS:
					switch(e.getName()) {
					case "blocks":
						getSubFiles(e, EFileTypes.TAGS_BLOCK);
						break;
					case "entity_types":
						getSubFiles(e, EFileTypes.TAGS_ENTITY);
						break;
					case "fluids":
						getSubFiles(e, EFileTypes.TAGS_FLUID);
						break;
					case "functions":
						getSubFiles(e, EFileTypes.TAGS_FUNCTION);
						break;
					case "game_events":
						getSubFiles(e, EFileTypes.TAGS_EVENT);
						break;
					case "items":
						getSubFiles(e, EFileTypes.TAGS_ITEM);
						break;
					default:
						getSubFiles(e, type);
						break;
					}
					break;
				default:
					getSubFiles(e, type);
				}
			}
		} else {
			if(type.matchesExtension(file.getName())) {
				fileCount[type.getIndex()]++;
				EFileTypes parent = type.getParent();
				if(parent != null)
					fileCount[parent.getIndex()]++;
			}
			if(type == EFileTypes.FUNCTIONS && type.matchesExtension(file.getName()))
				readFunction(file);
		}
	}

	private void readFunction(File file) {
		try {
			FileReader reader = new FileReader(file);
			BufferedReader input = new BufferedReader(reader);
			
			String in = input.readLine();
			while(in != null) {
				in = in.trim();
				
				//comment stat
				functionStats[0]++;
				if(in.startsWith("#", 0)) {
					functionStats[2]++;
				} else if(!in.trim().isEmpty()) {
					functionStats[1]++;
				}
				
				//selector stats
				for(ESelectors arg: ESelectors.values()) {
					selectorStats[arg.getIndex()] += instancesOf(in, "@" + arg.toString().toLowerCase());
				}
				
				//Execute stats + Subcommand stats
				if(in.startsWith("execute", 0)) {
					
					//execute subcommand stats
					for(EExecuteArg arg: EExecuteArg.values()) {
						executeStats[arg.getIndex()] += instancesOf(in, " " + arg.toString().toLowerCase() + " ");
					}				
					
					int pos = in.indexOf("run ");
					if(pos > 0 && in.length() > pos + 4) {
						String command = in.substring(pos + 4);
						int index = command.indexOf(' ');
						if(index == -1)
							addCommand(in);
						else
							addCommand(command.substring(0,index));
					}
				}
				
				//Command stats	
				if(!in.trim().isEmpty() && !in.startsWith("#", 0)) {
					int index = in.indexOf(' ');
					if(index == -1)
						addCommand(in);
					else
						addCommand(in.substring(0,index));
				}
				in = input.readLine();
			}
			input.close();
			reader.close();
		} catch (IOException e) {}
	}
	
	private void addCommand(String command) {
		if(!commandStats.containsKey(command)) {
			commandStats.put(command, 1);
		} else {
			int index = commandStats.get(command);
			commandStats.put(command, index+1);
		}
	}
	
	private int instancesOf(String string, String of) {
		int index = string.indexOf(of);
		int out = 0;
		while(index > 0) {
			out++;
			index = string.indexOf(of,index+1);
		}
		return out;
	}

}
