package dialogmenus.util;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

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
                        if (material != null) {
                            ItemStack item = new ItemStack(material);
                            String itemName = itemSection.getString("name");
                            if (itemName != null) {
                                ItemMeta meta = item.getItemMeta();
                                meta.displayName(mm.deserialize(itemName));
                                item.setItemMeta(meta);
                            }
                            // Documentation implies plainMessage returns the body,
                            // but item() returns a builder in 1.21.10
                            bodies.add(DialogBody.item(item).build());
                        }
                    }
                }
                baseBuilder.body(bodies);
            }

            builder.base(baseBuilder.build());

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
            }
        });
    }

    private static ActionButton parseButton(ConfigurationSection section) {
        Component text = mm.deserialize(section.getString("text", "OK"));
        return ActionButton.builder(text).build();
    }
}
