package com.arlenh7.eaglercraft.awt;

public class Dimension {

	public int width;
	public int height;

	public Dimension() {
		this(0, 0);
	}

	public Dimension(Dimension d) {
		this(d.width, d.height);
	}

	public Dimension(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public Dimension getSize() {
		return new Dimension(width, height);
	}

	public boolean equals(Object obj) {
		if (obj instanceof Dimension) {
			Dimension d = (Dimension) obj;
			return d.width == width && d.height == height;
		}
		return false;
	}

	public int hashCode() {
		int sum = width + height;
		return sum * (sum + 1) / 2 + width;
	}

	public String toString() {
		return getClass().getName() + "[width=" + width + ",height=" + height + "]";
	}
}
