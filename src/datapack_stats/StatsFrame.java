package datapack_stats;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class StatsFrame extends JFrame {

	private static final long serialVersionUID = -3789573175931956716L;
	
	private final DatapackStats stats;
	
	public StatsFrame(DatapackStats stats) {
		this.stats = stats;
		
		this.setSize(new Dimension(850,480));
		this.setLayout(new FlowLayout());
		this.setResizable(false);
		
		try {
			String path = new File("data").getAbsolutePath();
			path = path.substring(0,path.lastIndexOf('\\'));
			path = path.substring(path.lastIndexOf('\\')+1);
			this.setTitle(path);
		} catch(StringIndexOutOfBoundsException e) {
			this.setTitle("Datapack Stats");
		}
		
		this.setIconImage(new ImageIcon("pack.png").getImage());
		
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
		
		JScrollPane basicScroll = new JScrollPane( createBasicPanel(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		basicScroll.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		basicScroll.setAutoscrolls(true);
		basicScroll.setPreferredSize( new Dimension(230,430) );
		this.add(basicScroll);
		
		JScrollPane statsScroll = new JScrollPane( createStatsPanel(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		statsScroll.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		statsScroll.setAutoscrolls(true);
		statsScroll.setPreferredSize( new Dimension(230,430) );
		this.add(statsScroll);
		
		JScrollPane commandScroll = new JScrollPane( createCommandPanel(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		commandScroll.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		commandScroll.setAutoscrolls(true);
		commandScroll.setPreferredSize( new Dimension(350,430) );
		this.add(commandScroll);
		
	    this.setVisible(true);
	}
	
	
	private JPanel createBasicPanel() {
		JPanel basicPanel = new JPanel();
		basicPanel.setPreferredSize( new Dimension(230,420) );
		FlowLayout layoutBasic = new FlowLayout();
		layoutBasic.setAlignment(FlowLayout.LEFT);
		basicPanel.setLayout(layoutBasic);

		int[] fileCount = stats.getFileCount();
		int[] functionStats = stats.getFunctionStats();
		
		//Header
		JLabel header = new JLabel("Datapack Statistics");
		header.setFont(new Font(header.getName(), Font.BOLD, 18));
		header.setPreferredSize(new Dimension(180,25));
		basicPanel.add( header );

		//file
		JLabel directories = new JLabel("Directories: " + fileCount[EFileTypes.DIRECTORIES.getIndex()]);
		directories.setPreferredSize(new Dimension(180,15));
		basicPanel.add( directories );

		JLabel files = new JLabel("Files: " + (Arrays.stream(fileCount).sum() - fileCount[0]));
		files.setPreferredSize(new Dimension(180,15));
		basicPanel.add( files );
		
		Map<EFileTypes,JComponent> labels = new HashMap<EFileTypes,JComponent>();
		for(EFileTypes e: EFileTypes.values()) {
			if(e != EFileTypes.DIRECTORIES) {
				if(e.getParent() == null) {
					JLabel label = new JLabel("  * " + e.getTitle() + ": " + fileCount[e.getIndex()]);
					label.setPreferredSize(new Dimension(180,15));
					labels.put(e, label);
				} else {
					EFileTypes parent = e.getParent();
					if(labels.get(parent) instanceof JLabel) {
						JExandableLabel list = new JExandableLabel(parent.getTitle() + ": " + fileCount[parent.getIndex()], 180, 15);
						list.setPreferredSize(new Dimension(180,15));
						labels.put(e.getParent(), list);
					}
					JExandableLabel list = (JExandableLabel) labels.get(parent);
					JLabel label = new JLabel("    > " + e.getTitle() + ": " + fileCount[e.getIndex()]);
					label.setPreferredSize(new Dimension(180,15));
					list.addLabel(label);
				}
			}
		}
		for(JComponent comp: labels.values()) {
			basicPanel.add(comp);
		}
		
		//line break
		JLabel lineBreak = new JLabel("");
		lineBreak.setPreferredSize(new Dimension(180,15));
		basicPanel.add( lineBreak );
		
		//line totals
		JLabel lines = new JLabel("Total Lines: " + functionStats[0]);
		lines.setPreferredSize(new Dimension(180,15));
		basicPanel.add( lines );

		JLabel code = new JLabel("  * Lines of Code: " + functionStats[1]);
		code.setPreferredSize(new Dimension(180,15));
		basicPanel.add( code );

		JLabel comments = new JLabel("  * Comments: " + functionStats[2]);
		comments.setPreferredSize(new Dimension(180,15));
		basicPanel.add( comments );
		
		return basicPanel;
	}
	
	private JPanel createStatsPanel() {
		
		JPanel commandStatsPanel = new JPanel();
		commandStatsPanel.setPreferredSize(new Dimension(230,420));
		
		FlowLayout layoutCommandStats = new FlowLayout();
		layoutCommandStats.setAlignment(FlowLayout.LEFT);
		commandStatsPanel.setLayout(layoutCommandStats);
		
		int[] selectorStats = stats.getSelectorStats();
		int[] executeStats = stats.getExecuteStats();
		
		//header
		JLabel header = new JLabel("Command Statistics");
		header.setFont(new Font(header.getName(), Font.BOLD, 18));
		header.setPreferredSize(new Dimension(180,25));
		commandStatsPanel.add( header );
		
		//selectors
		JLabel selectors = new JLabel("Total Selectors Used: " + Arrays.stream(selectorStats).sum() );
		selectors.setPreferredSize(new Dimension(180,15));
		commandStatsPanel.add( selectors );
		
		for(ESelectors e: ESelectors.values()) {
			JLabel label = new JLabel("  * @" + e.toString().toLowerCase() + ": " + selectorStats[e.getIndex()]);
			label.setPreferredSize(new Dimension(180,15));
			commandStatsPanel.add(label);
		}
		
		//line break
		JLabel lineBreak = new JLabel("");
		lineBreak.setPreferredSize(new Dimension(180,15));
		commandStatsPanel.add( lineBreak );
		
		//execute sub commands
		JLabel exeSubs = new JLabel("Execute Subcommands: " + Arrays.stream(executeStats).sum() );
		exeSubs.setPreferredSize(new Dimension(180,15));
		commandStatsPanel.add( exeSubs );
		
		for(EExecuteArg e: EExecuteArg.values()) {
			JLabel label = new JLabel("  * " + e.toString().toLowerCase() + ": " + executeStats[e.getIndex()]);
			label.setPreferredSize(new Dimension(180,15));
			commandStatsPanel.add(label);
		}
		
		//return
		return commandStatsPanel;
	}
	
	private JPanel createCommandPanel() {
		JPanel commandPanel = new JPanel();
		commandPanel.setPreferredSize(new Dimension(350,420));
		FlowLayout layoutCommand = new FlowLayout();
		layoutCommand.setAlignment(FlowLayout.LEFT);
		commandPanel.setLayout(layoutCommand);
	    
		JLabel commandTitle = new JLabel("Command Totals");
		commandTitle.setFont(new Font(commandTitle.getName(), Font.BOLD, 18));
		commandTitle.setPreferredSize(new Dimension(340,25));
		commandPanel.add(commandTitle);
		
		for(Map.Entry<String,Integer> entry: stats.getCommandStats().entrySet()) {
			String labelText = entry.getKey() + ": " + entry.getValue();
			JLabel label = new JLabel(labelText);
			label.setPreferredSize(new Dimension(160,15));
			commandPanel.add(label);
		}
		
		return commandPanel;
	}
}
