package com.arlenh7.eaglercraft.awt;

public class Point {

	public int x;
	public int y;

	public Point() {
		this(0, 0);
	}

	public Point(Point p) {
		this(p.x, p.y);
	}

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public Point getLocation() {
		return new Point(x, y);
	}

	public void setLocation(Point p) {
		setLocation(p.x, p.y);
	}

	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void setLocation(double x, double y) {
		this.x = (int) Math.floor(x + 0.5D);
		this.y = (int) Math.floor(y + 0.5D);
	}

	public void move(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void translate(int dx, int dy) {
		this.x += dx;
		this.y += dy;
	}

	public double distance(double px, double py) {
		px -= x;
		py -= y;
		return Math.sqrt(px * px + py * py);
	}

	public double distance(Point pt) {
		return distance(pt.x, pt.y);
	}

	public double distanceSq(double px, double py) {
		px -= x;
		py -= y;
		return px * px + py * py;
	}

	public int hashCode() {
		long bits = Double.doubleToLongBits(getX());
		bits ^= Double.doubleToLongBits(getY()) * 31;
		return (((int) bits) ^ ((int) (bits >> 32)));
	}

	public boolean equals(Object obj) {
		if (obj instanceof Point) {
			Point pt = (Point) obj;
			return (x == pt.x) && (y == pt.y);
		}
		return false;
	}

	public String toString() {
		return "java.awt.Point[x=" + x + ",y=" + y + "]";
	}
}
