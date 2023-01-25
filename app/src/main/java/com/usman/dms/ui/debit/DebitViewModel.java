package com.usman.dms.ui.debit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DebitViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public DebitViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue( "This is notifications fragment" );
    }

    public LiveData<String> getText() {
        return mText;
    }
}