package vazkii.aurelienribon.tweenengine;

import vazkii.aurelienribon.tweenengine.paths.CatmullRom;
import vazkii.aurelienribon.tweenengine.paths.Linear;

public interface TweenPaths {
   Linear linear = new Linear();
   CatmullRom catmullRom = new CatmullRom();
}
