package com.famille.cotisation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MembresAdapter extends RecyclerView.Adapter<MembresAdapter.VH> {

    interface OnEditListener { void onEdit(Membre m); }
    interface OnDeleteListener { void onDelete(Membre m); }

    private List<Membre> items;
    private OnEditListener editListener;
    private OnDeleteListener deleteListener;

    public MembresAdapter(List<Membre> items, OnEditListener e, OnDeleteListener d) {
        this.items = items;
        this.editListener = e;
        this.deleteListener = d;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_membre, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Membre m = items.get(pos);
        h.tvNom.setText(m.getNomComplet());
        h.tvTel.setText(m.telephone != null && !m.telephone.isEmpty() ? m.telephone : "—");
        h.btnEdit.setOnClickListener(v -> editListener.onEdit(m));
        h.btnDelete.setOnClickListener(v -> deleteListener.onDelete(m));
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvNom, tvTel;
        ImageButton btnEdit, btnDelete;
        VH(View v) {
            super(v);
            tvNom = v.findViewById(R.id.tvNomMembre);
            tvTel = v.findViewById(R.id.tvTelMembre);
            btnEdit = v.findViewById(R.id.btnEditMembre);
            btnDelete = v.findViewById(R.id.btnDeleteMembre);
        }
    }
}
