# DialogMenus

**DialogMenus** is a powerful and easy-to-configure plugin for Minecraft 1.21.10+ that allows you to create in-game menus using the native **Paper Dialogs API**. No more chest-like interfaces – just modern, fluid dialogs!

## 📚 Wiki and Documentation
For detailed information on setup, commands, and examples, check out our Wiki:
👉 **[Read DialogMenus Wiki](https://ariskrisen.github.io/Docs/docs/DialogMenus/intro)**

## 🚀 Features
- **Configuration via YAML**: Create menus by simply adding files to the `menus/` folder.
- **MiniMessage**: Full support for modern gradients and text formatting (e.g. `<gold><bold>Title`).
- **Two types of dialogs**:
- `notice`: A standard information window with one button.
- `confirmation`: A confirmation window with "Yes" and "No" buttons.
- `multi-action`: A menu with any number of buttons.
- **Interactive fields (Inputs)**: Add text fields and sliders for entering numbers.
- **Button actions**: Support for action chains (commands, messages, links, closing windows).
- **Dynamic body**: Add an unlimited number of text messages and items to the dialog body.
- **PlaceholderAPI integration**: Use any placeholders in texts, titles, and commands.

---

## 🛠 Installation
1. Download and compile `DialogMenus.jar`.
2. Place it in the `plugins` folder of your Paper 1.21.10+ server.
3. Start the server. The plugin will automatically create the `plugins/DialogMenus/menus/` folder with an example.

---

## 📂 Configuring the Menu
Each menu is a separate `.yml` file in the `menus/` folder.

### Example: `example.yml`
``yaml
title: "<gold><bold>Main Menu"
can-close-with-escape: true
type: notice
body:
text1:
type: text
content: "<gray>Welcome to our server!"
text2:
type: text
content: "<white>This menu is configured via YAML."
item1:
type: item
material: NETHERITE_SWORD
name: "<red>Sword of Truth"
button:
text: "<green>Close"
```

### Example: `confirm.yml` (Confirmation Window)
```yaml
title: "<red>Attention!"
type: confirmation
body:
msg:
type: text
content: "Are you sure you want to teleport?"
yes-button:
text: "<green>Yes"
no-button:
text: "<red>No"
``

### Example: `multi.yml` (Complex Menu with Action Chain)
```yaml
title: "<gradient:gold:yellow>Complex Menu"
type: multi-action
body:
info:
type: text
content: "Choose an action and get a reward, %player_name%!"
Inputs:
Amount:
Type: Number-Range
Label: "Amount"
Min: 1
Max: 64
Initial: 1
Buttons:
Reward:
Text: "<gold>Get Diamond"
Hover: "Click to get reward"
Action:
- Type: Command
Value: "Give %player_name% diamond 1"
- Type: Message
Value: "<green>You got a diamond!"
- Type: Close
Cancel:
Text: "<red>Cancel"
Action:
Type: Close
```

---

## ⌨️ Commands and Aliases
Main command: `/dialogmenus`
Aliases: `/dim`

| Command | Description | Permission |
| :--- | :--- | :--- |
| `/dim reload` | Reload menu files | `dialogmenus.reload` |
| `/dim open <player> <menu>` | Open the menu for a player | `dialogmenus.open` |

---

## 🔐 Permissions
- `dialogmenus.admin`: Full access to all commands (default for OP).
- `dialogmenus.reload`: Access to reload configs.
- `dialogmenus.open`: Access to open the menu for players.

---

## ⚙️ YAML Syntax

| Parameter | Description | Options |
| :--- | :--- | :--- |
| `title` | Dialog title (MiniMessage) | String |
| `type` | Window type | `notice`, `confirmation` |
| `can-close-with-escape` | Close with ESC | `true`, `false` |
| `body` | List of elements in the center | Section with elements |
| `body.[id].type` | Body element type | `text`, `item` |
| `body.[id].content` | Text (for type `text`) | String |
| `body.[id].material` | Item ID (for type `item`) | [Material Enum](https://jd.papermc.io/paper/1.21/org/bukkit/Material.html) |
| `button` | Button (for `notice`) | Section with `text` |
| `yes-button` | Confirm button | Section with `text` |
| `no-button` | Cancel button | Section with `text` |
| `buttons` | List of buttons (for `multi-action`) | List of sections |
| `inputs` | Input fields | Section with input parameters |
| `action` | Button action | Section with `type` and `value` |

### Inputs
- `text`: Text field.
- `number-range`: Slider. Parameters: `min`, `max`, `initial`, `step` (default: `1.0` for integers).

### Actions
- `command`: Executes a console command (supports `<player>` and placeholders).
- `message`: Sends a MiniMessage message to the player.
- `url`: Sends a clickable link to the chat.
- `close`: Closes the current dialog.
- `open`: Opens another dialog by name (e.g., `open confirm`).

---
