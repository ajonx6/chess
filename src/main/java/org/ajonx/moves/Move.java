package org.ajonx.moves;

public class Move {
	public int sfile, srank;
	public int efile, erank;

	public Move(int sfile, int srank, int efile, int erank) {
		this.sfile = sfile;
		this.srank = srank;
		this.efile = efile;
		this.erank = erank;
	}

	public Move(int sindex, int eindex) {
		this.sfile = sindex % 8;
		this.srank = sindex / 8;
		this.efile = eindex % 8;
		this.erank = eindex / 8;
	}

	public String toString() {
		String start = "" + (char) ('A' + sfile) + (srank + 1);
		String end = "" + (char) ('A' + efile) + (erank + 1);
		return start + "->" + end;
	}
}
