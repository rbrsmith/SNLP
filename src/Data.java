import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Data {

    HashMap data;
    int size;

    public Data(int size) {
        this.size = size;
        if(size == 1) {
            data = new HashMap<String, TE>();
        } else {
            data = new HashMap<String, Data>();
        }
    }

    public boolean add(String words) {
        return this.add(new NGram(words), new TE());
    }


    public boolean add(NGram words, TE entry) {
        NGram ws = new NGram(words);
        if(words.size() != size) {
            System.err.println("Invalid entry for ngram");
            return false;
        }


        String key = words.get(0);
        if(data.get(key) != null) {

            if(size == 1) {
                TE cur = (TE) data.get(key);
                cur.addCount();
                return true;
            } else {
                Data d = (Data) data.get(key);
                ws.remove(0);
                d.add(ws, entry);
                data.put(key, d);
                return true;
            }
        } else {
            if(size == 1) {
                data.put(key, entry);
                return true;
            } else {
                Data d = new Data(size-1);
                ws.remove(0);
                d.add(ws, entry);
                data.put(key, d);
                return true;
            }
        }



    }

    public TE get(String words) {
        ArrayList<String> ws = new ArrayList<>(Arrays.asList(words.split(" ")));
        return get(ws);
    }

    public TE get(ArrayList<String> words) {
        ArrayList<String> ws = new ArrayList<>(words);

        if(ws.size() != size) {
            return null;
        }

        String key = ws.get(0);
        if(size == 1) {
            return (TE) data.get(key);
        } else {
            ws.remove(0);
            Data d = (Data) data.get(key);
            if(d == null) {
                return null;
            }
            return d.get(ws);
        }




    }

    public void refreshProbabilities() {
        if(size == 1) {
            int totalCount = 0;
            for(Object obj : data.values()){
                TE entry = (TE) obj;
                totalCount += entry.getCount();
            }


            for(Object obj : data.values()){
                TE entry = (TE) obj;
                entry.setProb(entry.getCount() / (double) totalCount * 100);
            }
        } else {
            for(Object obj : data.values()) {
                Data d = (Data) obj;
                if(d == null) continue;
                d.refreshProbabilities();
            }

        }


    }




}
