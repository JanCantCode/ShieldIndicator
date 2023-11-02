package tk.jandev.shieldindicator.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShieldIndicatorClient implements ClientModInitializer {
    public static ShieldIndicatorClient INSTANCE;
    private final Map<PlayerEntity, Integer> shieldCoolDowns = new HashMap<>();

    @Override
    public void onInitializeClient() {
        INSTANCE = this;

        ClientTickEvents.START_CLIENT_TICK.register(this::tick);
    }


    public static boolean wouldBreakShield(LivingEntity entity, Vec3d hitFromPos) { // Kindly inspired / stolen from https://github.com/Noryea/shield-extensions/blob/1.19-fabric/src/main/java/cn/noryea/shield_extensions/ShieldExtensionsMod.java#L20
        Vec3d yawVector = entity.getRotationVec(1.0f);
        Vec3d relativeDir = hitFromPos.relativize(entity.getPos().normalize());
        relativeDir = relativeDir.add(0, -relativeDir.y, 0);
        return (relativeDir.dotProduct(yawVector) < 0.0);
    }

    public void onShieldBreak(PlayerEntity playerEntity) {
        shieldCoolDowns.put(playerEntity, 100);
    }

    public void tick(MinecraftClient mc) {
        List<Map.Entry<PlayerEntity, Integer>> toRemove = new ArrayList<>();
        for (Map.Entry<PlayerEntity, Integer> mapEntry : shieldCoolDowns.entrySet()) {
            mapEntry.setValue(mapEntry.getValue() - 1);
            if (mapEntry.getValue() < 0) {
                toRemove.add(mapEntry);
            }
        }
        toRemove.forEach(shieldCoolDowns.entrySet()::remove);
    }

    public int getCooldownTicks(PlayerEntity player) {
        if (shieldCoolDowns.containsKey(player)) {
            return shieldCoolDowns.get(player);
        }
        return -1;
    }
}
