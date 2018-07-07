
plugin.onEnable {
    onCommand('example') { sender, String[] args ->
        if (args.length > 0) {
            switch (args[0]) {
                case 'version':
                    sender.sendMessage 'Commands 插件，版本 1.0', '在 http://example.com 下载更新'
                    break
                case 'tp':
                    if (bukkit.getPlayer(args[1]) != null) {
                        sender.teleport bukkit.getPlayer(args[1])
                    }
                    break
            }
        }
    }
}

plugin.description {
    name 'Commands'
}