package de.amazingsax.japanese_sums;

import android.content.Context;
import android.widget.EditText;

/**
 * UI Element des Spielfeldes
 * kindklasse von EditText
 */
public class PlayFieldCell extends EditText {
	
	byte line;
	byte column;
	boolean ignoreTextChange;
	
	
	
	public boolean isIgnoreTextChange() {
		return ignoreTextChange;
	}

	public void setIgnoreTextChange(boolean ignoreTextChange) {
		this.ignoreTextChange = ignoreTextChange;
	}

	public PlayFieldCell(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public byte getLine() {
		return line;
	}

	public void setLine(byte line) {
		this.line = line;
	}

	public byte getColumn() {
		return column;
	}

	public void setColumn(byte column) {
		this.column = column;
	}

	

}
