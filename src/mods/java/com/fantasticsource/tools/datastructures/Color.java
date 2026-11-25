package com.fantasticsource.tools.datastructures;

import com.fantasticsource.tools.Tools;

public class Color {
   public static ColorImmutable BLANK;
   public static ColorImmutable BLACK;
   public static ColorImmutable WHITE;
   public static ColorImmutable RED;
   public static ColorImmutable GREEN;
   public static ColorImmutable BLUE;
   public static ColorImmutable YELLOW;
   public static ColorImmutable AQUA;
   public static ColorImmutable PURPLE;
   public static ColorImmutable GRAY;
   public static ColorImmutable ORANGE;
   protected int intValue;
   protected int r;
   protected int g;
   protected int b;
   protected int a;
   protected float rf;
   protected float gf;
   protected float bf;
   protected float af;
   protected String hex;

   public Color(int color) {
      this(color, false);
   }

   public Color(int color, boolean noAlpha) {
      if (noAlpha) {
         color = color << 8 | 255;
      }

      this.intValue = color;
      this.r = this.intValue >>> 24 & 255;
      this.g = this.intValue >>> 16 & 255;
      this.b = this.intValue >>> 8 & 255;
      this.a = this.intValue & 255;
      this.rf = Tools.min(Tools.max((float)this.r / 255.0F, 0.0F), 1.0F);
      this.gf = Tools.min(Tools.max((float)this.g / 255.0F, 0.0F), 1.0F);
      this.bf = Tools.min(Tools.max((float)this.b / 255.0F, 0.0F), 1.0F);
      this.af = Tools.min(Tools.max((float)this.a / 255.0F, 0.0F), 1.0F);
      this.hex = Integer.toHexString(this.intValue);
   }

   public Color(int r, int g, int b) {
      this(r, g, b, 255);
   }

   public Color(int r, int g, int b, int a) {
      this.r = Tools.min(Tools.max(r, 0), 255);
      this.g = Tools.min(Tools.max(g, 0), 255);
      this.b = Tools.min(Tools.max(b, 0), 255);
      this.a = Tools.min(Tools.max(a, 0), 255);
      this.rf = Tools.min(Tools.max((float)r / 255.0F, 0.0F), 1.0F);
      this.gf = Tools.min(Tools.max((float)g / 255.0F, 0.0F), 1.0F);
      this.bf = Tools.min(Tools.max((float)b / 255.0F, 0.0F), 1.0F);
      this.af = Tools.min(Tools.max((float)a / 255.0F, 0.0F), 1.0F);
      this.intValue = r << 24 | g << 16 | b << 8 | a;
      this.hex = Integer.toHexString(this.intValue);
   }

   public Color(float r, float g, float b) {
      this(r, g, b, 1.0F);
   }

   public Color(float r, float g, float b, float a) {
      this.rf = Tools.min(Tools.max(r, 0.0F), 1.0F);
      this.gf = Tools.min(Tools.max(g, 0.0F), 1.0F);
      this.bf = Tools.min(Tools.max(b, 0.0F), 1.0F);
      this.af = Tools.min(Tools.max(a, 0.0F), 1.0F);
      this.r = Tools.min(Tools.max((int)(this.rf * 255.0F), 0), 255);
      this.g = Tools.min(Tools.max((int)(this.gf * 255.0F), 0), 255);
      this.b = Tools.min(Tools.max((int)(this.bf * 255.0F), 0), 255);
      this.a = Tools.min(Tools.max((int)(this.af * 255.0F), 0), 255);
      this.intValue = this.r << 24 | this.g << 16 | this.b << 8 | this.a;
      this.hex = Integer.toHexString(this.intValue);
   }

   public Color(String hex) {
      this(hex, false);
   }

   public Color(String hex, boolean noAlpha) {
      if (noAlpha) {
         hex = hex + "ff";
      }

      this.hex = hex;
      this.intValue = Tools.parseHexInt(hex);
      this.r = this.intValue >>> 24 & 255;
      this.g = this.intValue >>> 16 & 255;
      this.b = this.intValue >>> 8 & 255;
      this.a = this.intValue & 255;
      this.rf = Tools.min(Tools.max((float)this.r / 255.0F, 0.0F), 1.0F);
      this.gf = Tools.min(Tools.max((float)this.g / 255.0F, 0.0F), 1.0F);
      this.bf = Tools.min(Tools.max((float)this.b / 255.0F, 0.0F), 1.0F);
      this.af = Tools.min(Tools.max((float)this.a / 255.0F, 0.0F), 1.0F);
   }

