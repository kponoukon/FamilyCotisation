package com.famille.cotisation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.*;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RapportActivity extends AppCompatActivity {

    private TextView tvResumeCotis, tvResumeDep, tvResumeSolde, tvResumeMembres;
    private Button btnExporter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rapport);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Rapport & Export");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvResumeCotis = findViewById(R.id.tvResumeCotisations);
        tvResumeDep = findViewById(R.id.tvResumeDepenses);
        tvResumeSolde = findViewById(R.id.tvResumeSolde);
        tvResumeMembres = findViewById(R.id.tvResumeMembres);
        btnExporter = findViewById(R.id.btnExporterExcel);
        progressBar = findViewById(R.id.progressExport);

        chargerResume();

        btnExporter.setOnClickListener(v -> exporterExcel());
    }

    private void chargerResume() {
        double totalCotis = App.db.getTotalCotisations();
        double totalDep = App.db.getTotalDepenses();
        double solde = totalCotis - totalDep;
        int nbMembres = App.db.getMembres().size();
        int nbCotis = App.db.getCotisations().size();
        int nbDep = App.db.getDepenses().size();

        tvResumeMembres.setText(nbMembres + " membre(s)");
        tvResumeCotis.setText(String.format("%,.0f FCFA  (%d cotisation(s))", totalCotis, nbCotis));
        tvResumeDep.setText(String.format("%,.0f FCFA  (%d dépense(s))", totalDep, nbDep));
        tvResumeSolde.setText(String.format("%,.0f FCFA", solde));
        tvResumeSolde.setTextColor(solde >= 0 ? ContextCompat.getColor(this, R.color.vert) : ContextCompat.getColor(this, R.color.rouge));
    }

    private void exporterExcel() {
        btnExporter.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                List<Membre> membres = App.db.getMembres();
                List<Cotisation> cotisations = App.db.getCotisations();
                List<Depense> depenses = App.db.getDepenses();

                ExcelExporter exporter = new ExcelExporter(this);
                File file = exporter.exporterRapport(membres, cotisations, depenses);

                handler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnExporter.setEnabled(true);
                    ouvrirFichier(file);
                });
            } catch (Exception e) {
                handler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnExporter.setEnabled(true);
                    Toast.makeText(this, "Erreur export : " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void ouvrirFichier(File file) {
        try {
            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Proposer aussi le partage
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            Intent chooser = Intent.createChooser(shareIntent, "Exporter via...");
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intent});

            Toast.makeText(this, "Fichier généré : " + file.getName(), Toast.LENGTH_SHORT).show();
            startActivity(chooser);
        } catch (Exception e) {
            Toast.makeText(this, "Fichier sauvegardé : " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        }
    }

    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
