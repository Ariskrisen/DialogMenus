package dialogmenus.util;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MenuLoader {

    private static final MiniMessage mm = MiniMessage.miniMessage();

    public static Dialog buildDialog(ConfigurationSection config) {
        return Dialog.create(factory -> {
            var builder = factory.empty();

            // Base
            String titleStr = config.getString("title", "Dialog");
            DialogBase.Builder baseBuilder = DialogBase.builder(mm.deserialize(titleStr));

            baseBuilder.canCloseWithEscape(config.getBoolean("can-close-with-escape", true));

            // Body
            ConfigurationSection bodySection = config.getConfigurationSection("body");
            if (bodySection != null) {
                List<DialogBody> bodies = new ArrayList<>();
                for (String key : bodySection.getKeys(false)) {
                    ConfigurationSection itemSection = bodySection.getConfigurationSection(key);
                    if (itemSection == null)
                        continue;

                    String type = itemSection.getString("type", "text");
                    if (type.equalsIgnoreCase("text")) {
                        bodies.add(DialogBody.plainMessage(mm.deserialize(itemSection.getString("content", ""))));
                    } else if (type.equalsIgnoreCase("item")) {
                        Material material = Material.matchMaterial(itemSection.getString("material", "STONE"));
                        if (material != null && material.isItem()) {
                            ItemStack item = new ItemStack(material);
                            String itemName = itemSection.getString("name");
                            if (itemName != null) {
                                ItemMeta meta = item.getItemMeta();
                                meta.displayName(mm.deserialize(itemName));
                                item.setItemMeta(meta);
                            }
                            bodies.add(DialogBody.item(item).build());
                        }
                    }
                }
                baseBuilder.body(bodies);
            }

            // Inputs
            ConfigurationSection inputsSection = config.getConfigurationSection("inputs");
            if (inputsSection != null) {
                List<DialogInput> inputs = new ArrayList<>();
                for (String key : inputsSection.getKeys(false)) {
                    ConfigurationSection inputConfig = inputsSection.getConfigurationSection(key);
                    if (inputConfig == null)
                        continue;

                    String type = inputConfig.getString("type", "text");
                    Component label = mm.deserialize(inputConfig.getString("label", key));

                    if (type.equalsIgnoreCase("text")) {
                        inputs.add(DialogInput.text(key, label).build());
                    } else if (type.equalsIgnoreCase("number-range")) {
                        float min = (float) inputConfig.getDouble("min", 0.0);
                        float max = (float) inputConfig.getDouble("max", 100.0);
                        float initial = (float) inputConfig.getDouble("initial", min);
                        inputs.add(DialogInput.numberRange(key, label, min, max)
                                .initial(initial)
                                .build());
                    }
                }
                baseBuilder.inputs(inputs);
            }

            builder.base(baseBuilder.build());

            // Note: onClose callback is not currently supported in the builder
            // of the experimental Paper Dialogs API version 1.21.10.
            // If needed, it would require a custom listener for dialog close events.

            // Type
            String typeStr = config.getString("type", "notice");
            if (typeStr.equalsIgnoreCase("notice")) {
                ConfigurationSection buttonSection = config.getConfigurationSection("button");
                if (buttonSection != null) {
                    builder.type(DialogType.notice(parseButton(buttonSection)));
                } else {
                    builder.type(DialogType.notice());
                }
            } else if (typeStr.equalsIgnoreCase("confirmation")) {
                ConfigurationSection yesSection = config.getConfigurationSection("yes-button");
                ConfigurationSection noSection = config.getConfigurationSection("no-button");
                if (yesSection != null && noSection != null) {
                    builder.type(DialogType.confirmation(parseButton(yesSection), parseButton(noSection)));
                } else {
                    builder.type(DialogType.notice()); // Fallback
                }
            } else if (typeStr.equalsIgnoreCase("multi-action")) {
                ConfigurationSection buttonsSection = config.getConfigurationSection("buttons");
                if (buttonsSection != null) {
                    List<ActionButton> buttons = new ArrayList<>();
                    for (String key : buttonsSection.getKeys(false)) {
                        ConfigurationSection btnConfig = buttonsSection.getConfigurationSection(key);
                        if (btnConfig != null) {
                            buttons.add(parseButton(btnConfig));
                        }
                    }
                    builder.type(DialogType.multiAction(buttons).build());
                } else {
                    builder.type(DialogType.notice()); // Fallback
                }
            } else {
                // Fallback for missing or invalid type
                builder.type(DialogType.notice());
            }
        });
    }

    private static ActionButton parseButton(ConfigurationSection section) {
        Component text = mm.deserialize(section.getString("text", "OK"));
        ActionButton.Builder builder = ActionButton.builder(text);

        String hoverStr = section.getString("hover");
        if (hoverStr != null) {
            builder.tooltip(mm.deserialize(hoverStr));
        }

        ConfigurationSection actionSection = section.getConfigurationSection("action");
        if (actionSection != null) {
            String type = actionSection.getString("type", "");
            String value = actionSection.getString("value", "");

            if (type.equalsIgnoreCase("command")) {
                // Command execution logic. Note: Server-side execution usually
                // requires handling PlayerCustomClickEvent with this key.
                builder.action(DialogAction.customClick(Key.key("dialogmenus:button_click"), null));
            } else if (type.equalsIgnoreCase("url")) {
                // builder.action(DialogAction.openUrl(value));
            } else if (type.equalsIgnoreCase("clipboard")) {
                // builder.action(DialogAction.copyToClipboard(value));
            }
        }

        return builder.build();
    }
}
