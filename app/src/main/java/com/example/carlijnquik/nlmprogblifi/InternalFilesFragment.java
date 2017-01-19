package com.example.carlijnquik.nlmprogblifi;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;

/**
 * Enables the user to view and open files
 */

public class InternalFilesFragment extends Fragment {

    ArrayList<FileObject> fileList;
    ArrayList<FileObject> driveFileList;
    FileAdapter adapter;
    RecyclerView rvFiles;
    String path;
    String location;

    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle.getString("filePath") != null) {
            path = bundle.getString("filePath");
            location = bundle.getString("fileLocation");
            Log.d("filePath", path);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_list, container, false);

        // create an array list to put the file objects in
        fileList = AllInternalFiles.getInstance().getFileList();
        driveFileList = AllDriveFiles.getInstance().getFileList();

        if(path == null || location == null){
            // get files from device storage via path
            getFiles(System.getenv("EXTERNAL_STORAGE"), "PHONE");

            // get files from sd card if present
            if(isExternalStorageWritable()){
                getFiles(System.getenv("SECONDARY_STORAGE"), "SD");
            }

            fileList.addAll(driveFileList);
        }
        else{
            // create an array list to put the file objects in
            fileList = new ArrayList<>();
            getFiles(path, location);
        }

        // set the adapter
        rvFiles = (RecyclerView) view.findViewById(R.id.rvFiles);
        adapter = new FileAdapter(getContext(), fileList);
        rvFiles.setAdapter(adapter);

        // Set layout manager to position the items
        rvFiles.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Adds the files from the given path to the array list */
    public void getFiles(String path, String location){
        File list = new File(path);
        File[] files = list.listFiles();

        Log.d("string filepath", path);
        Log.d("string location",location);

        // loop over the files and folders
        for (File file : files) {
            Log.d("string file", file.getName());
            fileList.add(new FileObject(null, file, location, "file"));

        }


    }

}
