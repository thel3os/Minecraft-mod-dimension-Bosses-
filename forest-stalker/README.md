# Forest Stalker — мод для Minecraft 1.12.2 (Forge)

Мод добавляет в леса **Лесного стража** (`foreststalker:stalker`) — огромного
горбатого босса, который носит на спине целую рощу и швыряет деревьями.
С него падает **Древесный меч**, которым тоже можно метать деревья.

## Что внутри

| Объект | Идентификатор | Описание |
|---|---|---|
| Лесной страж | `foreststalker:stalker` | **200 HP (100 сердец)**, 16 урона в ближнем бою, деревья на спине, полоса здоровья босса |
| Древесный меч | `foreststalker:stalker_blade` | 30 урона в ближнем бою, **ПКМ — метнуть дерево** (10 урона = 5 сердец) |
| Летающее дерево | `foreststalker:thrown_tree` | Снаряд босса и меча |
| Яйцо призыва | `foreststalker:stalker_egg` | Для тестов и карт — ПКМ по блоку |

### Босс

- **Огромный и горбатый**: высота ~4.6 блока, на спине растут три дерева.
- **200 HP** и **полоса здоровья босса** сверху экрана для игроков в радиусе 64 блоков.
- **Метает деревья** в игрока с дистанции 4.5–32 блока (раз в 4 секунды по умолчанию).
  Одно дерево снимает **10 HP (5 сердец)** и отбрасывает цель.
- В ближнем бою наносит 16 урона (8 сердец) и с шансом 35% накладывает замедление.
- Устойчив к отбрасыванию, имеет 10 брони и 6 твёрдости брони, 200 очков опыта.
- Спавнится **только в лесных биомах** (Biome Dictionary, тип `FOREST`), по одному.
- Не исчезает сам по себе — это босс.

### Древесный меч

- **30 урона** в ближнем бою (настраивается, как и раньше, через `bladeDamage`).
- **ПКМ** — бросает дерево вперёд. Урон попадания — **10 (5 сердец)**, перезарядка 1.25 с.
- Падает с босса (по умолчанию — всегда).
- Не крафтится: единственный способ получить — убить Лесного стража.

### Команда для админов

```
/stalkerinfo
```

Показывает HP, урон в ближнем бою, урон дерева и перезарядку ближайшего стража
(в радиусе 256 блоков). Удобно для проверки конфига на сервере.

## Настройки

После первого запуска появится файл `config/foreststalker.cfg`:

```ini
spawning {
    # Вес спавна в лесах (у зомби 100). 0 — отключить естественный спавн.
    I:spawnWeight=8
    I:minGroupSize=1
    I:maxGroupSize=1
}

combat {
    # Здоровье босса (200 = 100 сердец)
    D:bossHealth=200.0
    # Урон в ближнем бою (16 = 8 сердец)
    D:meleeDamage=16.0
    # Урон одного брошенного дерева (10 = 5 сердец)
    D:treeDamage=10.0
    # Перезарядка метания у босса, в тиках (80 = 4 секунды)
    I:bossThrowCooldown=80
    # Перезарядка метания у игрока, в тиках (25 = 1.25 секунды)
    I:playerThrowCooldown=25
}

gear {
    # Урон Древесного меча в ближнем бою
    D:bladeDamage=30.0
    # Шанс выпадения меча с трупа (0..1)
    D:bladeDropChance=1.0
    # Шанс замедления при ударе (0..1)
    D:slownessChance=0.35
}
```

## Сборка

Нужен **JDK 8** (Forge 1.12.2 не собирается на Java 17+). ForgeGradle скачает
Minecraft и MCP-маппинги при первой сборке.

```bat
build.bat
```

Готовый мод: `dist/forest-stalker-1.12.2-2.0.0.jar`
(сборка кладёт его в `build/libs/` и копирует в `dist/` через `build.bat`).

Скрипт берёт JDK 8 из `.\jdk8\bin\java.exe`, если такая папка есть, иначе из `JAVA_HOME`.

Запуск тестового клиента с модом:

```bat
gradlew.bat runClient
```

## Установка

1. Установите Forge для Minecraft **1.12.2** (проект собран против `14.23.5.2860`).
2. Скопируйте `dist/forest-stalker-1.12.2-2.0.0.jar` в папку `.minecraft/mods`.
3. Запускайте игру на **Java 8**.

## Структура проекта

```
src/main/java/com/foreststalker/
  ForestStalkerMod.java               — точка входа, @Mod
  ModConfig.java                      — конфиг
  ModItems.java                       — предметы + креативная вкладка
  ModEntities.java                    — регистрация моба, снаряда и спавна
  command/CommandStalkerInfo.java     — команда /stalkerinfo
  entity/EntityStalker.java           — босс: 200 HP, босс-бар, метание
  entity/EntityThrownTree.java        — летящее дерево (урон 10)
  entity/ai/EntityAITreeThrow.java    — ИИ метания деревьев
  item/ItemStalkerBlade.java          — Древесный меч (30 урона + ПКМ)
  item/ItemStalkerEgg.java            — яйцо призыва
  client/model/ModelForestStalker.java— горбатая модель с деревьями
  client/model/ModelThrownTree.java   — модель летящего дерева
  client/render/RenderStalker.java
  client/render/RenderThrownTree.java
  proxy/CommonProxy.java, ClientProxy.java
src/main/resources/assets/foreststalker/
  lang/en_us.lang, ru_ru.lang
  models/item/*.json
  textures/items/*.png, textures/entity/stalker.png
tools/make-textures.ps1               — генератор всех PNG (можно менять и перегенерировать)
```

## Как расширять

- **Больше деревьев на спине** — добавьте элементы в массив `treePositions` в `ModelForestStalker`.
- **Другой урон/HP** — правьте `config/foreststalker.cfg`, код менять не нужно.
- **Новые биомы** — в `ModEntities#registerSpawns` добавьте ещё типы, например `BiomeDictionary.Type.SWAMP`.
- **Крафт меча** — зарегистрируйте `ShapedRecipes` в `CraftingHelper` (см. `ModItems`).
- **Новые снаряды** — по образцу `EntityThrownTree` + регистрация в `ModEntities`.
