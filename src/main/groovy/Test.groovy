// 到第九行的这一部分完全可以删除，但是删掉之后idea没有补全
import com.ilummc.tlib.scripting.scriptapi.GroovyPluginApi
import com.ilummc.tlib.scripting.scriptapi.TabooScriptingApi
import org.bukkit.Bukkit
def plugin = new GroovyPluginApi(null)
def api = new TabooScriptingApi()
def bukkit = Bukkit.getServer()
// 下面的就按需import/删除

import org.bukkit.event.player.AsyncPlayerChatEvent

plugin.onEnable {
    listen { AsyncPlayerChatEvent event ->
        if (event.message == 'test')
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
        bukkit.getPlayer 'xxx'
        command 'say hello'
    }
    listen('AsyncPlayerChatEvent') {
        if (it.message == 'test2')
            it.player.sendMessage 'No type register'
    }
}

plugin.description {
    name 'Test'
    version '1.0'
    depend 'TabooLib', 'Vault'
}
