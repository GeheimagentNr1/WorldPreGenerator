package de.geheimagentnr1.world_pre_generator.save;

import de.geheimagentnr1.world_pre_generator.WorldPreGenerator;
import de.geheimagentnr1.world_pre_generator.elements.workers.PregenWorker;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.storage.IServerConfiguration;
import net.minecraft.world.storage.SaveFormat;
import net.minecraftforge.fml.WorldPersistenceHooks;


public class PregenerationWorldPersistenceHook implements WorldPersistenceHooks.WorldPersistenceHook {
	
	
	private static final String queueName = "queue";
	
	private final PregenWorker pregenWorker;
	
	public PregenerationWorldPersistenceHook( PregenWorker _pregenWorker ) {
		
		pregenWorker = _pregenWorker;
	}
	
	@Override
	public String getModId() {
		
		return WorldPreGenerator.MODID;
	}
	
	@Override
	public CompoundNBT getDataForWriting( SaveFormat.LevelSave levelSave, IServerConfiguration serverInfo ) {
		
		CompoundNBT compound = new CompoundNBT();
		compound.put( queueName, pregenWorker.getQueue().writeNBT() );
		return compound;
	}
	
	@Override
	public void readData( SaveFormat.LevelSave levelSave, IServerConfiguration serverInfo, CompoundNBT tag ) {
		
		if( tag.contains( queueName, NBTType.COMPOUND.getId() ) ) {
			pregenWorker.getQueue().readNBT( tag.getCompound( queueName ) );
		}
	}
}
