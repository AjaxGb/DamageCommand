package ajaxgb.damagecommand;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class CommandDamage extends CommandBase {

	@Override
	public String getName() {
		return "damage";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "commands.damage.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
			throws CommandException {
		if (args.length < 2) {
			throw new WrongUsageException("commands.damage.usage");
		}

		Entity target = getEntity(server, sender, args[0]);
		float amount = (float)parseDouble(args[1], 0);
		
		DamageSource source = DamageSource.GENERIC;
		if (args.length > 2) {
			ResourceLocation id = new ResourceLocation(args[2]);
			
			DamageSourceFactory sourceMaker =
					ModBase.damageSourceRegistry.getValue(id);
			
			if (sourceMaker == null) {
				throw new CommandException("commands.damage.unknown_type", id);
			} else {
				source = sourceMaker.makeSource(server, sender, target, 3, args);
			}
		}
		
		if (target.attackEntityFrom(source, amount)) {
			notifyCommandListener(sender, this, "commands.damage.success");
		} else {
			throw new CommandException("commands.damage.failure");
		}
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return index == 0;
	}
	
	@SubscribeEvent
	public static void registerDamageSources(RegistryEvent.Register<DamageSourceFactory> event) {
	    event.getRegistry().registerAll(
	    		DamageSourceFactory.simple("generic",        DamageSource.GENERIC),
	    		DamageSourceFactory.simple("void",           DamageSource.OUT_OF_WORLD),
	    		DamageSourceFactory.simple("in_fire",        DamageSource.IN_FIRE),
	    		DamageSourceFactory.simple("lightning_bolt", DamageSource.LIGHTNING_BOLT),
	    		DamageSourceFactory.simple("lava",           DamageSource.LAVA),
	    		DamageSourceFactory.simple("hot_floor",      DamageSource.HOT_FLOOR),
	    		DamageSourceFactory.simple("in_wall",        DamageSource.IN_WALL),
	    		DamageSourceFactory.simple("cramming",       DamageSource.CRAMMING),
	    		DamageSourceFactory.simple("drown",          DamageSource.DROWN),
	    		DamageSourceFactory.simple("starve",         DamageSource.STARVE),
	    		DamageSourceFactory.simple("cactus",         DamageSource.CACTUS),
	    		DamageSourceFactory.simple("fall",           DamageSource.FALL),
	    		DamageSourceFactory.simple("fly_into_wall",  DamageSource.FLY_INTO_WALL),
	    		DamageSourceFactory.simple("wither",         DamageSource.WITHER),
	    		DamageSourceFactory.simple("anvil",          DamageSource.ANVIL),
	    		DamageSourceFactory.simple("falling_block",  DamageSource.FALLING_BLOCK),
	    		DamageSourceFactory.simple("dragon_breath",  DamageSource.DRAGON_BREATH),
	    		DamageSourceFactory.simple("fireworks",      DamageSource.FIREWORKS),
	    		new DamageSourceFactory("on_fire") {
					@Override
					public DamageSource makeSourceUnchecked(MinecraftServer server, ICommandSender sender, Entity target, int argStart, String[] args) throws CommandException {
						if (args.length != argStart) {
							// Set fire
							int fireTime = parseInt(args[argStart + 0], 0);
							if (fireTime == 0) {
								target.extinguish();
							} else {
								target.setFire(fireTime);
							}
						}
						return DamageSource.ON_FIRE;
					}
					@Override
					public String getParamUsage() {
						return "commands.damage.params.on_fire";
					}
	    		}.setExtraArgs(0, 1),
	    		new DamageSourceFactory("player") {
					@Override
					public DamageSource makeSourceUnchecked(MinecraftServer server, ICommandSender sender, Entity target, int argStart, String[] args) throws CommandException {
						return DamageSource.causePlayerDamage(
								getPlayer(server, sender, args[argStart + 0]));
					}
					@Override
					public String getParamUsage() {
						return "commands.damage.params.player";
					}
	    		}.setExtraArgs(1, 1),
	    		new DamageSourceFactory("thorns") {
					@Override
					public DamageSource makeSourceUnchecked(MinecraftServer server, ICommandSender sender, Entity target, int argStart, String[] args) throws CommandException {
						return DamageSource.causeThornsDamage(
								getEntity(server, sender, args[argStart + 0]));
					}
					@Override
					public String getParamUsage() {
						return "commands.damage.params.thorns";
					}
	    		}.setExtraArgs(1, 1),
	    		new DamageSourceFactory("mob") {
					@Override
					public DamageSource makeSourceUnchecked(MinecraftServer server, ICommandSender sender, Entity target, int argStart, String[] args) throws CommandException {
						if (args.length - argStart == 1) {
							// Direct
							return DamageSource.causeMobDamage(
									getEntity(server, sender, args[argStart + 0],
											EntityLivingBase.class));
						} else {
							// Indirect
							return DamageSource.causeIndirectDamage(
									getEntity(server, sender, args[argStart + 1]),
									getEntity(server, sender, args[argStart + 0],
											EntityLivingBase.class));
						}
					}
					@Override
					public String getParamUsage() {
						return "commands.damage.params.mob";
					}
	    		}.setExtraArgs(1, 2),
	    		new DamageSourceFactory("arrow") {
					@Override
					public DamageSource makeSourceUnchecked(MinecraftServer server, ICommandSender sender, Entity target, int argStart, String[] args) throws CommandException {
						if (args.length - argStart == 1) {
							// Acting alone
							return DamageSource.causeArrowDamage(
									getEntity(server, sender, args[argStart + 0],
											EntityArrow.class),
									null);
						} else {
							// Fired by entity
							return DamageSource.causeArrowDamage(
									getEntity(server, sender, args[argStart + 0],
											EntityArrow.class),
									getEntity(server, sender, args[argStart + 1]));
						}
					}
					@Override
					public String getParamUsage() {
						return "commands.damage.params.arrow";
					}
	    		}.setExtraArgs(1, 2),
	    		new DamageSourceFactory("fireball") {
					@Override
					public DamageSource makeSourceUnchecked(MinecraftServer server, ICommandSender sender, Entity target, int argStart, String[] args) throws CommandException {
						if (args.length - argStart == 1) {
							// Acting alone
							return DamageSource.causeFireballDamage(
									getEntity(server, sender, args[argStart + 0],
											EntityFireball.class),
									null);
						} else {
							// Fired by entity
							return DamageSource.causeFireballDamage(
									getEntity(server, sender, args[argStart + 0],
											EntityFireball.class),
									getEntity(server, sender, args[argStart + 1]));
						}
					}
					@Override
					public String getParamUsage() {
						return "commands.damage.params.fireball";
					}
	    		}.setExtraArgs(1, 2),
	    		new DamageSourceFactory("thrown") {
					@Override
					public DamageSource makeSourceUnchecked(MinecraftServer server, ICommandSender sender, Entity target, int argStart, String[] args) throws CommandException {
						if (args.length - argStart == 1) {
							// Acting alone
							return DamageSource.causeThrownDamage(
									getEntity(server, sender, args[argStart + 0]),
									null);
						} else {
							// Thrown by entity
							return DamageSource.causeThrownDamage(
									getEntity(server, sender, args[argStart + 0]),
									getEntity(server, sender, args[argStart + 1]));
						}
					}
					@Override
					public String getParamUsage() {
						return "commands.damage.params.thrown";
					}
	    		}.setExtraArgs(1, 2),
	    		new DamageSourceFactory("magic") {
					@Override
					public DamageSource makeSourceUnchecked(MinecraftServer server, ICommandSender sender, Entity target, int argStart, String[] args) throws CommandException {
						if (args.length <= argStart) {
							// Sourceless magic
							return DamageSource.MAGIC;
						} else if (args.length - argStart == 1) {
							// Acting alone
							return DamageSource.causeIndirectMagicDamage(
									getEntity(server, sender, args[argStart + 0]),
									null);
						} else {
							// Fired by entity
							return DamageSource.causeIndirectMagicDamage(
									getEntity(server, sender, args[argStart + 0]),
									getEntity(server, sender, args[argStart + 1]));
						}
					}
					@Override
					public String getParamUsage() {
						return "commands.damage.params.magic";
					}
	    		}.setExtraArgs(0, 2),
	    		new DamageSourceFactory("explosion") {
					@Override
					public DamageSource makeSourceUnchecked(MinecraftServer server, ICommandSender sender, Entity target, int argStart, String[] args) throws CommandException {
						if (args.length <= argStart) {
							return DamageSource.causeExplosionDamage((EntityLivingBase)null);
						} else {
							return DamageSource.causeExplosionDamage(
									getEntity(server, sender, args[argStart + 0],
											EntityLivingBase.class));
						}
					}
					@Override
					public String getParamUsage() {
						return "commands.damage.params.explosion";
					}
	    		}.setExtraArgs(0, 1)
	    		);
	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        switch (args.length) {
        case 1:
        	return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        case 3:
        	return getListOfStringsMatchingLastWord(args, ModBase.damageSourceRegistry.getKeys());
        default:
        	return Collections.<String>emptyList();
        }
    }

}
