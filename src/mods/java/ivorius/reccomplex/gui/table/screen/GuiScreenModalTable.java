package ivorius.reccomplex.gui.table.screen;

import com.google.common.collect.Lists;
import ivorius.reccomplex.gui.table.GuiTable;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import java.io.IOException;
import java.util.List;
import java.util.Stack;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Post;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class GuiScreenModalTable extends GuiScreen implements TableDelegate, TableNavigator {
   private Stack<GuiTable> tableStack = new Stack();

   public GuiScreenModalTable() {
   }

   public void initGui() {
      super.initGui();
      Keyboard.enableRepeatEvents(true);
      this.buttonList.clear();
      if (this.tableStack.size() > 0) {
         ((GuiTable)this.tableStack.peek()).initGui();
      }

   }

   protected void mouseClicked(int x, int y, int button) throws IOException {
      if (button == 0) {
         List<GuiButton> buttonListCopy = Lists.newArrayList(this.buttonList);

         for(int l = 0; l < buttonListCopy.size(); ++l) {
            GuiButton guibutton = (GuiButton)buttonListCopy.get(l);
            if (guibutton.mousePressed(this.mc, x, y)) {
               Pre event = new Pre(this, guibutton, buttonListCopy);
               if (MinecraftForge.EVENT_BUS.post(event)) {
                  break;
               }

               ReflectionHelper.setPrivateValue(GuiScreen.class, this, event.getButton(), "selectedButton", "selectedButton");
               event.getButton().playPressSound(this.mc.getSoundHandler());
               this.actionPerformed(event.getButton());
               if (this.equals(this.mc.currentScreen)) {
                  MinecraftForge.EVENT_BUS.post(new Post(this, event.getButton(), buttonListCopy));
               }
            }
         }
      }

      if (this.tableStack.size() > 0) {
         ((GuiTable)this.tableStack.peek()).mouseClicked(x, y, button);
      }

   }

   protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
      super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
      if (this.tableStack.size() > 0) {
         ((GuiTable)this.tableStack.peek()).mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
      }

   }

   protected void mouseReleased(int mouseX, int mouseY, int state) {
      super.mouseReleased(mouseX, mouseY, state);
      if (this.tableStack.size() > 0) {
         ((GuiTable)this.tableStack.peek()).mouseReleased(mouseX, mouseY, state);
      }

   }

   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      this.currentTable().handleMouseInput();
   }

   protected void keyTyped(char keyChar, int keyCode) throws IOException {
      super.keyTyped(keyChar, keyCode);
      boolean used = false;
      if (this.tableStack.size() > 0) {
         used = ((GuiTable)this.tableStack.peek()).keyTyped(keyChar, keyCode);
      }

      if (!used) {
         if (keyCode == 200) {
            this.currentTable().tryScrollUp();
         } else if (keyCode == 208) {
            this.currentTable().tryScrollDown();
         }
      }

   }

   protected void actionPerformed(GuiButton button) throws IOException {
      super.actionPerformed(button);
      if (this.tableStack.size() > 0) {
         ((GuiTable)this.tableStack.peek()).actionPerformed(button);
      }

   }

   public void updateScreen() {
      super.updateScreen();
      if (this.tableStack.size() > 0) {
         ((GuiTable)this.tableStack.peek()).updateScreen();
      }

   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      super.drawScreen(mouseX, mouseY, partialTicks);
      if (this.tableStack.size() > 0) {
         ((GuiTable)this.tableStack.peek()).drawScreen(this, mouseX, mouseY, partialTicks);
      }

   }

   public void onGuiClosed() {
      super.onGuiClosed();
      Keyboard.enableRepeatEvents(false);
   }

   public Stack<GuiTable> tableStack() {
      return (Stack)this.tableStack.clone();
   }

   public void pushTable(GuiTable table) {
      this.tableStack.push(table);
      if (this.mc != null) {
         this.reloadData();
      }

   }

   public GuiTable currentTable() {
      return (GuiTable)this.tableStack.peek();
   }

   public GuiTable popTable() {
      GuiTable table = (GuiTable)this.tableStack.pop();
      if (this.mc != null) {
         this.reloadData();
      }

      return table;
   }

   public void setTable(GuiTable table) {
      this.tableStack.clear();
      if (table != null) {
         this.pushTable(table);
      }

   }

   @Nonnull
   @ParametersAreNonnullByDefault
   public <T extends GuiButton> T addButtonToTable(T button) {
      return (T)this.addButton(button);
   }

   public void redrawTable() {
      this.initGui();
   }

   public void reloadData() {
      if (this.tableStack.size() > 0) {
         ((GuiTable)this.tableStack.peek()).clearCellCache();
      }

      this.redrawTable();
   }

   public void setLocked(String cell, boolean lock) {
      ((GuiTable)this.tableStack.peek()).setLocked(cell, lock);
   }
}
