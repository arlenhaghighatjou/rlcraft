package mcjty.lostcities.gui;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import mcjty.lostcities.config.LostCityConfiguration;
import mcjty.lostcities.config.LostCityProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;

public class GuiLostCityConfiguration extends GuiScreen {
   private final GuiCreateWorld parent;
   private Map<Integer, Runnable> actionHandler = new HashMap();
   private Map<Integer, String> profileNames = new HashMap();
   private int page = 0;
   private int numpages;
   private GuiMutableLabel pagelabel;

   public GuiLostCityConfiguration(GuiCreateWorld parent) {
      this.parent = parent;
   }

   public void initGui() {
      JsonParser parser = new JsonParser();
      String profileName = LostCityConfiguration.DEFAULT_PROFILE;
      if (this.parent.chunkProviderSettingsJson != null && !this.parent.chunkProviderSettingsJson.trim().isEmpty()) {
         JsonElement parsed = parser.parse(this.parent.chunkProviderSettingsJson);
         if (parsed.getAsJsonObject().has("profile")) {
            profileName = parsed.getAsJsonObject().get("profile").getAsString();
         }
      }

      this.page = 0;
      this.numpages = (this.countPublicProfiles() + 7) / 8;
      this.setupGui(profileName);
   }

   private int countPublicProfiles() {
      int cnt = 0;

      for(Entry<String, LostCityProfile> entry : LostCityConfiguration.profiles.entrySet()) {
         if (((LostCityProfile)entry.getValue()).isPublic()) {
            ++cnt;
         }
      }

      return cnt;
   }

   private void setupGui(String profileName) {
      this.actionHandler.clear();
      this.profileNames.clear();
      this.buttonList.clear();
      this.labelList.clear();
      int id = 301;
      int y = 8;
      int num = -1;
      int cnt = 0;
      List<String> profileKeys = new ArrayList(LostCityConfiguration.profiles.keySet());
      profileKeys.sort(String::compareTo);

      for(String key : profileKeys) {
         LostCityProfile profile = (LostCityProfile)LostCityConfiguration.profiles.get(key);
         if (profile.isPublic()) {
            ++num;
            if (num >= this.page * 8) {
               if (cnt >= 8) {
                  break;
               }

               ++cnt;
               GuiButton button = new GuiButton(id, 10, y, 90, 20, key);
               if (profileName.equals(profile.getName())) {
                  button.packedFGColour = -256;
               }

               this.buttonList.add(button);
               this.actionHandler.put(Integer.valueOf(id), () -> {
                  this.setProfile(profile);
               });
               this.profileNames.put(Integer.valueOf(id), profile.getName());
               ++id;
               GuiLabel label = new GuiLabel(Minecraft.getMinecraft().fontRenderer, id++, 110, y, 230, 20, -1);
               label.addLine(profile.getDescription());
               this.labelList.add(label);
               y += 22;
            }
         }
      }

      y = 200;
      GuiLabel label = new GuiLabel(Minecraft.getMinecraft().fontRenderer, id++, 20, y, 340, 20, -1);
      label.addLine("(note, you can create your own profiles and many more");
      label.addLine("configuration options in 'lostcities.cfg')");
      this.labelList.add(label);
      if (this.numpages > 1) {
         GuiButton prev = new GuiButton(id, 330, y, 20, 19, "<");
         this.buttonList.add(prev);
         this.actionHandler.put(Integer.valueOf(id), () -> {
            this.page = this.page > 0 ? this.page - 1 : this.page;
            this.setupGui(profileName);
         });
         ++id;
         this.pagelabel = new GuiMutableLabel(Minecraft.getMinecraft().fontRenderer, id++, 360, y, 30, 20, -1);
         this.pagelabel.addLine("" + (this.page + 1) + "/" + this.numpages);
         GuiButton next = new GuiButton(id, 390, y, 20, 19, ">");
         this.buttonList.add(next);
         this.actionHandler.put(Integer.valueOf(id), () -> {
            this.page = this.page < this.numpages - 1 ? this.page + 1 : this.page;
            this.setupGui(profileName);
         });
         ++id;
      }

   }

   private void setProfile(LostCityProfile profile) {
      this.parent.chunkProviderSettingsJson = "{ \"profile\": \"" + profile.getName() + "\" }";
      this.mc.displayGuiScreen(this.parent);
   }

   protected void actionPerformed(GuiButton button) throws IOException {
      super.actionPerformed(button);
      if (this.actionHandler.containsKey(Integer.valueOf(button.id))) {
         ((Runnable)this.actionHandler.get(Integer.valueOf(button.id))).run();
      }

   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      super.drawScreen(mouseX, mouseY, partialTicks);
      if (this.numpages > 1) {
         this.pagelabel.clearLines();
         this.pagelabel.addLine("" + (this.page + 1) + "/" + this.numpages);
         this.pagelabel.drawLabel(Minecraft.getMinecraft(), mouseX, mouseY);
      }

      for(GuiButton button : this.buttonList) {
         if (button.isMouseOver()) {
            String name = (String)this.profileNames.get(Integer.valueOf(button.id));
            if (name != null) {
               LostCityProfile profile = (LostCityProfile)LostCityConfiguration.profiles.get(name);
               if (profile != null && profile.getIcon() != null) {
                  int bx = button.x + 95;
                  int by = button.y + 6;
                  this.drawGradientRect(bx - 5, by - 5, bx + 320, by + 85, -1, -1);
                  this.mc.getTextureManager().bindTexture(profile.getIcon());
                  drawScaledCustomSizeModalRect(bx, by, 0.0F, 0.0F, 128, 128, 80, 80, 128.0F, 128.0F);
                  String output = profile.getDescription() + "\n" + profile.getExtraDescription();
                  this.mc.fontRenderer.drawSplitString(output, bx + 90, by, 220, -16777216);
               }
            }
         }
      }

   }
}
