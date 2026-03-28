package com.famille.cotisation;

import android.content.Context;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExcelExporter {

    private Context context;

    public ExcelExporter(Context context) {
        this.context = context;
    }

    public File exporterRapport(List<Membre> membres, List<Cotisation> cotisations, List<Depense> depenses) throws Exception {
        XSSFWorkbook wb = new XSSFWorkbook();

        // Styles
        XSSFCellStyle headerStyle = createHeaderStyle(wb);
        XSSFCellStyle titleStyle = createTitleStyle(wb);
        XSSFCellStyle moneyStyle = createMoneyStyle(wb);
        XSSFCellStyle totalStyle = createTotalStyle(wb);
        XSSFCellStyle altStyle = createAltStyle(wb);

        // === Feuille 1 : Résumé ===
        XSSFSheet resume = wb.createSheet("Résumé");
        fillResumeSheet(resume, membres, cotisations, depenses, titleStyle, headerStyle, moneyStyle, totalStyle);

        // === Feuille 2 : Membres ===
        XSSFSheet fMembres = wb.createSheet("Membres");
        fillMembresSheet(fMembres, membres, titleStyle, headerStyle, altStyle);

        // === Feuille 3 : Cotisations ===
        XSSFSheet fCotis = wb.createSheet("Cotisations");
        fillCotisationsSheet(fCotis, cotisations, titleStyle, headerStyle, moneyStyle, totalStyle, altStyle);

        // === Feuille 4 : Dépenses ===
        XSSFSheet fDep = wb.createSheet("Dépenses");
        fillDepensesSheet(fDep, depenses, titleStyle, headerStyle, moneyStyle, totalStyle, altStyle);

        // Enregistrement
        String dateStr = new SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(new Date());
        File dir = context.getExternalFilesDir(null);
        if (dir == null) dir = context.getFilesDir();
        File file = new File(dir, "Famille_Rapport_" + dateStr + ".xlsx");
        FileOutputStream fos = new FileOutputStream(file);
        wb.write(fos);
        fos.close();
        wb.close();
        return file;
    }

    private void fillResumeSheet(XSSFSheet sheet, List<Membre> membres,
                                  List<Cotisation> cotisations, List<Depense> depenses,
                                  XSSFCellStyle titleStyle, XSSFCellStyle headerStyle,
                                  XSSFCellStyle moneyStyle, XSSFCellStyle totalStyle) {
        sheet.setColumnWidth(0, 7000);
        sheet.setColumnWidth(1, 5000);

        int row = 0;
        Row title = sheet.createRow(row++);
        Cell titleCell = title.createCell(0);
        titleCell.setCellValue("RAPPORT FAMILLE - " +
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()));
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));

        row++;
        Row h = sheet.createRow(row++);
        createHeaderCell(h, 0, "INDICATEUR", headerStyle);
        createHeaderCell(h, 1, "VALEUR", headerStyle);

        double totalCotis = 0; for (Cotisation c : cotisations) totalCotis += c.montant;
        double totalDep = 0; for (Depense d : depenses) totalDep += d.montant;
        double solde = totalCotis - totalDep;

        String[][] data = {
                {"Nombre de membres", String.valueOf(membres.size())},
                {"Nombre de cotisations", String.valueOf(cotisations.size())},
                {"Nombre de dépenses", String.valueOf(depenses.size())},
        };
        for (String[] d : data) {
            Row r = sheet.createRow(row++);
            r.createCell(0).setCellValue(d[0]);
            r.createCell(1).setCellValue(d[1]);
        }

        row++;
        Row rCotis = sheet.createRow(row++);
        rCotis.createCell(0).setCellValue("Total Cotisations (FCFA)");
        Cell cCotis = rCotis.createCell(1);
        cCotis.setCellValue(totalCotis);
        cCotis.setCellStyle(moneyStyle);

        Row rDep = sheet.createRow(row++);
        rDep.createCell(0).setCellValue("Total Dépenses (FCFA)");
        Cell cDep = rDep.createCell(1);
        cDep.setCellValue(totalDep);
        cDep.setCellStyle(moneyStyle);

        Row rSolde = sheet.createRow(row++);
        Cell soldeLabel = rSolde.createCell(0);
        Cell soldeVal = rSolde.createCell(1);
        soldeLabel.setCellValue("SOLDE (FCFA)");
        soldeVal.setCellValue(solde);
        soldeLabel.setCellStyle(totalStyle);
        soldeVal.setCellStyle(totalStyle);
    }

    private void fillMembresSheet(XSSFSheet sheet, List<Membre> membres,
                                   XSSFCellStyle titleStyle, XSSFCellStyle headerStyle, XSSFCellStyle altStyle) {
        sheet.setColumnWidth(0, 1500);
        sheet.setColumnWidth(1, 5000);
        sheet.setColumnWidth(2, 5000);
        sheet.setColumnWidth(3, 4000);

        int row = 0;
        Row title = sheet.createRow(row++);
        Cell tc = title.createCell(0);
        tc.setCellValue("LISTE DES MEMBRES");
        tc.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
        row++;

        Row h = sheet.createRow(row++);
        createHeaderCell(h, 0, "#", headerStyle);
        createHeaderCell(h, 1, "NOM", headerStyle);
        createHeaderCell(h, 2, "PRÉNOM", headerStyle);
        createHeaderCell(h, 3, "TÉLÉPHONE", headerStyle);

        for (int i = 0; i < membres.size(); i++) {
            Membre m = membres.get(i);
            Row r = sheet.createRow(row++);
            if (i % 2 == 1) {
                r.createCell(0).setCellStyle(altStyle);
                r.createCell(1).setCellStyle(altStyle);
                r.createCell(2).setCellStyle(altStyle);
                r.createCell(3).setCellStyle(altStyle);
            }
            r.createCell(0).setCellValue(i + 1);
            r.createCell(1).setCellValue(m.nom != null ? m.nom : "");
            r.createCell(2).setCellValue(m.prenom != null ? m.prenom : "");
            r.createCell(3).setCellValue(m.telephone != null ? m.telephone : "");
        }
    }

    private void fillCotisationsSheet(XSSFSheet sheet, List<Cotisation> cotisations,
                                       XSSFCellStyle titleStyle, XSSFCellStyle headerStyle,
                                       XSSFCellStyle moneyStyle, XSSFCellStyle totalStyle, XSSFCellStyle altStyle) {
        sheet.setColumnWidth(0, 1500);
        sheet.setColumnWidth(1, 6000);
        sheet.setColumnWidth(2, 4000);
        sheet.setColumnWidth(3, 3000);
        sheet.setColumnWidth(4, 3000);
        sheet.setColumnWidth(5, 5000);

        int row = 0;
        Row title = sheet.createRow(row++);
        Cell tc = title.createCell(0);
        tc.setCellValue("LISTE DES COTISATIONS");
        tc.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
        row++;

        Row h = sheet.createRow(row++);
        createHeaderCell(h, 0, "#", headerStyle);
        createHeaderCell(h, 1, "MEMBRE", headerStyle);
        createHeaderCell(h, 2, "MONTANT (FCFA)", headerStyle);
        createHeaderCell(h, 3, "DATE", headerStyle);
        createHeaderCell(h, 4, "MOIS/ANNÉE", headerStyle);
        createHeaderCell(h, 5, "NOTE", headerStyle);

        String[] moisNoms = {"", "Jan", "Fév", "Mar", "Avr", "Mai", "Jun",
                "Jul", "Aoû", "Sep", "Oct", "Nov", "Déc"};

        for (int i = 0; i < cotisations.size(); i++) {
            Cotisation c = cotisations.get(i);
            Row r = sheet.createRow(row++);
            r.createCell(0).setCellValue(i + 1);
            r.createCell(1).setCellValue(c.membreNom != null ? c.membreNom.trim() : "");
            Cell mc = r.createCell(2);
            mc.setCellValue(c.montant);
            mc.setCellStyle(moneyStyle);
            r.createCell(3).setCellValue(c.date != null ? c.date : "");
            String periode = (c.mois > 0 && c.mois <= 12) ?
                    moisNoms[c.mois] + " " + c.annee : "";
            r.createCell(4).setCellValue(periode);
            r.createCell(5).setCellValue(c.note != null ? c.note : "");
        }

        // Total
        if (!cotisations.isEmpty()) {
            row++;
            double total = 0; for (Cotisation c : cotisations) total += c.montant;
            Row rTotal = sheet.createRow(row);
            Cell lbl = rTotal.createCell(1);
            Cell val = rTotal.createCell(2);
            lbl.setCellValue("TOTAL");
            val.setCellValue(total);
            lbl.setCellStyle(totalStyle);
            val.setCellStyle(totalStyle);
        }
    }

    private void fillDepensesSheet(XSSFSheet sheet, List<Depense> depenses,
                                    XSSFCellStyle titleStyle, XSSFCellStyle headerStyle,
                                    XSSFCellStyle moneyStyle, XSSFCellStyle totalStyle, XSSFCellStyle altStyle) {
        sheet.setColumnWidth(0, 1500);
        sheet.setColumnWidth(1, 6000);
        sheet.setColumnWidth(2, 4000);
        sheet.setColumnWidth(3, 3000);
        sheet.setColumnWidth(4, 4000);
        sheet.setColumnWidth(5, 7000);

        int row = 0;
        Row title = sheet.createRow(row++);
        Cell tc = title.createCell(0);
        tc.setCellValue("LISTE DES DÉPENSES");
        tc.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
        row++;

        Row h = sheet.createRow(row++);
        createHeaderCell(h, 0, "#", headerStyle);
        createHeaderCell(h, 1, "TITRE", headerStyle);
        createHeaderCell(h, 2, "MONTANT (FCFA)", headerStyle);
        createHeaderCell(h, 3, "DATE", headerStyle);
        createHeaderCell(h, 4, "CATÉGORIE", headerStyle);
        createHeaderCell(h, 5, "DESCRIPTION", headerStyle);

        for (int i = 0; i < depenses.size(); i++) {
            Depense d = depenses.get(i);
            Row r = sheet.createRow(row++);
            r.createCell(0).setCellValue(i + 1);
            r.createCell(1).setCellValue(d.titre != null ? d.titre : "");
            Cell mc = r.createCell(2);
            mc.setCellValue(d.montant);
            mc.setCellStyle(moneyStyle);
            r.createCell(3).setCellValue(d.date != null ? d.date : "");
            r.createCell(4).setCellValue(d.categorie != null ? d.categorie : "");
            r.createCell(5).setCellValue(d.description != null ? d.description : "");
        }

        if (!depenses.isEmpty()) {
            row++;
            double total = 0; for (Depense d : depenses) total += d.montant;
            Row rTotal = sheet.createRow(row);
            Cell lbl = rTotal.createCell(1);
            Cell val = rTotal.createCell(2);
            lbl.setCellValue("TOTAL");
            val.setCellValue(total);
            lbl.setCellStyle(totalStyle);
            val.setCellStyle(totalStyle);
        }
    }

    private void createHeaderCell(Row row, int col, String value, XSSFCellStyle style) {
        Cell c = row.createCell(col);
        c.setCellValue(value);
        c.setCellStyle(style);
    }

    private XSSFCellStyle createHeaderStyle(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        XSSFFont f = wb.createFont();
        f.setBold(true);
        f.setColor(IndexedColors.WHITE.getIndex());
        s.setFont(f);
        s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)33, (byte)97, (byte)140}, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setBorderBottom(BorderStyle.THIN);
        return s;
    }

    private XSSFCellStyle createTitleStyle(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        XSSFFont f = wb.createFont();
        f.setBold(true);
        f.setFontHeightInPoints((short) 14);
        f.setColor(IndexedColors.WHITE.getIndex());
        s.setFont(f);
        s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)21, (byte)67, (byte)96}, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER);
        return s;
    }

    private XSSFCellStyle createMoneyStyle(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        DataFormat df = wb.createDataFormat();
        s.setDataFormat(df.getFormat("#,##0"));
        s.setAlignment(HorizontalAlignment.RIGHT);
        return s;
    }

    private XSSFCellStyle createTotalStyle(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        XSSFFont f = wb.createFont();
        f.setBold(true);
        s.setFont(f);
        DataFormat df = wb.createDataFormat();
        s.setDataFormat(df.getFormat("#,##0"));
        s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)212, (byte)230, (byte)241}, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setBorderTop(BorderStyle.MEDIUM);
        return s;
    }

    private XSSFCellStyle createAltStyle(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)235, (byte)245, (byte)251}, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return s;
    }
}
