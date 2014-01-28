import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputListener;


public class pattern extends JPanel implements MouseInputListener {


	private static final long serialVersionUID = -6266314472870886324L;
	private final static int MAX_WINDOW_WIDTH =592;
	private final static int MAX_WINDOW_HEIGHT =592;
	
	public static int windowHeight = MAX_WINDOW_HEIGHT;
	public static int windowWidth =  MAX_WINDOW_WIDTH;
	private final int distanceToSelect = 30;
	
	private static BufferedImage originalImage; //for reposition the image to the center
	
	
	private int clickedX = windowWidth/2;
	private int clickedY = windowHeight/2;
	private int dragX=windowWidth/2;
	private int dragY= windowHeight/2;
	public static int offsetX=0;
	public static int offsetY=0;
	

	
	private Drawings drawings = new Drawings();
	private CustomShape currentDrawingShape = new CustomShape();
	private CustomShape selectedShape = new CustomShape();
	private HistoryRecord history = new HistoryRecord();
	
	private int strokeThicknessFactor = 1;
	private Color backgroundColor=Color.BLACK;
	
	
	///ui componments
	private Container menu = new Container();
	private final JLabel strokeLabel = new JLabel();
	JSlider strokeThicknessSlider = new JSlider(JSlider.HORIZONTAL,
            Settings.getMinstrokethickness(), Settings.getMaxstrokethickness(), strokeThicknessFactor);
	private JButton undo = new JButton();
	private JButton redo= new JButton();
	private static final int RED=0;
	private static final int GREEN=1;
	private static final int BLUE=2;
	
	private JLabel selectedColor = new JLabel( "The Color Selected");
	private int[] selectedColorArray = new int[]{0,0,0};
	
	private double newCircleX =0;
	private double newCircleY=0;
	public  pattern() {
		originalImage = new BufferedImage(windowHeight, windowWidth,BufferedImage.TYPE_INT_ARGB);
		// add listeners
		addMouseListener(this);
		addMouseMotionListener(this);
		addComponentListener(new ComponentListener() {
			@Override
			public void componentResized(ComponentEvent e) {
				if (e.getComponent().getHeight()>0 &&   e.getComponent().getWidth() >0){
					double newX=drawings.getCricleLocationX();
					double newY=drawings.getCricleLocationY();
					
					windowHeight = e.getComponent().getHeight();
					windowWidth = e.getComponent().getWidth();
					offsetX = (MAX_WINDOW_WIDTH - windowWidth) / 2;
					offsetY = (MAX_WINDOW_HEIGHT - windowHeight) / 2;
					originalImage = new BufferedImage(windowWidth, windowHeight,BufferedImage.TYPE_INT_ARGB);
					
					newCircleX = Drawings.getNewCircleX();
					newCircleY = Drawings.getNewCircleY();
					
					flagDirtyBit(newX,newY);
					repaint();
				}
			}

			@Override
			public void componentHidden(ComponentEvent arg0) {
			}

			@Override
			public void componentMoved(ComponentEvent arg0) {
			}

			@Override
			public void componentShown(ComponentEvent arg0) {
			}

		});
		setLayout(new BorderLayout());
		menu.setLayout(new GridLayout());
		
		addHistoryButtons();
		
		addStrokeSlider();
		
		addSwitchColorButton();
		
		addSavePicture();

		this.add(menu, BorderLayout.SOUTH);

		currentDrawingShape.setLastOffsetX(Drawings.getNewCircleX());
		currentDrawingShape.setLastOffsetY(Drawings.getNewCircleY());
		
		repaint();
	}

	
	public static void main(String[] args) {
		pattern canvas = new pattern();
        JFrame f = new JFrame("Pattern"); // jframe is the app window
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //f.setPreferredSize(new Dimension(MAX_WINDOW_WIDTH,MAX_WINDOW_HEIGHT));
        canvas.setPreferredSize(new Dimension(MAX_WINDOW_WIDTH,MAX_WINDOW_HEIGHT));
        f.setContentPane(canvas); // add canvas to jframe    
        f.pack();
        f.setVisible(true); // show the window

	}
	
	
	private void flagDirtyBit(double x, double y){
		for (CustomShape s : drawings.getShapes()) {
			s.setLastOffsetX(x);
			s.setLastOffsetY(y);
			s.onResize(newCircleX,newCircleY);
		}
		drawings.onResize();
	}
	
