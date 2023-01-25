package com.usman.dms.ui.credit;

import static com.usman.dms.StaticData.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.usman.dms.R;
import com.usman.dms.databinding.CreditSingleRowBinding;
import com.usman.dms.inters.CreditRecInterface;
import com.usman.dms.models.CreditModel;


import java.io.File;
import java.io.FileOutputStream;
import java.security.Permission;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreditAdapter extends RecyclerView.Adapter<CreditAdapter.Holder>
{

    CreditModel[] credit_data;
    Activity activity;
    CreditRecInterface creditRecInterface;
    String data1[];

    public CreditAdapter( CreditModel[] credit_data, Activity activity,CreditRecInterface creditRecInterface) {
        this.credit_data = credit_data;
        this.activity = activity;
        this.creditRecInterface = creditRecInterface;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from( parent.getContext() );
        CreditSingleRowBinding creditSingleRowBinding = CreditSingleRowBinding.inflate( layoutInflater,parent,false );
        return new Holder( creditSingleRowBinding );
//        View vw = LI.inflate( R.layout.credit_single_row, null);
//        return new CreditAdapter.Holder(vw);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, @SuppressLint("RecyclerView") int position) {

        // data Array Rule = {"Giver Name","Amount","For","Receipt number","Reciept Given By","Date"}
            holder.bindView( String.valueOf( credit_data[position].getCr_amount() ),credit_data[position].getCr_date(),
                    String.valueOf( position ),credit_data[position].getCr_reciept_no()
            ,credit_data[position].getCr_for_id(),credit_data[position].getCr_by(),
                    credit_data[position].getCr_reciept_by()
            );

        holder.creditSingleRowBinding.viewCredit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] recpData = {credit_data[position].getCr_by(), String.valueOf( credit_data[position].getCr_amount() ),credit_data[position].getCr_for_id(),credit_data[position].getCr_reciept_no(),credit_data[position].getCr_reciept_by(),credit_data[position].getCr_date()};
                Log.e( TAG, "onClick: "+credit_data[position].getCr_by()+ String.valueOf( credit_data[position].getCr_amount() )+ credit_data[position].getCr_for_id()+
                        credit_data[position].getCr_reciept_no()+credit_data[position].getCr_reciept_by()+credit_data[position].getCr_date() );
                loadData( view.getContext(), recpData);
            }
        } );

    }

    @Override
    public int getItemCount() {
        return credit_data.length;
    }

    class Holder extends RecyclerView.ViewHolder{
        CreditSingleRowBinding creditSingleRowBinding;
        public Holder(@NonNull CreditSingleRowBinding creditSingleRowBinding ){
            super(creditSingleRowBinding.getRoot());
            this.creditSingleRowBinding = creditSingleRowBinding;
            // data Array Rule = {"Giver Name","Amount","For","Receipt number","Reciept Given By","Date"}


            creditSingleRowBinding.viewCredit.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    creditRecInterface.onItemClick( getAdapterPosition(),data1 );
                }
            } );
        }
        public void bindView(String amount,String timeStamp, String sno ,String recNo,String for1,String by,String recBy){
            String[] date = timeStamp.split( " " );

            data1 = new String[6];
            data1[0]=by;
            data1[1]=amount;
            data1[2]=for1;
            data1[3]=recNo;
            data1[4]=recBy;
            data1[5]=date[0];
            creditSingleRowBinding.amount.setText( creditSingleRowBinding.amount.getResources().getString( R.string.amount )+": "+amount );
            creditSingleRowBinding.date.setText( date[0]);
            creditSingleRowBinding.sno.setText( sno );
        }
    }

    private void loadData(Context context, String[] data) {
        Dialog dialog = new Dialog(context );
        dialog.setContentView( R.layout.show_credit_details_dialog );
        dialog.getWindow().setLayout( ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT );
        dialog.setCancelable( true );
        dialog.getWindow().setBackgroundDrawableResource( android.R.color.transparent );
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;

        TextView amount = dialog.findViewById( R.id.total_amount );
        TextView amountBy = dialog.findViewById( R.id.amount_by );
        TextView recieptBy = dialog.findViewById( R.id.amount_rec_by );
        TextView recieptNo = dialog.findViewById( R.id.amount_rec );
        TextView amountfor = dialog.findViewById( R.id.amount_for );
        TextView recieptDate = dialog.findViewById( R.id.amount_date );
        LinearLayout btnLayout = dialog.findViewById( R.id.btn_layout_to_hide );
        LinearLayout recLayout = dialog.findViewById( R.id.reciept_layout );

        Log.e( TAG, "loadData: "+data[0]+":"+data[1]+":"+data[2]+":"+data[3]+":"+data[4]+":"+data[5]  );
        // data Array Rule = {"Giver Name","Amount","For","Receipt number","Reciept Given By","Date"}
        Button printBtn = dialog.findViewById( R.id.print_rec );
        Button shareBtn = dialog.findViewById( R.id.share_rec );
        amountBy.setText( "Name: "+data[0] );
        amount.setText( "Amount:  "+data[1] );
        amountfor.setText( "Amount For: "+data[2] );
        recieptBy.setText( "Receipt No: "+data[3] );
        recieptNo.setText( "Received By: "+data[4] );
        recieptDate.setText( "Date: "+data[5] );
        printBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnLayout.setVisibility( View.GONE );
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(false); // if you want user to wait for some process to finish,
                builder.setView(R.layout.progress_dialog);
                AlertDialog dialog1 = builder.create();
                dialog1.show();
                saveReciept(recLayout,true,"RECIEPTFOR"+data1[0]+".png",context);
                dialog1.dismiss();
                Toast.makeText( context, "printing ....", Toast.LENGTH_SHORT ).show();
                dialog.dismiss();
            }
        } );
        shareBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Dexter Start
                Dexter.withContext( view.getContext() )
                        .withPermission( Manifest.permission.WRITE_EXTERNAL_STORAGE )
                        .withListener( new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                btnLayout.setVisibility( View.GONE );
                                saveReciept(recLayout,false,"IMG-RECIEPT-"+data[0]+"-"+ data[5] +".png",context);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                                Toast.makeText( view.getContext(), "Permission Denied", Toast.LENGTH_SHORT ).show();
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        } ).check();
                //Dexter Start
                dialog.dismiss();
            }
        } );
        dialog.show();
    }
    private void saveReciept(LinearLayout linearLayout,boolean PrintOrShare,String filename,Context context) {
        linearLayout.setDrawingCacheEnabled( true );
        linearLayout.buildDrawingCache();
        linearLayout.setDrawingCacheQuality( View.DRAWING_CACHE_QUALITY_HIGH );
        Bitmap bitmap = linearLayout.getDrawingCache();
        if(PrintOrShare){

//            chkPermission(linearLayout.getContext());
            String root = Environment.getExternalStorageDirectory().getAbsolutePath();
            File file = new File( root+"/Download" );
            File myFile = new File( file,filename );
            if(myFile.exists()){
                myFile.delete();
            }
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream( myFile );
                bitmap.compress( Bitmap.CompressFormat.PNG,100,fileOutputStream );
                fileOutputStream.flush();
                fileOutputStream.close();
                Toast.makeText( context, "Saved SuccessFully", Toast.LENGTH_LONG ).show();
                linearLayout.setDrawingCacheEnabled( false );

            } catch (Exception e) {
                linearLayout.setDrawingCacheEnabled( false );
                e.printStackTrace();
                Toast.makeText( context, "Error while Saving Image", Toast.LENGTH_LONG ).show();
            }


        }else{
            try {
                String mapPath = MediaStore.Images.Media.insertImage( context.getContentResolver(),bitmap,"Reciept share","Sharing reciept with user " );
                Uri uri = Uri.parse( mapPath );

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType( "image/*" );
                intent.putExtra( Intent.EXTRA_STREAM,uri );
                context.startActivity( Intent.createChooser( intent,"Share Image" ) );
                Toast.makeText( context, "Sharing", Toast.LENGTH_SHORT ).show();

            }catch (Exception e){
                Toast.makeText( context, "Error", Toast.LENGTH_SHORT ).show();
            }

        }
        linearLayout.setDrawingCacheEnabled( false );


    }

}
