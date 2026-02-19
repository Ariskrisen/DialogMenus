package dialogmenus.util;

import dialogmenus.DialogMenus;
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

    public static Dialog buildDialog(DialogMenus plugin, ConfigurationSection config, org.bukkit.entity.Player player) {
        return Dialog.create(factory -> {
            var builder = factory.empty();

            // Base
            String titleStr = parseText(plugin, player, config.getString("title", "Dialog"));
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
                        String content = parseText(plugin, player, itemSection.getString("content", ""));
                        bodies.add(DialogBody.plainMessage(mm.deserialize(content)));
                    } else if (type.equalsIgnoreCase("item")) {
                        Material material = Material.matchMaterial(itemSection.getString("material", "STONE"));
                        if (material != null && material.isItem()) {
                            ItemStack item = new ItemStack(material);
                            String itemName = itemSection.getString("name");
                            if (itemName != null) {
                                ItemMeta meta = item.getItemMeta();
                                meta.displayName(mm.deserialize(parseText(plugin, player, itemName)));
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
                    Component label = mm.deserialize(parseText(plugin, player, inputConfig.getString("label", key)));

                    if (type.equalsIgnoreCase("text")) {
                        inputs.add(DialogInput.text(key, label).build());
                    } else if (type.equalsIgnoreCase("number-range")) {
                        float min = (float) inputConfig.getDouble("min", 0.0);
                        float max = (float) inputConfig.getDouble("max", 100.0);
                        float initial = (float) inputConfig.getDouble("initial", min);
                        float step = (float) inputConfig.getDouble("step", 1.0);
                        inputs.add(DialogInput.numberRange(key, label, min, max)
                                .initial(initial)
                                .step(step)
                                .build());
                    }
                }
                baseBuilder.inputs(inputs);
            }

            builder.base(baseBuilder.build());

            // Type
            String typeStr = config.getString("type", "notice");
            if (typeStr.equalsIgnoreCase("notice")) {
                ConfigurationSection buttonSection = config.getConfigurationSection("button");
                if (buttonSection != null) {
                    builder.type(DialogType.notice(parseButton(plugin, player, buttonSection)));
                } else {
                    builder.type(DialogType.notice());
                }
            } else if (typeStr.equalsIgnoreCase("confirmation")) {
                ConfigurationSection yesSection = config.getConfigurationSection("yes-button");
                ConfigurationSection noSection = config.getConfigurationSection("no-button");
                if (yesSection != null && noSection != null) {
                    builder.type(DialogType.confirmation(parseButton(plugin, player, yesSection),
                            parseButton(plugin, player, noSection)));
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
                            buttons.add(parseButton(plugin, player, btnConfig));
                        }
                    }
                    builder.type(DialogType.multiAction(buttons).build());
                } else {
                    builder.type(DialogType.notice()); // Fallback
                }
            } else {
                builder.type(DialogType.notice());
            }
        });
    }

    private static String parseText(DialogMenus plugin, org.bukkit.entity.Player player, String text) {
        if (text == null)
            return "";
        if (plugin.isPlaceholderApiEnabled()) {
            return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, text);
        }
        return text;
    }

    private static ActionButton parseButton(DialogMenus plugin, org.bukkit.entity.Player player,
            ConfigurationSection section) {
        Component text = mm.deserialize(parseText(plugin, player, section.getString("text", "OK")));
        ActionButton.Builder builder = ActionButton.builder(text);

        String hoverStr = section.getString("hover");
        if (hoverStr != null) {
            builder.tooltip(mm.deserialize(parseText(plugin, player, hoverStr)));
        }

        if (section.contains("action")) {
            List<Map<String, Object>> actionsList = new ArrayList<>();

            if (section.isList("action")) {
                for (Map<?, ?> map : section.getMapList("action")) {
                    actionsList.add((Map<String, Object>) map);
                }
            } else if (section.isConfigurationSection("action")) {
                ConfigurationSection actionSection = section.getConfigurationSection("action");
                actionsList.add(actionSection.getValues(false));
            }

            if (!actionsList.isEmpty()) {
                String actionKey = "btn_" + java.util.UUID.randomUUID().toString();
                plugin.getMenuManager().registerActions(actionKey, actionsList);
                builder.action(DialogAction.customClick(Key.key("dialogmenus", actionKey), null));
            }
        }

        return builder.build();
    }
}
