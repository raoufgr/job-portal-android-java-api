package libyacvpro.libya_cv;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import libyacvpro.libya_cv.entities.Message;
import libyacvpro.libya_cv.network.ApiService;
import libyacvpro.libya_cv.network.RetrofitBuilder;

public class DeleteActivity extends AppCompatActivity {

    private static final String TAG = "DeleteActivity";

    @BindView(R.id.txtDeleteName)
    TextView txtDeleteName;

    Integer itemID ;
    Character itemName;
    ApiService service;
    TokenManager tokenManager;
    Call<Message> call;
    private static final String APP_ID = "ca-app-pub-9929016091047307~2213947061";
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);
        ButterKnife.bind(this);

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
       // MobileAds.initialize(this, APP_ID);

        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-9929016091047307/3960713000");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        if(tokenManager.getToken() == null){
            startActivity(new Intent(DeleteActivity.this, LoginActivity.class));
            finish();
        }

        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
        itemID = getIntent().getExtras().getInt("id");
        itemName = getIntent().getExtras().getChar("name");
        String st = "???? ?????? ?????????? ???? ?????? ";
        String stt=Helper.SectionTitled(itemName);


        String sttt= st + " "+stt;
        txtDeleteName.setText(sttt.toString());
    }
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.side_bar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.toolback:
                onBackPressed();

                return true;

            default:
                return true;//super.onOptionsItemSelected(item);
        }
    }
    @OnClick(R.id.btn_Delete)
    void confirmDelete(){

        /*Bundle b = getIntent().getExtras();
        Integer itemID = b.getInt("id");
        String itemName = b.getString("name");*/

        switch (itemName){
            case 'E':
                call = service.deleteEducation(itemID,"DELETE");
                break;
            case 'X':
                call = service.deleteExperience(itemID,"DELETE");
                break;
            case 'L':
                call = service.deleteLanguage(itemID,"DELETE");
                break;
            case 'S':
                call = service.deleteSpecialty(itemID,"DELETE");
                break;
            case 'K':
                call = service.deleteSkills(itemID,"DELETE");
                break;
            case 'I':
                call = service.deleteInfo(itemID,"DELETE");
                break;
            case 'C':
                call = service.deleteCertificate(itemID,"DELETE");
                break;
            case 'T':
                call = service.deleteTraining(itemID,"DELETE");
                break;
            case 'H':
                call = service.deleteHobby(itemID,"DELETE");
                break;
        }
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                Log.w(TAG, "onResponse: " + response );

                if(response.isSuccessful()){

                    String msg = response.body().getMessage();
                    Context context = getApplicationContext();
                    Toast.makeText(context, msg, Toast.LENGTH_LONG)
                            .show();
                    setResult(RESULT_OK, null);
                    finish();

                }else {
                    tokenManager.deleteToken();
                    startActivity(new Intent(DeleteActivity.this, LoginActivity.class));
                    finish();

                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage() );
            }
        });

    }

    @OnClick(R.id.btn_Back)
    void movePage(){
        setResult(RESULT_CANCELED, null);
        finish();
    }

}
