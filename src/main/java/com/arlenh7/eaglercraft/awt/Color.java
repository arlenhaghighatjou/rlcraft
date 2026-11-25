package com.arlenh7.eaglercraft.awt;

public class Color {

	public static final Color white = new Color(255, 255, 255);
	public static final Color WHITE = white;
	public static final Color lightGray = new Color(192, 192, 192);
	public static final Color LIGHT_GRAY = lightGray;
	public static final Color gray = new Color(128, 128, 128);
	public static final Color GRAY = gray;
	public static final Color darkGray = new Color(64, 64, 64);
	public static final Color DARK_GRAY = darkGray;
	public static final Color black = new Color(0, 0, 0);
	public static final Color BLACK = black;
	public static final Color red = new Color(255, 0, 0);
	public static final Color RED = red;
	public static final Color pink = new Color(255, 175, 175);
	public static final Color PINK = pink;
	public static final Color orange = new Color(255, 200, 0);
	public static final Color ORANGE = orange;
	public static final Color yellow = new Color(255, 255, 0);
	public static final Color YELLOW = yellow;
	public static final Color green = new Color(0, 255, 0);
	public static final Color GREEN = green;
	public static final Color magenta = new Color(255, 0, 255);
	public static final Color MAGENTA = magenta;
	public static final Color cyan = new Color(0, 255, 255);
	public static final Color CYAN = cyan;
	public static final Color blue = new Color(0, 0, 255);
	public static final Color BLUE = blue;

	private static final double FACTOR = 0.7;

	private final int value;

	public Color(int r, int g, int b) {
		this(r, g, b, 255);
	}

	public Color(int r, int g, int b, int a) {
		this.value = ((a & 255) << 24) | ((r & 255) << 16) | ((g & 255) << 8) | (b & 255);
	}

	public Color(int rgb) {
		this.value = 0xff000000 | rgb;
	}

	public Color(int rgba, boolean hasalpha) {
		this.value = hasalpha ? rgba : (0xff000000 | rgba);
	}

	public Color(float r, float g, float b) {
		this((int) (r * 255.0F + 0.5F), (int) (g * 255.0F + 0.5F), (int) (b * 255.0F + 0.5F));
	}

	public Color(float r, float g, float b, float a) {
		this((int) (r * 255.0F + 0.5F), (int) (g * 255.0F + 0.5F), (int) (b * 255.0F + 0.5F), (int) (a * 255.0F + 0.5F));
	}

	public int getRed() {
		return (value >> 16) & 255;
	}

	public int getGreen() {
		return (value >> 8) & 255;
	}

	public int getBlue() {
		return value & 255;
	}

	public int getAlpha() {
		return (value >> 24) & 255;
	}

	public int getRGB() {
		return value;
	}

	public Color brighter() {
		int r = getRed();
		int g = getGreen();
		int b = getBlue();
		int a = getAlpha();
		int i = (int) (1.0 / (1.0 - FACTOR));
		if (r == 0 && g == 0 && b == 0) {
			return new Color(i, i, i, a);
		}
		if (r > 0 && r < i) {
			r = i;
		}
		if (g > 0 && g < i) {
			g = i;
		}
		if (b > 0 && b < i) {
			b = i;
		}
		return new Color(Math.min((int) (r / FACTOR), 255), Math.min((int) (g / FACTOR), 255),
				Math.min((int) (b / FACTOR), 255), a);
	}

	public Color darker() {
		return new Color(Math.max((int) (getRed() * FACTOR), 0), Math.max((int) (getGreen() * FACTOR), 0),
				Math.max((int) (getBlue() * FACTOR), 0), getAlpha());
	}

	public float[] getRGBComponents(float[] compArray) {
		float[] f = compArray == null ? new float[4] : compArray;
		f[0] = getRed() * 0.003921569F;
		f[1] = getGreen() * 0.003921569F;
		f[2] = getBlue() * 0.003921569F;
		f[3] = getAlpha() * 0.003921569F;
		return f;
	}

