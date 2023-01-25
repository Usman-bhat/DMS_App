package com.usman.dms.ui.debit;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.usman.dms.databinding.DebitSingleRowBinding;
import com.usman.dms.models.DebitModel;

public class DebitAdapter extends RecyclerView.Adapter<DebitAdapter.DebitViewHolder>
{
    String [] data1;
    DebitModel[] debitData;
    Activity activity;


    public DebitAdapter(DebitModel[] debitData, Activity activity) {
        this.debitData = debitData;
        this.activity = activity;

    }

    @NonNull
    @Override
    public DebitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from( parent.getContext() );
        DebitSingleRowBinding debitSingleRowBinding = DebitSingleRowBinding.inflate( layoutInflater,parent,false );
        return new DebitViewHolder( debitSingleRowBinding );
    }

    @Override
    public void onBindViewHolder(@NonNull DebitViewHolder holder, int position) {

        holder.bindDebitView(
                debitData[position].getDt_by(),
                String.valueOf( debitData[position].getDt_amount()),
                debitData[position].getDt_date(),
                String.valueOf( position ),
                debitData[position].getDt_for()
        );
    }

    @Override
    public int getItemCount() {
        return debitData.length;
    }


    /**
     * Array which We Supply TO bindView Must be like his.
     * data Array Rule = {"Receipt Given By","Amount","date","For"}
     */
    class DebitViewHolder extends RecyclerView.ViewHolder{

        DebitSingleRowBinding debitSingleRowBinding;
        public DebitViewHolder(@NonNull DebitSingleRowBinding debitSingleRowBinding ){
            super(debitSingleRowBinding.getRoot());
            this.debitSingleRowBinding = debitSingleRowBinding;
        }
        public void bindDebitView(String by,String amount,String timeStamp, String sno ,String for1){
            String[] date = timeStamp.split( " " );

            data1 = new String[4];
            data1[0]=by;
            data1[1]=amount;
            data1[2]=date[0];
            data1[3]=for1;

            debitSingleRowBinding.dtAmount.setText( "Rs: "+amount );
            debitSingleRowBinding.dtBy.setText( by);
            debitSingleRowBinding.dtDate.setText( date[0]);
            debitSingleRowBinding.dtSno.setText( sno );
        }
        }
}
