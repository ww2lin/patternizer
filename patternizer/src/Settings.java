
public class Settings {
	private static final int MIN_STROKE_THICKNESS = 1;
	private static final int MAX_STROKE_THICKNESS = 10;
	private static final int MIN_RBG_VALUE = 0;
	private static final int MAX_RBG_VALUE = 255;
	private static final int NUMBER_OF_COLOURS = 3;
	public static boolean turnOffUndo = false;
	public static int getMinstrokethickness() {
		return MIN_STROKE_THICKNESS;
	}
	public static int getMaxstrokethickness() {
		return MAX_STROKE_THICKNESS;
	}
	public static int getMinRbgValue() {
		return MIN_RBG_VALUE;
	}
	public static int getMaxRbgValue() {
		return MAX_RBG_VALUE;
	}
	public static int getNumberOfColours() {
		return NUMBER_OF_COLOURS;
	}
	
}
