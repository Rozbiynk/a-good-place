package nl.enjarai.a_good_place.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import nl.enjarai.a_good_place.particles.BlocksParticlesManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {
    @Inject(
            method = "place",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BlockItem;updateBlockStateFromTag(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/level/block/state/BlockState;")
    )
    private void wonkyblock$startPlaceAnimation(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir,
                                                @Local Level level, @Local BlockPos pos,
                                                @Local(ordinal = 0) BlockState oldState,
                                                @Local(ordinal = 1) BlockState state,
                                                @Local Player player) {
        if (oldState.is(state.getBlock()) && level instanceof ClientLevel cl) {
            BlocksParticlesManager.addParticle(state, pos, cl, context.getClickedFace(), player, context.getHand());
        }
    }
}
