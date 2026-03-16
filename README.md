# DialogMenus

**DialogMenus** — это мощный и легкий в настройке плагин для Minecraft 1.21.10+, который позволяет создавать внутриигровые меню с использованием нативного **Paper Dialogs API**. Больше никаких сундуков-интерфейсов — только современные, плавные диалоги!


## 📚 Вики и Документация
Подробную информацию о настройке, командах и примерах вы найдете в нашей Вики:
👉 **[Читать DialogMenus Wiki](https://ariskrisen.github.io/Docs/docs/DialogMenus/intro)**

## 🚀 Особенности
- **Настройка через YAML**: Создавайте меню, просто добавляя файлы в папку `menus/`.
- **MiniMessage**: Полная поддержка современных градиентов и форматирования текста (например, `<gold><bold>Заголовок`).
- **Два типа диалогов**: 
  - `notice`: Обычное информационное окно с одной кнопкой.
  - `confirmation`: Окно подтверждения с кнопками "Да" и "Нет".
  - `multi-action`: Меню с любым количеством кнопок.
- **Интерактивные поля (Inputs)**: Добавляйте текстовые поля и ползунки для ввода чисел.
- **Действия кнопок**: Поддержка цепочек действий (команды, сообщения, ссылки, закрытие окон).
- **Динамическое тело**: Добавляйте неограниченное количество текстовых сообщений и предметов в тело диалога.
- **Интеграция с PlaceholderAPI**: Используйте любые плейсхолдеры в текстах, заголовках и командах.

---

## 🛠 Установка
1. Скачайте/скомпилируйте `DialogMenus.jar`.
2. Поместите его в папку `plugins` вашего сервера Paper 1.21.10+.
3. Запустите сервер. Плагин автоматически создаст папку `plugins/DialogMenus/menus/` с примером.

---

## 📂 Настройка меню
Каждое меню — это отдельный файл `.yml` в папке `menus/`.

### Пример: `example.yml`
```yaml
title: "<gold><bold>Главное Меню"
can-close-with-escape: true
type: notice
body:
  text1:
    type: text
    content: "<gray>Добро пожаловать на наш сервер!"
  text2:
    type: text
    content: "<white>Это меню настроено через YAML."
  item1:
    type: item
    material: NETHERITE_SWORD
    name: "<red>Меч Истины"
button:
  text: "<green>Закрыть"
```

### Пример: `confirm.yml` (Окно подтверждения)
```yaml
title: "<red>Внимание!"
type: confirmation
body:
  msg:
    type: text
    content: "Вы действительно хотите телепортироваться?"
yes-button:
  text: "<green>Да"
no-button:
  text: "<red>Нет"
```

### Пример: `multi.yml` (Сложное меню с цепочкой действий)
```yaml
title: "<gradient:gold:yellow>Сложное Меню"
type: multi-action
body:
  info:
    type: text
    content: "Выберите действие и получите награду, %player_name%!"
inputs:
  amount:
    type: number-range
    label: "Количество"
    min: 1
    max: 64
    initial: 1
buttons:
  reward:
    text: "<gold>Получить Алмаз"
    hover: "Нажмите, чтобы получить награду"
    action:
      - type: command
        value: "give %player_name% diamond 1"
      - type: message
        value: "<green>Вы получили алмаз!"
      - type: close
  cancel:
    text: "<red>Отмена"
    action:
      type: close
```

---

## ⌨️ Команды и Алиасы
Основная команда: `/dialogmenus`
Алиасы: `/dim`

| Команда | Описание | Пермишен |
| :--- | :--- | :--- |
| `/dim reload` | Перезагрузить файлы меню | `dialogmenus.reload` |
| `/dim open <игрок> <меню>` | Открыть меню для игрока | `dialogmenus.open` |

---

## 🔐 Права (Permissions)
- `dialogmenus.admin`: Полный доступ ко всем командам (по умолчанию у OP).
- `dialogmenus.reload`: Доступ к перезагрузке конфигов.
- `dialogmenus.open`: Доступ к открытию меню игрокам.

---

## ⚙️ Синтаксис YAML

| Параметр | Описание | Варианты |
| :--- | :--- | :--- |
| `title` | Заголовок диалога (MiniMessage) | Строка |
| `type` | Тип окна | `notice`, `confirmation` |
| `can-close-with-escape` | Закрытие на ESC | `true`, `false` |
| `body` | Список элементов в центре | Раздел с элементами |
| `body.[id].type` | Тип элемента тела | `text`, `item` |
| `body.[id].content` | Текст (для типа `text`) | Строка |
| `body.[id].material` | ID предмета (для типа `item`) | [Material Enum](https://jd.papermc.io/paper/1.21/org/bukkit/Material.html) |
| `button` | Кнопка (для `notice`) | Раздел с `text` |
| `yes-button` | Кнопка подтверждения | Раздел с `text` |
| `no-button` | Кнопка отказа | Раздел с `text` |
| `buttons` | Список кнопок (для `multi-action`) | Список разделов |
| `inputs` | Поля ввода | Раздел с параметрами ввода |
| `action` | Действие кнопки | Раздел с `type` и `value` |

### Типы ввода (Inputs)
- `text`: Текстовое поле.
- `number-range`: Ползунок (слайдер). Параметры: `min`, `max`, `initial`, `step` (по умолчанию `1.0` для целых чисел).

### Действия (Actions)
- `command`: Выполняет консольную команду (поддерживает `<player>` и плейсхолдеры).
- `message`: Отправляет MiniMessage сообщение игроку.
- `url`: Отправляет интерактивную ссылку в чат.
- `close`: Закрывает текущий диалог.
- `open`: Открывает другой диалог по его имени (например, `open confirm`).

---
