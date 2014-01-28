

public class History<T extends Drawable> {
	
	private T item;
	
	public History (T item){
		this.item=item;
	}

	
	public void setItem(T item) {
		this.item = item;
	}

	public Object getItem() {
		return item;
	}

	
}
