以下为 TabooScript 内置的 API

## ItemStack 构建

```groovy
ItemStack item = api.item {
    type 'POTION'
    enchant LUCK: 1, DAMAGE_ALL: 2
    name '物品名'
    lore 'lore1', 'lore2'
    unbreakable true
    potionEffect {
        type 'HARM'
        level 1
        duration 100
        color 'RED'
        particle false
    }
}
```

## 药水效果创建

```groovy
def player = bukkit.getPlayer 'name'
PotionEffect effect = api.effect {
    type 'SPEED'
    level 1
    duration 30
}
effect.apply player
```
