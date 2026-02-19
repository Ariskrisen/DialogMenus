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

        for (Map<String, Object> action : actions) {
            String type = (String) action.get("type");
            String value = (String) action.get("value");

            if (type == null || value == null)
                continue;

            if (type.equalsIgnoreCase("command")) {
                String cmd = parseActionValue(player, value);
                Bukkit.getScheduler().runTask(plugin, () -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                });
            } else if (type.equalsIgnoreCase("message")) {
                String msg = parseActionValue(player, value);
                player.sendMessage(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(msg));
            } else if (type.equalsIgnoreCase("url")) {
                String url = parseActionValue(player, value);
                player.sendMessage(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(
                        "<click:open_url:'" + url + "'><underlined><blue>" + url + "</blue></underlined></click>"));
            } else if (type.equalsIgnoreCase("close")) {
                Bukkit.getScheduler().runTask(plugin, player::closeDialog);
            } else if (type.equalsIgnoreCase("open")) {
                String nextMenu = parseActionValue(player, value);
                org.bukkit.configuration.file.YamlConfiguration menuConfig = plugin.getMenuManager().getMenu(nextMenu);
                if (menuConfig != null) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        io.papermc.paper.dialog.Dialog dialog = dialogmenus.util.MenuLoader.buildDialog(plugin,
                                menuConfig, player);
                        player.showDialog(dialog);
                    });
                } else {
                    player.sendMessage(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(
                            "<red>Menu not found: " + nextMenu));
                }
            }
        }
    }

    private String parseActionValue(Player player, String value) {
        String parsed = value.replace("<player>", player.getName());
        if (plugin.isPlaceholderApiEnabled()) {
            parsed = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, parsed);
        }
        return parsed;
    }
}
