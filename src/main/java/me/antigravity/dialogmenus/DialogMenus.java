package me.antigravity.dialogmenus;

import me.antigravity.dialogmenus.commands.DialogCommand;
import me.antigravity.dialogmenus.manager.MenuManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class DialogMenus extends JavaPlugin {

    private MenuManager menuManager;

    @Override
    public void onEnable() {

        File menusFolder = new File(getDataFolder(), "menus");
        if (!menusFolder.exists()) {
            menusFolder.mkdirs();
            saveResource("menus/example.yml", false);
        }

        this.menuManager = new MenuManager(this, menusFolder);
        this.menuManager.loadMenus();

        getCommand("dialogmenus").setExecutor(new DialogCommand(this));

        getLogger().info("DialogMenus has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("DialogMenus has been disabled!");
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }
}
