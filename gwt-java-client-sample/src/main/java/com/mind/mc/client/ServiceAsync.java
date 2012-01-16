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
package com.mind.mc.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.mind.mc.dto.MovieDTO;

public interface ServiceAsync
{
    void login(String login, String password, AsyncCallback<Void> callback);

    void getMovieList(AsyncCallback<ArrayList<MovieDTO>> callback);

    void rateMovie(long movieId, byte rate, AsyncCallback<Void> callback);

    void logout(AsyncCallback<Void> callback);

}