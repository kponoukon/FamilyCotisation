package com.famille.cotisation;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class MembresActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MembresAdapter adapter;
    private List<Membre> membres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membres);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Membres");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.recyclerMembres);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fabAjouterMembre);
        fab.setOnClickListener(v -> afficherDialogMembre(null));

        chargerMembres();
    }

    private void chargerMembres() {
        membres = App.db.getMembres();
        adapter = new MembresAdapter(membres,
                m -> afficherDialogMembre(m),
                m -> confirmerSuppression(m));
        recyclerView.setAdapter(adapter);

        TextView tvVide = findViewById(R.id.tvVideMembres);
        tvVide.setVisibility(membres.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void afficherDialogMembre(Membre membreExist) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_membre, null);
        EditText etNom = dialogView.findViewById(R.id.etNom);
        EditText etPrenom = dialogView.findViewById(R.id.etPrenom);
        EditText etTel = dialogView.findViewById(R.id.etTelephone);

        if (membreExist != null) {
            etNom.setText(membreExist.nom);
            etPrenom.setText(membreExist.prenom);
            etTel.setText(membreExist.telephone);
        }

        new AlertDialog.Builder(this)
                .setTitle(membreExist == null ? "Ajouter un membre" : "Modifier le membre")
                .setView(dialogView)
                .setPositiveButton("Enregistrer", (d, w) -> {
                    String nom = etNom.getText().toString().trim();
                    if (nom.isEmpty()) { Toast.makeText(this, "Le nom est requis", Toast.LENGTH_SHORT).show(); return; }
                    Membre m = membreExist == null ? new Membre() : membreExist;
                    m.nom = nom;
                    m.prenom = etPrenom.getText().toString().trim();
                    m.telephone = etTel.getText().toString().trim();
                    if (membreExist == null) App.db.ajouterMembre(m);
                    else App.db.modifierMembre(m);
                    chargerMembres();
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void confirmerSuppression(Membre m) {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer")
                .setMessage("Supprimer " + m.getNomComplet() + " et ses cotisations ?")
                .setPositiveButton("Supprimer", (d, w) -> { App.db.supprimerMembre(m.id); chargerMembres(); })
                .setNegativeButton("Annuler", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}
