package com.sickboots.sickvideos.lists;

import android.os.AsyncTask;

import com.sickboots.sickvideos.database.BaseDatabase;
import com.sickboots.sickvideos.database.PlaylistDatabase;
import com.sickboots.sickvideos.database.VideoDatabase;
import com.sickboots.sickvideos.database.YouTubeData;
import com.sickboots.sickvideos.misc.ApplicationHub;
import com.sickboots.sickvideos.misc.PreferenceCache;
import com.sickboots.sickvideos.misc.Util;
import com.sickboots.sickvideos.youtube.YouTubeAPI;

import java.util.List;
import java.util.Map;

public class YouTubeListDB extends YouTubeList {
  YouTubeListDBTask runningTask = null;
  BaseDatabase database;

  public YouTubeListDB(YouTubeListSpec s, UIAccess a) {
    super(s, a);

    switch (s.type) {
      case RELATED:
      case SEARCH:
      case LIKED:
      case VIDEOS:
        database = new VideoDatabase(getActivity(), s.databaseName());

        break;
      case PLAYLISTS:
        database = new PlaylistDatabase(getActivity(), s.databaseName());
        break;
    }

    loadData(TaskType.FIRSTLOAD, true);
  }

  @Override
  public void updateHighestDisplayedIndex(int position) {
    // doi nothing - not used in DB list
  }

  @Override
  public void refresh() {
    loadData(TaskType.USER_REFRESH, false);
  }

  @Override
  protected void loadData(TaskType taskType, boolean askUser) {
    YouTubeAPI helper = youTubeHelper(askUser);

    if (helper != null) {
      if (runningTask == null) {
        boolean showHiddenVideos = ApplicationHub.preferences().getBoolean(PreferenceCache.SHOW_HIDDEN_VIDEOS, false);

        runningTask = new YouTubeListDBTask(taskType, showHiddenVideos);
        runningTask.execute(helper);
      }
    }
  }

  @Override
  public void updateItem(YouTubeData itemMap) {
    database.updateItem(itemMap);

    // reload the data so the UI updates
    loadData(TaskType.REFETCH, false);
  }

  private class YouTubeListDBTask extends AsyncTask<YouTubeAPI, Void, List<YouTubeData>> {
    boolean mFilterHidden = false;
    TaskType mTaskType;

    public YouTubeListDBTask(TaskType taskType, boolean showHiddenVideos) {
      super();

      mFilterHidden = !showHiddenVideos;
      mTaskType = taskType;
    }

    protected List<YouTubeData> doInBackground(YouTubeAPI... params) {
      YouTubeAPI helper = params[0];
      List<YouTubeData> result = null;

      Util.log("YouTubeListDBTask: started");

      database.setFlags(mFilterHidden ? VideoDatabase.FILTER_HIDDEN_ITEMS : 0);

      switch (mTaskType) {
        case USER_REFRESH:
          /*
            - save hidden state
            - delete old data
            - fetch from internet (don't delete first incase internet is down then we would just delete what we had)
            - add back saved hidden data

            NET: always
            DEL: always
          */

          // does this need to go in a task?
          Map currentListSavedData=null;
          result = loadFreshDataToDatabase(helper, currentListSavedData);

          break;
        case REFETCH:
          //  item hidden, or hidden visible pref toggled
          result = database.getItems();
          break;
        case FIRSTLOAD:
          // are the results already in the DB?
          result = database.getItems();

          // this is lame, fix later
          if (result.size() == 0) {
            result = loadFreshDataToDatabase(helper, null);
          }

          break;
      }

      return result;
    }

    private List<YouTubeData> loadFreshDataToDatabase(YouTubeAPI helper, Map currentListSavedData) {
      List<YouTubeData> result = getDataFromInternet(helper);

      if (result != null) {

        result = prepareDataFromNet(result, currentListSavedData);

        // we are only deleting if we know we got good data
        // otherwise if we delete first a network failure would just make the app useless
        database.deleteAllRows();

        database.insertItems(result);
      }

      return result;
    }

    private List<YouTubeData> prepareDataFromNet(List<YouTubeData> inList, Map currentListSavedData) {
      List<YouTubeData> result = inList;

      if (currentListSavedData != null && currentListSavedData.size() > 0) {

      }

      return result;
    }

    private List<YouTubeData> getDataFromInternet(YouTubeAPI helper) {
      List<YouTubeData> result = null;

      YouTubeAPI.BaseListResults listResults = null;

      switch (type()) {
        case RELATED:
          YouTubeAPI.RelatedPlaylistType type = (YouTubeAPI.RelatedPlaylistType) listSpec.getData("type");
          String channelID = (String) listSpec.getData("channel");

          String playlistID = helper.relatedPlaylistID(type, channelID);

          listResults = helper.videoListResults(playlistID, true);
          break;
        case SEARCH:
        case LIKED:
        case VIDEOS:
          break;
        case PLAYLISTS:
          String channel = (String) listSpec.getData("channel");

          listResults = helper.playlistListResults(channel, true);
          break;
        case SUBSCRIPTIONS:
          listResults = helper.subscriptionListResults();
          break;
        case CATEGORIES:
          break;
      }

      if (listResults != null) {
        while (listResults.getNext()) {
          // getting all
        }

        result = listResults.getItems();
      }

      return result;
    }

    protected void onPostExecute(List<YouTubeData> result) {

      Util.log("YouTubeListDBTask: finished");

      items = result;
      access.onListResults();

      runningTask = null;
    }
  }

}
