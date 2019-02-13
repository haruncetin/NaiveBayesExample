package hcetin;

import java.math.BigDecimal;

/**
 * İstatistik metotları için
 * @author Cetin
 */
public class Mat {
    public static double Ortalama(Object[] girdi) {
        Double toplam = 0.0;
        for (int i=0; i<girdi.length; i++){
            toplam += Double.parseDouble(girdi[i].toString());
        }
        return (double)(toplam/girdi.length);
    }
    
    public static double StdSapma(Object[] girdi) {
        return Math.sqrt(Varyans(girdi));
    }
    
    public static double Varyans(Object[] girdi){
        Double top=0.0;
        Double ort = Ortalama(girdi);
        for (int i=0; i<girdi.length; i++){
            top += Math.pow((Double.parseDouble(girdi[i].toString())-ort), 2);
        }

        return top/(girdi.length - 1);
    }

    // Standart Normal Dağılım için Olasılık Yoğunluk Fonksiyonu
    public static double GaussOYF(Object x, Object ort, Object ss){
        // ss: standart sapma değeri
        Double nx, nort, nss;
        nx = Double.parseDouble(x.toString());
        nort = Double.parseDouble(ort.toString());
        nss = Double.parseDouble(ss.toString());
        Double c = Double.parseDouble(ss.toString())* Math.sqrt(2.0 * Math.PI);
        Double us = (Math.pow((nx-nort),2))/(2.0 * Math.pow(nss,2));
        return (Double)Math.exp(-us)/c;
    }
    
    public static double Olasilik (Object[] girdi, Object x){
        double say = 0;
        for (int i = 0; i < girdi.length; i++) if (girdi[i].equals(x)) say++;
        
        return (double)(say/girdi.length);
    }
    
    public static int Say (Object[] girdi, Object x){
        int sayi = 0;
        for (int i = 0; i < girdi.length; i++) if (girdi[i].equals(x.toString())) sayi++;        
        
        return (int)(sayi);
    }
    
    public static double Yuvarla(double d) {
        BigDecimal bd = new BigDecimal(d);
        bd = bd.setScale(2, BigDecimal.ROUND_UP);
        return (double)bd.doubleValue();
    }

    public static double Maksimum(Object[] girdi) {
        double maks = Double.parseDouble(girdi[0].toString());  
        for(int i=1;i < girdi.length;i++){  
            if(Double.parseDouble(girdi[i].toString()) > maks){  
                maks = Double.parseDouble(girdi[i].toString());  
            }
        }
        
        return maks;  
    }
    
    public static double Minimum(Object[] girdi) throws NumberFormatException { 
        double min = Double.parseDouble(girdi[0].toString());  
        for(int i=1;i<girdi.length;i++){  
            if(Double.parseDouble(girdi[i].toString()) < min)
                min = Double.parseDouble(girdi[i].toString());            
        }  
        
        return min;
    }
}