	public float[] getRGBColorComponents(float[] compArray) {
		float[] f = compArray == null ? new float[3] : compArray;
		f[0] = getRed() * 0.003921569F;
		f[1] = getGreen() * 0.003921569F;
		f[2] = getBlue() * 0.003921569F;
		return f;
	}

	public static Color decode(String nm) {
		return new Color(Integer.decode(nm).intValue());
	}

	public static int HSBtoRGB(float hue, float saturation, float brightness) {
		int r = 0;
		int g = 0;
		int b = 0;
		if (saturation == 0.0F) {
			r = g = b = (int) (brightness * 255.0F + 0.5F);
		} else {
			float h = (hue - (float) Math.floor(hue)) * 6.0F;
			float f = h - (float) Math.floor(h);
			float p = brightness * (1.0F - saturation);
			float q = brightness * (1.0F - saturation * f);
			float t = brightness * (1.0F - (saturation * (1.0F - f)));
			switch ((int) h) {
			case 0:
				r = (int) (brightness * 255.0F + 0.5F);
				g = (int) (t * 255.0F + 0.5F);
				b = (int) (p * 255.0F + 0.5F);
				break;
			case 1:
				r = (int) (q * 255.0F + 0.5F);
				g = (int) (brightness * 255.0F + 0.5F);
				b = (int) (p * 255.0F + 0.5F);
				break;
			case 2:
				r = (int) (p * 255.0F + 0.5F);
				g = (int) (brightness * 255.0F + 0.5F);
				b = (int) (t * 255.0F + 0.5F);
				break;
			case 3:
				r = (int) (p * 255.0F + 0.5F);
				g = (int) (q * 255.0F + 0.5F);
				b = (int) (brightness * 255.0F + 0.5F);
				break;
			case 4:
				r = (int) (t * 255.0F + 0.5F);
				g = (int) (p * 255.0F + 0.5F);
				b = (int) (brightness * 255.0F + 0.5F);
				break;
			case 5:
				r = (int) (brightness * 255.0F + 0.5F);
				g = (int) (p * 255.0F + 0.5F);
				b = (int) (q * 255.0F + 0.5F);
				break;
			}
		}
		return 0xff000000 | (r << 16) | (g << 8) | b;
	}

	public static float[] RGBtoHSB(int r, int g, int b, float[] hsbvals) {
		float[] hsb = hsbvals == null ? new float[3] : hsbvals;
		int cmax = r > g ? r : g;
		if (b > cmax) {
			cmax = b;
		}
		int cmin = r < g ? r : g;
		if (b < cmin) {
			cmin = b;
		}
		float brightness = cmax * 0.003921569F;
		float saturation = cmax != 0 ? (float) (cmax - cmin) / (float) cmax : 0.0F;
		float hue;
		if (saturation == 0.0F) {
			hue = 0.0F;
		} else {
			float range = 1.0F / (float) (cmax - cmin);
			float redc = (cmax - r) * range;
			float greenc = (cmax - g) * range;
			float bluec = (cmax - b) * range;
			if (r == cmax) {
				hue = bluec - greenc;
			} else if (g == cmax) {
				hue = 2.0F + redc - bluec;
			} else {
				hue = 4.0F + greenc - redc;
			}
			hue = hue * 0.16666667F;
			if (hue < 0.0F) {
				hue = hue + 1.0F;
			}
		}
		hsb[0] = hue;
		hsb[1] = saturation;
		hsb[2] = brightness;
		return hsb;
	}

	public static Color getHSBColor(float h, float s, float b) {
		return new Color(HSBtoRGB(h, s, b));
	}

	public int hashCode() {
		return value;
	}

	public boolean equals(Object obj) {
		return obj instanceof Color && ((Color) obj).value == this.value;
	}

	public String toString() {
		return "Color[r=" + getRed() + ",g=" + getGreen() + ",b=" + getBlue() + "]";
	}

}
