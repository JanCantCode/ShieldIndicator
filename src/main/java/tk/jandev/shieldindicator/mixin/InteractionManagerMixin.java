package tk.jandev.shieldindicator.mixin;


import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tk.jandev.shieldindicator.ShieldIndicator;
import tk.jandev.shieldindicator.client.ShieldIndicatorClient;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class InteractionManagerMixin {
    @Shadow public abstract ActionResult interactBlock(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult);

    @Inject(method = "attackEntity", at=@At("TAIL"))
    public void attackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
        if (target instanceof PlayerEntity playerEntity) {
            Vec3d relative = player.getPos().subtract(playerEntity.getPos());
            if (ShieldIndicatorClient.wouldBreakShield(playerEntity, relative) && playerEntity.isBlocking() && player.getMainHandStack().getItem() instanceof AxeItem) {
                ShieldIndicatorClient.INSTANCE.onShieldBreak(playerEntity);
            }
        }
    }
}
