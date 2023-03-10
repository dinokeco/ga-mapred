package edu.ibu.ga.mapreduce.domain;

import java.io.Serializable;
import java.util.BitSet;

public class Chromosome implements Serializable {

	private static final long serialVersionUID = 1092603564372024005L;

	private BitSet bits;

	private int length;

	private double fintess;

	public Chromosome(int length) {
		bits = new BitSet(length);
		this.length = length;
	}
	
	public Chromosome(int length, String values){
		bits = new BitSet(length);
		this.length = length;
		
		String[] activeBits = values.split(",");
		for (String activeBit : activeBits) {
			int parsedActiveBit = Integer.valueOf(activeBit.trim());
			this.bits.set(parsedActiveBit);
		}
	}

	public BitSet getBits() {
		return bits;
	}

	public void setBits(BitSet bits) {
		this.bits = bits;
	}

	public double getFintess() {
		return fintess;
	}

	public void setFintess(double fintess) {
		this.fintess = fintess;
	}

	public Chromosome deepCopy() {
		Chromosome c = new Chromosome(length);
		for (int i = 0; i < this.length; i++) {
			c.getBits().set(i, this.getBits().get(i));
		}
		c.setFintess(this.getFintess());
		return c;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (bits.get(i)) {
				builder.append("1");
			} else {
				builder.append("0");
			}
		}
		builder.append("=").append(fintess);
		return builder.toString();
	}

	public void fromString(String value) {
		String[] records = value.split("=");
		this.setFintess(Double.valueOf(records[1]));
		this.length = records[0].length();
		for (int i = 0; i < records[0].length(); i++) {
			char c = records[0].charAt(i);
			if (c == '1') {
				this.getBits().set(i);
			}
		}
	}

	public int getLength() {
		return length;
	}
	
	public int countOnes() {
		int counter = 0;
		for (int i = 0; i < this.getLength(); i++) {
			if (this.getBits().get(i)) {
				counter++;
			}
		}
		return counter;
	}
	
	public int[] getOneIndices(){
		int totalOnes = this.countOnes();
		int[] indices = new int[totalOnes+1];
		int counter = 1;
		indices[0] = 0;
		for (int i = 0; i < this.getLength(); i++) {
			if (this.getBits().get(i)) {
				indices[counter] = i+1;
				counter++;
			}
		}
		return indices;
	}

}
