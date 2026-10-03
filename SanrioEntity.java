package com.sanriofriends.entity;

import com.sanriofriends.SanrioCharacter;
import com.sanriofriends.dialogue.Dialogue;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.UUID;

/**
 * Un amigo de Sanrio. Vive en su casita (no se aleja de ella), es inofensivo e inmortal, y puedes hablar con él
 * (clic derecho). Cuando completas una misión os hacéis amigos y puede seguirte o quedarse quieto.
 */
public class SanrioEntity extends PathfinderMob {
    private final SanrioCharacter character;

    @Nullable
    private UUID followUuid;
    private boolean staying;
    @Nullable
    private BlockPos home;

    private int talkTicks;
    @Nullable
    private Player talkPlayer;

    public SanrioEntity(EntityType<? extends SanrioEntity> type, Level level, SanrioCharacter character) {
        super(type, level);
        this.character = character;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FLYING_SPEED, 0.5D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    public SanrioCharacter getCharacter() {
        return this.character;
    }

    // ------------------------------------------------------------------ IA

    /** Cinnamoroll lo sobrescribe para volar. */
    protected Goal createWanderGoal() {
        return new WaterAvoidingRandomStrollGoal(this, 0.8D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new TalkGoal());
        this.goalSelector.addGoal(2, new StayGoal());
        this.goalSelector.addGoal(3, new FollowFriendGoal());
        this.goalSelector.addGoal(5, this.createWanderGoal());
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    /** Se queda quieto y mira al jugador mientras habláis. */
    private final class TalkGoal extends Goal {
        TalkGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            return talkTicks > 0;
        }

        @Override
        public boolean canContinue() {
            return talkTicks > 0;
        }

        @Override
        public void tick() {
            getNavigation().stop();
            if (talkPlayer != null) {
                getLookControl().setLookAt(talkPlayer, 30.0F, 30.0F);
            }
        }
    }

    /** "Quédate quieto". */
    private final class StayGoal extends Goal {
        StayGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            return staying;
        }

        @Override
        public boolean canContinue() {
            return staying;
        }

        @Override
        public void tick() {
            getNavigation().stop();
        }
    }

    /** "Sígueme". */
    private final class FollowFriendGoal extends Goal {
        private Player target;
        private int recalc;

        FollowFriendGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        private Player findTarget() {
            if (followUuid == null || staying) return null;
            Player p = level().getPlayerByUUID(followUuid);
            if (p == null || p.isSpectator() || !p.isAlive()) return null;
            return p;
        }

        @Override
        public boolean canUse() {
            Player p = findTarget();
            if (p == null || distanceToSqr(p) < 9.0D) return false;
            this.target = p;
            return true;
        }

        @Override
        public boolean canContinue() {
            Player p = findTarget();
            return p != null && p == this.target && distanceToSqr(p) > 4.0D;
        }

        @Override
        public void start() {
            this.recalc = 0;
        }

        @Override
        public void tick() {
            getLookControl().setLookAt(this.target, 10.0F, getMaxHeadXRot());
            if (--this.recalc > 0) return;
            this.recalc = 10;
            if (distanceToSqr(this.target) > 24.0D * 24.0D) {
                teleportTo(this.target.getX() + 1.0D, this.target.getY(), this.target.getZ() + 1.0D);
                getNavigation().stop();
            } else {
                getNavigation().moveTo(this.target, 1.15D);
            }
        }

        @Override
        public void stop() {
            this.target = null;
            getNavigation().stop();
        }
    }

    // ------------------------------------------------------------------ estado (seguir / quieto / hogar)

    public boolean isFollowing(Player player) {
        return this.followUuid != null && this.followUuid.equals(player.getUUID());
    }

    public boolean isStaying() {
        return this.staying;
    }

    public void setFollow(@Nullable UUID uuid) {
        this.followUuid = uuid;
        if (uuid != null) {
            this.staying = false;
        }
        this.refreshRestriction();
    }

    public void setStaying(boolean stay) {
        this.staying = stay;
        if (stay) {
            this.followUuid = null;
            this.getNavigation().stop();
        }
        this.refreshRestriction();
    }

    @Nullable
    public BlockPos getHome() {
        return this.home;
    }

    /** Si sigue a alguien no tiene límites; si no, solo pasea cerca de su casa. */
    private void refreshRestriction() {
        if (this.followUuid != null || this.home == null) {
            this.clearRestriction();
        } else {
            this.restrictTo(this.home, 12);
        }
    }

    public void startTalking(Player player) {
        this.talkTicks = 200;
        this.talkPlayer = player;
        this.getNavigation().stop();
    }

    public void stopTalking() {
        this.talkTicks = 0;
        this.talkPlayer = null;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            if (this.talkTicks > 0) {
                this.talkTicks--;
            }
            if (this.home == null) {
                this.home = this.blockPosition();
                this.refreshRestriction();
            }
        }
    }

    // ------------------------------------------------------------------ interacción

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }
        if (this.level().isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (player instanceof ServerPlayer sp) {
            this.startTalking(sp);
            Dialogue.open(sp, this);
        }
        return InteractionResult.CONSUME;
    }

    // ------------------------------------------------------------------ inmortal, sonidos y persistencia

    /** Los amigos de Sanrio no reciben daño (salvo /kill o el vacío). */
    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        if (!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return true;
        }
        return super.isInvulnerableTo(source);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 300;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ALLAY_HURT;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (this.followUuid != null) {
            tag.putUUID("SanrioFollow", this.followUuid);
        }
        tag.putBoolean("SanrioStaying", this.staying);
        if (this.home != null) {
            tag.putInt("SanrioHomeX", this.home.getX());
            tag.putInt("SanrioHomeY", this.home.getY());
            tag.putInt("SanrioHomeZ", this.home.getZ());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.followUuid = tag.hasUUID("SanrioFollow") ? tag.getUUID("SanrioFollow") : null;
        this.staying = tag.getBoolean("SanrioStaying");
        if (tag.contains("SanrioHomeX")) {
            this.home = new BlockPos(tag.getInt("SanrioHomeX"), tag.getInt("SanrioHomeY"), tag.getInt("SanrioHomeZ"));
        }
        this.refreshRestriction();
    }
}
