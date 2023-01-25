package com.usman.dms.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue( "This is home fragment" );
    }

    public LiveData<String> getText() {
        return mText;
    }
}

//Set Data In Graph

//    private void setData(int count, float range) {
//        ArrayList<PieEntry> entries = new ArrayList<>();
//
//        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
//        // the chart.
//        for (int i = 0; i < count; i++) {
//            entries.add( new PieEntry( (float) ((Math.random() * range) + range / 5),
//                    parties[i % parties.length],
//                    getResources().getDrawable( R.drawable.star ) ) );
//        }
//        PieDataSet dataSet = new PieDataSet(entries, "Election Results");
//
//        dataSet.setDrawIcons(false);
//
//        dataSet.setSliceSpace(3f);
//        dataSet.setIconsOffset(new MPPointF(0, 40));
//        dataSet.setSelectionShift(5f);
//
//        // add a lot of colors
//
//        ArrayList<Integer> colors = new ArrayList<>();
//
//        for (int c : ColorTemplate.VORDIPLOM_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.JOYFUL_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.COLORFUL_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.LIBERTY_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.PASTEL_COLORS)
//            colors.add(c);
//
//        colors.add( ColorTemplate.getHoloBlue());
//
//        dataSet.setColors(colors);
//        //dataSet.setSelectionShift(0f);
//
//        PieData data = new PieData(dataSet);
//        data.setValueFormatter(new PercentFormatter());
//        data.setValueTextSize(11f);
//        data.setValueTextColor( Color.WHITE);
//        data.setValueTypeface(tfLight);
//        chart.setData(data);
//
//        // undo all highlights
//        chart.highlightValues(null);
//
//        chart.invalidate();
//    }