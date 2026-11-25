package com.yungnickyoung.minecraft.yungslaw.config.io;

import com.yungnickyoung.minecraft.yungslaw.YungsLaw;
import com.yungnickyoung.minecraft.yungslaw.config.util.ConfigHolder;
import com.yungnickyoung.minecraft.yungslaw.config.util.ConfigOption;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Configuration.UnicodeInputStreamReader;
import net.minecraftforge.common.config.Property.Type;
import org.apache.commons.io.IOUtils;

public class ConfigLoader {
   private static final String ALLOWED_CHARS = "._-";
   private static final String DEFAULT_ENCODING = "UTF-8";

   public ConfigLoader() {
   }

   public static ConfigHolder loadConfigFromFileForDimension(int dimensionID) {
      String fileName = "DIM" + dimensionID + "_config.cfg";
      File configFile = new File(YungsLaw.customConfigDir, fileName);
      if (configFile.exists() && !configFile.isDirectory()) {
         if (!configFile.canRead()) {
            YungsLaw.LOGGER.warn("YUNG's Law config file for dimension {} not readable. Using global config...", Integer.valueOf(dimensionID));
            return new ConfigHolder();
         } else {
            YungsLaw.LOGGER.info("Reading YUNG's Law config from file for dimension {}...", Integer.valueOf(dimensionID));
            return parseConfigFromFile(configFile);
         }
      } else {
         YungsLaw.LOGGER.info("YUNG's Law config file for dimension {} not found. Using global config...", Integer.valueOf(dimensionID));
         return new ConfigHolder();
      }
   }

