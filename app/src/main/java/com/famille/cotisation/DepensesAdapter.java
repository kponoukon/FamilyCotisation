package com.famille.cotisation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DepensesAdapter extends RecyclerView.Adapter<DepensesAdapter.VH> {

    interface OnDeleteListener { void onDelete(Depense d); }

    private List<Depense> items;
    private OnDeleteListener deleteListener;

    public DepensesAdapter(List<Depense> items, OnDeleteListener d) {
        this.items = items;
        this.deleteListener = d;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_depense, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Depense d = items.get(pos);
        h.tvTitre.setText(d.titre);
        h.tvMontant.setText(String.format("%,.0f FCFA", d.montant));
        h.tvDate.setText(d.date != null ? d.date : "");
        h.tvCategorie.setText(d.categorie != null ? d.categorie : "");
        h.tvDescription.setVisibility(d.description != null && !d.description.isEmpty() ? View.VISIBLE : View.GONE);
        h.tvDescription.setText(d.description);
        h.btnDelete.setOnClickListener(v -> deleteListener.onDelete(d));
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitre, tvMontant, tvDate, tvCategorie, tvDescription;
        ImageButton btnDelete;
        VH(View v) {
            super(v);
            tvTitre = v.findViewById(R.id.tvTitreDepense);
            tvMontant = v.findViewById(R.id.tvMontantDepense);
            tvDate = v.findViewById(R.id.tvDateDepense);
            tvCategorie = v.findViewById(R.id.tvCategorieDepense);
            tvDescription = v.findViewById(R.id.tvDescriptionDepense);
            btnDelete = v.findViewById(R.id.btnDeleteDepense);
        }
    }
}
