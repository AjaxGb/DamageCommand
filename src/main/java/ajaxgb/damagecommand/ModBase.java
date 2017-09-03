package ajaxgb.damagecommand;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

@Mod(modid = ModBase.MODID, version = ModBase.VERSION)
public class ModBase {

	public static final String MODID = "damagecommand";
	public static final String VERSION = "1.0";
	
	public static IForgeRegistry<DamageSourceFactory> damageSourceRegistry;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		damageSourceRegistry = new RegistryBuilder<DamageSourceFactory>()
				.setName(new ResourceLocation(MODID, "damage_source"))
				.setType(DamageSourceFactory.class)
				.create();
	}
	
	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandDamage());
	}
}
