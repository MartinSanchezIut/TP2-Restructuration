package Test.dossier2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import com.example.projetmobile.BDD.Repository.AppDataBase;
import com.example.projetmobile.BDD.Repository.UserDao;
import com.example.projetmobile.BDD.models.Controllers.UserControlers;
import com.example.projetmobile.BDD.models.UserBDD;
import com.example.projetmobile.Model.Annonce;
import com.example.projetmobile.Model.serveur;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class AnnonceRecentAdaptateur extends BaseAdapter  {

    private List<Annonce> listData;
    private List<Long> favoris;
    private LayoutInflater layoutInflater;
    private Context context;
    private ImageView iconFavoris;
    private List<Boolean> fav ;
    prodHolder holder = null;

    public AnnonceRecentAdaptateur(Context aContext, List<Annonce> listData) {
        this.context = aContext;
        this.listData = listData;
        this.fav = new ArrayList<>();
        AppDataBase db = Room.databaseBuilder(aContext, AppDataBase.class,"database-name").allowMainThreadQueries().build();
        UserDao userDao = db.userDao();
        System.out.println(userDao.getCount());
        if(userDao.getCount()!=0) {
            serveur s = new serveur("annonce/Getsauvegardeid/" + userDao.getAll().get(0).getId_user());

            String reponse = null;
            try {
                reponse = s.getRequest();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Gson gson = new Gson();
            this.favoris = gson.fromJson(reponse, new TypeToken<ArrayList<Long>>() {
            }.getType());
        }
        layoutInflater = LayoutInflater.from(aContext);
    }
    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Annonce getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = layoutInflater.inflate(R.layout.list_view_adapteur_annonce_recent, null);
        View row = view;
        getItem(i).getPrix();
        byte[] myImage = Base64.getDecoder().decode(getItem(i).getimage().get(0).getBytes(StandardCharsets.UTF_8));
        Bitmap bmp= BitmapFactory.decodeByteArray(myImage,0,myImage.length);
        ImageView myView = (ImageView)view.findViewById(R.id.photo_annonce);
        prodHolder holder = new prodHolder();
        holder.iconFavoris = (ImageView)view.findViewById(R.id.favoris);
        this.fav.add(false);
        if(this.favoris !=null) {
            for (long id : this.favoris) {
                if (id == getItem(i).getId_annonce()) {
                    holder.iconFavoris.setImageResource(R.drawable.baseline_favorite_black_24dp);
                    this.fav.set(i, true);
                }
            }
        }
        myView.setImageBitmap(bmp);
        TextView Titre = (TextView)view.findViewById(R.id.Titre);
        TextView Description = (TextView)view.findViewById(R.id.Descriptions);
        TextView Date = (TextView)view.findViewById(R.id.Date);
        TextView Lieux = (TextView)view.findViewById(R.id.Lieux);
        TextView Prix = (TextView)view.findViewById(R.id.Prix);
        LinearLayout ln = view.findViewById(R.id.fraude);
        if(!getItem(i).isIsfraude()){
            ln.removeAllViews();
        }else {
            TextView MotifFraude = (TextView)view.findViewById(R.id.motifFraude);
            MotifFraude.setText(getItem(i).getMotifFraude());
            LinearLayout ln2 = view.findViewById(R.id.lineargeneral);
            ln2.setBackgroundColor(Color.RED);
        }
        Prix.setText(String.valueOf(getItem(i).getPrix()));
        Titre.setText(getItem(i).getTitre());
        Description.setText(getItem(i).getDescription());
        Date.setText(getItem(i).getDate_publication());
        Lieux.setText(getItem(i).getville());
        holder.iconFavoris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconFavoris(i,v);
            }
            });
        row.setTag(holder);
        return view;
    }

    public List<Boolean> getFav(){
        return this.fav;
    }
    public void iconFavoris(int position,View v){
        ImageView iconfavoris = v.findViewById(R.id.favoris);
        String url = "http://172.16.5.209:8080/LeMauvaisCoin/api/annonce/Favoris";
        AppDataBase db = Room.databaseBuilder(this.context, AppDataBase.class,"database-name").allowMainThreadQueries().build();
        UserDao userDao = db.userDao();
        ArrayList<String> params = new ArrayList<>();
        params.add(String.valueOf(userDao.getAll().get(0).getId_user()));
        params.add(String.valueOf(getItem(position).getId_annonce()));
        Gson gson = new Gson();
        String query = gson.toJson(params);
        try {
            serveur s = new serveur("annonce/Favoris");
            s.PostRequest(query);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(iconfavoris.getDrawable().getConstantState().equals(this.context.getResources().getDrawable(R.drawable.baseline_favorite_black_24dp).getConstantState())){
            iconfavoris.setImageResource(R.drawable.baseline_favorite_border_black_24dp);
            this.fav.set(position,false);
        }else {
            iconfavoris.setImageResource(R.drawable.baseline_favorite_black_24dp);
            this.fav.set(position,true);
        }

    }
    static class prodHolder
    {
        ImageView iconFavoris;
    }
}
