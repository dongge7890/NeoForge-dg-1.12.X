package com.dongge.dgmod.block.custom;

import com.dongge.dgmod.block.ModBlocks;
import com.dongge.dgmod.item.ModItems;
import com.dongge.dgmod.util.ModTags;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.util.List;
import java.util.Random;


public class MagicBlock extends Block {
    public MagicBlock(Properties properties) {
        super(properties);
    }

    public static void sendTitle(Player player, Component title) {
        if (player instanceof ServerPlayer serverPlayer) {
            // 获取玩家连接
            var connection = serverPlayer.connection.getConnection();

            // 设置标题动画时间：淡入、停留、淡出
            ClientboundSetTitlesAnimationPacket animationPacket = new ClientboundSetTitlesAnimationPacket(10, 20, 20);
            connection.send(animationPacket);

            // 发送主标题
            ClientboundSetTitleTextPacket titlePacket = new ClientboundSetTitleTextPacket(title);
            connection.send(titlePacket);
        }
    }



    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {

        level.playSound(player, pos, SoundEvents.AMETHYST_CLUSTER_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
        if (!level.isClientSide()) {
            Component title = Component.literal("请献上你的心意").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.BOLD);
            sendTitle(player, title); // 只发送主标题
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        Random r = new Random();
        int num = r.nextInt(8);
        ItemStack stack = new ItemStack(Items.DIAMOND, num);
        ItemEntity drop = new ItemEntity(level,
                pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                stack
        );
        level.addFreshEntity(drop);
    }


    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof ItemEntity itemEntity) {
            if (isValidItem(itemEntity.getItem())) {

                level.scheduleTick(pos, this, 20); // 40 ticks = 2 seconds
                // 生成闪电
                LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level);
                if (lightning != null) {
                    lightning.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                    level.addFreshEntity(lightning);
                }

                // 显示爆炸粒子效果
                for (int i = 0; i < 30; i++) {
                    double motionX = level.random.nextGaussian() * 0.1;
                    double motionY = level.random.nextGaussian() * 0.1;
                    double motionZ = level.random.nextGaussian() * 0.1;

                    level.addParticle(ParticleTypes.EXPLOSION,
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            motionX, motionY, motionZ);
                }
                // 播放雷声
                level.playSound(null, pos, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 1.0F, 1.0F);



            }
            if (itemEntity.getItem().getItem() == Items.DANDELION) {

                itemEntity.setItem(new ItemStack(Items.WITHER_ROSE, itemEntity.getItem().getCount()));
            }
        }

        super.stepOn(level, pos, state, entity);
    }

    private boolean isValidItem(ItemStack item) {
        return item.is(ModTags.Items.TRANSFORMABLE_ITEMS);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.dgmod.magic_block.tooltip"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
