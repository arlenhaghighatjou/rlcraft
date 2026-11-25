package levelup2.player;

import java.util.Map;
import levelup2.api.IPlayerSkill;
import net.minecraft.nbt.NBTTagCompound;

public interface IPlayerClass {
   NBTTagCompound saveNBTData(NBTTagCompound var1);

   void loadNBTData(NBTTagCompound var1);

   IPlayerSkill getSkillFromName(String var1);

   int getSkillLevel(String var1);

   void setSkillLevel(String var1, int var2);

   void setPlayerData(String[] var1, int[] var2);

   void addToSkill(String var1, int var2);

   boolean hasClass();

   byte getSpecialization();

   void setSpecialization(byte var1);

   Map<String, Integer> getSkills();
}
