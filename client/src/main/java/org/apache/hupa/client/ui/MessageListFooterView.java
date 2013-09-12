/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/

package org.apache.hupa.client.ui;

<<<<<<< HEAD
<<<<<<< HEAD
import java.util.ArrayList;
import java.util.List;

import org.apache.hupa.client.activity.MessageListFooterActivity;
import org.apache.hupa.client.rf.HupaRequestFactory;
import org.apache.hupa.shared.domain.ImapFolder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

public class MessageListFooterView extends Composite implements MessageListFooterActivity.Displayable {

	@UiField(provided = true) SimplePager simplePager;

	@UiField ListBox labels;
	@UiField SimplePanel labelsPanel;
	private List<LabelNode> folderNodes = new ArrayList<LabelNode>();

	private static final String ROOT_PATH = "imap_root";

	@Inject
	public MessageListFooterView(final MessagesCellTable table, final HupaRequestFactory rf) {
		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		simplePager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		simplePager.setDisplay(table);
		// simplePager.setRangeLimited(false);
		initWidget(binder.createAndBindUi(this));

		rf.fetchFoldersRequest().fetch(null, Boolean.TRUE).fire(new Receiver<List<ImapFolder>>() {

			private String INTENTS = "&nbsp;&nbsp;&nbsp;&nbsp;";

			@Override
			public void onSuccess(List<ImapFolder> response) {
				folderNodes.clear();
				if (response == null || response.size() == 0) {
				} else {
					for (ImapFolder folder : response) {
						fillCellList(folderNodes, folder, LabelNode.ROOT, "");
					}
				}

				makeParentList();
			}

			private void fillCellList(List<LabelNode> folderNodes, ImapFolder curFolder, LabelNode parent,
					String intents) {
				LabelNode labelNode = new LabelNode();
				labelNode.setFolder(curFolder);
				labelNode.setName(curFolder.getName());
				labelNode.setNameForDisplay(intents + curFolder.getName());
				labelNode.setParent(parent);
				labelNode.setPath(curFolder.getFullName());
				folderNodes.add(labelNode);
				if ("inbox".equalsIgnoreCase(curFolder.getName())) {
					// if(selectionModel.getSelectedObject() == null){
					// selectionModel.setSelected(labelNode, true);
					// }
				}
				if (curFolder.getHasChildren()) {
					for (ImapFolder subFolder : curFolder.getChildren()) {
						fillCellList(folderNodes, subFolder, labelNode, intents + INTENTS);
					}
				}
			}

			@Override
			public void onFailure(ServerFailure error) {
				if (error.isFatal()) {
					throw new RuntimeException(error.getMessage());
				}
			}

		});
	}

	private void makeParentList() {
		labels.clear();
		labels.addItem("Move to...", ROOT_PATH);
		for (LabelNode folderNode : this.folderNodes) {
			labels.addItem(folderNode.getNameForDisplay().replace("&nbsp;&nbsp;", ". "), folderNode.getPath());
		}

	}

	interface MessageListFooterUiBinder extends UiBinder<HorizontalPanel, MessageListFooterView> {
=======
=======
import org.apache.hupa.client.activity.MessageListFooterActivity;

>>>>>>> integrate all of the views to their corresponding activities and mappers
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Inject;

public class MessageListFooterView extends Composite implements MessageListFooterActivity.Displayable {

	@UiField(provided = true)
	SimplePager simplePager;

	@Inject
	public MessageListFooterView(final MessagesCellTable table) {
		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		simplePager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		simplePager.setDisplay(table);
//		simplePager.setRangeLimited(false);
		initWidget(binder.createAndBindUi(this));
	}

<<<<<<< HEAD
<<<<<<< HEAD
	interface MessageListFooterUiBinder extends UiBinder<HTMLPanel, MessageListFooterView> {
>>>>>>> make login page as one part of the overall layout & splite layout to little one
=======
	interface MessageListFooterUiBinder extends
			UiBinder<HTMLPanel, MessageListFooterView> {
>>>>>>> integrate all of the views to their corresponding activities and mappers
=======
	interface MessageListFooterUiBinder extends UiBinder<SimplePanel, MessageListFooterView> {
>>>>>>> make message list view panel work as expected partly
	}

	private static MessageListFooterUiBinder binder = GWT.create(MessageListFooterUiBinder.class);

<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> try to rearrange the places and history managment.
	@Override
	public SimplePager getPager() {
		return simplePager;
	}

<<<<<<< HEAD
	@Override
	public HasVisibility getLabelsPanel() {
		return labelsPanel;
	}

	@Override
	public ListBox getLabels() {
		return labels;
	}

=======
>>>>>>> make login page as one part of the overall layout & splite layout to little one
=======
>>>>>>> try to rearrange the places and history managment.
}
