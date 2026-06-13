package io.github.caecorthus.advancedspark.mixin;

import dev.doctor4t.wathe.cca.PlayerShopComponent;
import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.util.ShopEntry;
import io.github.caecorthus.advancedspark.api.event.ShopEvents;
import io.github.caecorthus.advancedspark.bridge.AdvancedSparkShopEntries;
import io.github.caecorthus.advancedspark.bridge.AdvancedSparkShopBridge;
import io.github.caecorthus.advancedspark.bridge.AdvancedSparkShopStateAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * English: Bridges Wathe shop purchase attempts before vanilla purchase logic runs.
 * Chinese: 在原版购买逻辑运行前桥接 Wathe 商店购买尝试。
 */
@Mixin(PlayerShopComponent.class)
public abstract class PlayerShopComponentPurchaseBridgeMixin implements AdvancedSparkShopStateAccess {
    @Shadow
    @Final
    private PlayerEntity player;

    @Shadow
    public abstract void sync();

    @Unique
    private final Map<String, Integer> advancedspark$cooldowns = new HashMap<>();
    @Unique
    private final Map<String, Integer> advancedspark$stock = new HashMap<>();
    @Unique
    private final Map<String, Integer> advancedspark$maxStock = new HashMap<>();
    @Unique
    private final Set<String> advancedspark$initializedEntries = new HashSet<>();

    @Inject(method = "tryBuy(I)V", at = @At("HEAD"), cancellable = true)
    private void advancedspark$beforePurchase(int index, CallbackInfo ci) {
        if (!(this.player instanceof ServerPlayerEntity serverPlayer)) {
            return;
        }
        List<ShopEntry> entries = AdvancedSparkShopBridge.buildShopEntries(this.player, GameConstants.SHOP_ENTRIES);
        this.advancedspark$initializeEntryState(entries);
        if (index < 0 || index >= entries.size()) {
            return;
        }

        ShopEntry entry = entries.get(index);
        if (this.advancedspark$isOnCooldown(entry) || !this.advancedspark$isInStock(entry)) {
            ci.cancel();
            return;
        }
        Identifier itemId = this.advancedspark$itemId(entry);
        ActionResult result = AdvancedSparkShopBridge.dispatchBeforePurchase(serverPlayer, itemId);
        if (ShopEvents.shouldCancelVanillaPurchase(result)) {
            ci.cancel();
        }
    }

