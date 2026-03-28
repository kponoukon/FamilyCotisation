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

public class DepensesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DepensesAdapter adapter;
    private List<Depense> depenses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_depenses);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Dépenses");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.recyclerDepenses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fabAjouterDepense);
        fab.setOnClickListener(v -> afficherDialogDepense());

        chargerDepenses();
    }

    private void chargerDepenses() {
        depenses = App.db.getDepenses();
        adapter = new DepensesAdapter(depenses, d -> confirmerSuppression(d));
        recyclerView.setAdapter(adapter);

        TextView tvTotal = findViewById(R.id.tvTotalDepenses);
        double total = 0;
        for (Depense d : depenses) total += d.montant;
        tvTotal.setText(String.format("Total : %,.0f FCFA", total));

        TextView tvVide = findViewById(R.id.tvVideDepenses);
        tvVide.setVisibility(depenses.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void afficherDialogDepense() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_depense, null);
        EditText etTitre = dialogView.findViewById(R.id.etTitreDepense);
        EditText etMontant = dialogView.findViewById(R.id.etMontantDepense);
        EditText etDate = dialogView.findViewById(R.id.etDateDepense);
        Spinner spinnerCategorie = dialogView.findViewById(R.id.spinnerCategorie);
        EditText etDescription = dialogView.findViewById(R.id.etDescriptionDepense);

        String[] categories = {"Alimentation", "Santé", "Éducation", "Transport",
                "Logement", "Événement", "Autre"};
        ArrayAdapter<String> adpCat = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adpCat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategorie.setAdapter(adpCat);

        String today = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        etDate.setText(today);

        new AlertDialog.Builder(this)
                .setTitle("Ajouter une dépense")
                .setView(dialogView)
                .setPositiveButton("Enregistrer", (d, w) -> {
                    String titre = etTitre.getText().toString().trim();
                    String montantStr = etMontant.getText().toString().trim();
                    if (titre.isEmpty() || montantStr.isEmpty()) {
                        Toast.makeText(this, "Titre et montant requis", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Depense dep = new Depense();
                    dep.titre = titre;
                    dep.montant = Double.parseDouble(montantStr);
                    dep.date = etDate.getText().toString().trim();
                    dep.categorie = categories[spinnerCategorie.getSelectedItemPosition()];
                    dep.description = etDescription.getText().toString().trim();

                    App.db.ajouterDepense(dep);
                    chargerDepenses();
                    Toast.makeText(this, "Dépense enregistrée", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void confirmerSuppression(Depense d) {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer")
                .setMessage("Supprimer la dépense \"" + d.titre + "\" ?")
                .setPositiveButton("Supprimer", (dlg, w) -> { App.db.supprimerDepense(d.id); chargerDepenses(); })
                .setNegativeButton("Annuler", null)
                .show();
    }

    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
