package com.starlotte.seeking_hearts.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.TrailParticleOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.CreakingHeartBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.CreakingHeartBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

// ik this is messy

@Mixin(CreakingHeartBlockEntity.class)
public abstract class CreakingHeartBlockEntityMixin extends BlockEntity {

    private CreakingHeartBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Inject(method = "computeAnalogOutputSignal", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
    private void test(CallbackInfoReturnable<Integer> cir) {
        if (this.getBlockState().getValue(CreakingHeartBlock.ACTIVE)) return;
        BlockPos blockPos = this.getBlockPos();
        List<Player> nearbyPlayers = level.getEntities(EntityType.PLAYER, new AABB(blockPos).inflate(32), player -> !player.isSpectator());
        int max = 0;
        for (Player player : nearbyPlayers) {
            int blockPower = (int) Math.ceil(seeking_hearts$isLookingAtBlock(player, blockPos) * 14);

            if (blockPower > max) {
                max = blockPower;
            }
        }
        cir.setReturnValue(max);
    }

    @Unique
    private double seeking_hearts$isLookingAtBlock(LivingEntity player, BlockPos blockPos) {
        Vec3 playerView = player.getViewVector(1.0F).normalize();
        Vec3 vectorToPlayer = new Vec3(blockPos.getX() + 0.5 - player.getX(), blockPos.getY() + 0.5 - player.getEyeY(), blockPos.getZ() + 0.5 - player.getZ());
        double distSqr = vectorToPlayer.lengthSqr();
        vectorToPlayer = vectorToPlayer.normalize();
        double dot = playerView.dot(vectorToPlayer);

        BlockHitResult blockLooksAtYouClip = level.clip(new ClipContext(player.getEyePosition(), Vec3.atCenterOf(blockPos), ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, player));
        BlockHitResult andILookAtBlockClip = level.clip(new ClipContext(player.getEyePosition(), player.getEyePosition().add(player.getLookAngle().normalize().scale(32)), ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, player));
        double lookAngle = 1 - (Math.acos(dot) - Math.PI / (45 * Math.sqrt(distSqr))) * 2 / Math.PI;

        if (Math.random() * lookAngle > 0.75 && !level.isClientSide) {
            ServerLevel serverLevel = (ServerLevel) level;
            RandomSource randomSource = serverLevel.random;
            AABB aabb = new AABB(blockPos).inflate(0.4);
            Vec3 vec3 = aabb.getMinPosition().add(randomSource.nextDouble() * aabb.getXsize(), randomSource.nextDouble() * aabb.getYsize(), randomSource.nextDouble() * aabb.getZsize());
            TrailParticleOption trailParticleOption = new TrailParticleOption(Vec3.atCenterOf(blockPos), 3814701, randomSource.nextInt(40) + 10);
            serverLevel.sendParticles(trailParticleOption, true, true, vec3.x, vec3.y, vec3.z, 1, 0.0, 0.0, 0.0, 0.0);
        }

        if (andILookAtBlockClip.getBlockPos().equals(blockPos)) return 15;
        else if (blockLooksAtYouClip.getType() == HitResult.Type.BLOCK && blockLooksAtYouClip.getBlockPos().equals(blockPos))
            return dot > 0.0 && distSqr < 32 * 32 ? lookAngle : 0;
        return 0;
    }
}
