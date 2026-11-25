package com.charles445.rltweaker.client.gui;

import com.charles445.rltweaker.network.MessageReskillableLockSkill;
import com.charles445.rltweaker.network.NetworkHandler;
import com.charles445.rltweaker.network.PacketHandler;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ReflectUtil;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;

public class ReskillableGuiDelegate extends GuiDelegateBase {
   public final Class c_GuiSkillInfo = Class.forName("codersafterdark.reskillable.client.gui.GuiSkillInfo");
   public final Field f_GuiSkillInfo_hoveredUnlockable;
   public final Field f_GuiSkillInfo_skill;
   public final Class c_Unlockable;
   public final Class c_PlayerData;
   public final Method m_PlayerData_getSkillInfo;
   public final Class c_PlayerDataHandler;
   public final Method m_PlayerDataHandler_get;
   public final Class c_PlayerSkillInfo;
   public final Method m_PlayerSkillInfo_isUnlocked;

   public ReskillableGuiDelegate() throws Exception {
      this.f_GuiSkillInfo_hoveredUnlockable = ReflectUtil.findField(this.c_GuiSkillInfo, "hoveredUnlockable");
      this.f_GuiSkillInfo_skill = ReflectUtil.findField(this.c_GuiSkillInfo, "skill");
      this.c_Unlockable = Class.forName("codersafterdark.reskillable.api.unlockable.Unlockable");
      this.c_PlayerData = Class.forName("codersafterdark.reskillable.api.data.PlayerData");
      this.m_PlayerData_getSkillInfo = ReflectUtil.findMethod(this.c_PlayerData, "getSkillInfo");
      this.c_PlayerDataHandler = Class.forName("codersafterdark.reskillable.api.data.PlayerDataHandler");
      this.m_PlayerDataHandler_get = ReflectUtil.findMethod(this.c_PlayerDataHandler, "get", EntityPlayer.class);
      this.c_PlayerSkillInfo = Class.forName("codersafterdark.reskillable.api.data.PlayerSkillInfo");
      this.m_PlayerSkillInfo_isUnlocked = ReflectUtil.findMethod(this.c_PlayerSkillInfo, "isUnlocked");
   }

   public boolean pollMousePre(GuiScreen guiScreen, int width, int height, int x, int y, int button, boolean state) {
      if (state && this.isButtonLeft(button) && NetworkHandler.serverVersion.isSameOrNewerVersion(0, 4, 3)) {
         try {
            Object unlockable = this.f_GuiSkillInfo_hoveredUnlockable.get(guiScreen);
            if (unlockable == null) {
               return false;
            }

            Object data = this.m_PlayerDataHandler_get.invoke((Object)null, this.mc.player);
            Object skill = this.f_GuiSkillInfo_skill.get(guiScreen);
            Object info = this.m_PlayerData_getSkillInfo.invoke(data, skill);
            boolean unlocked = ((Boolean)this.m_PlayerSkillInfo_isUnlocked.invoke(info, unlockable)).booleanValue();
            if (!unlocked) {
               return false;
            }

            this.playClickSound();
            MessageReskillableLockSkill outbound = new MessageReskillableLockSkill(((Impl)skill).getRegistryName(), ((Impl)unlockable).getRegistryName());
            if (NetworkHandler.serverVersion.isSameOrNewerVersion(0, 4, 3)) {
               PacketHandler.instance.sendToServer(outbound);
            }

            return true;
         } catch (Exception var14) {
            ErrorUtil.logSilent("Reskillable Toggleable Traits Gui Delegate Invocation");
            var14.printStackTrace();
         }
      }

      return false;
   }
}
