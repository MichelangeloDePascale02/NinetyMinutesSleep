package com.swdp31plus.ninetyminutessleep.utilities;

import android.content.Context;
import android.widget.Toast;

import com.swdp31plus.ninetyminutessleep.entities.NewAlarm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class StorageUtilities {

    public static void saveAlarms(Object obj, String path, Context context) {
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(path, Context.MODE_PRIVATE);
            ObjectOutputStream os = null;
            try {
                os = new ObjectOutputStream(fos);
                os.writeObject(obj);
                os.close();
                fos.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(context,"File not saved", Toast.LENGTH_LONG).show();
        }
    }
    public static Object loadAlarms(String path, Context context) throws NullPointerException {
        FileInputStream fis = null;
        ArrayList<NewAlarm> readAlarmList = null;
        try {
            fis = context.openFileInput(path);
            ObjectInputStream is = null;
            try {
                is = new ObjectInputStream(fis);
                readAlarmList = (ArrayList<NewAlarm>) is.readObject();
                is.close();
                fis.close();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(context,"File not loaded", Toast.LENGTH_LONG).show();
        }
        return readAlarmList;
    }
    public static Object loadObject(String path, Context context) throws NullPointerException {
        FileInputStream fis = null;
        Object readAlarm = null;
        try {
            fis = context.openFileInput(path);
            ObjectInputStream is = null;
            try {
                is = new ObjectInputStream(fis);
                readAlarm = is.readObject();
                is.close();
                fis.close();
            } catch (IOException | ClassNotFoundException e) {
                Toast.makeText(context,"File not loaded", Toast.LENGTH_LONG).show();
                throw new RuntimeException(e);
            }
        } catch (FileNotFoundException ignored) {
        }
        return readAlarm;
    }
    public static void saveAlarm(Object obj, String path, Context context) {
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(path, Context.MODE_PRIVATE);
            ObjectOutputStream os = null;
            try {
                os = new ObjectOutputStream(fos);
                os.writeObject(obj);
                os.close();
                fos.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(context,"File not saved", Toast.LENGTH_LONG).show();
        }
    }
}
