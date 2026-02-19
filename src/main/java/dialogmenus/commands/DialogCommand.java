package dialogmenus.commands;

import io.papermc.paper.dialog.Dialog;
import dialogmenus.DialogMenus;
import dialogmenus.util.MenuLoader;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DialogCommand implements CommandExecutor {

    private final DialogMenus plugin;

    public DialogCommand(DialogMenus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /dialogs <reload|open> [player] [menu]");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("dialogmenus.reload")) {
                sender.sendMessage("§cNo permission.");
                return true;
            }
            plugin.getMenuManager().loadMenus();
            sender.sendMessage("§aMenus reloaded!");
            return true;
        }

        if (args[0].equalsIgnoreCase("open")) {
            if (!sender.hasPermission("dialogmenus.open")) {
                sender.sendMessage("§cNo permission.");
                return true;
            }

            if (args.length < 3) {
                sender.sendMessage("§cUsage: /dialogs open <player> <menu>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("§cPlayer not found.");
                return true;
            }

            String menuName = args[2].toLowerCase();
            YamlConfiguration menuConfig = plugin.getMenuManager().getMenu(menuName);

            if (menuConfig == null) {
                sender.sendMessage("§cMenu not found: " + menuName);
                return true;
            }

            try {
                Dialog dialog = MenuLoader.buildDialog(menuConfig);
                target.showDialog(dialog);
                sender.sendMessage("§aOpened menu " + menuName + " for " + target.getName());
            } catch (Exception e) {
                sender.sendMessage("§cError building dialog: " + e.getMessage());
                plugin.getLogger().severe("Error building dialog " + menuName + ": " + e.getMessage());
                e.printStackTrace();
            }

            return true;
        }

        return false;
    }
}
