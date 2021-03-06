/*
 * Copyright 2012 Mind Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
*/
package com.mind.mc.client.activities.mc;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.mind.mc.dto.MovieDTO;

public class MovieChartViewImpl extends Composite implements MovieChartView
{
    interface Ui extends UiBinder<Widget, MovieChartViewImpl> {}

    private static Ui ui = GWT.create(Ui.class);
    
    private Listener listener;

    public MovieChartViewImpl()
    {
        initWidget(ui.createAndBindUi(this));
    }

    @Override
    public void setDisplay(AcceptsOneWidget display)
    {
        display.setWidget(this);
    }

    @Override
    public void setListener(Listener listener)
    {
        this.listener = listener;
    }

    @Override
    public void setMovies(List<MovieDTO> movies)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void setMovieRating(int movieIndex, float rating)
    {
        // TODO Auto-generated method stub
    }

    @UiHandler("logoutButton")
    public void onLogoutButtonClick(ClickEvent event)
    {
        listener.onLogout();
    }

}