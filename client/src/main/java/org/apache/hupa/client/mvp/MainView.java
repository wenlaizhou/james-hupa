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

package org.apache.hupa.client.mvp;

import java.util.ArrayList;
import java.util.List;

import net.customware.gwt.presenter.client.EventBus;

import org.apache.hupa.client.HupaConstants;
import org.apache.hupa.client.HupaMessages;
import org.apache.hupa.client.bundles.IMAPTreeImages;
import org.apache.hupa.client.dnd.PagingScrollTableRowDragController;
import org.apache.hupa.client.widgets.ConfirmDialogBox;
import org.apache.hupa.client.widgets.HasDialog;
import org.apache.hupa.client.widgets.IMAPTreeItem;
import org.apache.hupa.client.widgets.Loading;
import org.apache.hupa.shared.data.IMAPFolder;
import org.apache.hupa.shared.data.Message;
import org.apache.hupa.shared.data.User;
import org.apache.hupa.shared.data.Message.IMAPFlag;
import org.apache.hupa.shared.events.LoginEvent;
import org.apache.hupa.shared.events.LoginEventHandler;
import org.apache.hupa.shared.events.LogoutEvent;
import org.apache.hupa.shared.events.LogoutEventHandler;
import org.apache.hupa.shared.events.MoveMessageEvent;
import org.apache.hupa.widgets.event.EditEvent;
import org.apache.hupa.widgets.event.EditHandler;
import org.apache.hupa.widgets.ui.EnableHyperlink;
import org.apache.hupa.widgets.ui.HasEditable;
import org.apache.hupa.widgets.ui.HasEnable;
import org.cobogw.gwt.user.client.ui.Button;
import org.cobogw.gwt.user.client.ui.RoundedPanel;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * MainView acts like a container of other widgets which will get displayed after the user successfully logged in
 * 
 *
 */
public class MainView extends Composite implements MainPresenter.Display {

    private DockPanel dockPanel;
    private VerticalPanel north;
    private HupaConstants constants = GWT.create(HupaConstants.class);
    private RoundedPanel west;
    private IMAPTreeImages tImages = GWT.create(IMAPTreeImages.class);
    private Tree folderTree = new Tree(tImages, true);
    private MultiWordSuggestOracle oracle = new MultiWordSuggestOracle(" ,@");
    private SuggestBox searchBox = new SuggestBox(oracle);
    private Button searchButton = new Button(constants.searchButton());
    private Loading loading = new Loading(false);
    private Widget centerWidget;
    private RoundedPanel center;
    private IMAPMessageListView mListView;
    private HupaMessages messages = GWT.create(HupaMessages.class);
    private VerticalPanel folderPanel = new VerticalPanel();
    private SimplePanel panel = new SimplePanel();
    private HorizontalPanel folderButtonBar = new HorizontalPanel();
    private EnableHyperlink newFolderButton = new EnableHyperlink(constants.newFolder(), "");
    private EnableHyperlink renameFolderButton = new EnableHyperlink(constants.renameFolder(), "");
    private EnableHyperlink deleteFolderButton = new EnableHyperlink(constants.deleteFolder(), "");
    private ConfirmDialogBox confirmFolderDeleteBox = new ConfirmDialogBox();
    private Loading loader = new Loading(false);
    private Loading messageLoader = new Loading(true);
    private List<DropController> dropControllerList = new ArrayList<DropController>();
    private EventBus bus;
    private PagingScrollTableRowDragController controller;
    protected User user;

    @Inject
    public MainView(EventBus bus, PagingScrollTableRowDragController controllerProvider) {
        this.controller = controllerProvider;
        this.bus = bus;

        dockPanel = new DockPanel();

        dockPanel.setSpacing(10);
        dockPanel.setWidth("100%");

        createNorth();
        createWest();
        createCenter();

        dockPanel.add(north, DockPanel.NORTH);
        dockPanel.add(west, DockPanel.WEST);
        dockPanel.add(center, DockPanel.CENTER);
        dockPanel.setCellHorizontalAlignment(north, DockPanel.ALIGN_RIGHT);
        dockPanel.setCellHorizontalAlignment(center, DockPanel.ALIGN_LEFT);

        initWidget(dockPanel);
    }

