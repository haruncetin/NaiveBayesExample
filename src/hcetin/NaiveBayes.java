package hcetin;

import java.util.Vector;
import javax.swing.table.DefaultTableModel;

/**
 * NaiveBayes ile ilgili metotlar burada yer almakta
 * @author Harun Cetin 10280161
 * Fen Bilimleri Enstitüsü İleri Teknolojiler A.B.D.
 */
public class NaiveBayes extends Siniflayici implements Runnable {

    private boolean debug;
        
    private DefaultTableModel Dagilimlar;

    public NaiveBayes(Object[] kategoriler, int sinifKolonu, DefaultTableModel dagilimlar){
        Dagilimlar = new DefaultTableModel();
        Dagilimlar = dagilimlar;

        ToplamOrnekSay = 0;
        
        EgitimSetiOrani = 0.66;
        
        /*
         * Sınıflama (Kategorizasyon) hangi sutun üzerinden yapılacak..
         * Neden sinifKolonu-1? Çünkü görsel sıralama 1'den indeks 0'dan başlıyor.
        */
        KategoriKolonu = sinifKolonu-1;
        
        Kategoriler = kategoriler;
        
        /* Toplam kategori sayısı */
        KategoriSay = kategoriler.length;

        debug = true;

        // System.out.println("Naive Bayes...\n");
    }
    
    public void KategoriKolonuAyarla(int katkolonu){
        KategoriKolonu = katkolonu;
    }
    
    public void NitelikleriAyarla(Object[] nitelikler){
        Nitelikler = nitelikler;
    }    
    
    public void DegerDagilimlariniAyarla(DefaultTableModel dagilimlar){
        Dagilimlar = dagilimlar;
    }
    
    public void EgitimSetiOraniAyarla(Object oran){
        EgitimSetiOrani = Double.parseDouble(oran.toString());
    }
    
    public Object[] KategorileriAl() { return Kategoriler; }
    
    public int KategoriSayisiniAl(){
        return KategoriSay;
    }
    
    public Object[] NitelikleriAl(){
        return Nitelikler;
    }
    
    public int NitelikSayisiniAl(){
        return Nitelikler.length;
    }
    
    public int ToplamOrnekSayisiniAl(){
        return ToplamOrnekSay;
    }
    
    public int EgitimOrnekSayisiniAl(){
        return EgitimOrnekSay;
    }
    
    @Override 
    public void ModeliEgit(DefaultTableModel table){
        final DefaultTableModel t = table;
        new Thread(new Runnable() {
            @Override
            public void run() {
                ToplamOrnekSay = t.getRowCount();
                EgitimOrnekSay = (int)(ToplamOrnekSay * (EgitimSetiOrani/100.0));
                NitelikSay = t.getColumnCount();

                DataSet.put("Ornekler", t);

                DefaultTableModel SinifDagilimlari = new DefaultTableModel();
                DataSet.put("SiniflarinDagilimi", SinifDagilimlari);

                SinifDagilimlari.addColumn(t.getColumnName(KategoriKolonu).toString()+"(Sınıf)");
                SinifDagilimlari.addColumn("P(Ci)");

                for (int i=0; i<NitelikSay; i++){
                    if (i == KategoriKolonu) continue;
                        SinifDagilimlari.addColumn(t.getColumnName(i).toString()+"(O)");
                        SinifDagilimlari.addColumn(t.getColumnName(i).toString()+"(V)");
                }

                Vector<Object> snfsut = new Vector();
                for (int i=0; i<EgitimOrnekSay; i++) snfsut.add(t.getValueAt(i, KategoriKolonu));

                for (int i=0; i<KategoriSay; i++) {
                    Vector<Object> v1 = new Vector();
                    v1.add(Kategoriler[i]);
                    v1.add(Mat.Olasilik(snfsut.toArray(), Kategoriler[i]));
                    for (int j=0; j<NitelikSay; j++){                
                        if (j==KategoriKolonu) continue;                
                        v1.add(Mat.Ortalama(KosulluSec(t, j, Kategoriler[i]).toArray()));
                        v1.add(Mat.Varyans(KosulluSec(t, j, Kategoriler[i]).toArray()));
                    }
                    SinifDagilimlari.addRow(v1);
                    if (debug) System.out.println(v1);
                }
            }
        }).start();

    }
    
