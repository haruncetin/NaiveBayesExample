/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hcetin;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
import java.util.logging.*;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 *
 * @author Cetin
 */
public class Veriler{
    private Object[] Sutunlar;
    private Vector<Object> Veri = new Vector();
    private static DefaultTableModel tablo;
    private static int KategoriKolonu;
    
    public DagilimTablosu Dagilimlar = new DagilimTablosu();
    public VeriTablosu Tablolar = new VeriTablosu();
    public Filtreler Filtreler = new Filtreler();
    
    private double EPSILON = 1e-6;
    
    public Veriler(final String dosyaAdi, final Object[] sutunlar, final int katkolonu) throws FileNotFoundException {
        System.out.println("Veri seti alınıyor...");
        new Thread(new Runnable(){

            @Override
            public void run() {
                FileInputStream d = null;
                try {
                    KategoriKolonu = katkolonu-1;
                    Sutunlar = sutunlar;
                    d = new FileInputStream(dosyaAdi);
                    try (BufferedReader oku = new BufferedReader(new InputStreamReader(d))) {
                        tablo = new DefaultTableModel();
                        String satir;
                        StringTokenizer st = new StringTokenizer(oku.readLine(), ",");
                        
                        tablo.setColumnIdentifiers(Sutunlar);
                        
                        while ((satir = oku.readLine()) != null) {
                            int i=0; st = new StringTokenizer(satir, ",");
                            Veri = new Vector();
                            while(st.hasMoreTokens()){
                                Object tmpv;
                                int tmpn = 0;
                                if (i>9 && i<14){
                                    for (int a=0; a<4; a++){
                                        tmpv = st.nextToken().replaceAll("\"", "");
                                        if (tmpv.equals("1")) tmpn = 4-a;
                                        //System.out.println("i="+i+", a="+a);
                                        //System.out.println("tmpv="+tmpv+", tmpn="+tmpn);
                                        i++;
                                    }
                                    Veri.addElement(tmpn);
                                    tmpn=0;
                                }else if (i>13 && i<54){
                                    for (int a=0; a<40; a++){
                                        tmpv = st.nextToken().replaceAll("\"", "");
                                        if (tmpv.equals("1")) tmpn = 40-a;
                                        //System.out.println("i="+i+", a="+a);
                                        //System.out.println("tmpv="+tmpv+", tmpn="+tmpn);
                                        i++;
                                    }
                                    Veri.addElement(tmpn);
                                    tmpn=0;
                                }else {
                                    tmpv = st.nextToken().replaceAll("\"", "");
                                    Veri.addElement(tmpv);
                                    //System.out.println("i="+i+", a="+a);
                                    //System.out.println("tmpv="+tmpv+", tmpn="+tmpn);
                                }
                                i++;
                            }
                            tablo.addRow(Veri);
                        }
                        Dagilimlar.DagilimlariHesapla();
                    } catch(Exception e){
                        System.out.println("Veriler alınırken hata oluştu: "+e.getMessage());
                    }
                } catch(FileNotFoundException ex){
                    Logger.getLogger(Veriler.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        d.close();
                    } catch (IOException ex) {
                        Logger.getLogger(Veriler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        
        });

    }

    public class VeriTablosu {
        public DefaultTableModel VeriTablosuAl(){
            return tablo;
        }
        
        public DefaultTableModel DagilimTablosuAl(){
            System.out.println("Verilerin dağılımı tablosu alınıyor...");
            DefaultTableModel tblret = new DefaultTableModel();
            Vector tmpvek;

            tblret.addColumn("İstatistik");
            for (int i = 0; i < tablo.getColumnCount(); i++){
                if (i == KategoriKolonu) continue;
                tblret.addColumn(tablo.getColumnName(i));
            }

            tmpvek = new Vector();
            tmpvek.add("Minimum");
            for (int i = 0; i < tablo.getColumnCount(); i++){
                if (i == KategoriKolonu) continue;
                tmpvek.add(Dagilimlar.SutunMinAl(i));
            }        
            tblret.addRow(tmpvek); 
            //System.out.println(tmpvek);

            tmpvek = new Vector();
            tmpvek.add("Maksimum");        
            for (int i = 0; i < tablo.getColumnCount(); i++){
                if (i == KategoriKolonu) continue;
                tmpvek.add(Dagilimlar.SutunMaxAl(i));
            }
            tblret.addRow(tmpvek); 
            //System.out.println(tmpvek);

            tmpvek = new Vector();
            tmpvek.add("Ortalama");        
            for (int i = 0; i < tablo.getColumnCount(); i++){
                if (i == KategoriKolonu) continue;
                tmpvek.add(Dagilimlar.SutunOrtAl(i));
            }
            tblret.addRow(tmpvek); 
            //System.out.println(tmpvek);

            tmpvek = new Vector();
            tmpvek.add("Varyans");        
            for (int i = 0; i < tablo.getColumnCount(); i++){
                if (i == KategoriKolonu) continue;
                tmpvek.add(Dagilimlar.SutunVarAl(i));
            }
            tblret.addRow(tmpvek); 
            //System.out.println(tmpvek);
            //System.out.println("Verilerin dağılımı tablosunu alma işlemi tamamlandı.");

            return tblret;
        } 

    }
    
    public class DagilimTablosu {
        private Vector<Object> SutunOrt = new Vector();
        private Vector<Object> SutunVar = new Vector();
        private Vector<Object> SutunMax = new Vector();
        private Vector<Object> SutunMin = new Vector();                

        private void DagilimlariHesapla(){
            System.out.println("Verilerin dağılımları hesaplanıyor...");
            
            Vector<Object> tmpv = new Vector();
            SutunOrt.clear(); SutunVar.clear();
            SutunMin.clear(); SutunMax.clear();
            for (int k=0; k<tablo.getColumnCount(); k++){
                if (k == KategoriKolonu) continue;
                tmpv.clear();
                for (int j=0; j<tablo.getRowCount(); j++){
                    tmpv.add(tablo.getValueAt(j, k));
                }
                //System.out.println(tmpv);
                SutunOrt.add(Mat.Ortalama(tmpv.toArray()));
                SutunVar.add(Mat.Varyans(tmpv.toArray()));            
                SutunMin.add(Mat.Minimum(tmpv.toArray()));
                SutunMax.add(Mat.Maksimum(tmpv.toArray()));
            }
            //System.out.println("Veri dağılımları hesaplama tamamlandı.");            
        }
        
        public double SutunMinAl(int sut){
            return Double.parseDouble(SutunMin.get(sut).toString());
        }
        public double SutunMaxAl(int sut){
            return Double.parseDouble(SutunMax.get(sut).toString());
        }
        public double SutunOrtAl(int sut){
            return Double.parseDouble(SutunOrt.get(sut).toString());
        }
        public double SutunVarAl(int sut){
            return Double.parseDouble(SutunVar.get(sut).toString());
        }
    }
    
    public class Filtreler {
        public void MinMaxUygula(Object min, Object max) {
            System.out.println("Veri setine MinMax Normalizasyonu uygulanıyor...");
            
            Double nmin = Double.parseDouble(min.toString());
            Double nmax = Double.parseDouble(max.toString());    

            DefaultTableModel tmpTablo = new DefaultTableModel();
            for (int i=0; i<tablo.getColumnCount(); i++){
                if (i == KategoriKolonu) continue;
                    tmpTablo.addColumn(tablo.getColumnName(i).toString());
            }
            tmpTablo.addColumn(tablo.getColumnName(KategoriKolonu).toString()+"(Sınıf)");

            Vector<Object> v1;
            for (int i=0; i<tablo.getRowCount(); i++) {
                v1 = new Vector();
                for (int j=0; j<tablo.getColumnCount(); j++){
                    if (j == KategoriKolonu) continue;
                    Double v = Double.parseDouble(tablo.getValueAt(i, j).toString());
                    Double mina = Dagilimlar.SutunMinAl(j);
                    Double maxa = Dagilimlar.SutunMaxAl(j);
                    Double nval = ((v-mina)/(maxa-mina)*(nmax-nmin)+nmin);
                    if ((maxa-mina) == 0) nval = EPSILON;
                    v1.add(nval);
                }
                v1.add(tablo.getValueAt(i, KategoriKolonu));
                tmpTablo.addRow(v1);
            }
            tablo = new DefaultTableModel();
            tablo = tmpTablo;  
            Dagilimlar.DagilimlariHesapla();
            System.out.println("TAMAM.");
            

        }
        public void ZScoreUygula() {
            System.out.println("Veri setine Z-Score Normalizasyon işlemi uygulanıyor...");
            
            DefaultTableModel tmpTablo = new DefaultTableModel();
            for (int i=0; i<tablo.getColumnCount(); i++){
                if (i == KategoriKolonu) continue;
                    tmpTablo.addColumn(tablo.getColumnName(i).toString());
            }
            tmpTablo.addColumn(tablo.getColumnName(KategoriKolonu).toString()+"(Sınıf)");

            Vector<Object> v1;
            for (int i=0; i<tablo.getRowCount(); i++) {
                v1 = new Vector();
                for (int j=0; j<tablo.getColumnCount(); j++){
                    if (j == KategoriKolonu) continue;
                    Double v = Double.parseDouble(tablo.getValueAt(i, j).toString());
                    Double var = Dagilimlar.SutunVarAl(j);
                    Double ort = Dagilimlar.SutunOrtAl(j);
                    Double nval = ((v-ort)/(var));
                    if (var == (double)0) nval = EPSILON;
                    v1.add(nval);
                }
                v1.add(tablo.getValueAt(i, KategoriKolonu));
                tmpTablo.addRow(v1);
            }
            tablo = new DefaultTableModel();
            tablo = tmpTablo;
            Dagilimlar.DagilimlariHesapla();
            System.out.println("TAMAM.");            
        }        
    }
    
    public Object[] KolonBasliklari(){
        return Sutunlar;
    }
    
    public List<Object> SutunuAl(int kol){
        List<Object> kolon = new ArrayList();
        for (int i=0; i<tablo.getRowCount(); i++)
            kolon.add(tablo.getValueAt(i, kol));
        return kolon;
    }
    
}
