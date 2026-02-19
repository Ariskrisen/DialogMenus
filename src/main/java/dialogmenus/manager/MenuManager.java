package dialogmenus.manager;

import io.papermc.paper.dialog.Dialog;
import dialogmenus.DialogMenus;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class MenuManager {

    private final DialogMenus plugin;
    private final File menusFolder;
    private final Map<String, YamlConfiguration> menus = new HashMap<>();

    public MenuManager(DialogMenus plugin, File menusFolder) {
        this.plugin = plugin;
        this.menusFolder = menusFolder;
    }

    public void loadMenus() {
        menus.clear();
        File[] files = menusFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null)
            return;

        for (File file : files) {
            String name = file.getName().replace(".yml", "");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            menus.put(name.toLowerCase(), config);
            plugin.getLogger().info("Loaded menu: " + name);
        }
    }

    public Map<String, YamlConfiguration> getMenus() {
        return menus;
    }

    public YamlConfiguration getMenu(String name) {
        return menus.get(name.toLowerCase());
    }
}
