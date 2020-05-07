package best.reich.ingros.commands.module;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.util.logging.Logger;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.xenforu.kelo.command.Command;
import me.xenforu.kelo.command.annotation.CommandManifest;
import me.xenforu.kelo.module.IModule;
import me.xenforu.kelo.setting.impl.BooleanSetting;
import me.xenforu.kelo.setting.impl.ColorSetting;
import me.xenforu.kelo.setting.impl.ModeStringSetting;
import me.xenforu.kelo.setting.impl.StringSetting;
import me.xenforu.kelo.util.math.MathUtil;

import java.awt.*;

@CommandManifest(label = "")
public class ModuleCommand extends Command {
    private IModule module;

    public ModuleCommand(IModule module) {
        this.module = module;
        setLabel(module.getLabel());
        setDescription("a module");
    }

    @Override
    public void execute(String[] args) {
        if (args.length >= 2) {
            IngrosWare.INSTANCE.settingManager.getSettingsFromObject(module).forEach(property -> {
                if (args[1].equalsIgnoreCase(property.getLabel())) {
                    if (property instanceof BooleanSetting) {
                        final BooleanSetting booleanProperty = (BooleanSetting) property;
                        booleanProperty.setValue(!booleanProperty.getValue());
                        Logger.printMessage(booleanProperty.getLabel() + " has been " + (booleanProperty.getValue() ? ChatFormatting.PREFIX_CODE + "aenabled" + ChatFormatting.PREFIX_CODE + "7" : ChatFormatting.PREFIX_CODE + "cdisabled" + ChatFormatting.PREFIX_CODE + "7") + " for " + module.getLabel() + ".",true);
                    } else if (property instanceof ModeStringSetting) {
                        final ModeStringSetting modeStringProperty = (ModeStringSetting) property;
                        if (args.length >= 3) {
                            if (args[2].equalsIgnoreCase("help")) {
                                Logger.printMessage(modeStringProperty.getLabel() + "'s options are {",false);
                                for (String mode : modeStringProperty.getModes()) {
                                    Logger.printMessage(mode,false);
                                }
                                Logger.printMessage("}",false);
                            } else {
                                property.setValue(args[2]);
                                Logger.printMessage(property.getLabel() + " has been set to " + property.getValue() + " for " + module.getLabel() + ".",true);
                            }
                        } else {
                            Logger.printMessage("Not enough arguments to change property.",true);
                        }
                    } else if (property instanceof ColorSetting) {
                        final ColorSetting colorSetting = (ColorSetting) property;
                        if (args.length >= 5) {
                            try {
                                final int r = MathUtil.clamp(Integer.parseInt(args[2]),0,255);
                                final int g = MathUtil.clamp(Integer.parseInt(args[3]),0,255);
                                final int b = MathUtil.clamp(Integer.parseInt(args[4]),0,255);
                                if (args.length > 5) {
                                    final int a = MathUtil.clamp(Integer.parseInt(args[5]),0,255);
                                    colorSetting.setValue(new Color(r,g,b,a));
                                } else {
                                    colorSetting.setValue(new Color(r,g,b));
                                }
                                Logger.printMessage(property.getLabel() + " has been set to " + colorSetting.getValue().getRGB() + " for " + module.getLabel() + ".",true);
                            } catch (Exception e) {
                                Logger.printMessage("Not enough arguments to change property.",true);
                            }
                        } else {
                            Logger.printMessage("Not enough arguments to change property.",true);
                        }
                    } else if (property instanceof StringSetting) {
                        if (args.length >= 3) {
                            final StringBuilder stringBuilder = new StringBuilder();
                            for (int i = 2; i < args.length; i++) {
                                stringBuilder.append(args[i]);
                                if (i != args.length - 1) stringBuilder.append(" ");
                            }
                            property.setValue(stringBuilder.toString());
                            Logger.printMessage(property.getLabel() + " has been set to " + property.getValue() + " for " + module.getLabel() + ".",true);
                        }
                    } else {
                        if (args.length >= 3) {
                            property.setValue(args[2]);
                            Logger.printMessage(property.getLabel() + " has been set to " + property.getValue() + " for " + module.getLabel() + ".",true);
                        } else {
                            Logger.printMessage("Not enough arguments to change property.",true);
                        }
                    }
                }
            });
        } else {
            StringBuilder builder = new StringBuilder(module.getLabel() + " " + ChatFormatting.PREFIX_CODE + "8[" + ChatFormatting.PREFIX_CODE + "7" + IngrosWare.INSTANCE.settingManager.getSettingsFromObject(module).size() + ChatFormatting.PREFIX_CODE + "8]" + ChatFormatting.PREFIX_CODE + "7: ");
            IngrosWare.INSTANCE.settingManager.getSettingsFromObject(module)
                    .forEach(property -> builder.append(ChatFormatting.PREFIX_CODE).append(property instanceof BooleanSetting ? (((BooleanSetting) property).getValue() ? "a" : "c") : "7").append(property.getLabel()).append(ChatFormatting.PREFIX_CODE + "8, "));
            Logger.printMessage(builder.toString().substring(0, builder.length() - 2),false);
        }
    }
}
