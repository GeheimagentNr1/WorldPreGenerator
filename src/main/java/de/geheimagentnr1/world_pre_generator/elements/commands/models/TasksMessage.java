package de.geheimagentnr1.world_pre_generator.elements.commands.models;

import de.geheimagentnr1.world_pre_generator.elements.queues.tasks.pregen.PregenTask;
import de.geheimagentnr1.world_pre_generator.helpers.DimensionHelper;
import net.minecraft.core.SectionPos;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


public class TasksMessage {
	
	
	private final List<TaskRow> rows = new ArrayList<>();
	
	
	public void addAll( ArrayList<PregenTask> tasks ) {
		
		tasks.forEach( this::add );
	}
	
	public void add( PregenTask task ) {
		
		switch( task.getTaskType() ) {
			case BLOCK -> add(
				new TaskRow(
					DimensionHelper.getNameOfDim( task.getDimension() ),
					task.getTaskType().getSerializedName(),
					null,
					String.valueOf( SectionPos.sectionToBlockCoord( task.getCenterX() ) ),
					String.valueOf( SectionPos.sectionToBlockCoord( task.getCenterZ() ) ),
					String.valueOf( SectionPos.sectionToBlockCoord( task.getRadius() ) ),
					String.valueOf( task.isForceGeneration() )
				)
			);
			case CHUNK -> add(
				new TaskRow(
					DimensionHelper.getNameOfDim( task.getDimension() ),
					task.getTaskType().getSerializedName(),
					null,
					String.valueOf( task.getCenterX() ),
					String.valueOf( task.getCenterZ() ),
					String.valueOf( task.getRadius() ),
					String.valueOf( task.isForceGeneration() )
				)
			);
		}
	}
	
	public void add( TaskRow taskRow ) {
		
		rows.add( taskRow );
	}
	
	public List<String> buildMessages() {
		
		return rows.stream()
			.map( taskRow -> String.join(
				", ",
				taskRow.dimension(),
				taskRow.type(),
				String.join(
					" ",
					Stream.of(
							taskRow.center(),
							taskRow.centerX(),
							taskRow.centerZ()
						).filter( StringUtils::isNotEmpty )
						.toList()
				),
				taskRow.radius(),
				taskRow.forced()
			) )
			.toList();
	}
}
