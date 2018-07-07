
plugin.onEnable {
    listen('PlayerJoinEvent') {
        def name = it.player.displayName
        // 公告
        broadcast "玩家 ${name} 加入服务器"
        bukkit.onlinePlayers.forEach {
            actionbar it, "玩家 ${name} 加入服务器"
            title it, "玩家 ${name} 加入服务器"
        }
        // 给进服玩家药水效果
        api.effect {
            type 'SPEED'
            level 1
            duration 30
        } apply it.palyer
    }
}

plugin.description {
    name 'JoinEffect'
}

