package Test.dossier1.dossier12;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.projetmobile.Model.Annonce;
import com.google.gson.Gson;

public class DetailAnnonce extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_annonce);

    }
}