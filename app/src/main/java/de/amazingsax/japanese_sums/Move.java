package de.amazingsax.japanese_sums;

public class Move {
	byte line;
	byte column;
	String oldvalue;
	String newvalue;

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

	public String getOldvalue() {
		return new String(oldvalue);
	}

	public void setOldvalue(String oldvalue) {
		this.oldvalue = new String(oldvalue);
	}

	public String getNewvalue() {
		return new String(newvalue);
	}

	public void setNewvalue(String newvalue) {
		this.newvalue = new String(newvalue);
	}


}
