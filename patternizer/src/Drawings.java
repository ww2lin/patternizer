import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Ellipse2D.Double;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.vecmath.Point2d;


public class Drawings extends Drawable{
	private static final int size = 50;
	private int circleX=pattern.windowWidth/2 - size/2;
	private int circleY=pattern.windowHeight/2 - size/2;
	private Double centerCircle = new Ellipse2D.Double(circleX,circleY, size, size);
	private int selectedColor = 1;
	private Color color = Color.BLUE;
	private ArrayList<CustomShape> shapes = new ArrayList<CustomShape>();
	
	
	public void add(CustomShape cs){
		shapes.add(cs);
	}
	public void clear(){
		shapes.clear();
	}
	public ArrayList<CustomShape> getShapes(){
		return shapes;
	}
	
	public double getCricleLocationX(){
		return circleX;
	}
	
	public double getCricleLocationY(){
		return circleY;
	}
	
    public boolean isInsideCricle(int x, int y){
    	return centerCircle.contains(x, y);
    }
    
    public void onResize(){
    	circleX=pattern.windowWidth/2 - size/2;
    	circleY=pattern.windowHeight/2 - size/2;
    	centerCircle = new Ellipse2D.Double(circleX,circleY, size, size);
    }
    public static double getNewCircleX(){
    	return pattern.windowWidth/2 - size/2;
    }
    public static double getNewCircleY(){
    	return pattern.windowHeight/2 - size/2;
    }
    
    public static double getCenterCircleX(){
    	return pattern.windowWidth/2;
    }
    public static double getCenterCircleY(){
    	return pattern.windowHeight/2;
    }
    
    
    
    public void paint(Graphics g){
    	Graphics2D g2 = (Graphics2D)g;
    	g2.setColor(color);
    	g2.fillOval(circleX,circleY, size, size);
    }
    public void switchColor(){
    	switch (++selectedColor){
			case 1:
				color =  Color.BLUE;
				break;
			case 2:
				color =  Color.CYAN;
				break;
			case 3:
				color =  Color.GREEN;
				break;
			case 4:
				color =  Color.MAGENTA;
				break;
			case 5:
				color =  Color.ORANGE;
				break;
			case 6:
				color =  Color.PINK;
				break;
			case 7:
				color =  Color.RED;
				break;
			case 8:
				color =  Color.WHITE;
				break;
			default:
				selectedColor=0;
				color = Color.GRAY;
				break;
		}
    }
	public Color getNextColor (){
		switch (selectedColor+1){
			case 2:
				return Color.CYAN;
			case 3:
				return Color.GREEN;
			case 4:
				return Color.MAGENTA;
			case 5:
				return Color.ORANGE;
			case 6:
				return Color.PINK;
			case 7:
				return Color.RED;
			case 8:
				return Color.WHITE;
			case 9:
				return Color.GRAY;
			default:
				return  Color.BLUE;
		}
	}
    public void setColor(Color c){
    	color=c;
    }
    
	public Color getCurrentColor (){
		return color;
	}
    
	public void setSelectedColor(int selectedColor) {
		this.selectedColor = selectedColor;
	}
	public int getSelectedColor() {
		return selectedColor;
	}

	public Drawings deepClone() {
		Drawings clone = new Drawings();
		clone.selectedColor =  this.selectedColor;
		clone.color = color;
		ArrayList<CustomShape> deepCopy = new ArrayList<CustomShape>();
		for (CustomShape cs : shapes){
			deepCopy.add((CustomShape)cs.deepClone());
			
		}
		clone.shapes=deepCopy;
		clone.circleX = circleX;
		clone.circleY= circleY;
		return clone;
	}
	
}
