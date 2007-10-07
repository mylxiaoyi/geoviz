/* -------------------------------------------------------------------
 GeoVISTA Center (Penn State, Dept. of Geography)
 Java source file for the class ClassifierPicker
 Copyright (c), 2002, GeoVISTA Center
 All Rights Reserved.
 Original Author: Frank Hardisty
 $Author: hardisty $
 $Id: ClassifierPicker.java,v 1.6 2005/03/24 20:27:16 hardisty Exp $
 $Date: 2005/03/24 20:27:16 $
 This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.
  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.
  You should have received a copy of the GNU Lesser General Public
  License along with this library; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
  -------------------------------------------------------------------   */

package geovista.common.classification;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;

import edu.psu.geovista.common.data.DataSetForApps;
import edu.psu.geovista.common.event.ClassificationEvent;
import edu.psu.geovista.common.event.ClassificationListener;
import edu.psu.geovista.common.event.DataSetModifiedEvent;

public class ClassifierPicker
    extends JPanel
    implements ActionListener, ComponentListener {
  public static final String COMMAND_CLASSES_CHANGED = "colors";
  public static final String COMMAND_BEAN_REGISTERED = "hi!";
  public static final String COMMAND_SELECTED_VARIABLE_CHANGED = "var_change";
  public static final String COMMAND_SELECTED_CLASSIFIER_CHANGED =
      "classer_change";
  public static final int X_AXIS = 0;
  public static final int Y_AXIS = 1;
  public static final int VARIABLE_CHOOSER_MODE_ACTIVE = 0;
  public static final int VARIABLE_CHOOSER_MODE_FIXED = 1;
  public static final int VARIABLE_CHOOSER_MODE_HIDDEN = 2;
  private int nClasses;
  private boolean update;
  transient private JSlider classSlider;
  transient protected JComboBox classifCombo;
  protected transient JComboBox variableCombo;
  transient private WholeNumberField classesField;
  protected transient String currVariableName;
  protected transient int currVariableIndex;
  private transient DataSetForApps dataSet;
  protected transient DescribedClassifier[] classers;
  private transient int currClasser;
  private transient int currOrientation = ClassifierPicker.X_AXIS;
  private int variableChooserMode = ClassifierPicker.VARIABLE_CHOOSER_MODE_HIDDEN; //default

  public ClassifierPicker() {
    super();
    this.nClasses = 3;
    this.addComponentListener(this);
    this.classers = new DescribedClassifier[5];
    
    this.classers[1] = new ClassifierQuantiles();
    this.classers[2] = new ClassifierModifiedQuantiles();
    this.classers[0] = new ClassifierEqualIntervals();
    this.classers[3] = new ClassifierStdDev();
    this.classers[4] = new ClassifierRawQuantiles();

    
    this.update = true;
    this.init();
    this.setPreferredSize(new Dimension(365, 35)); //these match the colorRampPicker

    //this.setMinimumSize(new Dimension(200,20));
    //this.setMaximumSize(new Dimension(1000,60));
    this.setVariableChooserMode(this.variableChooserMode);
  }

  private void init() {
    this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

    //JLabel classesLabel = new JLabel("N Classes:");
    classSlider = new JSlider(JSlider.HORIZONTAL, 2, 20, nClasses);

    //classSlider.setPreferredSize(new Dimension(100,30));
    classSlider.addChangeListener(new SliderListener());
    classSlider.setMajorTickSpacing(5);
    classSlider.setMinorTickSpacing(1);
    classSlider.setPaintTicks(false);
    classSlider.setPaintLabels(false);
    classSlider.setSnapToTicks(true);
    this.classSlider.setMinimumSize(new Dimension(100, 20));
    this.classSlider.setMaximumSize(new Dimension(100, 20));

    //classSlider.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
    //this.add(classesLabel);
    this.add(classSlider);

    classesField = new WholeNumberField(nClasses, 1);
    classesField.addActionListener(this);
    this.add(classesField);

    classifCombo = new JComboBox();

    this.fillCombo();
    this.classifCombo.setMinimumSize(new Dimension(80, 20));
    this.classifCombo.setMaximumSize(new Dimension(120, 20));
    classifCombo.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JComboBox cb = (JComboBox) e.getSource();

        if (cb.getItemCount() > 0) {
          int classer = cb.getSelectedIndex();

          if (ClassifierPicker.this.currClasser != classer) {
            ClassifierPicker.this.setClasser(classer);
            ClassifierPicker.this.fireActionPerformed(
                ClassifierPicker.COMMAND_SELECTED_CLASSIFIER_CHANGED);
            ClassifierPicker.this.fireClassificationChanged();
          } //end if
        } //end if count > 0
      } //end inner class
    }); //end add listener

    JLabel twoSpacesclassifCombo = new JLabel("  ");
    this.add(twoSpacesclassifCombo);

    //this.add(classifLabel);
    this.add(classifCombo);

    variableCombo = new JComboBox();
    variableCombo.setMinimumSize(new Dimension(80, 20));
    variableCombo.setMaximumSize(new Dimension(120, 20));
    variableCombo.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JComboBox cb = (JComboBox) e.getSource();

        if (cb.getItemCount() > 0) {
          if (ClassifierPicker.this.currVariableIndex != cb.getSelectedIndex()) {
            String arrayName = (String) cb.getSelectedItem();
            ClassifierPicker.this.currVariableName = arrayName;
            ClassifierPicker.this.currVariableIndex = cb.getSelectedIndex();
            ClassifierPicker.this.fireActionPerformed(
                ClassifierPicker.COMMAND_SELECTED_VARIABLE_CHANGED);

            //ClassifierPicker.this.setVariableColumn(index + 1); //skip header + 1
            ClassifierPicker.this.fireClassificationChanged();
          } //end if
        } //end if count > 0
      } //end inner class
    }); //end add listener

    JLabel twoSpacesVariablePicker = new JLabel("  ");
    this.add(twoSpacesVariablePicker);

    // this.add(updateLabel);
    this.add(variableCombo);

    //this.setBorder(BorderFactory.createLineBorder(Color.black));
  }

  private void fillCombo() {
    for (int i = 0; i < this.classers.length; i++) {
      this.classifCombo.addItem(this.classers[i].getFullName());
    }
  }

  public void changeOrientation(int orientation) {
    if (orientation == this.currOrientation) {
      return;
    }
    else if (orientation == ClassifierPicker.X_AXIS) {
      Component[] comps = new Component[this.getComponentCount()];

      for (int i = 0; i < this.getComponentCount(); i++) {
        comps[i] = this.getComponent(i);
      }

      this.classSlider.setOrientation(JSlider.HORIZONTAL);
      this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

      for (int i = 0; i < this.getComponentCount(); i++) {
        this.add(comps[i]);
      }

      this.currOrientation = ClassifierPicker.X_AXIS;
      this.revalidate();
    }
    else if (orientation == ClassifierPicker.Y_AXIS) {
      Component[] comps = new Component[this.getComponentCount()];

      for (int i = 0; i < this.getComponentCount(); i++) {
        comps[i] = this.getComponent(i);
      }

      //this.classSlider.setOrientation(JSlider.VERTICAL);
      this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

      for (int i = 0; i < this.getComponentCount(); i++) {
        this.add(comps[i]);
      }

      this.currOrientation = ClassifierPicker.Y_AXIS;
      this.revalidate();
    }
  }

  //start component event handling
  //note: this class only listens to itself
  public void componentHidden(ComponentEvent e) {
  }

  public void componentMoved(ComponentEvent e) {
  }

  public void componentShown(ComponentEvent e) {
  }

  public void componentResized(ComponentEvent e) {
    float ratio = ( (float)this.getWidth() / (float)this.getHeight());

    if ( (ratio >= 1) && (this.currOrientation == ClassifierPicker.Y_AXIS)) {
      this.changeOrientation(ClassifierPicker.X_AXIS);
    }

    if ( (ratio < 1) && (this.currOrientation == ClassifierPicker.X_AXIS)) {
      this.changeOrientation(ClassifierPicker.Y_AXIS);
    }
  }

  //end component event handling
  
  /**
 * @param dataIn
 * 
 * This method is deprecated becuase it wants to create its very own pet
 * DataSetForApps. This is no longer allowed. Please use setDataSet
 * instead.
 */
  @Deprecated
