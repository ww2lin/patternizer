import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import javax.vecmath.Point2d;


public class CustomShape extends Shape implements Serializable  {
	
	private static final long serialVersionUID = -7177087463910873385L;
	private double rotateByAngle=0;
	//double oldRotateByAngle=Math.toRadians(5);
	private Point[] transformedShape =  new Point[points.size()];
	private Point[] originalShape =  new Point[points.size()];
	//int x_points[],y_points[];
	private double scaleFactor=1;
	private double oldScale = 1;
	
	//keep track of old rotation
	//then we can offset the new rotation angle starting from the old rotation
	private double oldRotation=0; 
	private double newRotation=0;
	private double lastOffsetX=0;
	private double lastOffsetY=0;
	private int strokeThicknessFactor = 1;
	ArrayList<Point2d[]> allTransformedShapes = new ArrayList<Point2d[]>();//the copied shape from 0 to 2Pi;
	
	public void setStrokeThicknessFactor(int strokeThicknessFactor) {
		this.strokeThicknessFactor = strokeThicknessFactor;
	}

	public int getStrokeThicknessFactor() {
		return strokeThicknessFactor;
	}

	@Override
	public void paint(Graphics2D g2) {
		  if (hasChanged) {
				transformedShape = new Point[points.size()];// new Point
				originalShape = new Point[points.size()];
	            for (int i=0; i < points.size(); i++) {
//	            	originalShape[i] = new Point((int) points.get(i).x-pattern.offsetX, (int) points.get(i).y-pattern.offsetY);
//	    			transformedShape[i] = new Point((int) points.get(i).x-pattern.offsetX, (int) points.get(i).y-pattern.offsetY);
	            	originalShape[i] = new Point((int) points.get(i).x, (int) points.get(i).y);
	    			transformedShape[i] = new Point((int) points.get(i).x, (int) points.get(i).y); 
	            }
	            hasChanged = false;
	        }
		  
		  
	        //don't draw if path2D is empty (not shape)
	        if (originalShape != null) {
				double maxAngle = 2 *Math.PI;//startingAngle;
				scaleShape(originalShape,transformedShape,scaleFactor);
				rotateShape(transformedShape, transformedShape, newRotation);
				x_points = new int [transformedShape.length];
				y_points= new int[transformedShape.length];
				for (int k = 0; k < transformedShape.length; ++k){
					x_points[k] = transformedShape[k].x;
					y_points[k] = transformedShape[k].y;
				}
	        	
	        	// special draw for selection
	        	if (isSelected) {
	        		g2.setColor(Color.YELLOW);
	        		g2.setStroke(new BasicStroke(strokeThickness * strokeThicknessFactor+10));
	            	if (isClosed)
	                    g2.drawPolygon(x_points, y_points, transformedShape.length);
	                else
	                    g2.drawPolyline(x_points, y_points, transformedShape.length);
	        	}
				g2.setColor(colour);
				drawTransformedShape(g2);
	        	allTransformedShapes.clear();
	        	//draw the copies
	    		for (double i = newRotation;i <= maxAngle && newRotation >= Math.toRadians(5) ;i = i + newRotation) {
	    			scaleShape(originalShape,transformedShape,scaleFactor);
	    			rotateShape(transformedShape, transformedShape, i);
	    			x_points = new int [transformedShape.length];
	    			y_points= new int[transformedShape.length];
	    			Point2d copies[] = new Point2d[transformedShape.length];
	    			for (int k = 0; k < transformedShape.length; ++k){
	    				x_points[k] = transformedShape[k].x;
	    				y_points[k] = transformedShape[k].y;
	    				copies[k] = new Point2d(x_points[k],y_points[k]);
	    			}
	    			allTransformedShapes.add(copies);
	    			drawTransformedShape(g2);
	    		} 

	        }

	}
	
	public double findCloestestDistance(int x, int y){
		double distance = pattern.windowHeight+pattern.windowWidth;
		double d = pattern.windowHeight+pattern.windowWidth; // temporary distance
		Point2d M = new Point2d(x,y);
		for (int j=0; j < points.size()-1; ++j){
			d = Util.closestDistance(M, points.get(j), points.get(j+1));
			if (d < distance) {
				
				distance = d;
			}
		}
		for (Point2d[] t : allTransformedShapes){
			Point2d[] shapes = t;
			for (int j = 0; j < shapes.length-1; ++j) {
				d = Util.closestDistance(M, shapes[j], shapes[j+1]);
				if (d < distance) {
					distance = d;
				}
			}
		}
		return distance;
		
	}
	
	
	public void drawTransformedShape(Graphics2D g2){
        // call right drawing function
        if (isFilled) {
            g2.fillPolygon(x_points, y_points, points.size());
        }
        else {
        	g2.setStroke(new BasicStroke(strokeThickness * strokeThicknessFactor));
        	if (isClosed)
                g2.drawPolygon(x_points, y_points, points.size());
            else
                g2.drawPolyline(x_points, y_points, points.size());
        }
	}
	
	
	public void onMouseRelease(){
		oldRotation=newRotation;
		oldRotation=Util.positveMod(oldRotation,Math.PI*2);
		oldScale=scaleFactor;
	}
	
