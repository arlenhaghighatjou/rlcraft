package com.arlenh7.eaglercraft.lwjgl;

public final class Point {

	private int x;
	private int y;

	public Point() {
	}

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Point(Point p) {
		this.x = p.x;
		this.y = p.y;
	}

	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void setLocation(Point p) {
		this.x = p.x;
		this.y = p.y;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void translate(int dx, int dy) {
		this.x += dx;
		this.y += dy;
	}

	public void translate(Point p) {
		this.x += p.x;
		this.y += p.y;
	}

	public void untranslate(Point p) {
		this.x -= p.x;
		this.y -= p.y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void getLocation(Point dest) {
		dest.setLocation(x, y);
	}

	public int hashCode() {
		int sum = x + y;
		return sum * (sum + 1) / 2 + x;
	}

	public boolean equals(Object obj) {
		if (obj instanceof Point) {
			Point pt = (Point) obj;
			return x == pt.x && y == pt.y;
		}
		return false;
	}

	public String toString() {
		return "Point[" + x + ", " + y + "]";
	}
}
