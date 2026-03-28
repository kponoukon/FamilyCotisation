package com.famille.cotisation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "famille_cotisation.db";
    private static final int DB_VERSION = 1;

    // Tables
    public static final String TABLE_MEMBRES = "membres";
    public static final String TABLE_COTISATIONS = "cotisations";
    public static final String TABLE_DEPENSES = "depenses";

    // Membres columns
    public static final String COL_ID = "id";
    public static final String COL_NOM = "nom";
    public static final String COL_PRENOM = "prenom";
    public static final String COL_TELEPHONE = "telephone";

    // Cotisations columns
    public static final String COL_MEMBRE_ID = "membre_id";
    public static final String COL_MONTANT = "montant";
    public static final String COL_DATE = "date";
    public static final String COL_MOIS = "mois";
    public static final String COL_ANNEE = "annee";
    public static final String COL_NOTE = "note";

    // Depenses columns
    public static final String COL_TITRE = "titre";
    public static final String COL_CATEGORIE = "categorie";
    public static final String COL_DESCRIPTION = "description";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_MEMBRES + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NOM + " TEXT NOT NULL, " +
                COL_PRENOM + " TEXT, " +
                COL_TELEPHONE + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_COTISATIONS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_MEMBRE_ID + " INTEGER NOT NULL, " +
                COL_MONTANT + " REAL NOT NULL, " +
                COL_DATE + " TEXT NOT NULL, " +
                COL_MOIS + " INTEGER, " +
                COL_ANNEE + " INTEGER, " +
                COL_NOTE + " TEXT, " +
                "FOREIGN KEY(" + COL_MEMBRE_ID + ") REFERENCES " + TABLE_MEMBRES + "(" + COL_ID + "))");

        db.execSQL("CREATE TABLE " + TABLE_DEPENSES + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TITRE + " TEXT NOT NULL, " +
                COL_MONTANT + " REAL NOT NULL, " +
                COL_DATE + " TEXT NOT NULL, " +
                COL_CATEGORIE + " TEXT, " +
                COL_DESCRIPTION + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEPENSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COTISATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEMBRES);
        onCreate(db);
    }

    // ============ MEMBRES ============
    public long ajouterMembre(Membre m) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NOM, m.nom);
        cv.put(COL_PRENOM, m.prenom);
        cv.put(COL_TELEPHONE, m.telephone);
        return db.insert(TABLE_MEMBRES, null, cv);
    }

    public List<Membre> getMembres() {
        List<Membre> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_MEMBRES, null, null, null, null, null, COL_NOM);
        while (c.moveToNext()) {
            Membre m = new Membre();
            m.id = c.getInt(c.getColumnIndexOrThrow(COL_ID));
            m.nom = c.getString(c.getColumnIndexOrThrow(COL_NOM));
            m.prenom = c.getString(c.getColumnIndexOrThrow(COL_PRENOM));
            m.telephone = c.getString(c.getColumnIndexOrThrow(COL_TELEPHONE));
            list.add(m);
        }
        c.close();
        return list;
    }

    public void modifierMembre(Membre m) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NOM, m.nom);
        cv.put(COL_PRENOM, m.prenom);
        cv.put(COL_TELEPHONE, m.telephone);
        db.update(TABLE_MEMBRES, cv, COL_ID + "=?", new String[]{String.valueOf(m.id)});
    }

    public void supprimerMembre(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_COTISATIONS, COL_MEMBRE_ID + "=?", new String[]{String.valueOf(id)});
        db.delete(TABLE_MEMBRES, COL_ID + "=?", new String[]{String.valueOf(id)});
    }

    // ============ COTISATIONS ============
    public long ajouterCotisation(Cotisation c) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_MEMBRE_ID, c.membreId);
        cv.put(COL_MONTANT, c.montant);
        cv.put(COL_DATE, c.date);
        cv.put(COL_MOIS, c.mois);
        cv.put(COL_ANNEE, c.annee);
        cv.put(COL_NOTE, c.note);
        return db.insert(TABLE_COTISATIONS, null, cv);
    }

    public List<Cotisation> getCotisations() {
        List<Cotisation> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT c.*, m.nom, m.prenom FROM " + TABLE_COTISATIONS + " c " +
                "LEFT JOIN " + TABLE_MEMBRES + " m ON c." + COL_MEMBRE_ID + " = m." + COL_ID +
                " ORDER BY c." + COL_DATE + " DESC";
        Cursor cur = db.rawQuery(sql, null);
        while (cur.moveToNext()) {
            Cotisation co = new Cotisation();
            co.id = cur.getInt(cur.getColumnIndexOrThrow(COL_ID));
            co.membreId = cur.getInt(cur.getColumnIndexOrThrow(COL_MEMBRE_ID));
            co.montant = cur.getDouble(cur.getColumnIndexOrThrow(COL_MONTANT));
            co.date = cur.getString(cur.getColumnIndexOrThrow(COL_DATE));
            co.mois = cur.getInt(cur.getColumnIndexOrThrow(COL_MOIS));
            co.annee = cur.getInt(cur.getColumnIndexOrThrow(COL_ANNEE));
            co.note = cur.getString(cur.getColumnIndexOrThrow(COL_NOTE));
            co.membreNom = cur.getString(cur.getColumnIndexOrThrow("nom")) + " " +
                    (cur.getString(cur.getColumnIndexOrThrow("prenom")) != null ?
                            cur.getString(cur.getColumnIndexOrThrow("prenom")) : "");
            list.add(co);
        }
        cur.close();
        return list;
    }

    public void supprimerCotisation(int id) {
        getWritableDatabase().delete(TABLE_COTISATIONS, COL_ID + "=?", new String[]{String.valueOf(id)});
    }

    public double getTotalCotisations() {
        Cursor c = getReadableDatabase().rawQuery("SELECT SUM(" + COL_MONTANT + ") FROM " + TABLE_COTISATIONS, null);
        double total = 0;
        if (c.moveToFirst()) total = c.getDouble(0);
        c.close();
        return total;
    }

    // ============ DEPENSES ============
    public long ajouterDepense(Depense d) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TITRE, d.titre);
        cv.put(COL_MONTANT, d.montant);
        cv.put(COL_DATE, d.date);
        cv.put(COL_CATEGORIE, d.categorie);
        cv.put(COL_DESCRIPTION, d.description);
        return db.insert(TABLE_DEPENSES, null, cv);
    }

    public List<Depense> getDepenses() {
        List<Depense> list = new ArrayList<>();
        Cursor c = getReadableDatabase().query(TABLE_DEPENSES, null, null, null, null, null, COL_DATE + " DESC");
        while (c.moveToNext()) {
            Depense d = new Depense();
            d.id = c.getInt(c.getColumnIndexOrThrow(COL_ID));
            d.titre = c.getString(c.getColumnIndexOrThrow(COL_TITRE));
            d.montant = c.getDouble(c.getColumnIndexOrThrow(COL_MONTANT));
            d.date = c.getString(c.getColumnIndexOrThrow(COL_DATE));
            d.categorie = c.getString(c.getColumnIndexOrThrow(COL_CATEGORIE));
            d.description = c.getString(c.getColumnIndexOrThrow(COL_DESCRIPTION));
            list.add(d);
        }
        c.close();
        return list;
    }

    public void supprimerDepense(int id) {
        getWritableDatabase().delete(TABLE_DEPENSES, COL_ID + "=?", new String[]{String.valueOf(id)});
    }

    public double getTotalDepenses() {
        Cursor c = getReadableDatabase().rawQuery("SELECT SUM(" + COL_MONTANT + ") FROM " + TABLE_DEPENSES, null);
        double total = 0;
        if (c.moveToFirst()) total = c.getDouble(0);
        c.close();
        return total;
    }
}