    private void createWest() {
        west = new RoundedPanel(RoundedPanel.ALL, 1);
        west.add(folderTree);
        west.setWidth("150px");

        folderTree.setAnimationEnabled(true);
        folderPanel.setSpacing(5);

        folderButtonBar.setSpacing(3);
        folderButtonBar.add(newFolderButton);
        folderButtonBar.add(renameFolderButton);
        folderButtonBar.add(deleteFolderButton);
        RoundedPanel buttonPanel = new RoundedPanel(RoundedPanel.ALL, 1);
        buttonPanel.setBorder();
        buttonPanel.add(folderButtonBar);
        folderPanel.add(buttonPanel);
        folderPanel.add(folderTree);
        panel.add(loader);
        confirmFolderDeleteBox.setText(messages.confirmDeleteFolder());
        bus.addHandler(LoginEvent.TYPE, new LoginEventHandler() {

            public void onLogin(LoginEvent event) {
                user = event.getUser();
            }

        });
        bus.addHandler(LogoutEvent.TYPE, new LogoutEventHandler() {

            public void onLogout(LogoutEvent event) {
                user = null;
            }

        });
        west.add(panel);
    }

    private void createNorth() {
        north = new VerticalPanel();
        north.setWidth("100%");

        HorizontalPanel barPanel = new HorizontalPanel();
        barPanel.setWidth("100%");

        HorizontalPanel hPanel = new HorizontalPanel();
        hPanel.setSpacing(5);
        hPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);