   public Color copy() {
      return new Color(this.intValue);
   }

   public Color setR(int r) {
      this.r = Tools.min(Tools.max(r, 0), 255);
      this.rf = Tools.min(Tools.max((float)r / 255.0F, 0.0F), 1.0F);
      this.intValue = r << 24 | this.g << 16 | this.b << 8 | this.a;
      this.hex = Integer.toHexString(this.intValue);
      return this;
   }

   public Color setRF(float rf) {
      this.rf = Tools.min(Tools.max(rf, 0.0F), 1.0F);
      this.r = Tools.min(Tools.max((int)(rf * 255.0F), 0), 255);
      this.intValue = this.r << 24 | this.g << 16 | this.b << 8 | this.a;
      this.hex = Integer.toHexString(this.intValue);
      return this;
   }

   public Color setG(int g) {
      this.g = Tools.min(Tools.max(g, 0), 255);
      this.gf = Tools.min(Tools.max((float)g / 255.0F, 0.0F), 1.0F);
      this.intValue = this.r << 24 | g << 16 | this.b << 8 | this.a;
      this.hex = Integer.toHexString(this.intValue);
      return this;
   }

   public Color setGF(float gf) {
      this.gf = Tools.min(Tools.max(gf, 0.0F), 1.0F);
      this.g = Tools.min(Tools.max((int)(gf * 255.0F), 0), 255);
      this.intValue = this.r << 24 | this.g << 16 | this.b << 8 | this.a;
      this.hex = Integer.toHexString(this.intValue);
      return this;
   }

   public Color setB(int b) {
      this.b = Tools.min(Tools.max(b, 0), 255);
      this.bf = Tools.min(Tools.max((float)b / 255.0F, 0.0F), 1.0F);
      this.intValue = this.r << 24 | this.g << 16 | b << 8 | this.a;
      this.hex = Integer.toHexString(this.intValue);
      return this;
   }

   public Color setBF(float bf) {
      this.bf = Tools.min(Tools.max(bf, 0.0F), 1.0F);
      this.b = Tools.min(Tools.max((int)(bf * 255.0F), 0), 255);
      this.intValue = this.r << 24 | this.g << 16 | this.b << 8 | this.a;
      this.hex = Integer.toHexString(this.intValue);
      return this;
   }

   public Color setA(int a) {
      this.a = Tools.min(Tools.max(a, 0), 255);
      this.af = Tools.min(Tools.max((float)a / 255.0F, 0.0F), 1.0F);
      this.intValue = this.r << 24 | this.g << 16 | this.b << 8 | a;
      this.hex = Integer.toHexString(this.intValue);
      return this;
   }

   public Color setAF(float af) {
      this.af = Tools.min(Tools.max(af, 0.0F), 1.0F);
      this.a = Tools.min(Tools.max((int)(af * 255.0F), 0), 255);
      this.intValue = this.r << 24 | this.g << 16 | this.b << 8 | this.a;
      this.hex = Integer.toHexString(this.intValue);
      return this;
   }

   public Color setColorNoAlpha(int color) {
      this.r = color >>> 16 & 255;
      this.g = color >>> 8 & 255;
      this.b = color & 255;
      this.a = 255;
      this.rf = Tools.min(Tools.max((float)this.r / 255.0F, 0.0F), 1.0F);
      this.gf = Tools.min(Tools.max((float)this.g / 255.0F, 0.0F), 1.0F);
      this.bf = Tools.min(Tools.max((float)this.b / 255.0F, 0.0F), 1.0F);
      this.af = 1.0F;
      this.intValue = this.r << 24 | this.g << 16 | this.b << 8 | this.a;
      this.hex = Integer.toHexString(this.intValue);
      return this;
   }

   public Color setColor(int color) {
      this.intValue = color;
      this.r = this.intValue >>> 24 & 255;
      this.g = this.intValue >>> 16 & 255;
      this.b = this.intValue >>> 8 & 255;
      this.a = this.intValue & 255;
      this.rf = Tools.min(Tools.max((float)this.r / 255.0F, 0.0F), 1.0F);
      this.gf = Tools.min(Tools.max((float)this.g / 255.0F, 0.0F), 1.0F);
      this.bf = Tools.min(Tools.max((float)this.b / 255.0F, 0.0F), 1.0F);
      this.af = Tools.min(Tools.max((float)this.a / 255.0F, 0.0F), 1.0F);
      this.hex = Integer.toHexString(this.intValue);
      return this;
   }

