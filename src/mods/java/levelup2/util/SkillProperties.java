package levelup2.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import levelup2.api.IPlayerSkill;
import levelup2.skills.SkillRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class SkillProperties {
   private String skillName;
   private int[] levels;
   private String[] prerequisites;
   private int column;
   private int row;
   private boolean enabled;
   private boolean active;

   public static SkillProperties fromJson(String skillName, JsonObject obj) {
      int[] levels = getLevels(obj);
      String[] prerequisites = getPrerequisites(obj);
      int column = JsonUtils.getInt(obj, "column", 0);
      int row = JsonUtils.getInt(obj, "row", 0);
      boolean enabled = JsonUtils.getBoolean(obj, "enabled", true);
      boolean active = JsonUtils.getBoolean(obj, "active", true);
      return new SkillProperties(skillName, levels, prerequisites, column, row, enabled, active);
   }

   public SkillProperties(String skillName, int[] levelCosts, String[] prerequisites, int column, int row, boolean enabled, boolean active) {
      this.skillName = skillName;
      this.levels = levelCosts;
      this.prerequisites = prerequisites;
      this.column = column;
      this.row = row;
      this.enabled = enabled;
      this.active = active;
   }

   public int[] getLevels() {
      return this.levels;
   }

   public String[] getPrerequisites() {
      return this.prerequisites;
   }

   public int getColumn() {
      return this.column;
   }

   public int getRow() {
      return this.row;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public boolean isActive() {
      return this.active;
   }

   public void writeToBytes(ByteBuf buf) {
      NBTTagCompound tag = new NBTTagCompound();
      tag.setString("name", this.skillName);
      if (this.getLevels() != null && this.getLevels().length > 0) {
         tag.setIntArray("levels", this.getLevels());
      }

      NBTTagCompound prereqs = new NBTTagCompound();
      if (this.getPrerequisites() != null && this.getPrerequisites().length > 0) {
         for(int i = 0; i < this.getPrerequisites().length; ++i) {
            String prereq = this.getPrerequisites()[i];
            prereqs.setString("prereq_" + i, prereq);
         }

         tag.setTag("prereqs", prereqs);
      }

      tag.setInteger("column", this.getColumn());
      tag.setInteger("row", this.getRow());
      tag.setBoolean("enabled", this.isEnabled());
      tag.setBoolean("active", this.isActive());
      ByteBufUtils.writeTag(buf, tag);
   }

   public static void fromNBT(NBTTagCompound tag) {
      IPlayerSkill skill = SkillRegistry.getSkillFromName(tag.getString("name"));
      if (skill != null && skill.hasExternalJson()) {
         if (tag.hasKey("levels")) {
            skill.setLevelCosts(tag.getIntArray("levels"));
         } else {
            skill.setLevelCosts(new int[0]);
         }

         if (!tag.hasKey("prereqs")) {
            skill.setPrerequisites(new String[0]);
         } else {
            NBTTagCompound t = tag.getCompoundTag("prereqs");
            int i = 0;

            List<String> prereqs;
            for(prereqs = new ArrayList(); t.hasKey("prereq_" + i); ++i) {
               prereqs.add(t.getString("prereq_" + i));
            }

            String[] str = new String[prereqs.size()];

            for(int j = 0; j < str.length; ++j) {
               str[j] = (String)prereqs.get(j);
            }

            skill.setPrerequisites(str);
         }

         skill.setSkillColumn(tag.getInteger("column"));
         skill.setSkillRow(tag.getInteger("row"));
         skill.setEnabled(tag.getBoolean("enabled"));
         skill.setActive(tag.getBoolean("active"));
      }

   }

   private static int[] getLevels(JsonObject obj) {
      JsonArray levels = JsonUtils.getJsonArray(obj, "levels", (JsonArray)null);
      if (levels == null) {
         return new int[0];
      } else {
         List<Integer> lvl = new ArrayList();

         for(JsonElement element : levels) {
            lvl.add(Integer.valueOf(element.getAsInt()));
         }

         int[] lvls = new int[lvl.size()];

         for(int i = 0; i < lvls.length; ++i) {
            lvls[i] = ((Integer)lvl.get(i)).intValue();
         }

         return lvls;
      }
   }

   private static String[] getPrerequisites(JsonObject obj) {
      JsonArray prereqs = JsonUtils.getJsonArray(obj, "prerequisites", (JsonArray)null);
      if (prereqs == null) {
         return new String[0];
      } else {
         List<String> prereq = new ArrayList();

         for(JsonElement element : prereqs) {
            prereq.add(element.getAsString());
         }

         String[] str = new String[prereq.size()];

         for(int i = 0; i < str.length; ++i) {
            str[i] = (String)prereq.get(i);
         }

         return str;
      }
   }
}
