package com.starlotte.seeking_hearts.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CreakingHeartBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.CreakingHeartBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.world.level.block.CreakingHeartBlock.ACTIVE;

@Mixin(CreakingHeartBlock.class)
public class CreakingHeartBlockMixin {

    @WrapOperation(method = "getTicker", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;"))
    public Comparable test(BlockState instance, Property property, Operation<Comparable> original) {
        return true;
    }

    @Inject(method = "getAnalogOutputSignal", at = @At(value = "HEAD"), cancellable = true)
    public void addAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos, CallbackInfoReturnable<Integer> cir) {
        if (!(Boolean) blockState.getValue(ACTIVE)) {
            BlockEntity be = level.getBlockEntity(blockPos);
            if (be instanceof CreakingHeartBlockEntity creakingHeartBlockEntity) {
                cir.setReturnValue(creakingHeartBlockEntity.getAnalogOutputSignal());
            } else {
                cir.setReturnValue(0);
            }
        }
    }
}
