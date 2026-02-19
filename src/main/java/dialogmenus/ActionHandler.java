package dialogmenus;

import io.papermc.paper.connection.PlayerGameConnection;
import io.papermc.paper.event.player.PlayerCustomClickEvent;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Map;

public class ActionHandler implements Listener {

    private final DialogMenus plugin;

    public ActionHandler(DialogMenus plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCustomClick(PlayerCustomClickEvent event) {
        Key identifier = event.getIdentifier();
        if (!identifier.namespace().equals("dialogmenus"))
            return;

        if (!(event.getCommonConnection() instanceof PlayerGameConnection gameConn))
            return;

        Player player = gameConn.getPlayer();

        List<Map<String, Object>> actions = plugin.getMenuManager().getActions(identifier.value());
        if (actions == null)
            return;

        io.papermc.paper.dialog.DialogResponseView responseView = event.getDialogResponseView();
        for (Map<String, Object> action : actions) {
            String type = (String) action.get("type");
            String value = (String) action.get("value");

            if (type == null)
                continue;

            if (type.equalsIgnoreCase("command")) {
                Bukkit.getScheduler().runTask(plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        parseActionValue(player, value, responseView)));
            } else if (type.equalsIgnoreCase("message")) {
                player.sendMessage(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage()
                        .deserialize(parseActionValue(player, value, responseView)));
            } else if (type.equalsIgnoreCase("url")) {
                String url = parseActionValue(player, value, responseView);
                player.sendMessage(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(
                        "<click:open_url:'" + url + "'><gray>Click to open: <underlined><blue>" + url
                                + "</blue></underlined></click>"));
            } else if (type.equalsIgnoreCase("clipboard")) {
                String text = parseActionValue(player, value, responseView);
                player.sendMessage(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(
                        "<click:copy_to_clipboard:'" + text + "'><gray>Click to copy: <gold>" + text
                                + "</gold></click>"));
            } else if (type.equalsIgnoreCase("close")) {
                Bukkit.getScheduler().runTask(plugin, player::closeDialog);
            } else if (type.equalsIgnoreCase("open")) {
                String nextMenu = parseActionValue(player, value, responseView);
                org.bukkit.configuration.file.YamlConfiguration menuConfig = plugin.getMenuManager()
                        .getMenu(nextMenu);
                if (menuConfig != null) {
                    try {
                        io.papermc.paper.dialog.Dialog dialog = dialogmenus.util.MenuLoader.buildDialog(plugin,
                                menuConfig, player);
                        Bukkit.getScheduler().runTask(plugin, () -> player.showDialog(dialog));
                    } catch (Exception e) {
                        player.sendMessage("§cError opening menu: " + e.getMessage());
                    }
                } else {
                    player.sendMessage("§cMenu not found: " + nextMenu);
                }
            }
        }
    }

    private String parseActionValue(Player player, String value,
            io.papermc.paper.dialog.DialogResponseView responseView) {
        if (value == null)
            return "";
        String parsed = value.replace("<player>", player.getName());

        if (responseView != null) {
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("<input:([a-zA-Z0-9_-]+)>");
            java.util.regex.Matcher matcher = pattern.matcher(parsed);
            StringBuilder sb = new StringBuilder();
            while (matcher.find()) {
                String key = matcher.group(1);
                String val = "";

                String textVal = responseView.getText(key);
                if (textVal != null) {
                    val = textVal;
                } else {
                    Float floatVal = responseView.getFloat(key);
                    if (floatVal != null) {
                        val = String.valueOf(floatVal);
                    } else {
                        Boolean boolVal = responseView.getBoolean(key);
                        if (boolVal != null) {
                            val = String.valueOf(boolVal);
                        }
                    }
                }
                matcher.appendReplacement(sb, java.util.regex.Matcher.quoteReplacement(val));
            }
            matcher.appendTail(sb);
            parsed = sb.toString();
        }

        if (plugin.isPlaceholderApiEnabled()) {
            parsed = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, parsed);
        }
        return parsed;
    }
}
