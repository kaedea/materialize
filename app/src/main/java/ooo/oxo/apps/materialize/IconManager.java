/*
 * Materialize - Materialize all those not material
 * Copyright (C) 2015  XiNGRZ <xxx@oxo.ooo>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ooo.oxo.apps.materialize;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class IconManager {

    private static final String TAG = "IconManager";

    @SuppressWarnings("SpellCheckingInspection")
    private static final String NO_MEDIA = ".nomedia";

    private final File root;

    public IconManager(Context context) {
        root = new File(context.getExternalCacheDir(), "icons");
        if (!root.mkdirs() && !root.isDirectory()) {
            Log.e(TAG, "Failed to create icons directory at " + root.getAbsolutePath());
        }
    }

    public void save(AppInfo app, Bitmap icon) {
        File file = makeIconFile(app);

        if (file == null) {
            Log.e(TAG, "Failed to prepare icon path for " + app.component.flattenToString());
            return;
        }

        OutputStream output;

        try {
            output = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Failed to write icon to " + file.getAbsolutePath(), e);
            return;
        }

        if (!icon.compress(Bitmap.CompressFormat.PNG, 100, output)) {
            Log.e(TAG, "Failed to compress icon to " + file.getAbsolutePath());
        }

        try {
            output.close();
        } catch (IOException ignored) {
        }

        Log.d(TAG, "Cached icon " + file.getAbsolutePath());
    }

    public void delete(AppInfo app) {
        File file = makeIconFile(app);
        if (file != null && file.isFile() && !file.delete()) {
            Log.e(TAG, "Failed to delete icon at " + file.getAbsolutePath());
        }
    }

    @Nullable
    public Uri get(AppInfo app) {
        File file = makeIconFile(app);

        if (file == null || !file.isFile()) {
            return null;
        } else {
            Log.d(TAG, "Resolved cached icon " + file.getAbsolutePath());
            return Uri.fromFile(file);
        }
    }

    @Nullable
    private File makeIconFile(AppInfo app) {
        File dir = new File(root, app.component.getPackageName());

        if (!dir.mkdirs() && !dir.isDirectory()) {
            Log.e(TAG, "Failed to create icon directory for package at " + dir.getAbsolutePath());
            return null;
        }

        createNoMediaFile(dir);

        return new File(dir, app.component.getClassName());
    }

    private boolean createNoMediaFile(File dir) {
        try {
            File noMedia = new File(dir, NO_MEDIA);
            return noMedia.isFile() || noMedia.createNewFile();
        } catch (IOException e) {
            return false;
        }
    }

}