   private static ConfigHolder parseConfigFromFile(File file) {
      ConfigHolder config = new ConfigHolder();
      BufferedReader buffer = null;
      UnicodeInputStreamReader input = null;
      String fileName = file.getName();

      try {
         Map<String, ConfigCategory> categories = new TreeMap();
         input = new UnicodeInputStreamReader(new FileInputStream(file), "UTF-8");
         buffer = new BufferedReader(input);
         ConfigCategory currCategory = null;
         Type type = null;
         ArrayList<String> tmpList = null;
         int lineNum = 0;
         String name = null;

         while(true) {
            ++lineNum;
            String line = buffer.readLine();
            if (line == null) {
               return config;
            }

            int nameStart = -1;
            int nameEnd = -1;
            boolean skip = false;
            boolean quoted = false;
            boolean isTypeSpecified = false;
            boolean isFirstNonWhitespaceCharOnLine = true;

            for(int i = 0; i < line.length() && !skip; ++i) {
               if (!Character.isLetterOrDigit(line.charAt(i)) && "._-".indexOf(line.charAt(i)) == -1 && (!quoted || line.charAt(i) == '"')) {
                  if (!Character.isWhitespace(line.charAt(i))) {
                     switch(line.charAt(i)) {
                     case '"':
                        if (tmpList == null) {
                           if (quoted) {
                              quoted = false;
                           }

                           if (!quoted && nameStart == -1) {
                              quoted = true;
                           }
                        }
                        break;
                     case '#':
                        if (tmpList == null) {
                           skip = true;
                           continue;
                        }
                        break;
                     case ':':
                        if (tmpList == null) {
                           String typeSubstr = line.substring(nameStart, nameEnd + 1);
                           type = Type.tryParse(typeSubstr.charAt(0));
                           nameEnd = -1;
                           nameStart = -1;
                           isTypeSpecified = true;
                        }
                        break;
                     case '<':
                        if (tmpList != null && i + 1 == line.length() || tmpList == null && i + 1 != line.length()) {
                           throw new RuntimeException(String.format("Malformed list property \"%s:%d\"", fileName, lineNum));
                        }

                        if (i + 1 == line.length()) {
                           name = line.substring(nameStart, nameEnd + 1);
                           if (currCategory == null) {
                              throw new RuntimeException(String.format("'%s' has no scope (missing category?) in '%s:%d'", name, fileName, lineNum));
                           }

                           tmpList = new ArrayList();
                           skip = true;
                        }
                        break;
                     case '=':
                        if (tmpList == null) {
                           name = line.substring(nameStart, nameEnd + 1);
                           if (currCategory == null) {
                              throw new RuntimeException(String.format("'%s' has no scope (missing category?) in '%s:%d'", name, fileName, lineNum));
                           }

                           if (!isTypeSpecified) {
                              YungsLaw.LOGGER.warn("Error in YUNG's Law config for {} (line {}): missing variable type specifier. Inferring String...", fileName, Integer.valueOf(lineNum));
                           }

                           Property prop = new Property(name, line.substring(i + 1), type, true);
                           String fullName = currCategory.getQualifiedName().toLowerCase() + "." + name.toLowerCase();
                           ConfigOption target = (ConfigOption)config.properties.get(fullName);
                           if (target == null) {
                              YungsLaw.LOGGER.error("ERROR: INVALID PROPERTY {} in config {}. Skipping...", fullName, fileName);
                              i = line.length();
                              continue;
                           }

                           switch(type) {
                           case INTEGER:
                              if (!target.type.equals(Integer.TYPE) && !target.type.equals(Integer.class)) {
                                 YungsLaw.LOGGER.error("ERROR: WRONG TYPE for {} in config {}. Skipping...", fullName, fileName);
                                 i = line.length();
                                 continue;
                              }

                              target.set(Integer.valueOf(prop.getInt()));
                              break;
                           case DOUBLE:
                              if (!target.type.equals(Double.TYPE) && !target.type.equals(Double.class) && !target.type.equals(Float.TYPE) && !target.type.equals(Float.class)) {
                                 YungsLaw.LOGGER.error("ERROR: WRONG TYPE for {} in config {}. Skipping...", fullName, fileName);
                                 i = line.length();
                                 continue;
                              }

                              if (target.type == Float.class) {
                                 target.set(Float.valueOf((float)prop.getDouble()));
                              } else {
                                 target.set(Double.valueOf(prop.getDouble()));
                              }
                              break;
                           case BOOLEAN:
                              if (!target.type.equals(Boolean.TYPE) && !target.type.equals(Boolean.class)) {
                                 YungsLaw.LOGGER.error("ERROR: WRONG TYPE for {} in config {}. Skipping...", fullName, fileName);
                                 i = line.length();
                                 continue;
                              }

                              if (!line.substring(i + 1).toLowerCase().equals("true") && !line.substring(i + 1).toLowerCase().equals("false")) {
                                 throw new RuntimeException(String.format("Invalid Boolean value for property '%s:%d'", fullName, lineNum));
                              }

                              target.set(Boolean.valueOf(prop.getBoolean()));
                              break;
                           default:
                              target.set(prop.getString());
                           }

                           currCategory.put(name, prop);
                           YungsLaw.LOGGER.debug("{}: overriding config option: {}", fileName, fullName);
                           i = line.length();
                        }
                        break;
                     case '>':
                        if (tmpList == null) {
                           throw new RuntimeException(String.format("Malformed list property \"%s:%d\"", fileName, lineNum));
                        }

                        if (isFirstNonWhitespaceCharOnLine) {
                           Property prop = new Property(name, (String[])tmpList.toArray(new String[0]), type);
                           currCategory.put(name, prop);
                           String fullName = currCategory.getQualifiedName().toLowerCase() + "." + name.toLowerCase();
                           ConfigOption target = (ConfigOption)config.properties.get(fullName);
                           if (target != null) {
                              target.set(prop.getStringList());
                              YungsLaw.LOGGER.debug("{}: overriding config option: {}", fileName, fullName);
                           } else {
                              YungsLaw.LOGGER.error("ERROR: INVALID LIST {} in config {}. Skipping...", fullName, fileName);
                           }

                           name = null;
                           tmpList = null;
                           type = null;
                        }
                        break;
                     case '{':
                        if (tmpList == null) {
                           name = line.substring(nameStart, nameEnd + 1);
                           name = name.toLowerCase(Locale.ENGLISH);
                           String qualifiedName = ConfigCategory.getQualifiedName(name, currCategory);
                           ConfigCategory category = (ConfigCategory)categories.get(qualifiedName);
                           if (category == null) {
                              currCategory = new ConfigCategory(name, currCategory);
                              categories.put(qualifiedName, currCategory);
                           } else {
                              currCategory = category;
                           }

                           name = null;
                        }
                        break;
                     case '}':
                        if (tmpList == null) {
                           if (currCategory == null) {
                              throw new RuntimeException(String.format("Invalid config file: attempted to close too many categories '%s:%d'", fileName, lineNum));
                           }

                           currCategory = currCategory.parent;
                        }
                        break;
                     case '~':
                        if (tmpList != null) {
                           ;
                        }
                        break;
                     default:
                        if (tmpList == null) {
                           throw new RuntimeException(String.format("Unknown character '%s' in '%s:%d'", line.charAt(i), fileName, lineNum));
                        }
                     }

                     isFirstNonWhitespaceCharOnLine = false;
                  }
               } else {
                  if (nameStart == -1) {
                     nameStart = i;
                  }

                  nameEnd = i;
                  isFirstNonWhitespaceCharOnLine = false;
               }
            }

            if (quoted) {
               throw new RuntimeException(String.format("Unmatched quote in '%s:%d'", fileName, lineNum));
            }

            if (tmpList != null && !skip) {
               tmpList.add(line.trim());
            }
         }
      } catch (Exception var28) {
         YungsLaw.LOGGER.error("ERROR LOADING YUNG'S LAW CONFIG {}: {}.", fileName, var28.toString());
         YungsLaw.LOGGER.info("USING GLOBAL CONFIG FILE INSTEAD...");
         ConfigHolder line = new ConfigHolder();
         return line;
      } finally {
         IOUtils.closeQuietly(buffer);
         IOUtils.closeQuietly(input);
      }
   }
}
