package com.example.carlijnquik.nlmprogblifi;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 * An asynchronous task that handles the Drive API call and puts the retrieved files in a list.
 */

public class ListDriveFilesAsyncTask extends AsyncTask<Void, Void, ArrayList<FileObject>> {

    private com.google.api.services.drive.Drive mService = null;
    private Exception mLastError = null;
    ArrayList<FileObject> driveFiles;
    ArrayList<FileObject> trashedFiles;
    // constructor
    ListDriveFilesAsyncTask(GoogleAccountCredential credential) {
        // connect to the Drive service
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.drive.Drive.Builder(transport, jsonFactory, credential)
                .setApplicationName("File Manager")
                .build();

    }

    /**
     * Background task to call Drive API.
     */
    @Override
    protected ArrayList<FileObject> doInBackground(Void... params) {
        try {
            return getDataFromApi();
        } catch (Exception e) {
            mLastError = e;
            cancel(true);
            return null;
        }

    }

    /**
     * Retrieve the Drive files and put them in the Drive Files Singleton.
     */
    private ArrayList<FileObject> getDataFromApi() throws IOException {
        // get the Drive files from the API
        FileList result = mService.files().list()
                .setFields("nextPageToken, files")
                .execute();
        List<File> files = result.getFiles();

        driveFiles = null;

        if (files != null) {
            // get the singleton
            driveFiles = DriveFilesSingleton.getInstance().getFileList();
            trashedFiles = TrashedFilesSingleton.getInstance().getFileList();

            // loop over the files and add them to the singleton
            for (File file : files) {
                Log.d("string driveFile", file.getName());
                if (!file.getTrashed()) {
                    driveFiles.add(new FileObject(file, null, "DRIVE", file.getMimeType()));
                }
                else {
                    trashedFiles.add(new FileObject(file, null, "DRIVE", file.getMimeType()));
                }

            }

        }

        // return type still to be changed for best practices
        return driveFiles;

    }

    @Override
    protected void onPostExecute(ArrayList<FileObject> output) {
        // check if there is output
        if (output == null || output.size() == 0) {
            Log.d("string no", "no results");
        } else {
            Log.d("string yes", "results");
        }

    }

    /**
     * Handles exceptions (needs to be rewritten to be compatible with an async task).
     */
    @Override
    protected void onCancelled() {

        if (mLastError != null) {
            if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                //showGooglePlayServicesAvailabilityErrorDialog(
                     //   ((GooglePlayServicesAvailabilityIOException) mLastError)
                       //         .getConnectionStatusCode());
            } else if (mLastError instanceof UserRecoverableAuthIOException) {
                //startActivityForResult(
                  //      ((UserRecoverableAuthIOException) mLastError).getIntent(),
                    //    CredentialActivity.REQUEST_AUTHORIZATION);
            } else {
                //mOutputText.setText("The following error occurred:\n"
                  //      + mLastError.getMessage());
            }
        } else {
            //mOutputText.setText("Request cancelled.");
        }

    }

}




