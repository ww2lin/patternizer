import java.util.ArrayList;


public class HistoryRecord {
		
	
	private ArrayList<History<Drawable>> historyStack = new ArrayList<History<Drawable>>();
	private final int EMPTY = -1;
	private int historyIndex =EMPTY;
	private int redoIndex = 0;
	private History<Drawable> current; //save the current drawing for redo function
	
	public History<Drawable> undo(){
		if (historyStack.size() == 0 || historyIndex <= EMPTY || Settings.turnOffUndo){
			return null;
		}
		addRedo();
		redoIndex= redoIndex-1 <=  0 ? 1 : --redoIndex;
//		historyIndex = Math.min(historyIndex, historyStack.size()-1);
		return historyStack.get(historyIndex--);
	}
	public void setRedo(Drawings current){
		this.current=new History<Drawable>(current);
	}
	
	public void addRedo(){
		if(current != null){
			historyStack.add(current);
			current = null;
		}
	}
	
	public History<Drawable> redo(){
		if (redoIndex< historyStack.size()){
			historyIndex=redoIndex -1 ;
			redoIndex++;
			return historyStack.get(redoIndex-1);
		}
		
		return null;
	}
	
	public void addHistory(Drawable item){
		if( Settings.turnOffUndo) return;
		deleteInvalidHistory();
		historyStack.add(new History<Drawable>(item.deepClone()));
	}

	
	private void deleteInvalidHistory(){
		historyIndex++;
		if (historyIndex > EMPTY){
			while (historyIndex < historyStack.size()){
				historyStack.remove(historyIndex);
			}
		}
		//since redo is only copied over the array when undo is press
		//and undo minus one from redoindex, therefore we need to add 2
		//to offset that change.
		redoIndex=historyIndex+2;
	}
	
	public boolean canRedo(){
		return redoIndex<historyStack.size();
	}
	

	
}
