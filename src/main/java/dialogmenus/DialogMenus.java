package dialogmenus;

import dialogmenus.commands.DialogCommand;
import dialogmenus.manager.MenuManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class DialogMenus extends JavaPlugin {

    private MenuManager menuManager;
    private boolean placeholderApiEnabled;

    @Override
    public void onEnable() {

        File menusFolder = new File(getDataFolder(), "menus");
        if (!menusFolder.exists()) {
            menusFolder.mkdirs();
            saveResource("menus/example.yml", false);
        }

        this.menuManager = new MenuManager(this, menusFolder);
        this.menuManager.loadMenus();

        this.placeholderApiEnabled = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
        if (placeholderApiEnabled) {
            getLogger().info("PlaceholderAPI integration enabled!");
        }

        getServer().getPluginManager().registerEvents(new ActionHandler(this), this);

        DialogCommand cmd = new DialogCommand(this);
        getCommand("dialogmenus").setExecutor(cmd);
        getCommand("dialogmenus").setTabCompleter(cmd);

        getLogger().info("DialogMenus has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("DialogMenus has been disabled!");
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }

    public boolean isPlaceholderApiEnabled() {
        return placeholderApiEnabled;
    }
}
