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

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.mind.mc.dto.MovieDTO;

public interface MovieChartView
{
    void setDisplay(AcceptsOneWidget display);

    void setListener(Listener listener);

    void setMovies(List<MovieDTO> movies);

    void setMovieRating(int movieIndex, float rating);

    public interface Listener
    {
        void onMovieRate(int index, byte rate);

        void onLogout();
    }

}