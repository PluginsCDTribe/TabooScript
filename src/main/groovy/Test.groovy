
plugin.onEnable {
    // 两种监听方式
    listen { org.bukkit.event.player.AsyncPlayerChatEvent event ->
        if (event.message == 'test')
            // 给玩家物品
            event.player.inventory.addItem api.item {
                type 'POTION'
                enchant LUCK: 1, DAMAGE: 2
                name '鸡毛'
                lore '狗毛', '皮蛇'
                unbreakable true
                potionEffect {
                    type 'HARM'
                    level 1
                    duration 100
                    color 'RED'
                    particle false
                }
            }
    }

    listen('AsyncPlayerChatEvent') {
        if (it.message == 'test2')
            it.player.sendMessage 'No type register'
    }
}

plugin.description {
    name 'Test'
    version '1.0'
}
