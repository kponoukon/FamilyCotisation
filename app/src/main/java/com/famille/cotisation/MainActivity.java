package com.famille.cotisation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {

    private TextView tvTotalCotis, tvTotalDep, tvSolde, tvMembres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTotalCotis = findViewById(R.id.tvTotalCotisations);
        tvTotalDep = findViewById(R.id.tvTotalDepenses);
        tvSolde = findViewById(R.id.tvSolde);
        tvMembres = findViewById(R.id.tvNbMembres);

        CardView cardMembres = findViewById(R.id.cardMembres);
        CardView cardCotisations = findViewById(R.id.cardCotisations);
        CardView cardDepenses = findViewById(R.id.cardDepenses);
        CardView cardRapport = findViewById(R.id.cardRapport);

        cardMembres.setOnClickListener(v -> startActivity(new Intent(this, MembresActivity.class)));
        cardCotisations.setOnClickListener(v -> startActivity(new Intent(this, CotisationsActivity.class)));
        cardDepenses.setOnClickListener(v -> startActivity(new Intent(this, DepensesActivity.class)));
        cardRapport.setOnClickListener(v -> startActivity(new Intent(this, RapportActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        rafraichirTableauBord();
    }

    private void rafraichirTableauBord() {
        double totalCotis = App.db.getTotalCotisations();
        double totalDep = App.db.getTotalDepenses();
        double solde = totalCotis - totalDep;
        int nbMembres = App.db.getMembres().size();

        tvTotalCotis.setText(String.format("%,.0f FCFA", totalCotis));
        tvTotalDep.setText(String.format("%,.0f FCFA", totalDep));
        tvSolde.setText(String.format("%,.0f FCFA", solde));
        tvMembres.setText(String.valueOf(nbMembres));

        // Couleur du solde
        tvSolde.setTextColor(solde >= 0 ?
                ContextCompat.getColor(this, R.color.vert) : ContextCompat.getColor(this, R.color.rouge));
    }
}