    // custom graphics drawing 
    public void paintComponent(Graphics g) {
    	super.paintComponent(g);
        Graphics2D g2 = originalImage.createGraphics();
    	g2.setColor(backgroundColor);
    	g2.fillRect(0, 0, windowWidth, windowHeight); //set the background to black
        for (CustomShape s : drawings.getShapes()){
        	s.paint(g2);
        }
        
        currentDrawingShape.paint(g2);//shape we are currently drawing
        drawings.paint(g2);
        g.drawImage(originalImage, 0, 0, this);
    }
    
    
	@Override
	public void mouseClicked(MouseEvent event) {
		int clickedX = event.getX();//offsetX;
		int clickedY = event.getY();//offsetY;
		if (!drawings.isInsideCricle(clickedX, clickedY)){
			if (selectedShape != null && selectedShape.isSelected()) {
				if (SwingUtilities.isRightMouseButton(event)){// double click
					history.addHistory(drawings);
					drawings.getShapes().remove(selectedShape);
					history.setRedo(drawings);
					selectedShape = new CustomShape();
					repaint();
					return;
				}
			}
			selectCloestPoint(clickedX,clickedY);

			
			if (selectedShape != null && selectedShape.isSelected()) {
					strokeLabel.setText("Thickness: "+selectedShape.getStrokeThicknessFactor());
					strokeThicknessSlider.setValue(selectedShape.getStrokeThicknessFactor());
				
			} else {
				strokeLabel.setText("Thickness: "+strokeThicknessFactor);
				strokeThicknessSlider.setValue(strokeThicknessFactor);
			}
		}else {
			if (event.getClickCount() == 2){// double click
				history.addHistory(drawings);
				drawings.clear();
				history.setRedo(drawings);
			}
			else {
				history.addHistory(drawings);
				drawings.switchColor();
				strokeLabel.setBackground(drawings.getNextColor());
				history.setRedo(drawings);
				if (selectedShape != null) {
					selectedShape.setSelected(false);
					selectedShape=null;
				}
				
			}

		}
		repaint();	
	}

	
	@Override
	public void mouseEntered(MouseEvent event) {}


	@Override
	public void mouseExited(MouseEvent event) {}


	@Override
	public void mousePressed(MouseEvent event) {
		clickedX = event.getX();//offsetX;
		clickedY = event.getY();//offsetY;
	}


	@Override
	public void mouseReleased(MouseEvent event) {
		if (currentDrawingShape.points != null && currentDrawingShape.points.size()>0){
			history.addHistory(drawings);
			currentDrawingShape.addPoint(Drawings.getCenterCircleX(), Drawings.getCenterCircleY(), 0);
			currentDrawingShape.setStrokeThicknessFactor(strokeThicknessFactor);
			drawings.add(currentDrawingShape);
			currentDrawingShape = new CustomShape();
			currentDrawingShape.setLastOffsetX(Drawings.getNewCircleX());
			currentDrawingShape.setLastOffsetY(Drawings.getNewCircleY());
			
			history.setRedo(drawings);
		}
		if (selectedShape != null && selectedShape.isSelected()) {
			selectedShape.onMouseRelease();
		}
	}


	@Override
	public void mouseDragged(MouseEvent event) {
		dragX = event.getX();
		dragY = event.getY();
		if(drawings.isInsideCricle(clickedX,clickedY)){
			currentDrawingShape.setStrokeThicknessFactor(strokeThicknessFactor);
			currentDrawingShape.addPoint(dragX, dragY);
			currentDrawingShape.setColour(drawings.getCurrentColor());
		} 
		else if (selectedShape != null && selectedShape.isSelected()){
			selectedShape.onMouseDrag(clickedX, clickedY, dragX, dragY);
		}
		repaint();	
	}


	@Override
	public void mouseMoved(MouseEvent event) {}
	

	private void selectCloestPoint(int x, int y){
		double distance = pattern.windowHeight+pattern.windowWidth;
		double d = pattern.windowHeight+pattern.windowWidth; //temporary distance
		//search the array backwards, so the line that is on top will get selected
		for (int i =drawings.getShapes().size()-1; i >=0 ; --i){
			CustomShape s = drawings.getShapes().get(i);
			s.setSelected(false);
			d = s.findCloestestDistance(x,y);

			if (d < distance ){
				distance = d;
				selectedShape = s;
				if (d == 0) {//close point found, no longer need to keep seraching
					break;
				}
			}

	    }
		if (selectedShape != null && distance <= distanceToSelect) selectedShape.setSelected(true);
	}
	