public void setData(Object[] dataIn) {
    this.dataSet = new DataSetForApps();
    //dataSet.setDataObject(dataIn);
    this.setVariableNames(dataSet.getAttributeNamesNumeric());
    if (dataSet.getNumberNumericAttributes() > 1) {
      this.variableCombo.setSelectedIndex(0);
    }
  }
  
  public void setDataSet(DataSetForApps data){
	 this.dataSet = data; 
	    this.setVariableNames(dataSet.getAttributeNamesNumeric());
	    if (dataSet.getNumberNumericAttributes() > 1) {
	      this.variableCombo.setSelectedIndex(0);
	    }
  }

  public void setAdditionalData(DataSetForApps dataIn) {
    this.currVariableIndex = this.variableCombo.getSelectedIndex();
    this.dataSet = this.dataSet.appendDataSet(dataIn);

    this.setVariableNames(dataSet.getAttributeNamesNumeric());
    this.variableCombo.setSelectedIndex(this.currVariableIndex);
  }

  public void setVariableNames(String[] variableNames) {
    this.variableCombo.removeAllItems();

    for (int i = 0; i < variableNames.length; i++) {
      this.variableCombo.addItem(variableNames[i]);
    }
  }

  public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();

    if (command.equals(WholeNumberField.COMMAND_NEW_VAL)) {
      WholeNumberField holeField = (WholeNumberField) e.getSource();
      this.nClasses = holeField.getValue();

      if (classSlider.getMinimum() > nClasses) {
        this.classSlider.setValueIsAdjusting(true);
        this.classSlider.setValue(classSlider.getMinimum());
      }
      else if (classSlider.getMaximum() < nClasses) {
        this.classSlider.setValueIsAdjusting(true);
        this.classSlider.setValue(classSlider.getMaximum());
      }
      else {
        /*
                 //removed by jin chen to fix the bug that seting number in the text field
          won't change the number of category although it move slider's knob
                 this.classSlider.setValueIsAdjusting(true);
         */
        this.classSlider.setValue(nClasses);
      }
    }
  }

  /**
   * implements ActionListener
   */
  public void addActionListener(ActionListener l) {
    listenerList.add(ActionListener.class, l);
    this.fireActionPerformed(ClassifierPicker.COMMAND_BEAN_REGISTERED);
  }

  /**
   * removes an ActionListener from the component
   */
  public void removeActionListener(ActionListener l) {
    listenerList.remove(ActionListener.class, l);
  }

  /**
   * Notify all listeners that have registered interest for
   * notification on this event type. The event instance
   * is lazily created using the parameters passed into
   * the fire method.
   * @see EventListenerList
   */
  private void fireActionPerformed(String command) {
    if (update) {
      // Guaranteed to return a non-null array
      Object[] listeners = listenerList.getListenerList();
      ActionEvent e = null;

      // Process the listeners last to first, notifying
      // those that are interested in this event
      for (int i = listeners.length - 2; i >= 0; i -= 2) {
        if (listeners[i] == ActionListener.class) {
          // Lazily create the event:
          if (e == null) {
            e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, command);
          }

          ( (ActionListener) listeners[i + 1]).actionPerformed(e);
        }
      } //next i
    } //end if
  }

  public void setNClasses(int nClasses) {
    this.classSlider.setValue(nClasses);
    this.classesField.setValue(nClasses);
    this.nClasses = nClasses;

  }

  public int getNClasses() {
    return nClasses;
  }

  public int getVariableChooserMode() {
    return this.variableChooserMode;
  }

  public void setVariableChooserMode(int variableChooserMode) {
    this.variableChooserMode = variableChooserMode;

    if (variableChooserMode == ClassifierPicker.VARIABLE_CHOOSER_MODE_ACTIVE) {
      this.variableCombo.setEnabled(true);
      this.variableCombo.setVisible(true);
    }
    else if (variableChooserMode == ClassifierPicker.VARIABLE_CHOOSER_MODE_FIXED) {
      this.variableCombo.setEnabled(false);
      this.variableCombo.setVisible(true);
    }
    else if (variableChooserMode == ClassifierPicker.VARIABLE_CHOOSER_MODE_HIDDEN) {
      this.variableCombo.setVisible(false);
    }
    else {
      throw new IllegalArgumentException(
          "ClassifierPicker.setVariableChooserMode," +
          " unknown mode encountered, mode = " + variableChooserMode);
    }
  }

  public Classifier getClasser() {
    return this.classers[currClasser];
  }

  public DataSetForApps getDataSet() {
    return this.dataSet;
  }

  private void setClasser(int classer) {
    this.currClasser = classer;
  }

  public int getCurrVariableIndex() {
    return currVariableIndex;
  }

  public void setCurrVariableIndex(int currVariableIndex) {
    this.currVariableIndex = currVariableIndex;
    this.variableCombo.setSelectedIndex(currVariableIndex);
    fireClassificationChanged(); //inserted fah 7 oct 03
  }
  public void dataSetModified(DataSetModifiedEvent e) {
    if (e.getEventType() == DataSetModifiedEvent.TYPE_EXTENDED) {
      //XXX unsafe, modifying for build only...
      //Object[] newData = (Object[])e.getNewData();

      Object[] newData = null;
      DataSetForApps newDataSet = new DataSetForApps(newData);
      this.dataSet = newDataSet;
      String[] newVarNames = newDataSet.getAttributeNamesOriginal();
      for(int i = 0; i < newVarNames.length; i++){
        this.variableCombo.addItem(newVarNames[i]);
      }

    }
  }

  /**
   *
   * @return the results of the current classification on the current data set
   */
  public int[] findClassification() {

    int nClasses = this.getNClasses();
    Classifier classer = this.getClasser();
    double[] data = dataSet.getNumericDataAsDouble(this.currVariableIndex);

    return classer.classify(data, nClasses);

  }
  public void tableChanged(TableModelEvent e) {

	  int col = e.getColumn();
	  this.setCurrVariableIndex(col);
		
	}
  /**
   * adds an ClassificationListener
   */
  public void addClassificationListener(ClassificationListener l) {
    listenerList.add(ClassificationListener.class, l);
  }

  /**
   * removes an ClassificationListener from the component
   */
  public void removeClassificationListener(ClassificationListener l) {
    listenerList.remove(ClassificationListener.class, l);
  }

  /**
   * Notify all listeners that have registered interest for
   * notification on this event type. The event instance
   * is lazily created using the parameters passed into
   * the fire method.
   * @see EventListenerList
   */
  public void fireClassificationChanged() {
    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();
    ClassificationEvent e = null;

    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == ClassificationListener.class) {
        // Lazily find the classification
        int[] newClassification = this.findClassification();
        // Lazily create the event:
        if (e == null) {
          e = new ClassificationEvent(this, newClassification);
        }

        ( (ClassificationListener) listeners[i + 1]).classificationChanged(e);
      }
    } //next i
  }

  /** Listens to the check boxen. */
  class CheckBoxListener
      implements ItemListener {
    public void itemStateChanged(ItemEvent e) {
      /*
        if (e.getSource().equals(ClassifierPicker.this.updateBox)){
          if (e.getStateChange() == ItemEvent.SELECTED && ClassifierPicker.this.setupFinished){
              ClassifierPicker.this.update = true;
              ClassifierPicker.this.fireActionPerformed(ClassifierPicker.COMMAND_CLASSES_CHANGED);

          } else if (e.getStateChange() == ItemEvent.DESELECTED){

              ClassifierPicker.this.update = false;
          }
        }*/
    }
  }

  class SliderListener
      implements ChangeListener {
    public void stateChanged(ChangeEvent e) {
      JSlider source = (JSlider) e.getSource();
      int classes = (int) source.getValue();

      if (!source.getValueIsAdjusting()) {
        ClassifierPicker.this.nClasses = classes;
        ClassifierPicker.this.classesField.setValue(classes);
        ClassifierPicker.this.fireClassificationChanged();
        ClassifierPicker.this.fireActionPerformed(
            ClassifierPicker.COMMAND_CLASSES_CHANGED);
      }
    }
  }

}
