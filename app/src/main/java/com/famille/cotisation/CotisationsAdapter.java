package com.famille.cotisation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CotisationsAdapter extends RecyclerView.Adapter<CotisationsAdapter.VH> {

    interface OnDeleteListener { void onDelete(Cotisation c); }

    private List<Cotisation> items;
    private OnDeleteListener deleteListener;

    public CotisationsAdapter(List<Cotisation> items, OnDeleteListener d) {
        this.items = items;
        this.deleteListener = d;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cotisation, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Cotisation c = items.get(pos);
        h.tvMembre.setText(c.membreNom != null ? c.membreNom.trim() : "—");
        h.tvMontant.setText(String.format("%,.0f FCFA", c.montant));
        h.tvDate.setText(c.date != null ? c.date : "");
        String[] moisNoms = {"","Jan","Fév","Mar","Avr","Mai","Jun","Jul","Aoû","Sep","Oct","Nov","Déc"};
        String periode = (c.mois >= 1 && c.mois <= 12) ? moisNoms[c.mois] + " " + c.annee : "";
        h.tvPeriode.setText(periode);
        h.tvNote.setText(c.note != null && !c.note.isEmpty() ? c.note : "");
        h.tvNote.setVisibility(c.note != null && !c.note.isEmpty() ? View.VISIBLE : View.GONE);
        h.btnDelete.setOnClickListener(v -> deleteListener.onDelete(c));
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvMembre, tvMontant, tvDate, tvPeriode, tvNote;
        ImageButton btnDelete;
        VH(View v) {
            super(v);
            tvMembre = v.findViewById(R.id.tvMembreCotisation);
            tvMontant = v.findViewById(R.id.tvMontantCotisation);
            tvDate = v.findViewById(R.id.tvDateCotisation);
            tvPeriode = v.findViewById(R.id.tvPeriodeCotisation);
            tvNote = v.findViewById(R.id.tvNoteCotisation);
            btnDelete = v.findViewById(R.id.btnDeleteCotisation);
        }
    }
}
