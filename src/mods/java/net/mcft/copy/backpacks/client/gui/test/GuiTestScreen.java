package net.mcft.copy.backpacks.client.gui.test;

import net.mcft.copy.backpacks.client.GuiTextureResource;
import net.mcft.copy.backpacks.client.gui.Alignment;
import net.mcft.copy.backpacks.client.gui.Direction;
import net.mcft.copy.backpacks.client.gui.GuiContainer;
import net.mcft.copy.backpacks.client.gui.GuiContainerScreen;
import net.mcft.copy.backpacks.client.gui.GuiElementBase;
import net.mcft.copy.backpacks.client.gui.GuiLabel;
import net.mcft.copy.backpacks.client.gui.GuiLayout;
import net.mcft.copy.backpacks.client.gui.GuiScrollable;
import net.mcft.copy.backpacks.client.gui.control.GuiButton;
import net.mcft.copy.backpacks.client.gui.control.GuiButtonIcon;
import net.mcft.copy.backpacks.client.gui.control.GuiField;
import net.mcft.copy.backpacks.client.gui.control.GuiSlider;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTestScreen extends GuiContainerScreen {
   public GuiTestScreen(final GuiScreen parentScreen) {
      this.container.add(new GuiLayout(Direction.VERTICAL) {
         {
            this.setCenteredHorizontal();
            this.setFillVertical(8);
            this.setSpacing(new int[]{4});
            this.addFixed(new GuiButton(200, "Test Alignment") {
               {
                  this.setAction(() -> {
                     display(GuiTestScreen.this.new AlignmentScreen());
                  });
               }
            });
            this.addFixed(new GuiLayout(Direction.HORIZONTAL) {
               {
                  this.setCenteredHorizontal(200);
                  this.addFixed(new GuiLabel(" Layout: ") {
                     {
                        this.setCenteredVertical();
                     }
                  });
                  this.addWeighted(new GuiButton("Test 1") {
                     {
                        this.setAction(() -> {
                           display(GuiTestScreen.this.new LayoutScreen1());
                        });
                     }
                  });
                  this.addWeighted(new GuiButton("Test 2") {
                     {
                        this.setAction(() -> {
                           display(GuiTestScreen.this.new LayoutScreen2());
                        });
                     }
                  });
               }
            });
            this.addFixed(new GuiButton(200, "Test Visibility / Enabled") {
               {
                  this.setAction(() -> {
                     display(GuiTestScreen.this.new VisibilityEnabledScreen());
                  });
               }
            });
            this.addFixed(new GuiButton(200, "Test Controls") {
               {
                  this.setAction(() -> {
                     display(GuiTestScreen.this.new ControlsScreen());
                  });
               }
            });
            this.addWeighted(new GuiContainer());
            this.addFixed(new GuiButton(200, "Close") {
               {
                  this.setAction(() -> {
                     display(parentScreen);
                  });
               }
            });
         }
      });
   }

   public class AlignmentScreen extends GuiContainerScreen {
      public AlignmentScreen() {
         for(final int i = 0; i < 3; ++i) {
            for(final int j = 0; j < 3; ++j) {
               if (i != 1 || j != 1) {
                  this.container.add(new GuiButton(12, 12) {
                     {
                        switch(i) {
                        case 0:
                           this.setLeft(4);
                           break;
                        case 1:
                           this.setLeftRight(20);
                           break;
                        case 2:
                           this.setRight(4);
                        }

                        switch(j) {
                        case 0:
                           this.setTop(4);
                           break;
                        case 1:
                           this.setTopBottom(20);
                           break;
                        case 2:
                           this.setBottom(4);
                        }

                     }
                  });
               }
            }
         }

         for(final int i = 0; i < 3; ++i) {
            for(final int j = 0; j < 3; ++j) {
               this.container.add(new GuiButton(100, 20) {
                  {
                     switch(i) {
                     case 0:
                        this.setLeft(20);
                        this.setText("Left");
                        break;
                     case 1:
                        this.setCenteredHorizontal();
                        this.setText("Center");
                        break;
                     case 2:
                        this.setRight(20);
                        this.setText("Right");
                     }

                     switch(j) {
                     case 0:
                        this.setTop(20);
                        this.addText(" + Top");
                        break;
                     case 1:
                        this.setCenteredVertical();
                        this.addText(" + Middle");
                        break;
                     case 2:
                        this.setBottom(20);
                        this.addText(" + Bottom");
                     }

                     this.setAction(() -> {
                        display(GuiTestScreen.thisx);
                     });
                  }
               });
            }
         }

      }
   }

   public class ControlsScreen extends GuiContainerScreen {
      public ControlsScreen() {
         this.container.add(new GuiLayout(Direction.VERTICAL) {
            {
               this.setCenteredHorizontal();
               this.setFillVertical(8);
               this.addFixed(new GuiSlider(Direction.HORIZONTAL) {
                  {
                     this.setFillHorizontal();
                     this.setRange(0.0D, 10.0D);
                     this.setStepSize(1.0D);
                  }
               });
               this.addFixed(new GuiField() {
                  {
                     this.setFillHorizontal();
                  }
               });
               this.addFixed(new GuiScrollable() {
                  {
                     this.setFillHorizontal();
                     this.setHeight(100);
                     this.setPadding(8);
                     GuiButtonIcon.Icon icon = new GuiButtonIcon.Icon(new GuiTextureResource("config_icons", 64, 64), 36, 0, 8, 16);
                     this.add(new GuiButtonIcon(icon, "Scroll!") {
                        {
                           this.setPosition(0, 0);
                        }
                     });
                     this.add(new GuiButtonIcon(icon) {
                        {
                           this.setPosition(0, 24);
                        }
                     });
                     this.add(new GuiButton("You found me!") {
                        {
                           this.setPosition(300, 200);
                        }
                     });
                  }
               });
               this.addWeighted(new GuiContainer());
               this.addFixed(new GuiButton(200, "Back") {
                  {
                     this.setCenteredHorizontal();
                     this.setAction(() -> {
                        display(GuiTestScreen.this);
                     });
                  }
               });
            }
         });
         this.container.add(new GuiButton(8, 8, 100, 20, "Drag me!") {
            public boolean canDrag() {
               return true;
            }

            public void onDragged(int mouseX, int mouseY, int deltaX, int deltaY, int startX, int startY) {
               this.setPosition(((Alignment.Min)this.getHorizontalAlign()).min + mouseX - startX, ((Alignment.Min)this.getVerticalAlign()).min + mouseY - startY);
            }
         });
      }
   }

   public class LayoutScreen1 extends GuiContainerScreen {
      public LayoutScreen1() {
         this.container.add(new GuiLayout(Direction.VERTICAL) {
            {
               this.setFill(8);

               for(int i = 1; i <= 4; ++i) {
                  final int weight = i * 10;
                  this.addWeighted(new GuiLayout(Direction.HORIZONTAL) {
                     {
                        this.setFillHorizontal();
                        this.addFixed(new GuiLabel(weight + "%") {
                           {
                              this.setCenteredVertical();
                           }
                        });
                        this.addFixed(new GuiButton("<") {
                           {
                              this.setCenteredVertical();
                              this.setSize(18, 18);
                           }
                        });

                        for(int j = 1; j <= 3; ++j) {
                           this.add(new GuiButton("Option " + j) {
                              {
                                 this.setTopBottom(0);
                              }
                           });
                        }

                        this.addFixed(new GuiButton(">") {
                           {
                              this.setCenteredVertical();
                              this.setSize(18, 18);
                           }
                        });
                     }
                  }, (double)weight);
               }

               this.addFixed(new GuiButton(200, "Back") {
                  {
                     this.setCenteredHorizontal();
                     this.setAction(() -> {
                        display(GuiTestScreen.this);
                     });
                  }
               });
            }
         });
      }
   }

   public class LayoutScreen2 extends GuiContainerScreen {
      private GuiLayout resizable;

      public LayoutScreen2() {
         this.container.add(new GuiLayout(Direction.VERTICAL) {
            {
               this.setCenteredHorizontal();
               this.setFillVertical(8);
               this.addFixed(new GuiLayout(Direction.HORIZONTAL) {
                  {
                     this.setFillHorizontal();
                     this.addWeighted(new GuiLabel(" Adjust width: ") {
                        {
                           this.setCenteredVertical();
                        }
                     });
                     this.addFixed(new GuiButton(20, "-") {
                        {
                           this.setAction(() -> {
                              LayoutScreen2.this.resizable.setWidth(LayoutScreen2.this.resizable.getWidth() - 40);
                           });
                        }
                     });
                     this.addFixed(new GuiButton(20, "+") {
                        {
                           this.setAction(() -> {
                              LayoutScreen2.this.resizable.setWidth(LayoutScreen2.this.resizable.getWidth() + 40);
                           });
                        }
                     });
                  }
               });
               this.addFixed(new GuiLayout(Direction.HORIZONTAL) {
                  {
                     LayoutScreen2.this.resizable = this;
                     this.setWidth(240);
                     this.addFixed(new GuiLabel(" Variable Height: ") {
                        {
                           this.setCenteredVertical();
                        }
                     });

                     for(int i = 1; i <= 3; ++i) {
                        this.addWeighted(new GuiButton() {
                           {
                              this.setCenteredVertical();
                           }

                           public void onSizeChanged(Direction direction) {
                              if (direction == Direction.HORIZONTAL) {
                                 this.setHeight(this.getWidth());
                              }

                           }
                        }, (double)i);
                     }

                  }
               });
               this.addFixed(new GuiLayout(Direction.HORIZONTAL) {
                  {
                     this.setFillHorizontal();
                     this.addFixed(new GuiLabel(" Fixed Height: ") {
                        {
                           this.setCenteredVertical();
                        }
                     });
                     this.addWeighted(new GuiButton());
                  }
               });
               this.addWeighted(new GuiContainer());
               this.addFixed(new GuiButton(200, "Back") {
                  {
                     this.setCenteredHorizontal();
                     this.setAction(() -> {
                        display(GuiTestScreen.this);
                     });
                  }
               });
            }
         });
      }
   }

   public class VisibilityEnabledScreen extends GuiContainerScreen {
      public VisibilityEnabledScreen() {
         this.container.add(new GuiLayout(Direction.VERTICAL) {
            {
               this.setCenteredHorizontal();
               this.setFillVertical(8);
               VisibilityEnabledScreen.this.addVisibilityEnabledControls(this, new GuiLayout(Direction.VERTICAL) {
                  {
                     this.setFillHorizontal(8);
                     this.addFixed(new GuiButton("Filler") {
                        {
                           this.setFillHorizontal();
                        }
                     });
                     VisibilityEnabledScreen.this.addVisibilityEnabledControls(this, new GuiLayout(Direction.VERTICAL) {
                        {
                           this.setFillHorizontal(8);
                           this.addFixed(new GuiButton("Filler") {
                              {
                                 this.setFillHorizontal();
                              }
                           });
                           VisibilityEnabledScreen.this.addVisibilityEnabledControls(this, new GuiButton("Another Element") {
                              {
                                 this.setFillHorizontal();
                              }
                           });
                           this.addFixed(new GuiButton("Filler") {
                              {
                                 this.setFillHorizontal();
                              }
                           });
                        }
                     });
                     this.addFixed(new GuiButton("Filler") {
                        {
                           this.setFillHorizontal();
                        }
                     });
                  }
               });
               this.addWeighted(new GuiContainer());
               this.addFixed(new GuiButton(200, "Back") {
                  {
                     this.setCenteredHorizontal();
                     this.setAction(() -> {
                        display(GuiTestScreen.this);
                     });
                  }
               });
            }
         });
      }

      private void addVisibilityEnabledControls(GuiLayout layout, final GuiElementBase element) {
         layout.addFixed(new GuiLayout(Direction.HORIZONTAL) {
            {
               this.setFillHorizontal();
               this.addFixed(new GuiLabel(" Visible: ") {
                  {
                     this.setCenteredVertical();
                  }
               });
               this.addWeighted(new GuiButton("yes") {
                  {
                     this.setAction(() -> {
                        element.setVisible(!element.isVisible());
                        this.setText(element.isVisible() ? "yes" : "no");
                     });
                  }
               });
               this.addFixed(new GuiLabel(" Enabled: ") {
                  {
                     this.setCenteredVertical();
                  }
               });
               this.addWeighted(new GuiButton("yes") {
                  {
                     this.setAction(() -> {
                        element.setEnabled(!element.isEnabled());
                        this.setText(element.isEnabled() ? "yes" : "no");
                     });
                  }
               });
            }
         });
         layout.addFixed(element);
      }
   }
}
