package nl.enjarai.wonkyblock.particle;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.lwjgl.system.windows.POINT;

import java.util.Random;

// we use a non registered particle because this is a client only mod and we need to render from event anyways
public class PlacingBlockParticle extends Particle {
    private static final Random RANDOM = new Random();
    private static final RandomSource MC_RANDOM = RandomSource.create();

    private final BlockPos pos;
    private final BlockState blockState;
    private final BakedModel model;
    private Direction facing;

    private Vec3 prevRot;
    private Vec3 rot;

    private float step = 0.00275f;

    private float height;
    private float prevHeight;

    private boolean destinationReached;

    Minecraft client;

    public PlacingBlockParticle(ClientLevel world, BlockPos blockPos) {
        super(world, blockPos.getX(), blockPos.getY(), blockPos.getZ());

        client = Minecraft.getInstance();

        pos = BlockPos.containing(x, y, z);
        blockState = world.getBlockState(pos);
        model = client.getBlockRenderer().getBlockModel(blockState);

        facing = client.player.getDirection();

        prevHeight = height = (float) RANDOM.nextDouble(0.065, 0.115);
        float startingAngle = (float) RANDOM.nextDouble(0.03125, 0.0635);

        prevRot = new Vec3(0, 0, 0);

        rot = switch (facing) {
            case EAST -> new Vec3(-startingAngle, 0, -startingAngle);
            case NORTH -> new Vec3(-startingAngle, 0, startingAngle);
            case SOUTH -> new Vec3(startingAngle, 0, -startingAngle);
            case WEST -> new Vec3(startingAngle, 0, startingAngle);
            default -> new Vec3(0, 0, 0);
        };

        hasPhysics = false;
        lifetime = 7;
    }

    @Override
    public void tick() {
        if (destinationReached) {
            remove();
            return;
        }
        if (age++ >= lifetime) {
            setRemovedNextTick();
        }

        if (removed || client.isPaused())
            return;

        prevHeight = height;
        prevRot = rot;

        rot = switch (facing) {
            case EAST -> rot.add(step, 0, step);
            case NORTH -> rot.add(step, 0, -step);
            case SOUTH -> rot.add(-step, 0, step);
            case WEST -> rot.add(-step, 0, -step);
            default -> new Vec3(0, 0, 0);
        };

        height -= step * 5f;
        height = Math.max(height, 0);

        step *= 1.5678982f;
    }

    private void setRemovedNextTick() {
        destinationReached = true;
    }


    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {

        PoseStack poseStack = new PoseStack();


        var cameraPos = camera.getPosition();
        float px = (float) (Mth.lerp(partialTicks, this.xo, this.x) - cameraPos.x());
        float py = (float) (Mth.lerp(partialTicks, this.yo, this.y) - cameraPos.y());
        float pz = (float) (Mth.lerp(partialTicks, this.zo, this.z) - cameraPos.z());

        poseStack.translate(px, py, pz);


        applyAnimation(poseStack, partialTicks);

        MultiBufferSource.BufferSource bufferSource = client.renderBuffers().bufferSource();
        var blockVertexConsumer = bufferSource.getBuffer(ItemBlockRenderTypes.getMovingBlockRenderType(blockState));

        renderBlock(level, model, blockState, pos, poseStack,
                blockVertexConsumer, true, MC_RANDOM, blockState.getSeed(pos));


    }


    public void applyAnimation(PoseStack poseStack, float partialTicks) {
        var tRot = switch (facing) {
            case EAST -> new Vec3(1, 0, -1);
            case NORTH -> new Vec3(-1, 0, -1);
            case SOUTH -> new Vec3(1, 0, 1);
            case WEST -> new Vec3(-1, 0, 1);
            default -> new Vec3(0, 0, 0);
        };

        float translationAmount = Mth.lerp(partialTicks, prevHeight, height);

        if (translationAmount <= 0)
            translationAmount = 0;

        var translate = switch (facing) {
            case EAST -> new Vec3(-translationAmount, translationAmount, translationAmount);
            case NORTH -> new Vec3(translationAmount, translationAmount, translationAmount);
            case SOUTH -> new Vec3(-translationAmount, translationAmount, -translationAmount);
            case WEST -> new Vec3(translationAmount, translationAmount, -translationAmount);
            default -> new Vec3(0, 0, 0);
        };

        Vec3 smoothRot = prevRot.lerp(rot, partialTicks);

        //anim
        poseStack.translate(tRot.x, tRot.y, tRot.z);

        //  poseStack.mulPose(Axis.YP.rotation((float) smoothRot.x));
        //   poseStack.mulPose(Axis.ZP.rotation((float) smoothRot.z));

        poseStack.translate(-tRot.x, -tRot.y, -tRot.z);

        poseStack.translate(translate.x, translate.y, translate.z);
    }

    private void renderBlock(BlockAndTintGetter world, BakedModel model, BlockState state, BlockPos pos,
                             PoseStack matrices, VertexConsumer vertexConsumer, boolean cull, RandomSource random, long seed) {
        client.getBlockRenderer().getModelRenderer().tesselateBlock(
                world, model, state, pos, matrices, vertexConsumer,
                cull, random, seed, OverlayTexture.NO_OVERLAY);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }


    public boolean reachedDestination() {
        return destinationReached;
    }

}