        searchBox.setAnimationEnabled(true);
        searchBox.setAutoSelectEnabled(false);
        searchBox.setWidth("250px");
        searchBox.setLimit(20);
        searchBox.addKeyUpHandler(new KeyUpHandler() {

            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    searchButton.click();
                }
            }

        });
        hPanel.add(searchBox);
        hPanel.add(searchButton);
        hPanel.add(messageLoader);
        barPanel.add(hPanel);
        barPanel.setCellHorizontalAlignment(hPanel, HorizontalPanel.ALIGN_LEFT);
        barPanel.add(loading);
        barPanel.setCellHorizontalAlignment(loading, HorizontalPanel.ALIGN_RIGHT);
        barPanel.setCellVerticalAlignment(loading, HorizontalPanel.ALIGN_MIDDLE);

        north.add(barPanel);

    }

    private void createCenter() {
        center = new RoundedPanel(RoundedPanel.ALL, 1);
        center.setBorder();
        center.setWidth("100%");
        center.add(mListView);

    }

    /*
     * (non-Javadoc)
     * @see org.apache.hupa.client.mvp.MainPresenter.Display#getSearchClick()
     */
    public HasClickHandlers getSearchClick() {
        return searchButton;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.hupa.client.mvp.MainPresenter.Display#getSearchValue()
     */
    public HasValue<String> getSearchValue() {
        return searchBox;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.hupa.client.mvp.MainPresenter.Display#fillSearchOracle(java.util.ArrayList)
     */
    public void fillSearchOracle(ArrayList<Message> messages) {
        for (int i = 0; i < messages.size(); i++) {
            String subject = messages.get(i).getSubject();
            String from = messages.get(i).getFrom();
            if (subject != null && subject.trim().length() > 0) {
                oracle.add(subject.trim());
            }
            if (from != null && from.trim().length() > 0) {
                oracle.add(from.trim());
            }
        }
        //searchBox.setText("");
    }

    /*
     * (non-Javadoc)
     * @see org.apache.hupa.client.mvp.MainPresenter.Display#bindTreeItems(java.util.ArrayList)
     */
    public void bindTreeItems(ArrayList<IMAPTreeItem> treeList) {
        folderTree.clear();
        for (int i = 0; i < dropControllerList.size(); i++) {
            controller.unregisterDropController(dropControllerList.get(i));
        }

        for (int i = 0; i < treeList.size(); i++) {
            IMAPTreeItem item = treeList.get(i);
            bindDropController(item);
            folderTree.addItem(item);

            if (((IMAPFolder) item.getUserObject()).getFullName().equalsIgnoreCase(user.getSettings().getInboxFolderName())) {
                folderTree.setSelectedItem(item, false);
            }

        }
    }

    /**
     * Bind a IMAPFolderDropController to the given Item and all its childs
     * 
     * @param item
     */
    private void bindDropController(IMAPTreeItem item) {
        IMAPFolderDropController dropController = new IMAPFolderDropController(item);
        controller.registerDropController(dropController);
        dropControllerList.add(dropController);

        if (item.getChildCount() > 0) {
            for (int i = 0; i < item.getChildCount(); i++) {
                bindDropController((IMAPTreeItem) item.getChild(i));
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.hupa.client.mvp.IMAPFolderPresenter.Display#getTree()
     */
    public HasSelectionHandlers<TreeItem> getTree() {
        return folderTree;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.customware.gwt.presenter.client.widget.WidgetDisplay#asWidget()
     */
    public Widget asWidget() {
        return this;
    }


    /*
     * (non-Javadoc)
     * @see org.apache.hupa.client.mvp.MainPresenter.Display#setLoadingFolders(boolean)
     */
    public void setLoadingFolders(boolean load) {
        if (load) {
            loader.show();
            panel.clear();
            panel.add(loader);
        } else {
            panel.clear();
            panel.add(folderPanel);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.apache.hupa.client.mvp.MainPresenter.Display#setLoadingMessage(boolean)
     */
    public void setLoadingMessage(boolean load) {
        if (load) {
            messageLoader.show();
        } else {
            messageLoader.hide();
        }
    }

    /**
     * Drop controller which handle drop on TreeItems
     * 
     * 
     */
    private class IMAPFolderDropController extends SimpleDropController {
        private IMAPTreeItem item;

        public IMAPFolderDropController(IMAPTreeItem item) {
            super(item.getWidget());
            this.item = item;
        }

        /**
         * Veto the Drop if the folder is the same
         */
        @Override
        public void onPreviewDrop(DragContext context) throws VetoDragException {
            if (item.equals(folderTree.getSelectedItem())) {
                throw new VetoDragException();
            }
        }

        /**
         * Set the right unseen count on the folders and fire an event
         */
        @Override
        public void onDrop(DragContext context) {
            IMAPTreeItem oldTreeItem = (IMAPTreeItem) folderTree.getSelectedItem();
            Message message = (Message) controller.getDragValue();
            if (message.getFlags().contains(IMAPFlag.SEEN) == false) {
                oldTreeItem.decreaseUnseenMessageCount();
                item.increaseUnseenMessageCount();
            }
            bus.fireEvent(new MoveMessageEvent(user, (IMAPFolder) oldTreeItem.getUserObject(), (IMAPFolder) item.getUserObject(), message));
        }

        /**
         * Update the proxy widget to show its valid to drop it
         * 
         */
        @Override
        public void onEnter(DragContext context) {
            if (item.equals(folderTree.getSelectedItem()) == false) {
                controller.getCurrentProxy().setIsValid(true);
            }
            super.onEnter(context);
        }

        /**
         * Update the proxy widget to show its invalid to drop it
         */
        @Override
        public void onLeave(DragContext context) {
            controller.getCurrentProxy().setIsValid(false);
            super.onLeave(context);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.hupa.client.mvp.IMAPFolderPresenter.Display#getRenameClick()
     */
    public HasClickHandlers getRenameClick() {
        return renameFolderButton;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.hupa.client.mvp.IMAPFolderPresenter.Display#getDeleteEnable()
     */
    public HasEnable getDeleteEnable() {
        return deleteFolderButton;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.hupa.client.mvp.IMAPFolderPresenter.Display#getNewEnable()
     */
    public HasEnable getNewEnable() {
        return newFolderButton;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.hupa.client.mvp.IMAPFolderPresenter.Display#getRenameEnable()
     */
    public HasEnable getRenameEnable() {
        return renameFolderButton;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.hupa.client.mvp.IMAPFolderPresenter.Display#getDeleteClick()
     */
    public HasClickHandlers getDeleteClick() {
        return deleteFolderButton;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.hupa.client.mvp.IMAPFolderPresenter.Display#getNewClick()
     */
    public HasClickHandlers getNewClick() {
        return newFolderButton;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.hupa.client.mvp.IMAPFolderPresenter.Display#getDeleteConfirmDialog
     * ()
     */
    public HasDialog getDeleteConfirmDialog() {
        return confirmFolderDeleteBox;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.hupa.client.mvp.IMAPFolderPresenter.Display#getDeleteConfirmClick
     * ()
     */
    public HasClickHandlers getDeleteConfirmClick() {
        return confirmFolderDeleteBox;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.hupa.client.mvp.IMAPFolderPresenter.Display#deleteSelectedFolder
     * ()
     */
    public void deleteSelectedFolder() {
        folderTree.getSelectedItem().remove();

        // Select the INBOX after delete folder
        for (int i = 0; i < folderTree.getItemCount(); i++) {
            IMAPTreeItem item = (IMAPTreeItem) folderTree.getItem(i);
            if (((IMAPFolder) item.getUserObject()).getFullName().equalsIgnoreCase(user.getSettings().getInboxFolderName())) {
                folderTree.setSelectedItem(item, true);
                break;
            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.hupa.client.mvp.IMAPFolderPresenter.Display#createFolder(org
     * .apache.hupa.client.widgets.EditHandler)
     */
    public HasEditable createFolder(EditHandler handler) {
        final IMAPTreeItem selected = (IMAPTreeItem) folderTree.getSelectedItem();
        IMAPFolder oldFolder = (IMAPFolder) selected.getUserObject();

        // Generate a new folder with a whitespace as name, this is needed as
        // workaround
        IMAPFolder folder = new IMAPFolder(oldFolder.getFullName() + oldFolder.getDelimiter() + " ");
        folder.setDelimiter(oldFolder.getDelimiter());

        final IMAPTreeItem newItem = new IMAPTreeItem(folder);

        // add the new item as child
        folderTree.getSelectedItem().addItem(newItem);
        newItem.addEditHandler(handler);
        newItem.addEditHandler(new EditHandler() {

            public void onEditEvent(EditEvent event) {
                if (event.getEventType().equals(EditEvent.EventType.Cancel)) {
                    // remove the folder
                    newItem.remove();
                    folderTree.setSelectedItem(selected, false);
                } else if (event.getEventType().equals(EditEvent.EventType.Stop)) {
                    // Select the new created folder and fire an event
                    folderTree.setSelectedItem(newItem, true);
                }

            }

        });
        // Expand the parent
        folderTree.getSelectedItem().setState(true, false);

        // Select the new folder and start editing it
        folderTree.setSelectedItem(newItem, false);
        newItem.startEdit();

        // reset the text of the new item (remove the whitespace)
        newItem.setText("");

        return newItem;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.apache.hupa.client.mvp.IMAPFolderPresenter.Display#
     * decreaseUnseenMessageCount(org.apache.hupa.shared.data.IMAPFolder, int)
     */
    public void decreaseUnseenMessageCount(IMAPFolder folder, int amount) {
        int count = folderTree.getItemCount();
        for (int i = 0; i < count; i++) {
            IMAPTreeItem item = findTreeItemForFolder((IMAPTreeItem) folderTree.getItem(i), folder);
            if (item != null) {
                item.descreaseUnseenMessageCount(amount);
                break;
            }

        }
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.apache.hupa.client.mvp.IMAPFolderPresenter.Display#
     * increaseUnseenMessageCount(org.apache.hupa.shared.data.IMAPFolder, int)
     */
    public void increaseUnseenMessageCount(IMAPFolder folder, int amount) {
        int count = folderTree.getItemCount();
        for (int i = 0; i < count; i++) {
            IMAPTreeItem item = findTreeItemForFolder((IMAPTreeItem) folderTree.getItem(i), folder);
            if (item != null) {
                item.increaseUnseenMessageCount(amount);
                break;
            }

        }
    }

    
    private IMAPTreeItem findTreeItemForFolder(IMAPTreeItem item, IMAPFolder folder) {
        if (folder.getFullName().equalsIgnoreCase(((IMAPFolder) item.getUserObject()).getFullName())) {
            return item;
        }
        for (int i = 0; i < item.getChildCount(); i++) {
            IMAPTreeItem tItem = findTreeItemForFolder((IMAPTreeItem) item.getChild(i), folder);
            if (tItem != null) {
                return tItem;
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.hupa.client.mvp.MainPresenter.Display#updateTreeItem(org.apache.hupa.shared.data.IMAPFolder)
     */
    public void updateTreeItem(IMAPFolder folder) {
        int count = folderTree.getItemCount();
        for (int i = 0; i < count; i++) {
            IMAPTreeItem item = findTreeItemForFolder((IMAPTreeItem) folderTree.getItem(i), folder);
            if (item != null) {
                item.setUserObject(folder);
                break;
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see net.customware.gwt.presenter.client.widget.WidgetContainerDisplay#addWidget(com.google.gwt.user.client.ui.Widget)
     */
    public void addWidget(Widget widget) {
        showWidget(widget);
    }

    /*
     * (non-Javadoc)
     * @see net.customware.gwt.presenter.client.widget.WidgetContainerDisplay#removeWidget(com.google.gwt.user.client.ui.Widget)
     */
    public void removeWidget(Widget widget) {
        centerWidget = null;
        center.remove(widget); 
    }

    /*
     * (non-Javadoc)
     * @see net.customware.gwt.presenter.client.widget.WidgetContainerDisplay#showWidget(com.google.gwt.user.client.ui.Widget)
     */
    public void showWidget(Widget widget) {
        centerWidget = widget;
        center.setWidget(centerWidget); 
    }


    /*
     * (non-Javadoc)
     * @see org.apache.hupa.client.mvp.NameAwareDisplay#getName()
     */
    public String getName() {
        return "Email";
    }
}
