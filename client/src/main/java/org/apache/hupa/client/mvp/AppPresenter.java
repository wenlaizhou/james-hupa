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

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.widget.WidgetContainerDisplay;
import net.customware.gwt.presenter.client.widget.WidgetContainerPresenter;

import org.apache.hupa.client.HupaCallback;
import org.apache.hupa.shared.data.User;
import org.apache.hupa.shared.events.LoginEvent;
import org.apache.hupa.shared.events.LoginEventHandler;
import org.apache.hupa.shared.events.LogoutEvent;
import org.apache.hupa.shared.events.LogoutEventHandler;
import org.apache.hupa.shared.events.ServerStatusEvent;
import org.apache.hupa.shared.events.ServerStatusEventHandler;
import org.apache.hupa.shared.events.SessionExpireEvent;
import org.apache.hupa.shared.events.SessionExpireEventHandler;
import org.apache.hupa.shared.events.ServerStatusEvent.ServerStatus;
import org.apache.hupa.shared.rpc.CheckSession;
import org.apache.hupa.shared.rpc.CheckSessionResult;
import org.apache.hupa.shared.rpc.LogoutUser;
import org.apache.hupa.shared.rpc.LogoutUserResult;
import org.apache.hupa.shared.rpc.Noop;
import org.apache.hupa.shared.rpc.NoopResult;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasText;
import com.google.inject.Inject;

/**
 * Presenter which flips the in the LoginPresenter or the LoginPresenter depending on if the user is 
 * logged in or not
 *
 */
public class AppPresenter extends WidgetContainerPresenter<AppPresenter.Display>{

    private static final int NOOP_INTERVAL = 15000;

    public interface Display extends WidgetContainerDisplay {
        public HasClickHandlers getLogoutClick();
        public void showTopNavigation(boolean show);
        public HasText getUserText();
        public void setServerStatus(ServerStatus status);
    }

    private Timer noopTimer = new NoopTimer();

    private DispatchAsync dispatcher;
    private User user;
    private ServerStatus serverStatus = ServerStatus.Available;
    private ContainerPresenter containerPresenter;
    private LoginPresenter loginPresenter;
    
    @Inject
    public AppPresenter(Display display, DispatchAsync dispatcher,final EventBus bus, LoginPresenter loginPresenter, ContainerPresenter containerPresenter) {
        super(display,bus, loginPresenter, containerPresenter);
        this.containerPresenter = containerPresenter;
        this.loginPresenter = loginPresenter;
        this.dispatcher = dispatcher;  
    }

    private void showMain(User user) {
        display.showTopNavigation(true);
        containerPresenter.revealDisplay(user);
    }
    
    
    private void showLogin(String username) {
        display.showTopNavigation(false);
        loginPresenter.revealDisplay();
    }

    @Override
    protected void onBind() {
        super.onBind();
        registerHandler(eventBus.addHandler(LoginEvent.TYPE, new LoginEventHandler() {

            public void onLogin(LoginEvent event) {
                user = event.getUser();
                display.getUserText().setText(event.getUser().getName());
                noopTimer.scheduleRepeating(NOOP_INTERVAL);
                showMain(user);
            }

        }));
        
        registerHandler(eventBus.addHandler(LogoutEvent.TYPE, new LogoutEventHandler() {

            public void onLogout(LogoutEvent event) {
                User u = event.getUser();
                String username = null;
                if (u != null) {
                    username = u.getName();
                }
                showLogin(username);
                noopTimer.cancel();
            }
            
        }));
        registerHandler(display.getLogoutClick().addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                doLogout();
            }
            
        }));
        registerHandler(Window.addWindowClosingHandler(new ClosingHandler() {

            public void onWindowClosing(ClosingEvent event) {
                // TODO: When the application is loaded, it checks if there is already a valid session in the server.
                // Executing here doLogout makes the application remove the user session when 
                // the user closes the window or reloads the application.
                // It is better let the user decide when to logout instead of do it automatically
                // doLogout();
            }
            
        }));
        registerHandler(eventBus.addHandler(SessionExpireEvent.TYPE, new SessionExpireEventHandler() {

            public void onSessionExpireEvent(SessionExpireEvent event) {
                doLogout();
            }
            
        }));
        registerHandler(eventBus.addHandler(ServerStatusEvent.TYPE, new ServerStatusEventHandler() {
            
            public void onServerStatusChange(ServerStatusEvent event) {
                if (event.getStatus() != serverStatus) {
                    GWT.log("Server status has hanged from " + serverStatus + " to" + event.getStatus(), null);
                    serverStatus = event.getStatus();
                    display.setServerStatus(serverStatus);
                }
            }
            
        }));

        checkSession();
    }
    

    private void doLogout() {
        if (user != null) {
            dispatcher.execute(new LogoutUser(), new HupaCallback<LogoutUserResult>(dispatcher, eventBus) {
                public void callback(LogoutUserResult result) {
                    eventBus.fireEvent(new LogoutEvent(result.getUser()));
                }
            });
        }
    }

    private void checkSession() {
        dispatcher.execute(new CheckSession(), new AsyncCallback<CheckSessionResult>() {
            public void onFailure(Throwable caught) {
                serverStatus = ServerStatus.Unavailable;
                display.setServerStatus(serverStatus);
                showLogin(null);
            }
            public void onSuccess(CheckSessionResult result) {
                serverStatus = ServerStatus.Available;
                display.setServerStatus(serverStatus);
                if (result.isValid()) {
                    eventBus.fireEvent(new LoginEvent(result.getUser()));
                } else {
                    showLogin(null);
                }
            }
        });
    }
    
    private class NoopTimer extends Timer {
        boolean running = false;
        public void run() {
            if (!running) {
                running = true;
                dispatcher.execute(new Noop(), new HupaCallback<NoopResult>(dispatcher, eventBus) {
                    public void callback(NoopResult result) {
                        running = false;
                        // Noop
                        // TODO: put code here to read new events from server (new messages ...)
                    }
                });
            }
        }
    }

    
}
