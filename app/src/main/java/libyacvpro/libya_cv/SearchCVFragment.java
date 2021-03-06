package libyacvpro.libya_cv;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.transition.TransitionManager;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import libyacvpro.libya_cv.adapter.SeekersAdapter;
import libyacvpro.libya_cv.entities.Seeker;
import libyacvpro.libya_cv.entities.ShowJobPackage.ShowParaJob;
import libyacvpro.libya_cv.network.ApiService;
import libyacvpro.libya_cv.network.RetrofitBuilder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;


public class SearchCVFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    RecyclerView recyclerView;
    List<Seeker> jobsList;
    SeekersAdapter adapter;
    //MoviesApi api;
    String TAG = "SearchCVFragment";
    Context context;
    SwipeRefreshLayout mSwipeRefreshLayout;

    private boolean isVisible;
    private boolean isStarted;

    Button btnCitycv;
    Button btnDomaincv;
    ImageButton imgAdd;

    ApiService service;
    TokenManager tokenManager;
    Call<List<Seeker>> call;
    Call<ShowParaJob> callPara;
    String clist[];
    String dlist[];

    String dPara;
    String cPara;
    RelativeLayout containerr;


    ConstraintLayout formContainer;
    ProgressBar loader;

    TextView lblInfo;
    Button imgWifi;

    Context con=null;
    View view;
    @Override
    public void onStart() {
        super.onStart();
        isStarted = true;

    }

    @Override
    public void onStop() {
        super.onStop();
        isStarted = false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        con= context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        context=con;

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_cv, container, false);
        formContainer =   view.findViewById(R.id.form_container);
        containerr =   view.findViewById(R.id.container);
        loader = (ProgressBar) view.findViewById(R.id.loader);


        Drawable leftDrawable = AppCompatResources.getDrawable(con, R.drawable.ic_keyboard_arrow_down_black_24dp);

        imgWifi = (Button) view.findViewById(R.id.imgWifi);
        Drawable topDrawable = AppCompatResources.getDrawable(context, R.drawable.ic_wifi);
        imgWifi.setCompoundDrawablesWithIntrinsicBounds(null, topDrawable, null, null);
        imgWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoading();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms

                        loadRecyclerViewData();
                    }
                }, 1000);
            }
        });
        recyclerView =  view.findViewById(R.id.recycler_view);
        btnDomaincv =  view.findViewById(R.id.btnDomaincv);
        btnCitycv =  view.findViewById(R.id.btnCitycv);
        btnCitycv.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);
        btnDomaincv.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);
        jobsList = new ArrayList<>();
        imgAdd =   view.findViewById(R.id.imgAdd);
        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getContext(),MycvFargment.class));

            }
        });
        final String stPara = "";//getIntent().getExtras().getString("string");
        String pCity = "";//getIntent().getExtras().getString("city");
        String pDomain = "";// getIntent().getExtras().getString("domain");

        final String cityPara = (pCity.equals("???? ??????????")? "" :pCity);
        final String domainPara = (pDomain.equals("???? ????????????????")? "" :pDomain);


        dPara = domainPara;
        cPara=cityPara;
        clist  =  new String[]{"????????????", "????????????", "????????????"};//getIntent().getExtras() .getStringArray("clist");
        dlist  =  new String[]{"??????????", "?????????? ??????????????????", "???? ????????????????"};//getIntent().getExtras() .getStringArray("dlist");

        //showLoading();
       // loadApiPara();
        loadApi();


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new VerticalLineDecorator(2));
        recyclerView.setAdapter(adapter);

        //api = ServiceGenerator.createService(MoviesApi.class);
       // load(1,stPara,cPara,dPara);//,typePara,statusPara


        // registerForContextMenu(btnCity);
        registerForContextMenu(btnCitycv);
        registerForContextMenu(btnDomaincv);
        btnCitycv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                v.showContextMenu();


            }});


        btnDomaincv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                v.showContextMenu();


            }});
        // loadApi();

        // SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {

                mSwipeRefreshLayout.setRefreshing(true);

                // Fetching data from server
                loadRecyclerViewData();
            }
        });

        return view;
    }

    public void loadRecyclerViewData(){
        mSwipeRefreshLayout.setRefreshing(true);
        //clear();
        loadApiPara();

        load(1,"",cPara,dPara);

        mSwipeRefreshLayout.setRefreshing(false);

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (view != null) {
            ViewGroup parentViewGroup = (ViewGroup) view.getParent();
            if (parentViewGroup != null) {
                parentViewGroup.removeAllViews();
            }
        }
    }
    private void loadApiPara(){

        boolean IsValid = isOnline();
        if (!IsValid) {
            showWifi();
            return;
        }


        tokenManager = TokenManager.getInstance(this.getActivity().getSharedPreferences("prefs", MODE_PRIVATE));

        if (tokenManager.getToken() == null) {
            startActivity(new Intent(getContext(), LoginActivity.class));
            //finish();
        }


        showLoading();
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
        callPara = service.getShowParaJob();
        callPara.enqueue(new Callback<ShowParaJob>() {
            @Override
            public void onResponse(Call<ShowParaJob> call, Response<ShowParaJob> response) {
                if(response.isSuccessful()){

                    ShowParaJob lstShowPara = response.body();
                    String[] cityArray = new String[lstShowPara.getCity().size()+1];
                    cityArray[0] = "???? ??????????";
                    int pos = 1;
                    for (int i = 0; i < lstShowPara.getCity().size(); i++)
                    {
                        cityArray[pos] = lstShowPara.getCity().get(i).getCityName();
                        pos++;
                    }
                    clist = cityArray;

                    String[] domainArray = new String[lstShowPara.getDomain().size()+1];
                    domainArray[0] = "???? ????????????????";
                    pos = 1;
                    for (int i = 0; i < lstShowPara.getDomain().size(); i++)
                    {
                        domainArray[pos] = lstShowPara.getDomain().get(i).getDomain_name();
                        pos++;
                    }
                    dlist = domainArray;

                    registerForContextMenu(btnCitycv);
                    registerForContextMenu(btnDomaincv);


                    showForm();
                }else{
                    Log.e(TAG," Response Error "+String.valueOf(response.code()));
                    showWifi();
                }
            }

            @Override
            public void onFailure(Call<ShowParaJob> call, Throwable t) {
                Log.e(TAG," Response Error "+t.getMessage());
                // showWifi();
            }
        });
    }
    public boolean isOnline() {


        try {
            ConnectivityManager cm =
                    (ConnectivityManager) getActivity()
                            .getSystemService(Context.CONNECTIVITY_SERVICE);

            return cm.getActiveNetworkInfo() != null &&
                    cm.getActiveNetworkInfo().isConnectedOrConnecting();


        } catch (Exception e) { return false; }
    }
    private void showWifi(){
        TransitionManager.beginDelayedTransition(containerr);
        formContainer.setVisibility(View.GONE);

        imgWifi.setVisibility(View.VISIBLE);


    }
    void loadApi(){
        adapter = new SeekersAdapter(getContext(), jobsList);
        adapter.setLoadMoreListener(new SeekersAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        int index = (jobsList.size() / 10) +1;

                        loadMore(index,"",cPara,dPara);
                    }
                });
              }
        });

    }
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
         if(v.getId() == R.id.btnCitycv){
            menu.setHeaderTitle("???????? ??????????????");
            if(clist.length != 0) {

                for (String strTemp : clist){
                    menu.add(0, v.getId(), 0, strTemp);
                }
            }
        }


        else if(v.getId() == R.id.btnDomaincv){
            menu.setHeaderTitle("???????? ????????????");

            if(dlist.length != 0) {
                for (String strTemp : dlist){
                    menu.add(0, v.getId(), 0, strTemp);
                }

            }
        }

    }
  /*  @Override
    public boolean onContextItemSelected(MenuItem item){
        if(item.getTitle()!=""){
            boolean contains = Arrays.asList(clist).contains(item.getTitle().toString());

            if(contains) {
                btnCitycv.setText(item.getTitle());
                cPara = (item.getTitle().toString().equals("???? ??????????")? "" :item.getTitle().toString());
                clear();

                load(1,"",cPara,dPara);//,typePara,statusPara

            }else {
                btnDomaincv.setText(item.getTitle());
                dPara = (item.getTitle().toString().equals("???? ????????????????")? "" :item.getTitle().toString());
                clear();

                load(1,"",cPara,dPara);

            }

        }else{
            return false;
        }
        return true;
    }*/

    @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);

        isVisible = visible;

        if (visible) {

            Log.i("Tag", "Reload fragment");
        }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item){

        if (isVisible && isStarted)
        {
            if (item.getTitle() != "") {
                boolean contains = Arrays.asList(clist).contains(item.getTitle().toString());

                if (contains) {
                    btnCitycv.setText(item.getTitle());
                    cPara = (item.getTitle().toString().equals("???? ??????????") ? "" : item.getTitle().toString());
                    clear();

                    load(1, "", cPara, dPara);//,typePara,statusPara

                } else {
                    btnDomaincv.setText(item.getTitle());
                    dPara = (item.getTitle().toString().equals("???? ????????????????") ? "" : item.getTitle().toString());
                    clear();

                    load(1, "", cPara, dPara);

                }

            } else {
                return false;
            }
            return true;
        }
        return false;
    }

    private void load(int index,String stName,String city,String domain){



      jobsList.add(new Seeker("load"));
        adapter.notifyItemInserted(jobsList.size() - 1);
        adapter.setMoreDataAvailable(true);
        tokenManager = TokenManager.getInstance(this.getActivity().getSharedPreferences("prefs", MODE_PRIVATE));

        if (tokenManager.getToken() == null) {
            startActivity(new Intent(getContext(), LoginActivity.class));
            //finish();
        }



        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
        call = service.getSearchCvs(index,stName,city,domain);//,type,status
        if(index == 1)
            jobsList.remove(jobsList.size() - 1);
        call.enqueue(new Callback<List<Seeker>>() {
            @Override
            public void onResponse(Call<List<Seeker>> call, Response<List<Seeker>> response) {
                if(response.isSuccessful()){
                    List<Seeker> result = response.body();

                    int size = jobsList.size();
                    jobsList.clear();
                    adapter.notifyItemRangeRemoved(0, size);
                    if (result.size() > 0) {

                       // jobsList.remove(jobsList.size() - 1);
                        jobsList.addAll(result);
                        if(result.size() < 10){
                            adapter.setMoreDataAvailable(false);
                            Toast.makeText(context, "?????????? ?????????? ??????????.", Toast.LENGTH_LONG).show();

                        }
                    } else {//result size 0 means there is no more data available at server
                        adapter.setMoreDataAvailable(false);
                        //telling adapter to stop calling load more as no more server data available
                        Toast.makeText(context, "?????????? ?????????? ??????????.", Toast.LENGTH_LONG).show();
                    }
                    adapter.notifyDataChanged();
                }else{
                    Log.e(TAG," Response Error "+String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<Seeker>> call, Throwable t) {
                Log.e(TAG," Response Error "+t.getMessage());
            }
        });
    }
    public void onResume() {
        super.onResume();

    }

    private void showForm(){
        TransitionManager.beginDelayedTransition(containerr);
        formContainer.setVisibility(View.VISIBLE);
        loader.setVisibility(View.GONE);
        imgWifi.setVisibility(View.GONE);

    }

    private void showLoading(){
        TransitionManager.beginDelayedTransition(containerr);
        formContainer.setVisibility(View.GONE);
        imgWifi.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);
    }
    private void loadMore(int index,String stName,String city,String domain){ //,String type,String status


        //add loading progress view
        jobsList.add(new Seeker("load"));
        adapter.notifyItemInserted(jobsList.size() - 1);


        tokenManager = TokenManager.getInstance(this.getActivity().getSharedPreferences("prefs", MODE_PRIVATE));

        if (tokenManager.getToken() == null) {
            startActivity(new Intent(getContext(), LoginActivity.class));
            //finish();
        }

        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);


        if(index !=0) {
            call = service.getSearchCvs(index, stName, city, domain); //, type, status
            call.enqueue(new Callback<List<Seeker>>() {
                @Override
                public void onResponse(Call<List<Seeker>> call, Response<List<Seeker>> response) {
                    if (response.isSuccessful()) {

                        //remove loading view


                        List<Seeker> result = response.body();
                        if (result.size() > 0) {
                            jobsList.remove(jobsList.size() - 1);
                            jobsList.addAll(result);
                            if(result.size() < 10){
                                adapter.setMoreDataAvailable(false);
                                Toast.makeText(context, "?????????? ?????????? ??????????.", Toast.LENGTH_LONG).show();

                            }
                        } else {//result size 0 means there is no more data available at server
                            adapter.setMoreDataAvailable(false);
                            //telling adapter to stop calling load more as no more server data available
                            Toast.makeText(context, "?????????? ?????????? ??????????.", Toast.LENGTH_LONG).show();
                        }
                        adapter.notifyDataChanged();
                        //should call the custom method adapter.notifyDataChanged here to get the correct loading status
                    } else {
                        Log.e(TAG, " Load More Response Error " + String.valueOf(response.code()));
                    }
                }

                @Override
                public void onFailure(Call<List<Seeker>> call, Throwable t) {
                    Log.e(TAG, " Load More Response Error " + t.getMessage());
                }
            });
        }
    }

    public void clear() {
        int size = jobsList.size();
        jobsList.clear();
        adapter.notifyItemRangeRemoved(0, size);

    }

    @Override
    public void onRefresh() {
        loadRecyclerViewData();
    }
}