	public void onMouseDrag(int clickedX, int clickedY, int dragX, int dragY){
		updateShapeScale(clickedX, clickedY, dragX,dragY);
		updateShapeRotation(clickedX, clickedY, dragX,dragY);
	}

	public void onResize(double offsetX, double offsetY){
		hasChanged=true;
		
		for (Point2d p : points){
			Point2d tempP = p;
			
			p.x = tempP.x - lastOffsetX + offsetX;
			p.y = tempP.y - lastOffsetY + offsetY;
		}
	}
	public void setLastOffsetX(double lastOffsetX){
		this.lastOffsetX = lastOffsetX;
	}
	
	public void setLastOffsetY(double lastOffsetY){
		this.lastOffsetY = lastOffsetY;
	}
	
	//update the scale of the ship, when mouse drags
	private void updateShapeScale(int clickedX, int clickedY, int dragX, int dragY){
		double dragDistance = Util.distance(Drawings.getCenterCircleX(), Drawings.getCenterCircleY(), dragX, dragY);
		double startDistance = Util.distance(Drawings.getCenterCircleX(),  Drawings.getCenterCircleY(), clickedX, clickedY);
		
		double scale =dragDistance/startDistance;
		scaleFactor = oldScale*scale;
	}
	
	//update the rotation of the ship, when mouse drags
	private void updateShapeRotation(int clickedX, int clickedY, int dragX, int dragY){
		double dragAngle = getRotatedAngle(dragX, dragY);
		// get the angle dragged
		rotateByAngle = dragAngle - getRotatedAngle(clickedX, clickedY);
		rotateByAngle = Util.positveMod(rotateByAngle, 2 * Math.PI);
		// update the roated angle.
		newRotation = oldRotation + rotateByAngle;
		newRotation = Util.positveMod(newRotation, 2 * Math.PI);
		//System.out.println("rotation "+newRotation + " pressed "+clickedX+" "+clickedY+" "+dragX+" "+dragY );
	}
	
    //return the angle of the point xy from the center of the circle
	private double getRotatedAngle(double x, double y){
		double px = x -  Drawings.getCenterCircleX();
		double py =  Drawings.getCenterCircleY() - y;
		double angle =-Math.atan2(py, px);
		return angle;
		
	}
	
	
    private void rotateShape(Point[] oldShape,Point[] rotatedShape,double angle){
        AffineTransform.getRotateInstance
        (angle, Drawings.getCenterCircleX(),  Drawings.getCenterCircleY())
                .transform(oldShape,0,rotatedShape,0,oldShape.length);
    }
    private void scaleShape(Point[] oldShape,Point[] returnedArray,double scale){
    	AffineTransform.getTranslateInstance(-Drawings.getCenterCircleX(), -Drawings.getCenterCircleY()).transform(oldShape,0,returnedArray,0,oldShape.length);
    	AffineTransform.getScaleInstance(scale, scale).transform(returnedArray,0,returnedArray,0,oldShape.length);
    	AffineTransform.getTranslateInstance(Drawings.getCenterCircleX(), Drawings.getCenterCircleY()).transform(returnedArray,0,returnedArray,0,oldShape.length);
    }
    
 
	public Drawable  deepClone() {
		CustomShape clone = new CustomShape();
		for (int i = 0; i < points.size(); ++i){
			clone.points.add(new Point2d(points.get(i).x,points.get(i).y));
		}
		//only need to set hasChange to true, then other arrays will get initialize
		clone.hasChanged = true;
		
		clone.isFilled=isFilled;
		clone.isClosed= isClosed;
		clone.colour = colour;
		clone.strokeThickness = strokeThickness;
		clone.isSelected = false;
		
		clone.rotateByAngle = rotateByAngle;
		clone.scaleFactor = scaleFactor;
		clone.oldScale = oldScale;
		clone.oldRotation = oldRotation;
		clone.newRotation = newRotation;
		clone.lastOffsetX = lastOffsetX;
		clone.lastOffsetY = lastOffsetY;
		clone.strokeThicknessFactor = strokeThicknessFactor;
		return clone;
		
	}
    
}
