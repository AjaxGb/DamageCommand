package ajaxgb.damagecommand;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.registries.IForgeRegistryEntry;

public abstract class DamageSourceFactory extends IForgeRegistryEntry.Impl<DamageSourceFactory> {

	protected int minArgs = 0;
	protected int maxArgs = 0;

	public DamageSourceFactory(String name) {
		this(new ResourceLocation(name));
	}

	public DamageSourceFactory(String modID, String name) {
		this(new ResourceLocation(modID, name));
	}

	public DamageSourceFactory(ResourceLocation name) {
		this.setRegistryName(name);
	}
	
	public String getParamUsage() {
		return null;
	}

	public DamageSource makeSource(
			MinecraftServer server, ICommandSender sender, Entity target,
			int argStart, String[] args) throws CommandException {
		if (args.length < argStart + minArgs || args.length > argStart + maxArgs)
			throw this.paramError();
		return makeSourceUnchecked(server, sender, target, argStart, args);
	}

	protected abstract DamageSource makeSourceUnchecked(
			MinecraftServer server, ICommandSender sender, Entity target,
			int argStart, String[] args) throws CommandException;

	protected DamageSourceFactory setExtraArgs(int min, int max) {
		minArgs = min;
		maxArgs = max;
		return this;
	}

	public CommandException paramError() {
		String usage = this.getParamUsage();
		if (usage == null) {
			return new CommandException("commands.damage.wrong_params.none", this.getRegistryName());
		}
		return new CommandException("commands.damage.wrong_params", this.getRegistryName(),
				new TextComponentTranslation(usage));
	}

	public static DamageSourceFactory simple(String id, DamageSource result) {

		return new DamageSourceFactory(new ResourceLocation(id)) {
			@Override
			public DamageSource makeSourceUnchecked(
					MinecraftServer server, ICommandSender sender, Entity target,
					int argStart, String[] args) throws CommandException {
				return result;
			}
		};
	}
}
