package libyacvpro.libya_cv.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import libyacvpro.libya_cv.DownloadImageTask;
import libyacvpro.libya_cv.JobActivity;
import libyacvpro.libya_cv.R;
import libyacvpro.libya_cv.entities.JobSearchPackage.Jobs;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;

import static android.support.v4.app.ActivityCompat.startActivityForResult;
import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * Created by Asasna on 10/5/2017.
 */

public class JobsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public final int TYPE_MOVIE = 0;
    public final int TYPE_LOAD = 1;



    static Context context;
    static List<Jobs> movies;
    OnLoadMoreListener loadMoreListener;
    boolean isLoading = false, isMoreDataAvailable = true;

    /*
    * isLoading - to set the remote loading and complete status to fix back to back load more call
    * isMoreDataAvailable - to set whether more data from server available or not.
    * It will prevent useless load more request even after all the server data loaded
    * */


    public JobsAdapter(Context context, List<Jobs> jobses) {
        this.context = context;
        this.movies = jobses;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        if(viewType==TYPE_MOVIE){


            return new MovieHolder(inflater.inflate(R.layout.row_jobs,parent,false));
        }else{
            return new LoadHolder(inflater.inflate(R.layout.row_load,parent,false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(position>=getItemCount()-1 && isMoreDataAvailable && !isLoading && loadMoreListener!=null){
            isLoading = true;
            loadMoreListener.onLoadMore();
        }

        if(getItemViewType(position)==TYPE_MOVIE){
            ((MovieHolder)holder).bindData(movies.get(position));
        }
        //No else part needed as load holder doesn't bind any data
    }

    @Override
    public int getItemViewType(int position) {
        if(movies.get(position).type.equals("movie")){
            return TYPE_MOVIE;
        }else{
            return TYPE_LOAD;
        }
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    /* VIEW HOLDERS */

    static class MovieHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        TextView tvName;
        TextView tvCompany;
        TextView tvCity;
        TextView tvDate;
        TextView tvSeeIT;
        TextView tvJob_desc;
        ImageView imgView;
        LinearLayout tvItem;



        public MovieHolder(View itemView) {
            super(itemView);
            tvName=(TextView)itemView.findViewById(R.id.lblJobName);
            tvCompany=(TextView)itemView.findViewById(R.id.lblCompanyName);
            tvCity=(TextView)itemView.findViewById(R.id.lblCity);
            tvDate=(TextView)itemView.findViewById(R.id.lblStartDate);
            tvSeeIT=(TextView)itemView.findViewById(R.id.lblSeeIT);
            tvJob_desc=(TextView)itemView.findViewById(R.id.lblTextJob);
            tvItem    = (LinearLayout) itemView.findViewById(R.id.lvvIdtems);
            imgView    = (ImageView) itemView.findViewById(R.id.imgCompany);
            itemView.setOnClickListener(this);

//            Picasso.setSingletonInstance(picasso);

        }

        void bindData(Jobs jobsModel){
            tvName.setText(jobsModel.getJob_name());
            tvCompany.setText(jobsModel.getComp_name());
            tvCity.setText(jobsModel.getCity_name());
            tvDate.setText(jobsModel.getJob_start());
            tvSeeIT.setText(jobsModel.getSee_it().toString());
            tvJob_desc.setText(jobsModel.getJob_desc());
       /*     new DownloadImageTask(imgView)
                    .execute(jobsModel.getImage());*/

            Picasso.get().load(jobsModel.getImage())  .into(imgView);


        }
//"http://192.168.1.20/libyacv/public/images/company/"+
        @Override
        public void onClick(View v) {
            tvItem.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {


                    Intent intent = new Intent(v.getContext(), JobActivity.class);
                    intent.putExtra("id", movies.get(getAdapterPosition()).getDesc_id());
                    startActivityForResult((Activity)context,intent,0,null);

                   /* Toast.makeText(v.getContext(),
                            "Online Data Clicked : "+ movies.get(getAdapterPosition()).getDesc_id(), Toast.LENGTH_LONG)
                            .show();*/
                }
            });
        }
    }

    static class LoadHolder extends RecyclerView.ViewHolder{
        public LoadHolder(View itemView) {
            super(itemView);
        }
    }

    public void setMoreDataAvailable(boolean moreDataAvailable) {
        isMoreDataAvailable = moreDataAvailable;
    }

    /* notifyDataSetChanged is final method so we can't override it
         call adapter.notifyDataChanged(); after update the list
         */
    public void notifyDataChanged(){
        notifyDataSetChanged();
        isLoading = false;
    }


    public interface OnLoadMoreListener{
        void onLoadMore();
    }

    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

}