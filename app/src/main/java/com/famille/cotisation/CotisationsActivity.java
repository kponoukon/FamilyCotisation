package com.famille.cotisation;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.*;

public class CotisationsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CotisationsAdapter adapter;
    private List<Cotisation> cotisations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cotisations);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Cotisations");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.recyclerCotisations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fabAjouterCotisation);
        fab.setOnClickListener(v -> afficherDialogCotisation());

        chargerCotisations();
    }

    private void chargerCotisations() {
        cotisations = App.db.getCotisations();
        adapter = new CotisationsAdapter(cotisations, c -> confirmerSuppressionCotis(c));
        recyclerView.setAdapter(adapter);

        TextView tvTotal = findViewById(R.id.tvTotalCotisations);
        double total = 0;
        for (Cotisation c : cotisations) total += c.montant;
        tvTotal.setText(String.format("Total : %,.0f FCFA", total));

        TextView tvVide = findViewById(R.id.tvVideCotisations);
        tvVide.setVisibility(cotisations.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void afficherDialogCotisation() {
        List<Membre> membres = App.db.getMembres();
        if (membres.isEmpty()) {
            Toast.makeText(this, "Ajoutez d'abord des membres", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_cotisation, null);
        Spinner spinnerMembre = dialogView.findViewById(R.id.spinnerMembre);
        EditText etMontant = dialogView.findViewById(R.id.etMontantCotisation);
        EditText etDate = dialogView.findViewById(R.id.etDateCotisation);
        Spinner spinnerMois = dialogView.findViewById(R.id.spinnerMois);
        EditText etAnnee = dialogView.findViewById(R.id.etAnnee);
        EditText etNote = dialogView.findViewById(R.id.etNoteCotisation);

        // Spinner membres
        List<String> nomsM = new ArrayList<>();
        for (Membre m : membres) nomsM.add(m.getNomComplet());
        ArrayAdapter<String> adpM = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nomsM);
        adpM.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMembre.setAdapter(adpM);

        // Date par défaut
        String today = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        etDate.setText(today);

        // Mois
        String[] moisNoms = {"Janvier","Février","Mars","Avril","Mai","Juin",
                "Juillet","Août","Septembre","Octobre","Novembre","Décembre"};
        ArrayAdapter<String> adpMois = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, moisNoms);
        adpMois.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMois.setAdapter(adpMois);
        spinnerMois.setSelection(Calendar.getInstance().get(Calendar.MONTH));

        // Année
        etAnnee.setText(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));

        new AlertDialog.Builder(this)
                .setTitle("Enregistrer une cotisation")
                .setView(dialogView)
                .setPositiveButton("Enregistrer", (d, w) -> {
                    String montantStr = etMontant.getText().toString().trim();
                    if (montantStr.isEmpty()) { Toast.makeText(this, "Montant requis", Toast.LENGTH_SHORT).show(); return; }

                    Cotisation c = new Cotisation();
                    c.membreId = membres.get(spinnerMembre.getSelectedItemPosition()).id;
                    c.montant = Double.parseDouble(montantStr);
                    c.date = etDate.getText().toString().trim();
                    c.mois = spinnerMois.getSelectedItemPosition() + 1;
                    c.annee = Integer.parseInt(etAnnee.getText().toString().trim());
                    c.note = etNote.getText().toString().trim();

                    App.db.ajouterCotisation(c);
                    chargerCotisations();
                    Toast.makeText(this, "Cotisation enregistrée", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void confirmerSuppressionCotis(Cotisation c) {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer")
                .setMessage("Supprimer cette cotisation de " + String.format("%,.0f FCFA", c.montant) + " ?")
                .setPositiveButton("Supprimer", (d, w) -> { App.db.supprimerCotisation(c.id); chargerCotisations(); })
                .setNegativeButton("Annuler", null)
                .show();
    }

    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
