package org.ajonx.moves;

import java.util.Objects;

public class Move implements Comparable<Move> {
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
		String start = "" + (char) ('a' + sfile) + (srank + 1);
		String end = "" + (char) ('a' + efile) + (erank + 1);
		return start + "" + end;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Move move = (Move) o;
		return sfile == move.sfile && srank == move.srank && efile == move.efile && erank == move.erank;
	}

	@Override
	public int hashCode() {
		return Objects.hash(sfile, srank, efile, erank);
	}

	@Override
	public int compareTo(Move o) {
		if (sfile != o.sfile) return Integer.compare(sfile, o.sfile);
		if (srank != o.srank) return Integer.compare(srank, o.srank);
		if (efile != o.efile) return Integer.compare(efile, o.efile);
		if (erank != o.erank) return Integer.compare(erank, o.erank);
		return 0;
	}
}
