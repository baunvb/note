package com.baunvb.note.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.baunvb.note.R;

/**
 * Created by Bau NV on 5/30/2017.
 */

public abstract class ConfirmDeleteDialog extends AlertDialog.Builder {
    public ConfirmDeleteDialog(Context context) {
        super(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.notification_delete))
                .setCancelable(false)
                .setTitle(context.getString(R.string.delete_label))
                .setPositiveButton(context.getString(R.string.yes_label),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deleteSomethings();
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(context.getString(R.string.no_label),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public abstract void deleteSomethings();
}
