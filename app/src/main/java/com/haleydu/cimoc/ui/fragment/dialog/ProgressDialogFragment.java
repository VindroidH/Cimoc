package com.haleydu.cimoc.ui.fragment.dialog;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.haleydu.cimoc.R;
import com.haleydu.cimoc.rx.RxBus;
import com.haleydu.cimoc.rx.RxEvent;
import com.haleydu.cimoc.utils.ThemeUtils;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Hiroshi on 2016/10/14.
 */

public class ProgressDialogFragment extends DialogFragment {

    ProgressBar mProgressBar;
    TextView mTextView;

    private CompositeSubscription mCompositeSubscription;

    public static ProgressDialogFragment newInstance() {
        return new ProgressDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_progress, container, false);
        mProgressBar = view.findViewById(R.id.dialog_progress_bar);
        mTextView = view.findViewById(R.id.dialog_progress_text);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(false);
        int resId = ThemeUtils.getResourceId(getActivity(), androidx.appcompat.R.attr.colorAccent);
        mProgressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getActivity(), resId), PorterDuff.Mode.SRC_ATOP);
        mCompositeSubscription = new CompositeSubscription();
        mCompositeSubscription.add(RxBus.getInstance().toObservable(RxEvent.EVENT_DIALOG_PROGRESS).subscribe(new Action1<RxEvent>() {
            @Override
            public void call(RxEvent rxEvent) {
                mTextView.setText((String) rxEvent.getData());
            }
        }));
        return view;
    }

    @Override
    public void onDestroyView() {
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }
        super.onDestroyView();
    }

}
