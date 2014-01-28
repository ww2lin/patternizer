import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;



public class Util {

    public static double sqr(double x){
    	return Math.pow(x, 2);
    }
	
    public static double distance(double x1,double y1,double x2,double y2){
    	return Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
    }
    
	public static double positveMod(double x, double modulo) {
		return ((x % modulo) + modulo) % modulo;
	}
	
    // find closest point using projection method method
	
    public static double closestDistance(Point2d M, Point2d P0, Point2d P1){
    	Vector2d v = new Vector2d();
    	v.sub(P1,P0); // v = P2 - P1
    	
    	// early out if line is less than 1 pixel long
    	if (v.lengthSquared() < 0.5){
    		return Util.distance(P0.x, P0.y, M.x, M.y);
    		//return 0;
    		//return P0;
    	}
    	Vector2d u = new Vector2d();
    	u.sub(M,P0); // u = M - P1
    
    	// scalar of vector projection ...
    	double s = u.dot(v)  // u dot v 
    			 / v.dot(v); // v dot v
    	
    	// find point for constrained line segment
    	if (s < 0) {
    		return Util.distance(P0.x, P0.y, M.x, M.y);
    		//return P0;
    	}
    	else if (s > 1){
    		return Util.distance(P1.x, P1.y, M.x, M.y);
    		//return P1;
    	}
    		
    	else {
    		Point2d I = P0;
        	Vector2d w = new Vector2d();
        	w.scale(s, v); // w = s * v
    		I.add(w); // I = P1 + w
    		return Util.distance(I.x, I.y, M.x, M.y);
    		//return I;
    	}
    }
}
