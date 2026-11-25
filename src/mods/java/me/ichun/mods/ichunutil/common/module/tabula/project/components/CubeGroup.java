package me.ichun.mods.ichunutil.common.module.tabula.project.components;

import java.util.ArrayList;
import org.apache.commons.lang3.RandomStringUtils;

public class CubeGroup {
   public ArrayList<CubeInfo> cubes = new ArrayList();
   public ArrayList<CubeGroup> cubeGroups = new ArrayList();
   public String name;
   public boolean txMirror = false;
   public boolean hidden = false;
   public ArrayList<String> metadata = new ArrayList();
   public String identifier;

   public CubeGroup(String name) {
      this.name = name;
      this.identifier = RandomStringUtils.randomAscii(20);
   }
}
