package lumien.custommainmenu.configuration;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import lumien.custommainmenu.CustomMainMenu;
import lumien.custommainmenu.configuration.elements.Background;
import lumien.custommainmenu.configuration.elements.Button;
import lumien.custommainmenu.configuration.elements.Image;
import lumien.custommainmenu.configuration.elements.Label;
import lumien.custommainmenu.configuration.elements.Panorama;
import lumien.custommainmenu.configuration.elements.Slideshow;
import lumien.custommainmenu.configuration.elements.SplashText;
import lumien.custommainmenu.gui.GuiCustom;
import lumien.custommainmenu.lib.ANCHOR;
import lumien.custommainmenu.lib.actions.ActionConnectToServer;
import lumien.custommainmenu.lib.actions.ActionLoadWorld;
import lumien.custommainmenu.lib.actions.ActionOpenFolder;
import lumien.custommainmenu.lib.actions.ActionOpenGUI;
import lumien.custommainmenu.lib.actions.ActionOpenLink;
import lumien.custommainmenu.lib.actions.ActionOpenModConfig;
import lumien.custommainmenu.lib.actions.ActionQuit;
import lumien.custommainmenu.lib.actions.ActionRefresh;
import lumien.custommainmenu.lib.actions.IAction;
import lumien.custommainmenu.lib.texts.IText;
import lumien.custommainmenu.lib.texts.TextResourceLocation;
import lumien.custommainmenu.lib.texts.TextString;
import lumien.custommainmenu.lib.texts.TextURL;
import lumien.custommainmenu.lib.textures.ITexture;
import lumien.custommainmenu.lib.textures.TextureApng;
import lumien.custommainmenu.lib.textures.TextureResourceLocation;
import lumien.custommainmenu.lib.textures.TextureURL;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class GuiConfig {
   public String name;
   public int guiScale;
   public ArrayList<Button> customButtons;
   public ArrayList<Label> customLabels;
   public ArrayList<Image> customImages;
   public HashMap<String, Alignment> alignments;
   public SplashText splashText;
   public Panorama panorama;
   public Background background;
   public List<TextURL> textUrls = new ArrayList();

   public GuiConfig() {
   }

   public void load(String name, JsonObject jsonObject) {
      if (name.endsWith("_small")) {
         this.guiScale = 1;
         name = name.replace("_small", "");
      } else if (name.endsWith("_normal")) {
         this.guiScale = 2;
         name = name.replace("_normal", "");
      } else if (name.endsWith("_large")) {
         this.guiScale = 3;
         name = name.replace("_large", "");
      } else if (name.endsWith("_auto")) {
         this.guiScale = 0;
         name = name.replace("_auto", "");
      } else {
         this.guiScale = -1;
      }

      this.name = name;
      this.loadAlignments(jsonObject);
      this.customLabels = new ArrayList();
      this.customImages = new ArrayList();
      this.customButtons = new ArrayList();
      this.splashText = null;
      this.panorama = null;
      this.background = null;
      if (jsonObject.has("buttons")) {
         this.loadButtons(jsonObject);
      }

      if (jsonObject.has("labels")) {
         this.loadLabels(jsonObject);
      }

      if (jsonObject.has("images")) {
         this.loadImages(jsonObject);
      }

      if (jsonObject.has("other")) {
         this.loadOthers(jsonObject);
      }

   }

   private void loadAlignments(JsonObject jsonObject) {
      this.alignments = new HashMap();
      this.alignments.put("bottom_left", new Alignment(0.0F, 1.0F));
      this.alignments.put("top_left", new Alignment(0.0F, 0.0F));
      this.alignments.put("top_right", new Alignment(1.0F, 0.0F));
      this.alignments.put("bottom_right", new Alignment(1.0F, 1.0F));
      this.alignments.put("center", new Alignment(0.5F, 0.5F));
      this.alignments.put("button", new Alignment(0.5F, 0.25F));
      this.alignments.put("top_center", new Alignment(0.5F, 0.0F));
      this.alignments.put("left_center", new Alignment(0.0F, 0.5F));
      this.alignments.put("bottom_center", new Alignment(0.5F, 1.0F));
      this.alignments.put("right_center", new Alignment(1.0F, 0.5F));
      if (jsonObject.has("alignments")) {
         JsonObject alignmentObject = (JsonObject)jsonObject.get("alignments");

         for(Entry<String, JsonElement> entry : alignmentObject.entrySet()) {
            String name = (String)entry.getKey();
            JsonObject object = (JsonObject)entry.getValue();
            Alignment a = new Alignment(object.get("factorWidth").getAsFloat(), object.get("factorHeight").getAsFloat());
            this.alignments.put(name, a);
         }
      }

   }

   private void loadOthers(JsonObject jsonObject) {
      JsonObject other = (JsonObject)jsonObject.get("other");
      JsonObject splashTextObject = (JsonObject)other.get("splash-text");
      if (splashTextObject != null) {
         if (splashTextObject.has("color") && splashTextObject.has("alignment")) {
            this.splashText = new SplashText(this, splashTextObject.get("posX").getAsInt(), splashTextObject.get("posY").getAsInt(), splashTextObject.get("color").getAsInt(), splashTextObject.get("alignment").getAsString());
         } else if (splashTextObject.has("color")) {
            this.splashText = new SplashText(this, splashTextObject.get("posX").getAsInt(), splashTextObject.get("posY").getAsInt(), splashTextObject.get("color").getAsInt(), "top_center");
         } else if (splashTextObject.has("alignment")) {
            this.splashText = new SplashText(this, splashTextObject.get("posX").getAsInt(), splashTextObject.get("posY").getAsInt(), splashTextObject.get("alignment").getAsString());
         } else {
            this.splashText = new SplashText(this, splashTextObject.get("posX").getAsInt(), splashTextObject.get("posY").getAsInt(), "top_center");
         }

         if (splashTextObject.has("synced")) {
            this.splashText.synced = splashTextObject.get("synced").getAsBoolean();
         }

         if (splashTextObject.has("texts")) {
            this.splashText.setSplashTexts(this.getWantedText(splashTextObject.get("texts")));
         }

         if (splashTextObject.has("file")) {
            this.splashText.setSplashTexts(new TextResourceLocation(this.getStringPlease(splashTextObject.get("file"))));
         }
      }

      JsonObject panoramaObject = (JsonObject)other.get("panorama");
      if (panoramaObject != null) {
         this.panorama = new Panorama(this, this.getStringPlease(panoramaObject.get("images")), panoramaObject.get("blur").getAsBoolean(), panoramaObject.get("gradient").getAsBoolean());
         if (panoramaObject.has("animate")) {
            this.panorama.setAnimate(panoramaObject.get("animate").getAsBoolean());
         }

         if (panoramaObject.has("position")) {
            this.panorama.setPosition(panoramaObject.get("position").getAsInt());
         }

         if (panoramaObject.has("animationSpeed")) {
            this.panorama.setAnimationSpeed(panoramaObject.get("animationSpeed").getAsInt());
         }

         if (panoramaObject.has("synced")) {
            this.panorama.synced = panoramaObject.get("synced").getAsBoolean();
         }
      }

      JsonElement backgroundElement = other.get("background");
      if (backgroundElement != null) {
         if (backgroundElement.isJsonPrimitive() && backgroundElement.getAsJsonPrimitive().isString() && backgroundElement.getAsString().equals("options")) {
            this.background = Background.OPTIONS_BACKGROUND;
         } else {
            JsonObject backgroundObject = (JsonObject)backgroundElement;
            this.background = new Background(this, getWantedTexture(this.getStringPlease(backgroundObject.get("image"))));
            if (backgroundObject.has("mode")) {
               this.background.setMode(backgroundObject.get("mode").getAsString());
            }

            if (backgroundObject.has("slideshow")) {
               JsonObject slideShowObject = backgroundObject.get("slideshow").getAsJsonObject();
               this.background.ichBinEineSlideshow = true;
               if (slideShowObject.has("synced") && slideShowObject.get("synced").getAsBoolean()) {
                  GuiCustom mainMenu = CustomMainMenu.INSTANCE.config.getGUI("mainmenu");
                  this.background.slideShow = mainMenu.guiConfig.background.slideShow;
               } else {
                  JsonArray imageArray = slideShowObject.get("images").getAsJsonArray();
                  String[] images = new String[imageArray.size()];

                  for(int i = 0; i < images.length; ++i) {
                     images[i] = imageArray.get(i).getAsString();
                  }

                  Slideshow slideShow = new Slideshow(this, images);
                  if (slideShowObject.has("displayDuration")) {
                     slideShow.displayDuration = slideShowObject.get("displayDuration").getAsInt();
                  }

                  if (slideShowObject.has("fadeDuration")) {
                     slideShow.fadeDuration = slideShowObject.get("fadeDuration").getAsInt();
                  }

                  if (slideShowObject.has("shuffle") && slideShowObject.get("shuffle").getAsBoolean()) {
                     slideShow.shuffle();
                  }

                  this.background.slideShow = slideShow;
               }
            }
         }
      }

   }

   private void loadImages(JsonObject jsonObject) {
      JsonObject textElements = jsonObject.get("images").getAsJsonObject();

      for(Entry<String, JsonElement> entry : textElements.entrySet()) {
         String name = (String)entry.getKey();
         JsonElement element = (JsonElement)entry.getValue();
         this.customImages.add(this.getImage((JsonObject)element));
      }

   }

   private void loadLabels(JsonObject jsonObject) {
      Set<Entry<String, JsonElement>> labels = new HashSet();
      JsonElement textElement = jsonObject.get("texts");
      if (textElement != null) {
         CustomMainMenu.INSTANCE.logger.log(Level.ERROR, "The texts category in CMM has been renamed to labels.");
      }

      JsonElement labelElement = jsonObject.get("labels");
      if (labelElement != null) {
         JsonObject labelElements = jsonObject.get("labels").getAsJsonObject();
         labels.addAll(labelElements.entrySet());
      }

      for(Entry<String, JsonElement> entry : labels) {
         String name = (String)entry.getKey();
         JsonElement element = (JsonElement)entry.getValue();
         this.customLabels.add(this.getLabel(name, (JsonObject)element));
      }

   }

   private void loadButtons(JsonObject jsonObject) {
      JsonObject buttonElements = jsonObject.get("buttons").getAsJsonObject();

      for(Entry<String, JsonElement> entry : buttonElements.entrySet()) {
         String name = (String)entry.getKey();
         JsonObject object = (JsonObject)entry.getValue();
         Button b = this.getButton(object);
         b.name = (String)entry.getKey();
         this.customButtons.add(b);
      }

   }

   private Button getButton(JsonObject jsonObject) {
      Button b = new Button(this, this.getWantedText(jsonObject.get("text")), jsonObject.get("posX").getAsInt(), jsonObject.get("posY").getAsInt(), jsonObject.get("width").getAsInt(), jsonObject.get("height").getAsInt());
      if (jsonObject.has("alignment")) {
         b.setStringAlignment(jsonObject.get("alignment").getAsString());
      }

      if (jsonObject.has("texture")) {
         b.setTexture(getWantedTexture(this.getStringPlease(jsonObject.get("texture"))));
      }

      if (jsonObject.has("normalTextColor")) {
         b.normalTextColor = jsonObject.get("normalTextColor").getAsInt();
      }

      if (jsonObject.has("hoverTextColor")) {
         b.hoverTextColor = jsonObject.get("hoverTextColor").getAsInt();
      }

      if (jsonObject.has("shadow")) {
         b.shadow = jsonObject.get("shadow").getAsBoolean();
      }

      if (jsonObject.has("imageWidth")) {
         b.imageWidth = jsonObject.get("imageWidth").getAsInt();
      }

      if (jsonObject.has("imageHeight")) {
         b.imageHeight = jsonObject.get("imageHeight").getAsInt();
      }

      if (jsonObject.has("wrappedButton")) {
         b.setWrappedButton(jsonObject.get("wrappedButton").getAsInt());
      }

      if (jsonObject.has("action")) {
         JsonObject actionObject = (JsonObject)jsonObject.get("action");
         b.action = this.getWantedAction(actionObject);
      }

      if (jsonObject.has("tooltip")) {
         b.tooltip = this.getWantedText(jsonObject.get("tooltip"));
      }

      if (jsonObject.has("hoverText")) {
         b.hoverText = this.getWantedText(jsonObject.get("hoverText"));
      }

      if (jsonObject.has("pressSound")) {
         b.pressSound = this.getStringPlease(jsonObject.get("pressSound"));
      }

      if (jsonObject.has("hoverSound")) {
         b.hoverSound = this.getStringPlease(jsonObject.get("hoverSound"));
      }

      if (jsonObject.has("textOffsetX")) {
         b.textOffsetX = jsonObject.get("textOffsetX").getAsInt();
      }

      if (jsonObject.has("textOffsetY")) {
         b.textOffsetY = jsonObject.get("textOffsetY").getAsInt();
      }

      return b;
   }

   private Image getImage(JsonObject jsonObject) {
      Image image = new Image(this, jsonObject.get("posX").getAsInt(), jsonObject.get("posY").getAsInt(), jsonObject.get("width").getAsInt(), jsonObject.get("height").getAsInt(), this.getAlignment("top_left"));
      if (jsonObject.has("alignment")) {
         image.alignment = this.getAlignment(jsonObject.get("alignment").getAsString());
      }

      if (jsonObject.has("hoverImage")) {
         image.hoverImage = getWantedTexture(this.getStringPlease(jsonObject.get("hoverImage")));
      }

      if (jsonObject.has("image")) {
         image.image = getWantedTexture(this.getStringPlease(jsonObject.get("image")));
      } else {
         if (!jsonObject.has("slideshow")) {
            throw new RuntimeException("Images either need an image or slideshow property");
         }

         JsonObject slideShowObject = jsonObject.get("slideshow").getAsJsonObject();
         image.ichBinEineSlideshow = true;
         JsonArray imageArray = slideShowObject.get("images").getAsJsonArray();
         String[] images = new String[imageArray.size()];

         for(int i = 0; i < images.length; ++i) {
            images[i] = imageArray.get(i).getAsString();
         }

         Slideshow slideShow = new Slideshow(this, images);
         if (slideShowObject.has("displayDuration")) {
            slideShow.displayDuration = slideShowObject.get("displayDuration").getAsInt();
         }

         if (slideShowObject.has("fadeDuration")) {
            slideShow.fadeDuration = slideShowObject.get("fadeDuration").getAsInt();
         }

         if (slideShowObject.has("shuffle") && slideShowObject.get("shuffle").getAsBoolean()) {
            slideShow.shuffle();
         }

         image.slideShow = slideShow;
      }

      return image;
   }

   private Label getLabel(String name, JsonObject jsonObject) {
      Label label = new Label(this, name, this.getWantedText(jsonObject.get("text")), jsonObject.get("posX").getAsInt(), jsonObject.get("posY").getAsInt());
      if (jsonObject.has("alignment")) {
         label.setAlignment(jsonObject.get("alignment").getAsString());
      }

      if (jsonObject.has("color")) {
         label.setColor(jsonObject.get("color").getAsInt());
      }

      if (jsonObject.has("hoverColor")) {
         label.setHoverColor(jsonObject.get("hoverColor").getAsInt());
      }

      if (jsonObject.has("action")) {
         JsonObject actionObject = (JsonObject)jsonObject.get("action");
         label.action = this.getWantedAction(actionObject);
      }

      if (jsonObject.has("hoverText")) {
         label.hoverText = this.getWantedText(jsonObject.get("hoverText"));
      }

      if (jsonObject.has("fontSize")) {
         label.fontSize = jsonObject.get("fontSize").getAsFloat();
      }

      if (jsonObject.has("pressSound")) {
         label.pressSound = this.getStringPlease(jsonObject.get("pressSound"));
      }

      if (jsonObject.has("hoverSound")) {
         label.hoverSound = this.getStringPlease(jsonObject.get("hoverSound"));
      }

      if (jsonObject.has("anchor")) {
         String stringAnchor = jsonObject.get("anchor").getAsString();
         if (stringAnchor.equals("start")) {
            label.anchor = ANCHOR.START;
         } else if (stringAnchor.equals("middle")) {
            label.anchor = ANCHOR.MIDDLE;
         } else if (stringAnchor.equals("end")) {
            label.anchor = ANCHOR.END;
         }
      }

      return label;
   }

   private String getStringPlease(JsonElement jsonElement) {
      EaglercraftRandom rng = new EaglercraftRandom();
      if (jsonElement.isJsonPrimitive()) {
         return jsonElement.getAsString();
      } else if (jsonElement.isJsonArray()) {
         JsonArray array = jsonElement.getAsJsonArray();
         return array.get(rng.nextInt(array.size())).getAsString();
      } else {
         CustomMainMenu.INSTANCE.logger.log(Level.ERROR, "Error getting random value out of " + jsonElement.toString());
         return "ERROR";
      }
   }

   public static ITexture getWantedTexture(String textureString) {
      if (textureString.startsWith("web:")) {
         String url = textureString.substring(4, textureString.length());
         return new TextureURL(url);
      } else {
         return (ITexture)(textureString.endsWith("apng") ? new TextureApng(textureString) : new TextureResourceLocation(textureString));
      }
   }

   private IAction getWantedAction(JsonObject actionObject) {
      String actionType = actionObject.get("type").getAsString();
      if (actionType.equals("openLink")) {
         return new ActionOpenLink(actionObject.get("link").getAsString());
      } else if (actionType.equalsIgnoreCase("loadWorld")) {
         return new ActionLoadWorld(actionObject.get("dirName").getAsString(), actionObject.get("saveName").getAsString());
      } else if (actionType.equalsIgnoreCase("connectToServer")) {
         return new ActionConnectToServer(actionObject.get("ip").getAsString(), actionObject.get("serverName") != null ? actionObject.get("serverName").getAsString() : "Minecraft Server");
      } else if (actionType.equalsIgnoreCase("openGui")) {
         return new ActionOpenGUI(actionObject.get("gui").getAsString());
      } else if (actionType.equalsIgnoreCase("quit")) {
         return new ActionQuit();
      } else if (actionType.equalsIgnoreCase("refresh")) {
         return new ActionRefresh();
      } else if (actionType.equalsIgnoreCase("openFolder")) {
         return new ActionOpenFolder(actionObject.get("folderName").getAsString());
      } else {
         return actionType.equalsIgnoreCase("openModConfig") ? new ActionOpenModConfig(actionObject.get("modid").getAsString()) : null;
      }
   }

   public Alignment getAlignment(String name) {
      return this.alignments.containsKey(name) ? (Alignment)this.alignments.get(name) : (Alignment)this.alignments.get("top_left");
   }

   public IText getWantedText(JsonElement element) {
      if (!element.isJsonPrimitive() && !element.isJsonArray()) {
         if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            String textType = object.get("type").getAsString();
            if (textType.equals("web")) {
               String url = object.get("url").getAsString();
               int refreshInterval = -1;
               if (object.has("refreshInterval")) {
                  refreshInterval = object.get("refreshInterval").getAsInt();
               }

               TextURL tURL = new TextURL(url, refreshInterval);
               this.textUrls.add(tURL);
               return tURL;
            }

            if (textType.equals("file")) {
               String rl = object.get("location").getAsString();
               return new TextResourceLocation(rl);
            }
         }

         return new TextString("INVALID TEXT");
      } else {
         String textString = this.getStringPlease(element);
         if (textString.startsWith("web:")) {
            String url = textString.substring(4, textString.length());
            TextURL tURL = new TextURL(url, -1);
            this.textUrls.add(tURL);
            return tURL;
         } else if (textString.startsWith("file:")) {
            String resource = textString.substring(5, textString.length());
            return new TextResourceLocation(resource);
         } else {
            return new TextString(textString);
         }
      }
   }

   public void tick() {
      this.textUrls.forEach((url) -> {
         url.tick();
      });
   }
}
