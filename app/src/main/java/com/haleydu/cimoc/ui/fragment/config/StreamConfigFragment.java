package com.haleydu.cimoc.ui.fragment.config;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haleydu.cimoc.R;
import com.haleydu.cimoc.component.DialogCaller;
import com.haleydu.cimoc.global.ClickEvents;
import com.haleydu.cimoc.manager.PreferenceManager;
import com.haleydu.cimoc.ui.activity.settings.EventSettingsActivity;
import com.haleydu.cimoc.ui.fragment.BaseFragment;
import com.haleydu.cimoc.ui.fragment.dialog.ChoiceDialogFragment;
import com.haleydu.cimoc.ui.widget.Option;
import com.haleydu.cimoc.ui.widget.preference.CheckBoxPreference;
import com.haleydu.cimoc.ui.widget.preference.ChoicePreference;

/**
 * Created by Hiroshi on 2016/10/13.
 */

public class StreamConfigFragment extends BaseFragment implements DialogCaller {

    private static final int DIALOG_REQUEST_ORIENTATION = 0;
    private static final int DIALOG_REQUEST_TURN = 1;
    private static final int DIALOG_REQUEST_OPERATION = 3;

    private static final int OPERATION_VOLUME_UP = 0;
    private static final int OPERATION_VOLUME_DOWN = 1;

    CheckBoxPreference mReaderInterval;
    CheckBoxPreference mReaderLoadPrev;
    CheckBoxPreference mReaderLoadNext;
    ChoicePreference mReaderOrientation;
    ChoicePreference mReaderTurn;

    @Override
    protected void initView() {
        mReaderInterval = mView.findViewById(R.id.settings_reader_interval);
        mReaderLoadPrev = mView.findViewById(R.id.settings_reader_load_prev);
        mReaderLoadNext = mView.findViewById(R.id.settings_reader_load_next);
        mReaderOrientation = mView.findViewById(R.id.settings_reader_orientation);
        mReaderTurn = mView.findViewById(R.id.settings_reader_turn);

        Option readerClickEvent = mView.findViewById(R.id.settings_reader_click_event);
        readerClickEvent.setOnClickListener(view1 -> {
            Intent intent = EventSettingsActivity.createIntent(getActivity(), false,
                    mReaderOrientation.getValue(), true);
            startActivity(intent);
        });

        Option readerLongClickEvent = mView.findViewById(R.id.settings_reader_long_click_event);
        readerLongClickEvent.setOnClickListener(view1 -> {
            Intent intent = EventSettingsActivity.createIntent(getActivity(), true,
                    mReaderOrientation.getValue(), true);
            startActivity(intent);
        });

        mReaderInterval.bindPreference(PreferenceManager.PREF_READER_STREAM_INTERVAL, false);
        mReaderLoadPrev.bindPreference(PreferenceManager.PREF_READER_STREAM_LOAD_PREV, false);
        mReaderLoadNext.bindPreference(PreferenceManager.PREF_READER_STREAM_LOAD_NEXT, true);
        mReaderOrientation.bindPreference(requireActivity().getSupportFragmentManager(), this, PreferenceManager.PREF_READER_STREAM_ORIENTATION,
                PreferenceManager.READER_ORIENTATION_AUTO, R.array.reader_orientation_items, DIALOG_REQUEST_ORIENTATION);
        mReaderTurn.bindPreference(requireActivity().getSupportFragmentManager(), this, PreferenceManager.PREF_READER_STREAM_TURN,
                PreferenceManager.READER_TURN_LTR, R.array.reader_turn_items, DIALOG_REQUEST_TURN);
    }

    private void showEventList(int index) {
        int[] mChoiceArray = ClickEvents.getStreamClickEventChoice(mPreference);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Context context = this.getContext();
            ChoiceDialogFragment fragment = ChoiceDialogFragment.newInstance(R.string.event_select,
                    ClickEvents.getEventTitleArray(context), mChoiceArray[index], index);
            fragment.show(requireActivity().getSupportFragmentManager(), null);
        }
    }

    @Override
    public void onDialogResult(int requestCode, Bundle bundle) {
        switch (requestCode) {
            case DIALOG_REQUEST_ORIENTATION:
                mReaderOrientation.setValue(bundle.getInt(EXTRA_DIALOG_RESULT_INDEX));
                break;
            case DIALOG_REQUEST_TURN:
                mReaderTurn.setValue(bundle.getInt(EXTRA_DIALOG_RESULT_INDEX));
                break;
            case DIALOG_REQUEST_OPERATION:
                int index = bundle.getInt(EXTRA_DIALOG_RESULT_INDEX);
                switch (index) {
                    case OPERATION_VOLUME_UP:
                        showEventList(5);
                        break;
                    case OPERATION_VOLUME_DOWN:
                        showEventList(6);
                        break;
                }
                break;
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_stream_config;
    }

}
