package com.googlecode.kanbanik.client.modules.editworkflow.projects;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.KanbanikAsyncCallback;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.services.ServerCommandInvoker;
import com.googlecode.kanbanik.client.services.ServerCommandInvokerAsync;
import com.googlecode.kanbanik.dto.ProjectDto;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class ProjectCreatingComponent extends AbstractProjectEditingComponent {

	final ServerCommandInvokerAsync serverCommandInvoker = GWT.create(ServerCommandInvoker.class);
	
	public ProjectCreatingComponent(HasClickHandlers clickHandlers) {
		super(clickHandlers);
	}

	@Override
	protected String getProjectName() {
		return "";
	}

	@Override
	protected void onOkClicked(final ProjectDto project) {
		
		new KanbanikServerCaller(
				new Runnable() {

					public void run() {
		ServerCommandInvokerManager.getInvoker().<SimpleParams<ProjectDto>, SimpleParams<ProjectDto>> invokeCommand(
				ServerCommand.SAVE_PROJECT,
				new SimpleParams<ProjectDto>(project),
				new KanbanikAsyncCallback<SimpleParams<ProjectDto>>() {

					@Override
					public void success(SimpleParams<ProjectDto> result) {
						MessageBus.sendMessage(new ProjectAddedMessage(result.getPayload(), ProjectCreatingComponent.this));
					}
				});
					}});
	}

	@Override
	protected ProjectDto createProject() {
		ProjectDto project = new ProjectDto();
		project.setId(null);
		return project;
	}
}