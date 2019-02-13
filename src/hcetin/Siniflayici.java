/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hcetin;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Cetin
 */
public abstract class Siniflayici {
    /*ArgMax tarafından kullanılacak*/
    protected static Random SayiUret;
    static { SayiUret = new Random(); }
    
    /* Sınıfların bulunduğu dizi */
    protected Object[] Kategoriler;
    
    /* Sınıf sayısı */
    protected int KategoriSay;
    
    /* Kategoriler (Sınıflar) veri tablosunun hangi kolonunda bulunacak */
    protected int KategoriKolonu;
    
    /* Veri setindeki toplam kayıt (satır) sayısı */
    protected int ToplamOrnekSay;
    
    /* Modeli eğitmek için kullanılacak örneklerin sayısı 
     * ModeliEgit metodu tarafından kullanılacak */
    protected int EgitimOrnekSay;
    
    protected Object[] Nitelikler;
    
    /* Nitelik sayısı*/
    protected int NitelikSay;
    
    /* Modeli eğitmek için kullanılacak örneklerin sayısı 
     * Test metodu tarafından kullanılacak */    
    protected int TestOrnekSay;    
    
    /* Modeli eğitmek için kullanılacak eğitim setinin,
     * veri setinin ne kadarlık kısmından alınacağının yüzdelik oranı 
     */
    protected double EgitimSetiOrani;

    protected Hashtable<String, DefaultTableModel> DataSet = new Hashtable();
        
    public abstract void ModeliEgit(DefaultTableModel egitimSeti);
    
    public abstract String Sinifla(Object[] veri);
    
    public abstract Double Test();
    
    protected int ArgMax(Vector<Object> girdi){
        ArrayList<Integer> indeks = new ArrayList();
        int donen;
        indeks.add(0);
        double maks = Double.parseDouble(girdi.get(0).toString());
        for (int i=0; i<girdi.size(); i++){
            if (Double.parseDouble(girdi.get(i).toString())>maks){
                maks = Double.parseDouble(girdi.get(i).toString());
                indeks.clear();
                indeks.add(i);
            } else if(Double.parseDouble(girdi.get(i).toString())==maks){
                indeks.add(i);
            }
        }
        if (indeks.size()>1){
            int yeni = SayiUret.nextInt(indeks.size());
            donen = indeks.get(yeni);
        } else {
            donen = indeks.get(0);
        }
        return donen;
    }
    
    
    protected Vector<Object> KosulluSec(DefaultTableModel ornekler, int col, Object kosul){
        Vector<Object> liste = new Vector();
        liste.clear();
        for (int i = 0; i < EgitimOrnekSay; i++)
            if (ornekler.getValueAt(i, KategoriKolonu).equals(kosul)) 
                liste.add(ornekler.getValueAt(i, col));
            
        return liste;
    }      
}
