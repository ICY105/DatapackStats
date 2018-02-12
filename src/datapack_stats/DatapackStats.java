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
		stats.getFiles(file);
		
		stats.prepairUI();
	}
	
	private DatapackStats() {
		fileCount = new int[5];
		functionStats = new int[3];
		commandStats = new HashMap<String,Integer>();
		selectorStats = new int[5];
	}
	
	//0 = directories, 1 = .mcfunction, 2 = .json, 3 = structures, 4 = other
	private int[] fileCount;
	
	//0 = lines, 1 = lines of code, 2 = comments
	private int[] functionStats;
	
	HashMap<String,Integer> commandStats;
	
	//0 = @p, 1 = @a, 2 = @r, 3 = @e, 4 = @s
	private int[] selectorStats;
	
	private void getFiles(File file) {
		File[] list = file.listFiles();
		for(File e: list) {
			if(e.isDirectory()) {
				fileCount[0]++;
				getFiles(e);
			} else {
				String name = e.getName();
				if(name.length() > 11 && name.substring(name.length()-11).equals(".mcfunction")) {
					fileCount[1]++;
					readFile(e);
				} else if(name.length() > 5 && name.substring(name.length()-5).equals(".json")) {
					fileCount[2]++;
				} else if(name.length() > 4 && name.substring(name.length()-4).equals(".nbt")) {
					fileCount[3]++;
				} else {
					fileCount[4]++;
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
				
				functionStats[0]++;
				if(in.startsWith("#", 0)) {
					functionStats[2]++;
				} else if(!input.equals("")) {
					functionStats[1]++;
				}
				
					
				if(!in.equals("") && !in.startsWith("#", 0)) {
					int index = in.indexOf(' ');
					String command = in.substring(0,index);
					if(!commandStats.containsKey(command)) {
						commandStats.put(command, 0);
					}
					index = commandStats.get(command);
					commandStats.put(command, index+1);
				}
				if(in.startsWith("execute", 0)) {
					
					int pos = in.indexOf("run");
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
				
				if(in.contains("@")) {
					int index = in.indexOf('@');
					while(index > 0) {
						if(in.charAt(index+1) == 'p')
							selectorStats[0]++;
						else if(in.charAt(index+1) == 'a')
							selectorStats[1]++;
						else if(in.charAt(index+1) == 'r')
							selectorStats[2]++;
						else if(in.charAt(index+1) == 'e')
							selectorStats[3]++;
						else if(in.charAt(index+1) == 's')
							selectorStats[4]++;
						index = in.indexOf('@',index+1);
					}
				}
				
				in = input.readLine();
			}
			
			input.close();
			reader.close();
		} catch (IOException e) {}
	}
	
	@SuppressWarnings("rawtypes")
	private void prepairUI() {		
		this.setSize(new Dimension(600,450));
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
		
		JPanel basicPanel = new JPanel();
		basicPanel.setPreferredSize(new Dimension(230,410));
		FlowLayout layoutBasic = new FlowLayout();
		layoutBasic.setAlignment(FlowLayout.LEFT);
		basicPanel.setLayout(layoutBasic);
		basicPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		JLabel[] statList = new JLabel[18];
		
		statList[0] = new JLabel("Datapack Statistics");
		statList[0].setFont(new Font(statList[0].getName(), Font.BOLD, 18));
		statList[1] = new JLabel("Directories: " + fileCount[0]);
		statList[2] = new JLabel("Files: " + (fileCount[1] + fileCount[2] + fileCount[3]));
		statList[3] = new JLabel("  * Functions: " + fileCount[1]);
		statList[4] = new JLabel("  * JSONs: " + fileCount[2]);
		statList[5] = new JLabel("  * Structures: " + fileCount[3]);
		statList[6] = new JLabel("  * Other Files: " + fileCount[4]);
		statList[7] = new JLabel(" ");
		statList[8] = new JLabel("Total Lines: " + functionStats[0]);
		statList[9] = new JLabel("  * Lines of Code: " + functionStats[1]);
		statList[10] = new JLabel("  * Comments: " + functionStats[2]);
		statList[11] = new JLabel(" ");
		statList[12] = new JLabel("Total Selectors Used: " + (selectorStats[0] + selectorStats[1] + selectorStats[2] + selectorStats[3] + selectorStats[4]));
		statList[13] = new JLabel("  * @p: " + selectorStats[0]);
		statList[14] = new JLabel("  * @a: " + selectorStats[1]);
		statList[15] = new JLabel("  * @r: " + selectorStats[2]);
		statList[16] = new JLabel("  * @e: " + selectorStats[3]);
		statList[17] = new JLabel("  * @s: " + selectorStats[4]);
		
		for(JLabel e:statList) {
			e.setPreferredSize(new Dimension(180,15));
			basicPanel.add(e);
		}
		statList[0].setPreferredSize(new Dimension(180,25));
		
		
		
		JPanel commandPanel = new JPanel();
		commandPanel.setPreferredSize(new Dimension(350,410));
		FlowLayout layoutCommand = new FlowLayout();
		layoutCommand.setAlignment(FlowLayout.LEFT);
		commandPanel.setLayout(layoutCommand);
		commandPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
	    
		JLabel commandTitle = new JLabel("Command Statistics");
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
	    this.add(commandPanel);
	    this.setVisible(true);
	}

}
