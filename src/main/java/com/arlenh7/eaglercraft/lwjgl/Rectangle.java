package com.arlenh7.eaglercraft.lwjgl;

public final class Rectangle {

	private int x;
	private int y;
	private int width;
	private int height;

	public Rectangle() {
	}

	public Rectangle(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
	}

	public Rectangle(Point p, int w, int h) {
		this(p.getX(), p.getY(), w, h);
	}

	public Rectangle(Rectangle r) {
		this(r.x, r.y, r.width, r.height);
	}

	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void setLocation(Point p) {
		this.x = p.getX();
		this.y = p.getY();
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setSize(int w, int h) {
		this.width = w;
		this.height = h;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setBounds(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
	}

	public void setBounds(Rectangle r) {
		setBounds(r.x, r.y, r.width, r.height);
	}

	public void translate(int x, int y) {
		this.x += x;
		this.y += y;
	}

	public void translate(Point p) {
		this.x += p.getX();
		this.y += p.getY();
	}

	public void untranslate(Point p) {
		this.x -= p.getX();
		this.y -= p.getY();
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean contains(Point p) {
		return contains(p.getX(), p.getY());
	}

	public boolean contains(int X, int Y) {
		int w = this.width;
		int h = this.height;
		if ((w | h) < 0) {
			return false;
		}
		int x = this.x;
		int y = this.y;
		if (X < x || Y < y) {
			return false;
		}
		w += x;
		h += y;
		return ((w < x || w > X) && (h < y || h > Y));
	}

	public boolean contains(Rectangle r) {
		return contains(r.x, r.y, r.width, r.height);
	}

	public boolean contains(int X, int Y, int W, int H) {
		int w = this.width;
		int h = this.height;
		if ((w | h | W | H) < 0) {
			return false;
		}
		int x = this.x;
		int y = this.y;
		if (X < x || Y < y) {
			return false;
		}
		w += x;
		W += X;
		if (W <= X) {
			if (w >= x || W > w) {
				return false;
			}
		} else {
			if (w >= x && W > w) {
				return false;
			}
		}
		h += y;
		H += Y;
		if (H <= Y) {
			if (h >= y || H > h) {
				return false;
			}
		} else {
			if (h >= y && H > h) {
				return false;
			}
		}
		return true;
	}

	public boolean intersects(Rectangle r) {
		int tw = this.width;
		int th = this.height;
		int rw = r.width;
		int rh = r.height;
		if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
			return false;
		}
		int tx = this.x;
		int ty = this.y;
		int rx = r.x;
		int ry = r.y;
		rw += rx;
		rh += ry;
		tw += tx;
		th += ty;
		return ((rw < rx || rw > tx) && (rh < ry || rh > ty) && (tw < tx || tw > rx) && (th < ty || th > ry));
	}

	public boolean isEmpty() {
		return width <= 0 || height <= 0;
	}

	public int hashCode() {
		int h = x;
		h = 31 * h + y;
		h = 31 * h + width;
		return 31 * h + height;
	}

	public boolean equals(Object obj) {
		if (obj instanceof Rectangle) {
			Rectangle r = (Rectangle) obj;
			return x == r.x && y == r.y && width == r.width && height == r.height;
		}
		return false;
	}

	public String toString() {
		return "Rectangle[" + x + ", " + y + ", " + width + ", " + height + "]";
	}
}
