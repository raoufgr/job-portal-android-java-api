package libyacvpro.libya_cv;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.transition.TransitionManager;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatDelegate;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import libyacvpro.libya_cv.entities.Message;
import libyacvpro.libya_cv.entities.Seeker;
import libyacvpro.libya_cv.entities.SettingForEdit;
import libyacvpro.libya_cv.network.ApiService;
import libyacvpro.libya_cv.network.RetrofitBuilder;

import static android.content.Context.MODE_PRIVATE;


public class SettingFragment extends Fragment {

    String TAG = "SettingFragment";

    ApiService service;
    TokenManager tokenManager;
    Call<SettingForEdit> call;
    Call<Message> callMsg;

     Spinner spHide;
     Spinner spPhone;
     Spinner spImage;

    EditText txtPassword;
    EditText txtPasswordConfirm;
    Button btnSaveHide;
    Button btnSavePassword;
    Button btnInfo;

    private FrameLayout framcontainer;
    private LinearLayout formContainer;
    private ProgressBar loader;
    Context con=null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        con= context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

       spHide = (Spinner)  view.findViewById(R.id.spHide);
        spPhone = (Spinner)  view.findViewById(R.id.spPhone);
       spImage = (Spinner)  view.findViewById(R.id.spImage);
        txtPassword = (EditText)  view.findViewById(R.id.txtPassword);
        txtPasswordConfirm = (EditText)  view.findViewById(R.id.txtConfirmPassword);
        btnSaveHide = (Button) view.findViewById(R.id.btnSaveHide);
        btnSavePassword = (Button) view.findViewById(R.id.btnSavePassword);
        // txtString = (TextView) view.findViewById(R.id.editSearchString);

        framcontainer = (FrameLayout) view.findViewById(R.id.framcontainer);
        formContainer = (LinearLayout) view.findViewById(R.id.form_container);
        loader = (ProgressBar) view.findViewById(R.id.loader);
       // btnInfo = (Button) view.findViewById(R.id.btnInfo);

        /*btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SettingNoteActivity.class);
                 startActivityForResult(intent,0);
            }

            });*/
        btnSaveHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 String pHide = spHide.getSelectedItem().toString();
                 String pPhone = spPhone.getSelectedItem().toString();
                 String pImage = spImage.getSelectedItem().toString();

                ButterKnife.bind(getActivity());
                tokenManager = TokenManager.getInstance(getActivity().getSharedPreferences("prefs", MODE_PRIVATE));

                if (tokenManager.getToken() == null) {
                    startActivity(new Intent(con, LoginActivity.class));
                    //finish();
                }

                service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
                callMsg = service.postChangeHide(pHide,pPhone,pImage);
                callMsg.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        if(response.isSuccessful()){

                            String msg = response.body().getMessage();
                            Context context = getActivity();
                            Toast.makeText(context, msg, Toast.LENGTH_LONG)
                                    .show();

                        }else{
                            Log.e(TAG," Response Error "+String.valueOf(response.code()));
                        }
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {
                        Log.e(TAG," Response Error "+t.getMessage());
                    }
                });

                //postChangeHide
            }
        });


        btnSavePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String password = txtPassword.getText().toString();
                String newpassword = txtPassword.getText().toString();

                ButterKnife.bind(getActivity());
                tokenManager = TokenManager.getInstance(getActivity().getSharedPreferences("prefs", MODE_PRIVATE));

                if (tokenManager.getToken() == null) {
                    startActivity(new Intent(con, LoginActivity.class));
                    //finish();
                }

                service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
                callMsg = service.postChangePassword(password,newpassword);
                callMsg.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        if(response.isSuccessful()){

                            String msg = response.body().getMessage();
                            Context context = getActivity();
                            Toast.makeText(context, msg, Toast.LENGTH_LONG)
                                    .show();

                        }else{
                            Log.e(TAG," Response Error "+String.valueOf(response.code()));
                        }
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {
                        Log.e(TAG," Response Error "+t.getMessage());
                    }
                });

                //postChangeHide
            }
        });



        loadApi();

        return view;
    }
    public void onResume() {
        super.onResume();
        getActivity().setTitle("??????????????????");
    }
    private void loadApi(){
        ButterKnife.bind(getActivity());
        tokenManager = TokenManager.getInstance(this.getActivity().getSharedPreferences("prefs", MODE_PRIVATE));

        if (tokenManager.getToken() == null) {
            startActivity(new Intent(con, LoginActivity.class));
            //finish();
        }
        showLoading();

        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
        call = service.getSeekerSetting();
        call.enqueue(new Callback<SettingForEdit>() {
            @Override
            public void onResponse(Call<SettingForEdit> call, Response<SettingForEdit> response) {
                if(response.isSuccessful()){
                    showForm();
                    SettingForEdit lstShowPara = response.body();
                    setData(lstShowPara.getInfo());
                }else{
                    Log.e(TAG," Response Error "+String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<SettingForEdit> call, Throwable t) {
                Log.e(TAG," Response Error "+t.getMessage());
            }
        });
    }
    private void showForm(){
        TransitionManager.beginDelayedTransition(framcontainer);
        formContainer.setVisibility(View.VISIBLE);
        loader.setVisibility(View.GONE);
    }

    private void showLoading(){
        TransitionManager.beginDelayedTransition(framcontainer);
        formContainer.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);
    }
    private void setData(Seeker c){



        String stringHide = c.getHide_cv();
        if(stringHide.equals("0")){stringHide ="??????????";}else {stringHide ="??????????";}


        String gender[] = {"??????????","??????????"};
        String images[] = {"?????????? ???? ????????","?????? ????????","?????? ???????????? ?????????????? ?????? ????????????"};
        String phones[] = {"?????????? ???? ????????","?????? ????????","?????? ???????????? ?????????????? ?????? ????????????"};

        int indexesImage = Integer.parseInt(c.getImage_view());
        String stringImage =  images[indexesImage];


        int indexesPhone = Integer.parseInt(c.getPhone_view());
        String stringPhone =  phones[indexesPhone];




try{
        ArrayAdapter<String> spinnerArrayAdapterSex = new ArrayAdapter<String>(con,   android.R.layout.simple_spinner_item, gender);
        spinnerArrayAdapterSex.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spHide.setAdapter(spinnerArrayAdapterSex);

        spHide.setSelection(getIndex(spHide, stringHide));

}catch (Exception e){
    Log.e(TAG," Response Error "+e.getMessage());
}

        try{
            ArrayAdapter<String> spinnerArrayAdapterphone = new ArrayAdapter<String>(con,   android.R.layout.simple_spinner_item, phones);
            spinnerArrayAdapterphone.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
            spPhone.setAdapter(spinnerArrayAdapterphone);
            spPhone.setSelection(getIndex(spPhone, stringPhone));

        }catch (Exception e){
            Log.e(TAG," Response Error "+e.getMessage());
        }


        try{
            ArrayAdapter<String> spinnerArrayAdapterImage = new ArrayAdapter<String>(con,   android.R.layout.simple_spinner_item, images);
            spinnerArrayAdapterImage.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
            spImage.setAdapter(spinnerArrayAdapterImage);
            spImage.setSelection(getIndex(spImage, stringImage));

        }catch (Exception e){
            Log.e(TAG," Response Error "+e.getMessage());
        }





    }


    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }

        return 0;
    }
}
