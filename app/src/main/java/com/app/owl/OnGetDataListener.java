package com.app.owl;

import com.google.firebase.database.DataSnapshot;

/**
 * Created by Christiane on 1/23/18.
 */

public interface OnGetDataListener {
    void onSuccess(DataSnapshot dataSnapshot);
    void onStart();
    void onFailure();
}
