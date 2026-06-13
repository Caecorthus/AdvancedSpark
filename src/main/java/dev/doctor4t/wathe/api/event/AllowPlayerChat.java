package dev.doctor4t.wathe.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.network.ClientPlayerEntity;

import static net.fabricmc.fabric.api.event.EventFactory.createArrayBacked;

/**
 * English: Client-side override hook for Spark-wathe chat suppression.
 * Chinese: Spark-wathe 聊天屏蔽的客户端覆盖钩子。
 */
public interface AllowPlayerChat {
    Event<AllowPlayerChat> EVENT = createArrayBacked(AllowPlayerChat.class, listeners -> player -> {
        for (AllowPlayerChat listener : listeners) {
            if (listener.allowChat(player)) {
                return true;
            }
        }
        return false;
    });

    boolean allowChat(ClientPlayerEntity player);
}