   public Color setColor(int r, int g, int b) {
      return this.setColor(r, g, b, 255);
   }

   public Color setColor(int r, int g, int b, int a) {
      this.r = Tools.min(Tools.max(r, 0), 255);
      this.g = Tools.min(Tools.max(g, 0), 255);
      this.b = Tools.min(Tools.max(b, 0), 255);
      this.a = Tools.min(Tools.max(a, 0), 255);
      this.rf = Tools.min(Tools.max((float)r / 255.0F, 0.0F), 1.0F);
      this.gf = Tools.min(Tools.max((float)g / 255.0F, 0.0F), 1.0F);
      this.bf = Tools.min(Tools.max((float)b / 255.0F, 0.0F), 1.0F);
      this.af = Tools.min(Tools.max((float)a / 255.0F, 0.0F), 1.0F);
      this.intValue = r << 24 | g << 16 | b << 8 | a;
      this.hex = Integer.toHexString(this.intValue);
      return this;
   }

   public Color setColor(float r, float g, float b) {
      return this.setColor(r, g, b, 1.0F);
   }

   public Color setColor(float r, float g, float b, float a) {
      this.rf = Tools.min(Tools.max(r, 0.0F), 1.0F);
      this.gf = Tools.min(Tools.max(g, 0.0F), 1.0F);
      this.bf = Tools.min(Tools.max(b, 0.0F), 1.0F);
      this.af = Tools.min(Tools.max(a, 0.0F), 1.0F);
      this.r = Tools.min(Tools.max((int)(this.rf * 255.0F), 0), 255);
      this.g = Tools.min(Tools.max((int)(this.gf * 255.0F), 0), 255);
      this.b = Tools.min(Tools.max((int)(this.bf * 255.0F), 0), 255);
      this.a = Tools.min(Tools.max((int)(this.af * 255.0F), 0), 255);
      this.intValue = this.r << 24 | this.g << 16 | this.b << 8 | this.a;
      this.hex = Integer.toHexString(this.intValue);
      return this;
   }

   public Color setColor(String hex) {
      this.hex = hex;
      this.intValue = Tools.parseHexInt(hex);
      this.r = this.intValue >>> 24 & 255;
      this.g = this.intValue >>> 16 & 255;
      this.b = this.intValue >>> 8 & 255;
      this.a = this.intValue & 255;
      this.rf = Tools.min(Tools.max((float)this.r / 255.0F, 0.0F), 1.0F);
      this.gf = Tools.min(Tools.max((float)this.g / 255.0F, 0.0F), 1.0F);
      this.bf = Tools.min(Tools.max((float)this.b / 255.0F, 0.0F), 1.0F);
      this.af = Tools.min(Tools.max((float)this.a / 255.0F, 0.0F), 1.0F);
      return this;
   }

   public Color setColorNoAlpha(String hex) {
      hex = hex + "ff";
      this.hex = hex;
      this.intValue = Tools.parseHexInt(hex);
      this.r = this.intValue >>> 24 & 255;
      this.g = this.intValue >>> 16 & 255;
      this.b = this.intValue >>> 8 & 255;
      this.a = 255;
      this.rf = Tools.min(Tools.max((float)this.r / 255.0F, 0.0F), 1.0F);
      this.gf = Tools.min(Tools.max((float)this.g / 255.0F, 0.0F), 1.0F);
      this.bf = Tools.min(Tools.max((float)this.b / 255.0F, 0.0F), 1.0F);
      this.af = 1.0F;
      return this;
   }

   public int color() {
      return this.intValue;
   }

   public int toARGB() {
      return this.intValue >>> 8 | this.a << 24;
   }

   public int r() {
      return this.r;
   }

   public int g() {
      return this.g;
   }

   public int b() {
      return this.b;
   }

   public int a() {
      return this.a;
   }

   public float rf() {
      return this.rf;
   }

   public float gf() {
      return this.gf;
   }

   public float bf() {
      return this.bf;
   }

   public float af() {
      return this.af;
   }

   public String hex() {
      return this.hex;
   }

   public String hex8() {
      StringBuilder result = new StringBuilder();

      for(int i = this.hex.length(); i < 8; ++i) {
         result.append("0");
      }

      return result + this.hex;
   }

   public String toString() {
      return this.hex();
   }