    @Redirect(method = "tryBuy(I)V", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I"))
    private int advancedspark$usePlayerShopSize(List<ShopEntry> entries) {
        return AdvancedSparkShopBridge.buildShopEntries(this.player, entries).size();
    }

    @Redirect(method = "tryBuy(I)V", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;"))
    private Object advancedspark$usePlayerShopEntry(List<ShopEntry> entries, int index) {
        return AdvancedSparkShopBridge.buildShopEntries(this.player, entries).get(index);
    }

    @Redirect(
            method = "tryBuy(I)V",
            at = @At(value = "INVOKE", target = "Ldev/doctor4t/wathe/util/ShopEntry;onBuy(Lnet/minecraft/entity/player/PlayerEntity;)Z")
    )
    private boolean advancedspark$applySparkEntryStateOnBuy(ShopEntry entry, PlayerEntity buyer) {
        boolean purchased = entry.onBuy(buyer);
        if (purchased) {
            this.advancedspark$applyCooldown(entry);
            this.advancedspark$consumeStock(entry);
            if (buyer instanceof ServerPlayerEntity serverBuyer) {
                AdvancedSparkShopBridge.dispatchAfterPurchase(serverBuyer, this.advancedspark$itemId(entry));
            }
        }
        return purchased;
    }

    @Inject(method = "serverTick", at = @At("HEAD"))
    private void advancedspark$tickShopCooldowns(CallbackInfo ci) {
        if (this.advancedspark$cooldowns.isEmpty()) {
            return;
        }
        this.advancedspark$cooldowns.replaceAll((id, ticks) -> Math.max(0, ticks - 1));
        this.advancedspark$cooldowns.entrySet().removeIf(entry -> entry.getValue() <= 0);
    }

    @Inject(method = "reset", at = @At("HEAD"))
    private void advancedspark$resetShopState(CallbackInfo ci) {
        this.advancedspark$cooldowns.clear();
        this.advancedspark$stock.clear();
        this.advancedspark$maxStock.clear();
        this.advancedspark$initializedEntries.clear();
    }

    @Inject(method = "writeToNbt", at = @At("TAIL"))
    private void advancedspark$writeShopState(
            NbtCompound tag,
            RegistryWrapper.WrapperLookup registryLookup,
            CallbackInfo ci
    ) {
        tag.put("AdvancedSparkCooldowns", this.advancedspark$writeIntMap(this.advancedspark$cooldowns));
        tag.put("AdvancedSparkStock", this.advancedspark$writeIntMap(this.advancedspark$stock));
        tag.put("AdvancedSparkMaxStock", this.advancedspark$writeIntMap(this.advancedspark$maxStock));
    }

    @Inject(method = "readFromNbt", at = @At("TAIL"))
    private void advancedspark$readShopState(
            NbtCompound tag,
            RegistryWrapper.WrapperLookup registryLookup,
            CallbackInfo ci
    ) {
        this.advancedspark$cooldowns.clear();
        this.advancedspark$stock.clear();
        this.advancedspark$maxStock.clear();
        this.advancedspark$initializedEntries.clear();
        this.advancedspark$readIntMap(tag.getCompound("AdvancedSparkCooldowns"), this.advancedspark$cooldowns);
        this.advancedspark$readIntMap(tag.getCompound("AdvancedSparkStock"), this.advancedspark$stock);
        this.advancedspark$readIntMap(tag.getCompound("AdvancedSparkMaxStock"), this.advancedspark$maxStock);
        this.advancedspark$initializedEntries.addAll(this.advancedspark$cooldowns.keySet());
        this.advancedspark$initializedEntries.addAll(this.advancedspark$stock.keySet());
        this.advancedspark$initializedEntries.addAll(this.advancedspark$maxStock.keySet());
    }

    @Override
    public void advancedspark$initializeShop(List<ShopEntry> entries) {
        this.advancedspark$cooldowns.clear();
        this.advancedspark$stock.clear();
        this.advancedspark$maxStock.clear();
        this.advancedspark$initializedEntries.clear();
        this.advancedspark$initializeEntryState(entries);
        this.sync();
    }

    @Override
    public boolean advancedspark$isOnCooldown(String entryId) {
        return this.advancedspark$cooldowns.getOrDefault(entryId, 0) > 0;
    }

    @Override
    public int advancedspark$getRemainingCooldown(String entryId) {
        return this.advancedspark$cooldowns.getOrDefault(entryId, 0);
    }

    @Override
    public boolean advancedspark$isInStock(String entryId) {
        if (!this.advancedspark$maxStock.containsKey(entryId)) {
            return true;
        }
        return this.advancedspark$stock.getOrDefault(entryId, 0) > 0;
    }

    @Override
    public int advancedspark$getRemainingStock(String entryId) {
        if (!this.advancedspark$maxStock.containsKey(entryId)) {
            return -1;
        }
        return this.advancedspark$stock.getOrDefault(entryId, 0);
    }

    @Override
    public int advancedspark$getMaxStock(String entryId) {
        return this.advancedspark$maxStock.getOrDefault(entryId, -1);
    }

    @Unique
    private void advancedspark$initializeEntryState(List<ShopEntry> entries) {
        for (ShopEntry entry : entries) {
            String id = AdvancedSparkShopEntries.id(entry);
            if (!this.advancedspark$initializedEntries.add(id)) {
                continue;
            }
            if (AdvancedSparkShopEntries.hasInitialCooldown(entry)) {
                this.advancedspark$cooldowns.put(id, AdvancedSparkShopEntries.initialCooldownTicks(entry));
            }
            if (AdvancedSparkShopEntries.hasStockLimit(entry)) {
                int maxStock = AdvancedSparkShopEntries.maxStock(entry);
                this.advancedspark$stock.put(id, maxStock);
                this.advancedspark$maxStock.put(id, maxStock);
            }
        }
    }

    @Unique
    private boolean advancedspark$isOnCooldown(ShopEntry entry) {
        return this.advancedspark$cooldowns.getOrDefault(AdvancedSparkShopEntries.id(entry), 0) > 0;
    }

    @Unique
    private Identifier advancedspark$itemId(ShopEntry entry) {
        return Registries.ITEM.getId(entry.stack().getItem());
    }

    @Unique
    private boolean advancedspark$isInStock(ShopEntry entry) {
        String id = AdvancedSparkShopEntries.id(entry);
        if (!this.advancedspark$maxStock.containsKey(id)) {
            return true;
        }
        return this.advancedspark$stock.getOrDefault(id, 0) > 0;
    }

    @Unique
    private void advancedspark$applyCooldown(ShopEntry entry) {
        if (AdvancedSparkShopEntries.hasCooldown(entry)) {
            this.advancedspark$cooldowns.put(AdvancedSparkShopEntries.id(entry), AdvancedSparkShopEntries.cooldownTicks(entry));
        }
    }

    @Unique
    private void advancedspark$consumeStock(ShopEntry entry) {
        String id = AdvancedSparkShopEntries.id(entry);
        if (!this.advancedspark$maxStock.containsKey(id)) {
            return;
        }
        int remaining = this.advancedspark$stock.getOrDefault(id, 0);
        this.advancedspark$stock.put(id, Math.max(0, remaining - 1));
    }

    @Unique
    private NbtCompound advancedspark$writeIntMap(Map<String, Integer> map) {
        NbtCompound compound = new NbtCompound();
        map.forEach(compound::putInt);
        return compound;
    }

    @Unique
    private void advancedspark$readIntMap(NbtCompound compound, Map<String, Integer> target) {
        for (String key : compound.getKeys()) {
            target.put(key, compound.getInt(key));
        }
    }
}
