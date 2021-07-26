package de.geheimagentnr1.world_pre_generator.save;

import de.geheimagentnr1.world_pre_generator.WorldPreGenerator;
import de.geheimagentnr1.world_pre_generator.elements.workers.PregenWorker;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import net.minecraftforge.fmllegacy.WorldPersistenceHooks;


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
	public CompoundTag getDataForWriting( LevelStorageSource.LevelStorageAccess levelSave, WorldData serverInfo ) {
		
		CompoundTag compound = new CompoundTag();
		compound.put( queueName, pregenWorker.getQueue().writeNBT() );
		return compound;
	}
	
	@Override
	public void readData( LevelStorageSource.LevelStorageAccess levelSave, WorldData serverInfo, CompoundTag tag ) {
		
		if( tag.contains( queueName, NBTType.COMPOUND.getId() ) ) {
			pregenWorker.clearUp();
			pregenWorker.getQueue().readNBT( tag.getCompound( queueName ) );
		}
	}
}
