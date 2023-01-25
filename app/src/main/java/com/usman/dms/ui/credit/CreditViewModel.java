package com.usman.dms.ui.credit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CreditViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public CreditViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue( "This is dashboard fragment" );
    }

    public LiveData<String> getText() {
        return mText;
    }
}