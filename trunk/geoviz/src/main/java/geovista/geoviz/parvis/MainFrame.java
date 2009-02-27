/*
 * MainFrame.java
 *
 * Created on 11. Februar 2002, 00:08
 *
 * Licensed under GNU General Public License (GPL).
 * See http://www.gnu.org/copyleft/gpl.html
 */

package geovista.geoviz.parvis;

import java.io.File;
import java.net.URL;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.UIManager;

/**
 * 
 * @author flo
 */
public class MainFrame extends javax.swing.JFrame implements ProgressListener {
	protected final static Logger logger = Logger.getLogger(MainFrame.class
			.getName());

	/** Creates new form MainFrame */
	public MainFrame() {
		initComponents();
		parallelDisplay.addProgressListener(this);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	private void initComponents() {// GEN-BEGIN:initComponents
		menuEditGroup = new javax.swing.ButtonGroup();
		buttonEditGroup = new javax.swing.ButtonGroup();
		statusPanel = new javax.swing.JPanel();
		progressPanel = new javax.swing.JPanel();
		progressLabel = new javax.swing.JLabel();
		progressBar = new javax.swing.JProgressBar();
		timeLabel = new javax.swing.JLabel();
		quickPrefPanel = new javax.swing.JPanel();
		tooltipBox = new javax.swing.JCheckBox();
		hoverBox = new javax.swing.JCheckBox();
		radiusLabel = new javax.swing.JLabel();
		radiusField = new javax.swing.JTextField();
		toolbarPanel = new javax.swing.JPanel();
		modeBar = new javax.swing.JToolBar();
		modeLabel = new javax.swing.JLabel();
		orderButton = new javax.swing.JToggleButton();
		scaleButton = new javax.swing.JToggleButton();
		translateButton = new javax.swing.JToggleButton();
		brushButton = new javax.swing.JToggleButton();
		urlBar = new javax.swing.JToolBar();
		datasourceLabel = new javax.swing.JLabel();
		urlField = new javax.swing.JTextField();
		jButton1 = new javax.swing.JButton();
		parallelDisplay = new geovista.geoviz.parvis.ParallelDisplay();
		menuBar = new javax.swing.JMenuBar();
		fileMenu = new javax.swing.JMenu();
		openMenu = new javax.swing.JMenuItem();
		editMenu = new javax.swing.JMenu();
		orderMenu = new javax.swing.JRadioButtonMenuItem();
		scaleMenu = new javax.swing.JRadioButtonMenuItem();
		translateMenu = new javax.swing.JRadioButtonMenuItem();
		brushMenu = new javax.swing.JRadioButtonMenuItem();
		jSeparator1 = new javax.swing.JSeparator();
		preferencesMenu = new javax.swing.JMenuItem();
		viewMenu = new javax.swing.JMenu();
		scaleZeroMaxItem = new javax.swing.JMenuItem();
		scaleMinMaxItem = new javax.swing.JMenuItem();
		scaleMinMaxAbsItem = new javax.swing.JMenuItem();

		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent evt) {
				exitForm(evt);
			}
		});

		statusPanel.setLayout(new java.awt.BorderLayout());

		statusPanel.setBorder(new javax.swing.border.TitledBorder(
				new javax.swing.border.EtchedBorder(), "status",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION,
				new java.awt.Font("Dialog", 0, 10)));
		statusPanel.setPreferredSize(new java.awt.Dimension(272, 50));
		statusPanel.setFont(new java.awt.Font("Dialog", 0, 10));
		progressPanel.setLayout(new java.awt.FlowLayout(
				java.awt.FlowLayout.CENTER, 5, 0));

		progressLabel.setText("progress:");
		progressLabel.setFont(new java.awt.Font("Dialog", 0, 10));
		progressPanel.add(progressLabel);

		progressBar.setFont(new java.awt.Font("Dialog", 0, 10));
		progressBar.setPreferredSize(new java.awt.Dimension(100, 18));
		progressBar.setMinimumSize(new java.awt.Dimension(10, 16));
		progressBar.setStringPainted(true);
		progressBar.setMaximumSize(new java.awt.Dimension(32767, 18));
		progressPanel.add(progressBar);

		timeLabel.setText("(0.0 s)");
		timeLabel.setFont(new java.awt.Font("Dialog", 0, 10));
		progressPanel.add(timeLabel);

		statusPanel.add(progressPanel, java.awt.BorderLayout.WEST);

		quickPrefPanel.setLayout(new java.awt.FlowLayout(
				java.awt.FlowLayout.CENTER, 5, 0));

		quickPrefPanel.setPreferredSize(new java.awt.Dimension(260, 20));
		tooltipBox.setSelected(true);
		tooltipBox.setFont(new java.awt.Font("Dialog", 0, 10));
		tooltipBox.setText("tooltips");
		tooltipBox.setMargin(new java.awt.Insets(0, 2, 0, 2));
		tooltipBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				tooltipBoxActionPerformed(evt);
			}
		});

		quickPrefPanel.add(tooltipBox);

		hoverBox.setSelected(true);
		hoverBox.setFont(new java.awt.Font("Dialog", 0, 10));
		hoverBox.setText("line");
		hoverBox.setMargin(new java.awt.Insets(0, 2, 0, 2));
		hoverBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				hoverBoxActionPerformed(evt);
			}
		});

		quickPrefPanel.add(hoverBox);

		radiusLabel.setText("Brush Fuzziness");
		radiusLabel.setFont(new java.awt.Font("Dialog", 0, 10));
		quickPrefPanel.add(radiusLabel);

		radiusField.setFont(new java.awt.Font("Dialog", 0, 10));
		radiusField.setText(" 20 %");
		radiusField.setPreferredSize(new java.awt.Dimension(30, 17));
		radiusField.setBorder(new javax.swing.border.LineBorder(
				(java.awt.Color) javax.swing.UIManager.getDefaults().get(
						"Button.select")));
		radiusField.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				radiusFieldActionPerformed(evt);
			}
		});

		radiusField.addFocusListener(new java.awt.event.FocusAdapter() {
			@Override
			public void focusGained(java.awt.event.FocusEvent evt) {
				radiusFieldFocusGained(evt);
			}
		});

		quickPrefPanel.add(radiusField);

		statusPanel.add(quickPrefPanel, java.awt.BorderLayout.EAST);

		getContentPane().add(statusPanel, java.awt.BorderLayout.SOUTH);

		toolbarPanel.setLayout(new java.awt.GridLayout(2, 0));

		modeLabel.setText("Edit Mode: ");
		modeLabel.setFont(new java.awt.Font("Dialog", 0, 10));
		modeBar.add(modeLabel);

		orderButton.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/geovista/geoviz/parvis/reorder.gif")));
		orderButton
				.setToolTipText("Reorder axes by dragging them across the display.");
		orderButton.setSelected(true);
		orderButton.setFont(new java.awt.Font("Dialog", 0, 10));
		orderButton.setText("Order");
		buttonEditGroup.add(orderButton);
		orderButton.setPreferredSize(new java.awt.Dimension(65, 27));
		orderButton.setMaximumSize(new java.awt.Dimension(65, 27));
		orderButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
		orderButton.setMinimumSize(new java.awt.Dimension(65, 27));
		orderButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				setEditModeOrder(evt);
			}
		});

		modeBar.add(orderButton);

		scaleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/geovista/geoviz/parvis/scale.gif")));
		scaleButton
				.setToolTipText("Scale axes by dragging up (zoom out) or down (zoom in).");
		scaleButton.setFont(new java.awt.Font("Dialog", 0, 10));
		scaleButton.setText("Scale");
		buttonEditGroup.add(scaleButton);
		scaleButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
		scaleButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				setEditModeScale(evt);
			}
		});

		modeBar.add(scaleButton);

		translateButton.setIcon(new javax.swing.ImageIcon(getClass()
				.getResource("/geovista/geoviz/parvis/move.gif")));
		translateButton
				.setToolTipText("Translate axes by dragging up or down.");
		translateButton.setFont(new java.awt.Font("Dialog", 0, 10));
		translateButton.setText("Translate");
		buttonEditGroup.add(translateButton);
		translateButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
		translateButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				setEditModeTranslate(evt);
			}
		});

		modeBar.add(translateButton);

		brushButton.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/geovista/geoviz/parvis/brush.gif")));
		brushButton.setToolTipText("Translate axes by dragging up or down.");
		brushButton.setFont(new java.awt.Font("Dialog", 0, 10));
		brushButton.setText("Brush");
		buttonEditGroup.add(brushButton);
		brushButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
		brushButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				setEditModeBrush(evt);
			}
		});

		modeBar.add(brushButton);

		toolbarPanel.add(modeBar);

		datasourceLabel.setText("Datasource: ");
		datasourceLabel.setFont(new java.awt.Font("Dialog", 0, 10));
		urlBar.add(datasourceLabel);

		urlField.setFont(new java.awt.Font("Dialog", 0, 10));
		urlField.setText("file:///D:/Uni/visualisierung/datasets/car.stf");
		urlField.setPreferredSize(new java.awt.Dimension(250, 15));
		urlField.setMargin(new java.awt.Insets(0, 0, 0, 5));
		urlField.setMinimumSize(new java.awt.Dimension(9, 15));
		urlField.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				urlFieldActionPerformed(evt);
			}
		});

		urlBar.add(urlField);

		jButton1.setFont(new java.awt.Font("Dialog", 0, 10));
		jButton1.setText("Load File...");
		jButton1.setMargin(new java.awt.Insets(0, 5, 0, 0));
		jButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				openItemActionPerformed(evt);
			}
		});

		urlBar.add(jButton1);

		toolbarPanel.add(urlBar);

		getContentPane().add(toolbarPanel, java.awt.BorderLayout.NORTH);

		getContentPane().add(parallelDisplay, java.awt.BorderLayout.CENTER);

		menuBar.setFont(new java.awt.Font("Dialog", 0, 11));
		fileMenu.setText("File");
		openMenu.setText("Open File...");
		openMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				openItemActionPerformed(evt);
			}
		});

		fileMenu.add(openMenu);
		menuBar.add(fileMenu);
		editMenu.setText("Edit");
		orderMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_O,
				java.awt.event.InputEvent.SHIFT_MASK
						| java.awt.event.InputEvent.CTRL_MASK));
		orderMenu.setSelected(true);
		orderMenu.setFont(new java.awt.Font("Dialog", 0, 11));
		orderMenu.setText("Reorder Axes");
		menuEditGroup.add(orderMenu);
		orderMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/geovista/geoviz/parvis/reorder.gif")));
		orderMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				setEditModeOrder(evt);
			}
		});

		editMenu.add(orderMenu);
		scaleMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_S,
				java.awt.event.InputEvent.SHIFT_MASK
						| java.awt.event.InputEvent.CTRL_MASK));
		scaleMenu.setFont(new java.awt.Font("Dialog", 0, 11));
		scaleMenu.setText("Scale Axis");
		menuEditGroup.add(scaleMenu);
		scaleMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/geovista/geoviz/parvis/scale.gif")));
		scaleMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				setEditModeScale(evt);
			}
		});

		editMenu.add(scaleMenu);
		translateMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_T,
				java.awt.event.InputEvent.SHIFT_MASK
						| java.awt.event.InputEvent.CTRL_MASK));
		translateMenu.setFont(new java.awt.Font("Dialog", 0, 11));
		translateMenu.setText("Translate Axis");
		menuEditGroup.add(translateMenu);
		translateMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/geovista/geoviz/parvis/move.gif")));
		translateMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				setEditModeTranslate(evt);
			}
		});

		editMenu.add(translateMenu);
		brushMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_B,
				java.awt.event.InputEvent.SHIFT_MASK
						| java.awt.event.InputEvent.CTRL_MASK));
		brushMenu.setFont(new java.awt.Font("Dialog", 0, 11));
		brushMenu.setText("Brush Records");
		menuEditGroup.add(brushMenu);
		brushMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/geovista/geoviz/parvis/brush.gif")));
		brushMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				setEditModeBrush(evt);
			}
		});

		editMenu.add(brushMenu);
		editMenu.add(jSeparator1);
		preferencesMenu.setFont(new java.awt.Font("Dialog", 0, 11));
		preferencesMenu.setText("Preferences...");
		preferencesMenu.setEnabled(false);
		editMenu.add(preferencesMenu);
		menuBar.add(editMenu);
		viewMenu.setText("View");
		scaleZeroMaxItem.setFont(new java.awt.Font("Dialog", 0, 11));
		scaleZeroMaxItem.setText("Scale Axes 0-max");
		scaleZeroMaxItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				scaleZeroMaxItemActionPerformed(evt);
			}
		});

		viewMenu.add(scaleZeroMaxItem);
		scaleMinMaxItem.setFont(new java.awt.Font("Dialog", 0, 11));
		scaleMinMaxItem.setText("Scale Axes min-max");
		scaleMinMaxItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				scaleMinMaxItemActionPerformed(evt);
			}
		});

		viewMenu.add(scaleMinMaxItem);
		scaleMinMaxAbsItem.setFont(new java.awt.Font("Dialog", 0, 11));
		scaleMinMaxAbsItem.setText("Scale Axes min-max (abs)");
		scaleMinMaxAbsItem
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						scaleMinMaxAbsItemActionPerformed(evt);
					}
				});

		viewMenu.add(scaleMinMaxAbsItem);
		menuBar.add(viewMenu);
		setJMenuBar(menuBar);

		pack();
	}// GEN-END:initComponents

	private void hoverBoxActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_hoverBoxActionPerformed
		parallelDisplay.setBoolPreference("hoverLine", hoverBox.isSelected());
	}// GEN-LAST:event_hoverBoxActionPerformed

	private void radiusFieldActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_radiusFieldActionPerformed
		int num = Integer.parseInt(radiusField.getText());
		parallelDisplay.setFloatPreference("brushRadius", (num) / 100.0f);
		radiusField.setText(" " + num + " %");
		radiusField.transferFocus();
	}// GEN-LAST:event_radiusFieldActionPerformed

	private void radiusFieldFocusGained(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_radiusFieldFocusGained
		radiusField.selectAll();
	}// GEN-LAST:event_radiusFieldFocusGained

	private void scaleMinMaxAbsItemActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_scaleMinMaxAbsItemActionPerformed
		parallelDisplay.minMaxAbsScale();
	}// GEN-LAST:event_scaleMinMaxAbsItemActionPerformed

	private void scaleMinMaxItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_scaleMinMaxItemActionPerformed
		parallelDisplay.minMaxScale();
	}// GEN-LAST:event_scaleMinMaxItemActionPerformed

	private void scaleZeroMaxItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_scaleZeroMaxItemActionPerformed
		parallelDisplay.zeroMaxScale();
	}// GEN-LAST:event_scaleZeroMaxItemActionPerformed

	private void setEditModeTranslate(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_setEditModeTranslate
		parallelDisplay.setEditMode(ParallelDisplay.TRANSLATE);
		translateButton.setSelected(true);
		translateMenu.setSelected(true);
	}// GEN-LAST:event_setEditModeTranslate

	private void setEditModeScale(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_setEditModeScale
		parallelDisplay.setEditMode(ParallelDisplay.SCALE);
		scaleButton.setSelected(true);
		scaleMenu.setSelected(true);
	}// GEN-LAST:event_setEditModeScale

	private void setEditModeOrder(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_setEditModeOrder
		parallelDisplay.setEditMode(ParallelDisplay.REORDER);
		orderButton.setSelected(true);
		orderMenu.setSelected(true);
	}// GEN-LAST:event_setEditModeOrder

	private void setEditModeBrush(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_setEditModeBrush
		parallelDisplay.setEditMode(ParallelDisplay.BRUSH);
		brushButton.setSelected(true);
		brushMenu.setSelected(true);
	}// GEN-LAST:event_setEditModeBrush

	private void urlFieldActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_urlFieldActionPerformed
		try {
			STFFile f = new STFFile(new URL(urlField.getText()));
			f.addProgressListener(this);

			f.readContents();

			parallelDisplay.setModel(f);
		} catch (Exception e) {
			logger.finest(e.toString() + e.getMessage());
		}
	}// GEN-LAST:event_urlFieldActionPerformed

	private void tooltipBoxActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_tooltipBoxActionPerformed
		parallelDisplay.setBoolPreference("hoverText", tooltipBox.isSelected());
	}// GEN-LAST:event_tooltipBoxActionPerformed

	File currentPath = null;

	private void openItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_openItemActionPerformed
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
			@Override
			public boolean accept(File f) {
				return (f.isDirectory() || f.getName().endsWith(".stf"));
			}

			@Override
			public String getDescription() {
				return "STF (Simple Table Format) Data Files";
			}
		});
		if (currentPath == null) {
			chooser
					.setCurrentDirectory(new File(System
							.getProperty("user.dir")));
		} else {
			chooser.setCurrentDirectory(currentPath);
		}

		int option = chooser.showOpenDialog(this);

		if (option == JFileChooser.APPROVE_OPTION) {
			if (chooser.getSelectedFile() != null) {
				currentPath = chooser.getSelectedFile().getParentFile();
				String urltext = "file:///"
						+ chooser.getSelectedFile().getAbsolutePath();
				urltext = urltext.replace('\\', '/');
				urlField.setText(urltext);
				try {
					STFFile f = new STFFile(new URL(urltext));
					f.readContents();

					parallelDisplay.setModel(f);
				} catch (Exception e) {
					logger.finest(e.toString() + e.getMessage());
				}

			}
		}
	}// GEN-LAST:event_openItemActionPerformed

	/** Exit the Application */
	private void exitForm(java.awt.event.WindowEvent evt) {// GEN-FIRST:event_exitForm
		System.exit(0);
	}// GEN-LAST:event_exitForm

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		UIManager.put("geovista.geoviz.parvis.gui.ParallelDisplayUI",
				"geovista.geoviz.parvis.gui.BasicParallelDisplayUI");
		// new MainFrame().setVisible(true);
		try {
			// MainFrame mf = (MainFrame) java.beans.Beans.instantiate(
			// MainFrame.class.getClassLoader(),
			// "geovista.geoviz.parvis.MainFrame");
			MainFrame mf = MainFrame.class.newInstance();
			mf.setVisible(true);
			// ((MainFrame) MainFrame.class.newInstance()).setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private long progressstart = 0;

	public void processProgressEvent(ProgressEvent e) {
		switch (e.getType()) {
		case ProgressEvent.PROGRESS_START:
			progressstart = e.getTimestamp();
			progressBar.setValue(0);
			timeLabel.setText("0 s");
			break;

		case ProgressEvent.PROGRESS_UPDATE:
			progressBar.setValue((int) (e.getProgress() * 100));
			timeLabel.setText(((e.getTimestamp() - progressstart) / 1000)
					+ " s");
			break;

		case ProgressEvent.PROGRESS_FINISH:
			progressBar.setValue(100);
			timeLabel.setText(((e.getTimestamp() - progressstart) / 1000)
					+ " s");
			break;
		}
		progressLabel.setText(e.getMessage());
		logger.finest(e.getMessage() + ": " + ((int) (e.getProgress() * 100))
				+ "%");
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JPanel quickPrefPanel;
	private javax.swing.ButtonGroup menuEditGroup;
	private javax.swing.JMenuBar menuBar;
	private javax.swing.JPanel statusPanel;
	private javax.swing.JToggleButton scaleButton;
	private javax.swing.JButton jButton1;
	private javax.swing.JRadioButtonMenuItem orderMenu;
	private javax.swing.JMenuItem scaleZeroMaxItem;
	private javax.swing.JCheckBox hoverBox;
	private javax.swing.JMenuItem preferencesMenu;
	private javax.swing.ButtonGroup buttonEditGroup;
	private javax.swing.JPanel progressPanel;
	private javax.swing.JToggleButton translateButton;
	private javax.swing.JPanel toolbarPanel;
	private javax.swing.JLabel datasourceLabel;
	private javax.swing.JMenuItem scaleMinMaxAbsItem;
	private javax.swing.JCheckBox tooltipBox;
	private javax.swing.JRadioButtonMenuItem brushMenu;
	private javax.swing.JLabel radiusLabel;
	private javax.swing.JLabel timeLabel;
	private javax.swing.JProgressBar progressBar;
	private javax.swing.JTextField urlField;
	private javax.swing.JLabel progressLabel;
	private geovista.geoviz.parvis.ParallelDisplay parallelDisplay;
	private javax.swing.JToggleButton orderButton;
	private javax.swing.JRadioButtonMenuItem translateMenu;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.JToolBar urlBar;
	private javax.swing.JMenu editMenu;
	private javax.swing.JToolBar modeBar;
	private javax.swing.JRadioButtonMenuItem scaleMenu;
	private javax.swing.JTextField radiusField;
	private javax.swing.JMenu fileMenu;
	private javax.swing.JLabel modeLabel;
	private javax.swing.JMenuItem scaleMinMaxItem;
	private javax.swing.JMenuItem openMenu;
	private javax.swing.JMenu viewMenu;
	private javax.swing.JToggleButton brushButton;
	// End of variables declaration//GEN-END:variables

}
