# Примеры меню (Examples)

Ниже приведены примеры различных типов меню, которые вы можете использовать как шаблоны.

## 🎁 Ежедневная награда (Notice + PAPI)
**Файл:** `daily.yml`
```yaml
title: "<gold>Награда за вход"
type: notice
body:
  msg:
    type: text
    content: "Привет, %player_name%! Твой текущий баланс: <green>%vault_balance%$"
  reward_icon:
    type: item
    material: GOLD_INGOT
    name: "<yellow>Твой ежедневный бонус"
button:
  text: "<green>Забрать и закрыть"
  action:
    - type: command
      value: "eco give %player_name% 100"
    - type: message
      value: "<green>Вы получили 100$!"
    - type: close
```

## ⚔️ Выбор арены (Multi-Action)
**Файл:** `arenas.yml`
```yaml
title: "<red>Выбор Арены"
type: multi-action
body:
  info:
    type: text
    content: "Выберите место для сражения:"
buttons:
  arena_1:
    text: "<yellow>Лес"
    hover: "Сложность: Легко"
    action:
      - type: command
        value: "tp %player_name% 100 64 100"
      - type: close
  arena_2:
    text: "<red>Ад"
    hover: "Сложность: ВЫСОКАЯ"
    action:
      - type: command
        value: "tp %player_name% 500 64 500"
      - type: close
  cancel:
    text: "<gray>Назад"
    action:
      type: open
      value: "main_menu"
```

## 🛒 Подтверждение покупки (Confirmation)
**Файл:** `buy_confirm.yml`
```yaml
title: "<yellow>Подтверждение"
type: confirmation
body:
  text:
    type: text
    content: "Вы действительно хотите купить этот меч за <green>500$?"
yes-button:
  text: "<green>Купить!"
  action:
    - type: command
      value: "eco take %player_name% 500"
    - type: command
      value: "give %player_name% diamond_sword 1"
    - type: close
no-button:
  text: "<red>Отмена"
  action:
    type: close
```