    @Override
    public String Sinifla(Object[] ornek){
        Vector<Object> kosulluolslar = new Vector();
        Vector<String> siniflar = new Vector();
     
        for (int i=0; i<KategoriSay; i++){
            double pc = Double.parseDouble(DataSet.get("SiniflarinDagilimi").getValueAt(i, 1).toString());
            for (int a=0, j=2; j<DataSet.get("SiniflarinDagilimi").getColumnCount(); j+=2){
                double ortalama = Double.parseDouble(DataSet.get("SiniflarinDagilimi").getValueAt(i, j).toString());
                double varyans = Double.parseDouble(DataSet.get("SiniflarinDagilimi").getValueAt(i, j+1).toString());
                double pxort = Double.parseDouble(Dagilimlar.getValueAt(2, a+1).toString());
                double pxvar = Double.parseDouble(Dagilimlar.getValueAt(3, a+1).toString());
                double px = Mat.GaussOYF(ornek[a], pxort, pxvar);
                double sonuc = Mat.GaussOYF(ornek[a], ortalama, Math.sqrt(varyans))*(pc/px);
                kosulluolslar.add(sonuc);
                siniflar.add(Kategoriler[i].toString());
                //if(debug) System.out.println("P(C="+ Kategoriler[i].toString()+"|x="+ornek[a]+")="+sonuc);
                a++;                
            }
        }
        String bayes =siniflar.get(ArgMax(kosulluolslar));
   
        //if(debug) System.out.println("Max(puanlar)="+Mat.Maksimum(kosulluolslar.toArray()));
        //if(debug) System.out.println("Öngörülen Sınıf="+bayes);
        kosulluolslar.clear(); siniflar.clear();        
        return bayes;        
    }

    @Override
    public Double Test(){
        final double topsay=0, dogru=0;
        new Thread(new Runnable() {
            @Override
            public void run() {
               double Topsay=0, Dogru=0;
               TestOrnekSay = ToplamOrnekSay - EgitimOrnekSay;
               if (debug) System.out.println("Test başladı...");
               if (debug) System.out.println("ToplamOrnekSay="+ToplamOrnekSay);
               //if (debug) System.out.println("EgitimOrnekSay="+EgitimOrnekSay);
               if (debug) System.out.println("TestOrnekSay="+TestOrnekSay);
               if (debug) System.out.println("EgitimSetiOrani="+EgitimSetiOrani);
               
               for (int k=0; k<TestOrnekSay; k++){
                   String gerceksinif = DataSet.get("Ornekler").getValueAt(k, KategoriKolonu).toString();
                   Object[] ornek = new Object[NitelikSay];
                   for (int i = 0; i<NitelikSay; i++){
                       if (i == KategoriKolonu) continue;
                       ornek[i] = DataSet.get("Ornekler").getValueAt(EgitimOrnekSay+k, i);
                   }

                   String tahminisinif = Sinifla(ornek);
                   //if (debug) System.out.println("Öngörülen Sınıf="+tahminisinif+", Gerçek Sınıf="+gerceksinif);

                   if (tahminisinif.equals(gerceksinif)) Dogru++; Topsay++;
                   /*if(debug){
                       if (k%90==0) System.out.println();
                       System.out.print(".");
                   } //ilerleme takibi*/
               }
               if (debug) System.out.println("\nDogru Sınıflanan Örnek Sayısı="+Dogru+", Test Örnek Sayısı="+Topsay);        
            }
        }).start();
        return 100*(dogru/topsay);
    }
    
    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}