	private void updateHistory(History<Drawable> updateHistory){
		if (history.canRedo())redo.setEnabled(true);
		else redo.setEnabled(false);
		if(updateHistory == null) return;
		drawings = (Drawings)updateHistory.getItem();
		//user might of resize window, thus try and offset all points
		//System.out.println(drawings.getCricleLocationX()+ " "+drawings.getCricleLocationY());
		flagDirtyBit(drawings.getCricleLocationX(), drawings.getCricleLocationY());
		
		repaint();
	}
	
	//for undo and redo
	private void addHistoryButtons(){

		
		undo.setText("Undo");
		redo.setText("Redo");
		undo.addActionListener(new ActionListener (){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				History<Drawable> h = history.undo();
				updateHistory(h);
				
			}
			
		});
		
		redo.addActionListener(new ActionListener (){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				History<Drawable> h = history.redo();
				updateHistory(h);
			}
			
		});
		updateHistory(null);
		menu.add(undo);
		menu.add(redo);
	}
	
	public void addStrokeSlider(){
		Container strokeContainer = new Container();
		strokeLabel.setBackground(drawings.getNextColor());
		strokeLabel.setText("Thickness: "+strokeThicknessFactor);
		strokeLabel.setOpaque(true);
		strokeThicknessSlider.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent arg0) {
				JSlider s = ((JSlider) arg0.getSource());
				if (s.getValueIsAdjusting()){
						if (selectedShape != null && selectedShape.isSelected()){
							selectedShape.setStrokeThicknessFactor(s.getValue());
							selectedShape.setHasChanged(true);
							strokeLabel.setText("Thickness: "+s.getValue());
							repaint();
						}
						else if (strokeThicknessFactor != s.getValue()){
							strokeThicknessFactor = s.getValue();
							strokeLabel.setText("Thickness: "+strokeThicknessFactor);
						
					}
				}				
			}
		});
		strokeContainer.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		// stretch the widget horizontally and vertically
		gc.fill = GridBagConstraints.BOTH;
		gc.gridwidth = 1; // 1 grid cell wide
		gc.gridheight = 1; // 3 grid cells tall
		gc.weightx = 1; // the proportion of space to give this column
		gc.weighty = 0.5; // the proportion of space to give this row
		gc.gridy = 0;
		strokeContainer.add(strokeLabel,gc);
		gc.fill = GridBagConstraints.BOTH;
		gc.gridwidth = 1; // 1 grid cell wide
		gc.gridheight = 1; // 3 grid cells tall
		gc.weightx = 1; // the proportion of space to give this column
		gc.weighty = 0.5; // the proportion of space to give this row
		gc.gridy = 1;
		strokeContainer.add(strokeThicknessSlider,gc);
		menu.add(strokeContainer);
	}
	
	public void addSavePicture(){
		JButton saveImage = new JButton("Save Image");
		saveImage.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try 
			    {
			        BufferedImage bi = (BufferedImage) originalImage;
			        int i = 0;
			        File outputfile = new File("Pattern"+i+".png");
			        while (outputfile.exists()){
			        	outputfile = new File("Pattern"+(++i)+".png");
			        }
			       
			        ImageIO.write(bi, "png", outputfile);
			        JOptionPane.showMessageDialog(pattern.this,
			        "Picture sucessfully saved");
			    } catch (IOException e)  {
			    	System.out.println("ERROR while saving picture");
			    }
			}
			
			});
		menu.add(saveImage);
	}
	
	public void addSwitchColorButton(){
		JButton switchColor = new JButton("Switch Color");
		switchColor.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				history.addHistory(drawings);
				swtichColor();
				history.setRedo(drawings);
			}
			
		});
		menu.add(switchColor);
	}
	
	
	
	
	private void swtichColor (){
		final JDialog rbgColorDialog = new JDialog();
		rbgColorDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		final JSlider colorsSliders[] = new JSlider[Settings.getNumberOfColours()];
		String colorlables[] = new String[]{"R","G","B"};
		//instruction label
		JLabel instruction = new JLabel( "Drag the slider to switch colours ");
		
		//check box, to see if we are switching the background color or the circle color
		final JCheckBox backgroundColorSwitchCheckBox= new JCheckBox();
		
		//the actuall dialog
		
		rbgColorDialog.setTitle("Color Setting");
		rbgColorDialog.setSize(500, 300);
		

		
		//color slider container
		JPanel colorSliders = new JPanel();
		colorSliders.setLayout(new BoxLayout(colorSliders,BoxLayout.Y_AXIS));
		colorSliders.setSize(250, 300);
		colorSliders.add(instruction);
		for (int i=0; i <colorsSliders.length; ++i){
			JPanel tempPanel= 	new JPanel();
			tempPanel.setLayout(new BoxLayout(tempPanel,BoxLayout.X_AXIS));
			JLabel colorLabel = new JLabel( colorlables[i]);
			tempPanel.add(colorLabel);
			colorsSliders[i] = getSlider(i);
			tempPanel.add(colorsSliders[i]);
			colorSliders.add(tempPanel);
		}
		
		//inital values for the sliders
		Color previewColor = resetColorPreview(false,colorsSliders);
		
		//the label to show the color that user picked
	
		selectedColor.setBackground(new Color(previewColor.getRGB()));
		selectedColor.setOpaque(true);
		selectedColor.setSize(50, 100);
		colorSliders.add(selectedColor);
		
		//check box to apply color to the background
		JPanel checkboxPanel = new JPanel();
		checkboxPanel.setLayout(new BoxLayout(checkboxPanel,BoxLayout.X_AXIS));
		JLabel checkboxLabel = new JLabel("tick here if you want the color to be applied to the background");
		
		backgroundColorSwitchCheckBox.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				resetColorPreview(backgroundColorSwitchCheckBox.isSelected(),colorsSliders);
			}
		});
		
		checkboxPanel.add(backgroundColorSwitchCheckBox);
		checkboxPanel.add(checkboxLabel);
		colorSliders.add(checkboxPanel);
		
		
		
		//ok and cancel buttons
		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons,BoxLayout.X_AXIS));
		JButton okButton = new JButton("Confirm");
		JButton cancelButton = new JButton("Cancel");
		
		okButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Color newColor = new Color(selectedColorArray[RED],selectedColorArray[GREEN],selectedColorArray[BLUE]);
				if (backgroundColorSwitchCheckBox.isSelected()){
					backgroundColor = newColor;
				} else if (selectedShape != null && selectedShape.isSelected()){
					selectedShape.setColour(newColor);
				}
				else {
					drawings.setColor(newColor);
				}
				repaint();
				rbgColorDialog.dispose();
			}
		});
		
		cancelButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				rbgColorDialog.dispose();
			}
			
		});
		
		buttons.add(cancelButton);
		buttons.add(okButton);
		colorSliders.add(buttons);
		
		rbgColorDialog.validate();
		rbgColorDialog.add(colorSliders);
		rbgColorDialog.setVisible(true);
	}
	private JSlider getSlider(final int colorIndex) {
		    JSlider slider = new JSlider(Settings.getMinRbgValue(),Settings.getMaxRbgValue(),selectedColorArray[colorIndex]);
		    slider.setMajorTickSpacing(25);
		    slider.setPaintTicks(true);
		    slider.setPaintLabels(true);
		    ChangeListener changeListener = new ChangeListener() {
		      public void stateChanged(ChangeEvent changeEvent) {
		        JSlider colorSlider = (JSlider) changeEvent.getSource();
		        	selectedColorArray[colorIndex] = colorSlider.getValue();
		        	selectedColor.setBackground(new Color(selectedColorArray[RED],selectedColorArray[GREEN],selectedColorArray[BLUE]));
		      }
		    };
		    slider.addChangeListener(changeListener);
		    return slider;
	}
	
	private Color resetColorPreview(boolean isBackgroundEnable, JSlider colorsSliders[]){
		Color c = null;
		if (isBackgroundEnable){
			selectedColorArray[RED] = backgroundColor.getRed();
			selectedColorArray[GREEN] = backgroundColor.getGreen();
			selectedColorArray[BLUE] = backgroundColor.getBlue();
			c = backgroundColor;
		}
		else if (selectedShape != null && selectedShape.isSelected()){ 
			selectedColorArray[RED] = selectedShape.getColour().getRed();
			selectedColorArray[GREEN] = selectedShape.getColour().getGreen();
			selectedColorArray[BLUE] = selectedShape.getColour().getBlue();
			c = selectedShape.getColour();
		}
		else {
			selectedColorArray[RED] = drawings.getCurrentColor().getRed();
			selectedColorArray[GREEN] = drawings.getCurrentColor().getGreen();
			selectedColorArray[BLUE] = drawings.getCurrentColor().getBlue();
			c = drawings.getCurrentColor();
		}
		colorsSliders[RED].setValue(c.getRed());
		colorsSliders[GREEN].setValue(c.getGreen());
		colorsSliders[BLUE].setValue(c.getBlue());
		return c;
	}
}