   public int h() {
      return Tools.min(Tools.max((int)(255.0F * this.hf()), 0), 255);
   }

   public float hf() {
      if (this.rf == this.gf && this.gf == this.bf) {
         return 0.0F;
      } else {
         float minF = Tools.min(this.rf, this.gf, this.bf);
         float maxF = this.vf();
         float result;
         if (maxF == this.rf) {
            result = Tools.posMod((this.gf - this.bf) / (maxF - minF) / 6.0F, 1.0F);
         } else if (maxF == this.gf) {
            result = Tools.posMod((this.bf - this.rf) / (maxF - minF) / 6.0F + 0.33333334F, 1.0F);
         } else {
            result = Tools.posMod((this.rf - this.gf) / (maxF - minF) / 6.0F + 0.6666667F, 1.0F);
         }

         return Tools.min(Tools.max(result, 0.0F), 1.0F);
      }
   }

   public int s() {
      return Tools.min(Tools.max((int)(this.sf() * 255.0F), 0), 255);
   }

   public float sf() {
      float result = this.vf();
      if (result <= 0.0F) {
         return 0.0F;
      } else {
         if (result >= 1.0F) {
            result = 1.0F - Tools.min(this.rf, this.gf, this.bf);
         } else {
            result = (result - Tools.min(this.rf, this.gf, this.bf)) / result;
         }

         return Tools.min(Tools.max(result, 0.0F), 1.0F);
      }
   }

   public int v() {
      return Tools.max(this.r, this.g, this.b);
   }

   public float vf() {
      return Tools.max(this.rf, this.gf, this.bf);
   }

   public Color setH(int h) {
      return this.setHF((float)h / 255.0F);
   }

   public Color setHF(float hf) {
      return this.setColorHSV(Tools.min(Tools.max(hf, 0.0F), 1.0F), this.sf(), this.vf(), this.af);
   }

   public Color setS(int s) {
      return this.setSF((float)s / 255.0F);
   }

   public Color setSF(float sf) {
      return this.setColorHSV(this.hf(), Tools.min(Tools.max(sf, 0.0F), 1.0F), this.vf(), this.af);
   }

   public Color setV(int v) {
      return this.setVF((float)v / 255.0F);
   }

   public Color setVF(float vf) {
      return this.setColorHSV(this.hf(), this.sf(), Tools.min(Tools.max(vf, 0.0F), 1.0F), this.af);
   }

   public Color setColorHSV(int h, int s, int v) {
      return this.setColorHSV(h, s, v, 255);
   }

   public Color setColorHSV(int h, int s, int v, int a) {
      return this.setColorHSV((float)h / 255.0F, (float)s / 255.0F, (float)v / 255.0F, (float)a / 255.0F);
   }

   public Color setColorHSV(float hf, float sf, float vf) {
      return this.setColorHSV(hf, sf, vf, 1.0F);
   }

   public Color setColorHSV(float hf, float sf, float vf, float af) {
      hf = Tools.min(Tools.max(hf, 0.0F), 1.0F);
      sf = Tools.min(Tools.max(sf, 0.0F), 1.0F);
      vf = Tools.min(Tools.max(vf, 0.0F), 1.0F);
      af = Tools.min(Tools.max(af, 0.0F), 1.0F);
      if (vf == 0.0F) {
         return this.setColor(0.0F, 0.0F, 0.0F, af);
      } else if (sf == 0.0F) {
         return this.setColor(vf, vf, vf, af);
      } else {
         hf = Tools.posMod(hf * 6.0F, 6.0F);
         int hSixth = (int)hf;
         float hFrac = hf - (float)hSixth;
         float p = vf * (1.0F - sf);
         float q = vf * (1.0F - sf * hFrac);
         float t = vf * (1.0F - sf * (1.0F - hFrac));
         switch(hSixth) {
         case 0:
            return this.setColor(vf, t, p, af);
         case 1:
            return this.setColor(q, vf, p, af);
         case 2:
            return this.setColor(p, vf, t, af);
         case 3:
            return this.setColor(p, q, vf, af);
         case 4:
            return this.setColor(t, p, vf, af);
         case 5:
            return this.setColor(vf, p, q, af);
         default:
            throw new IllegalStateException("This should never happen");
         }
      }
   }

   public boolean equals(Object obj) {
      return obj instanceof Color && ((Color)obj).intValue == this.intValue;
   }

   public int hashCode() {
      return this.intValue;
   }
}
