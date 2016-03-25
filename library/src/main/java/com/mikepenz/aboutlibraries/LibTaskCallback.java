package com.mikepenz.aboutlibraries;

import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import java.io.Serializable;

/**
 * This Class was created by Patrick J
 * on 14.12.15. For more Details and Licensing
 * have a look at the README.md
 */
public interface LibTaskCallback extends Serializable {
    void onLibTaskStarted();

    void onLibTaskFinished(FastItemAdapter fastItemAdapter);
}