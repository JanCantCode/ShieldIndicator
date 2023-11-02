package tk.jandev.shieldindicator.mixin;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tk.jandev.shieldindicator.client.ShieldIndicatorClient;

import java.text.DecimalFormat;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin <T extends Entity> {
    @Shadow EntityRenderDispatcher dispatcher;
    @Shadow TextRenderer textRenderer;
    @Inject(method = "renderLabelIfPresent", at=@At("HEAD"), cancellable = true)
    protected void renderLabelIfPresent(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (!(entity instanceof PlayerEntity player)) return;
        String stringText = text.getString();
        int ticksTillReEnable = ShieldIndicatorClient.INSTANCE.getCooldownTicks(player);
        Text originalText = text.copy();
        if (ticksTillReEnable > 0) {
            stringText += "  §c"+new DecimalFormat("#").format((ticksTillReEnable * 50.0)/1000);
        }
        text = Text.of(stringText);
        double d = this.dispatcher.getSquaredDistanceToCamera(entity);
        if (d > 4096.0) {
            return;
        }
        boolean bl = !entity.isSneaky();
        float f = entity.getNameLabelHeight();
        int i = "deadmau5".equals(text.getString()) ? -10 : 0;
        matrices.push();
        matrices.translate(0.0f, f, 0.0f);
        matrices.multiply(this.dispatcher.getRotation());
        matrices.scale(-0.025f, -0.025f, 0.025f);
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        float g = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25f);
        int j = (int)(g * 255.0f) << 24;
        TextRenderer textRenderer = this.textRenderer;
        float h = -textRenderer.getWidth(originalText) / 2;
        textRenderer.draw(text, h, (float)i, 0x20FFFFFF, false, matrix4f, vertexConsumers, bl ? TextRenderer.TextLayerType.SEE_THROUGH : TextRenderer.TextLayerType.NORMAL, j, light);
        if (bl) {
            textRenderer.draw(text, h, (float)i, -1, false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);
        }
        matrices.pop();
        ci.cancel();
    }
}
