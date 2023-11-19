package com.swdp31plus.ninetyminutessleep.utilities;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swdp31plus.ninetyminutessleep.BuildConfig;
import com.swdp31plus.ninetyminutessleep.R;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
public class NetworkUtilities {

    public static class GitHubReleaseTask extends AsyncTask<Void, Void, Boolean> {
        private Context context;

        @Override
        protected Boolean doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();

            String releasesUrl = context.getString(R.string.github_link_releases);

            Request request = new Request.Builder()
                    .url(releasesUrl)
                    .cacheControl(CacheControl.FORCE_NETWORK)
                    .build();

            String latestRelease = null;

            try {
                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    String responseBody = response.body().string();

                    ObjectMapper objectMapper = new ObjectMapper();

                    JsonNode jsonNode = objectMapper.readTree(responseBody);

                    latestRelease = String.valueOf(jsonNode.get(0).get("tag_name"));
                    latestRelease = latestRelease.replace("v","");
                    latestRelease = latestRelease.replace("\"","");
                } else {
                    return null;
                }
            } catch (IOException e) {
                return null;
            }

            return checkForUpdates(BuildConfig.VERSION_NAME, latestRelease);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result != null) {
                if (result)
                    Toast.makeText(context, context.getString(R.string.latest_app), Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(context, context.getString(R.string.update_app), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, context.getString(R.string.error_update_app), Toast.LENGTH_LONG).show();
            }
        }

        private boolean checkForUpdates(String actual, String latest) {
            String[] actualSplit = actual.split("\\.");
            String[] latestSplit = latest.split("\\.");
            boolean isUpdated = true;

            int i;

            for (i = 0; i < Math.min(actualSplit.length, latestSplit.length) - 1; i++) {

                if (Integer.parseInt(actualSplit[i]) == Integer.parseInt(latestSplit[i])) {
                } else {
                    if (Integer.parseInt(actualSplit[i]) < Integer.parseInt(latestSplit[i])) {
                        isUpdated = false;
                    } else {
                        isUpdated = true;
                    }
                }
            }
            return isUpdated;
        }

        public void setContext(Context context) {
            this.context = context;
        }
    }

}