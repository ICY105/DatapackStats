package datapack_stats;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class DatapackStats extends JFrame {

	public static void main(String[] args) {		
		DatapackStats stats = new DatapackStats();
		
		File file = new File("data");
		stats.getFiles(file,0);
		
		stats.prepairUI();
	}
	
	private DatapackStats() {
		fileCount = new int[8];
		functionStats = new int[3];
		commandStats = new HashMap<String,Integer>();
		selectorStats = new int[5];
		executeStats = new int[12];
	}
	
	//0 = directories, 1 = .mcfunction, 2 = advancements, 3 = structures, 4 = loot tables, 5 = recipes, 6 = tags, 7 = other
	private int[] fileCount;
	
	//0 = lines, 1 = lines of code, 2 = comments
	private int[] functionStats;
	
	HashMap<String,Integer> commandStats;
	
	//0 = @p, 1 = @a, 2 = @r, 3 = @e, 4 = @s
	private int[] selectorStats;
	
	//0 = as, 1 = at, 2 = positioned, 3 = align, 4 = facing, 5 = rotated, 6 = in, 7 = anchored, 8 = if, 10 = unless, 11 = store
	private int[] executeStats;
	
	private void getFiles(File file, int lock) {
		File[] list = file.listFiles();
		for(File e: list) {
			if(e.isDirectory()) {
				fileCount[0]++;
				if(lock > 0) {
					getFiles(e,lock);
				} else if(e.getName().equals("functions")) {
					getFiles(e,1);
				} else if(e.getName().equals("advancements")) {
					getFiles(e,2);
				} else if(e.getName().equals("structures")) {
					getFiles(e,3);
				} else if(e.getName().equals("loot_tables")) {
					getFiles(e,4);
				} else if(e.getName().equals("recipes")) {
					getFiles(e,5);
				} else if(e.getName().equals("tags")) {
					getFiles(e,6);
				} else {
					getFiles(e,lock);
				}
			} else {
				int index = e.getName().lastIndexOf('.');
				String extension;
				if(index > 0)
					extension = e.getName().substring(index);
				else
					extension = "";
				
				if(extension.equals(".mcfunction") && lock == 1) {
					fileCount[1]++;
					readFile(e);
				} else if(extension.equals(".json") && lock == 2) {
					fileCount[2]++;
				} else if(extension.equals(".nbt") && lock == 3) {
					fileCount[3]++;
				} else if(extension.equals(".json") && lock == 4) {
					fileCount[4]++;
				} else if(extension.equals(".json") && lock == 5) {
					fileCount[5]++;
				} else if(extension.equals(".json") && lock == 6) {
					fileCount[6]++;
				} else {
					fileCount[7]++;
				}
			}
		}
	}
	
	private void readFile(File file) {
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
				} else if(!input.equals("")) {
					functionStats[1]++;
				}
				
				//Command stats	
				if(!in.equals("") && !in.startsWith("#", 0)) {
					int index = in.indexOf(' ');
					String command = in.substring(0,index);
					if(!commandStats.containsKey(command)) {
						commandStats.put(command, 0);
					}
					index = commandStats.get(command);
					commandStats.put(command, index+1);
				}
				
				//Execute stats + Subcommand stats
				if(in.startsWith("execute", 0)) {					
					executeStats[0] += instancesOf(in," as ");
					executeStats[1] += instancesOf(in," at ");
					executeStats[2] += instancesOf(in," positioned ");
					executeStats[3] += instancesOf(in," align ");
					executeStats[4] += instancesOf(in," facing ");
					executeStats[5] += instancesOf(in," rotated ");
					executeStats[6] += instancesOf(in," in ");
					executeStats[7] += instancesOf(in," anchored ");
					executeStats[8] += instancesOf(in," if ");
					executeStats[9] += instancesOf(in," unless ");
					executeStats[10] += instancesOf(in," store ");					
					
					int pos = in.indexOf("run ");
					if(pos > 11 && in.length() > pos + 4) {
						int index = in.indexOf(' ',pos + 4);
						
						String command;
						if(index > 0)
							command = in.substring(pos+4,index);
						else 
							command = in.substring(pos+4);
						
						if(!commandStats.containsKey(command)) {
							commandStats.put(command, 0);
						}
						index = commandStats.get(command);
						commandStats.put(command, index+1);			
					}
				}
				
				//Selector Stats
				if(in.contains("@")) {
					selectorStats[0] += instancesOf(in,"@p");
					selectorStats[1] += instancesOf(in,"@a");
					selectorStats[2] += instancesOf(in,"@r");
					selectorStats[3] += instancesOf(in,"@e");
					selectorStats[4] += instancesOf(in,"@s");
				}
				
				in = input.readLine();
			}
			
			input.close();
			reader.close();
		} catch (IOException e) {}
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
	
	@SuppressWarnings("rawtypes")
	private void prepairUI() {		
		this.setSize(new Dimension(850,470));
		this.setLayout(new FlowLayout());
		this.setResizable(false);
		
		this.addWindowListener(new WindowListener() {
			
			@Override
			public void windowClosing(WindowEvent arg0) {
				System.exit(0);
			}
			
			@Override
			public void windowClosed(WindowEvent arg0) {}
			@Override
			public void windowActivated(WindowEvent arg0) {}
			@Override
			public void windowDeactivated(WindowEvent arg0) {}
			@Override
			public void windowDeiconified(WindowEvent arg0) {}
			@Override
			public void windowIconified(WindowEvent arg0) {}
			@Override
			public void windowOpened(WindowEvent arg0) {}
		});
		
		//Datapack Statistics
		
		JPanel basicPanel = new JPanel();
		basicPanel.setPreferredSize(new Dimension(230,430));
		FlowLayout layoutBasic = new FlowLayout();
		layoutBasic.setAlignment(FlowLayout.LEFT);
		basicPanel.setLayout(layoutBasic);
		basicPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		JLabel[] statList = new JLabel[14];
		
		statList[0] = new JLabel("Datapack Statistics");
		statList[0].setFont(new Font(statList[0].getName(), Font.BOLD, 18));
		statList[1] = new JLabel("Directories: " + fileCount[0]);
		statList[2] = new JLabel("Files: " + (sum(fileCount)-fileCount[0]));
		statList[3] = new JLabel("  * Functions: " + fileCount[1]);
		statList[4] = new JLabel("  * Advancements: " + fileCount[2]);
		statList[5] = new JLabel("  * Structures: " + fileCount[3]);
		statList[6] = new JLabel("  * Loot Tables: " + fileCount[4]);
		statList[7] = new JLabel("  * Recipes: " + fileCount[5]);
		statList[8] = new JLabel("  * Tags: " + fileCount[6]);
		statList[9] = new JLabel("  * Other Files: " + fileCount[4]);
		statList[10] = new JLabel(" ");
		statList[11] = new JLabel("Total Lines: " + functionStats[0]);
		statList[12] = new JLabel("  * Lines of Code: " + functionStats[1]);
		statList[13] = new JLabel("  * Comments: " + functionStats[2]);
		
		for(JLabel e:statList) {
			e.setPreferredSize(new Dimension(180,15));
			basicPanel.add(e);
		}
		statList[0].setPreferredSize(new Dimension(180,25));
		
		//Command Statistics
		
		JPanel commandStatsPanel = new JPanel();
		commandStatsPanel.setPreferredSize(new Dimension(230,430));
		FlowLayout layoutCommandStats = new FlowLayout();
		layoutCommandStats.setAlignment(FlowLayout.LEFT);
		commandStatsPanel.setLayout(layoutCommandStats);
		commandStatsPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		JLabel[] commandStatList = new JLabel[20];
		
		commandStatList[0] = new JLabel("Command Statistics");
		commandStatList[0].setFont(new Font(statList[0].getName(), Font.BOLD, 18));
		
		commandStatList[1] = new JLabel("Total Selectors Used: " + sum(selectorStats));
		commandStatList[2] = new JLabel("  * @p: " + selectorStats[0]);
		commandStatList[3] = new JLabel("  * @a: " + selectorStats[1]);
		commandStatList[4] = new JLabel("  * @r: " + selectorStats[2]);
		commandStatList[5] = new JLabel("  * @e: " + selectorStats[3]);
		commandStatList[6] = new JLabel("  * @s: " + selectorStats[4]);
		commandStatList[7] = new JLabel("");
		//0 = as, 1 = at, 2 = positioned, 3 = align, 4 = facing, 5 = rotated, 6 = in, 7 = anchored, 8 = if, 10 = unless, 11 = store
		commandStatList[8] = new JLabel("Execute Subcommands: " + sum(executeStats));
		commandStatList[9] = new JLabel("  * as: " + executeStats[0]);
		commandStatList[10] = new JLabel("  * at: " + executeStats[1]);
		commandStatList[11] = new JLabel("  * in: " + executeStats[6]);
		commandStatList[12] = new JLabel("  * if: " + executeStats[8]);
		commandStatList[13] = new JLabel("  * unless: " + executeStats[9]);
		commandStatList[14] = new JLabel("  * positioned: " + executeStats[2]);
		commandStatList[15] = new JLabel("  * align: " + executeStats[3]);
		commandStatList[16] = new JLabel("  * facing: " + executeStats[4]);
		commandStatList[17] = new JLabel("  * rotated: " + executeStats[5]);
		commandStatList[18] = new JLabel("  * anchored: " + executeStats[7]);
		commandStatList[19] = new JLabel("  * store: " + executeStats[10]);
		
		
		for(JLabel e:commandStatList) {
			e.setPreferredSize(new Dimension(180,15));
			commandStatsPanel.add(e);
		}
		commandStatList[0].setPreferredSize(new Dimension(180,25));
		
		//Commant Totals
		
		JPanel commandPanel = new JPanel();
		commandPanel.setPreferredSize(new Dimension(350,430));
		FlowLayout layoutCommand = new FlowLayout();
		layoutCommand.setAlignment(FlowLayout.LEFT);
		commandPanel.setLayout(layoutCommand);
		commandPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
	    
		JLabel commandTitle = new JLabel("Command Totals");
		commandTitle.setFont(new Font(statList[0].getName(), Font.BOLD, 18));
		commandTitle.setPreferredSize(new Dimension(340,25));
		commandPanel.add(commandTitle);
		
		Iterator iter = commandStats.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry pair = (Map.Entry)iter.next();
			
			String labelText = pair.getKey() + ": " + pair.getValue();
			JLabel label = new JLabel(labelText);
			label.setPreferredSize(new Dimension(160,15));
			commandPanel.add(label);
			
			iter.remove();
		}
		
		this.add(basicPanel);
	    this.add(commandStatsPanel);
	    this.add(commandPanel);
	    this.setVisible(true);
	}
	
	private int sum(int[] array) {
		int out = 0;
		for(int e:array)
			out += e;
		return out;
	}

}
