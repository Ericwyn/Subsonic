/*
	This file is part of Subsonic.

	Subsonic is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	Subsonic is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.

	Copyright 2016 (C) Scott Jackson
*/
package github.daneren2005.dsub.fragments;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import github.daneren2005.dsub.R;
import github.daneren2005.dsub.adapter.InternetRadioStationAdapter;
import github.daneren2005.dsub.adapter.SectionAdapter;
import github.daneren2005.dsub.domain.InternetRadioStation;
import github.daneren2005.dsub.service.DownloadService;
import github.daneren2005.dsub.service.MusicService;
import github.daneren2005.dsub.util.ProgressListener;
import github.daneren2005.dsub.util.TabBackgroundTask;
import github.daneren2005.dsub.util.Util;
import github.daneren2005.dsub.view.UpdateView;

public class SelectInternetRadioStationFragment extends SelectRecyclerFragment<InternetRadioStation> {
	@Override
	public int getOptionsMenu() {
		return R.menu.abstract_top_menu;
	}

	@Override
	public SectionAdapter<InternetRadioStation> getAdapter(List<InternetRadioStation> objs) {
		return new InternetRadioStationAdapter(context, objs, this);
	}

	@Override
	public List<InternetRadioStation> getObjects(MusicService musicService, boolean refresh, ProgressListener listener) throws Exception {
		return musicService.getInternetRadioStations(refresh, context, listener);
	}

	@Override
	public int getTitleResource() {
		return R.string.button_bar_internet_radio;
	}

	@Override
	public void onItemClicked(UpdateView<InternetRadioStation> updateView, final InternetRadioStation item) {
		new TabBackgroundTask<Void>(this) {
			@Override
			protected Void doInBackground() throws Throwable {
				DownloadService downloadService = getDownloadService();
				if(downloadService == null) {
					return null;
				}

				downloadService.download(item);
				return null;
			}

			@Override
			protected void done(Void result) {
				context.openNowPlaying();
			}
		}.execute();
	}

	@Override
	public void onCreateContextMenu(Menu menu, MenuInflater menuInflater, UpdateView<InternetRadioStation> updateView, InternetRadioStation item) {
		menuInflater.inflate(R.menu.select_internet_radio_context, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem menuItem, UpdateView<InternetRadioStation> updateView, InternetRadioStation item) {
		switch (menuItem.getItemId()) {
			case R.id.internet_radio_info:
				displayInternetRadioStationInfo(item);
				break;
		}

		return false;
	}

	private void displayInternetRadioStationInfo(final InternetRadioStation station) {
		List<Integer> headers = new ArrayList<>();
		List<String> details = new ArrayList<>();

		headers.add(R.string.details_title);
		details.add(station.getTitle());

		headers.add(R.string.details_home_page);
		details.add(station.getHomePageUrl());

		headers.add(R.string.details_stream_url);
		details.add(station.getStreamUrl());

		Util.showDetailsDialog(context, R.string.details_title_internet_radio_station, headers, details);
	}
}