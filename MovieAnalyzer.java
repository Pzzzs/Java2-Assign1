import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MovieAnalyzer {

    int     Poster_Link=0,
            Series_Title=1,
            Released_Year=2,
            Certificate=3,
            Runtime=4,
            Genre=5,
            IMDB_Rating=6,
            Overview=7,
            Meta_score=8,
            Director=9,
            Star1=10,
            Star2=11,
            Star3=12,
            Star4=13,
            No_of_Votes=14,
            Gross=15;

    String dataset_path;

    public MovieAnalyzer(String dataset_path) {
        this.dataset_path = dataset_path;
    }

    public Map<Integer, Integer> getMovieCountByYear(){
        String line;
        String[] splitArray;
        Map<Integer, Integer> map = new HashMap<>();

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(dataset_path), StandardCharsets.UTF_8))) {
            bufferedReader.readLine();
            while ((line = bufferedReader.readLine()) != null) {
                splitArray = line.trim().split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)",-1);
                int year = Integer.parseInt(splitArray[2]);
                if (map.containsKey(year)){
                    map.put(year,map.get(year)+1);
                }
                else map.put(year,1);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Map.Entry<Integer, Integer>> list = new LinkedList(map.entrySet());
        Collections.sort(list, (o1, o2) -> o2.getKey().compareTo(o1.getKey()));
        Map<Integer, Integer> result = new LinkedHashMap<>();
        for (Map.Entry<Integer, Integer> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;

    }

    public Map<String, Integer> getMovieCountByGenre(){
        String line;
        String[] splitArray;
        Map<String, Integer> map = new HashMap<>();

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(dataset_path), StandardCharsets.UTF_8))) {
            bufferedReader.readLine();
            while ((line = bufferedReader.readLine()) != null) {
                splitArray = line.trim().split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)",-1);
                String genre = splitArray[5];
                String[] splitGenre;
                splitGenre = genre.trim().replace("\"","").replace(" ","").split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)",-1);
                for (int i=0;i<splitGenre.length;i++){
                    if (map.containsKey(splitGenre[i])){
                        map.put(splitGenre[i],map.get(splitGenre[i])+1);
                    }
                    else map.put(splitGenre[i],1);
                }



            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Map.Entry<String, Integer>> list = new LinkedList(map.entrySet());
        Collections.sort(list, (o1, o2) -> {
            if (o2.getValue()==o1.getValue()){
                return o1.getKey().compareTo(o2.getKey());
            }
            else return o2.getValue().compareTo(o1.getValue());
        });

        Map<String, Integer> result = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public Map<List<String>, Integer> getCoStarCount(){
        String line;
        String[] splitArray;
        Map<List<String>, Integer> map = new HashMap<>();

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(dataset_path), StandardCharsets.UTF_8))) {
            bufferedReader.readLine();
            while ((line = bufferedReader.readLine()) != null) {
                splitArray = line.trim().split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)",-1);
                int flag=0;
                for (int i=10;i<13;i++){
                    for (int j=i+1;j<14;j++){
                        if (splitArray[i].equals("") || splitArray[j].equals("")){
                            flag=1;
                        }
                    }
                }
                if (flag==0){
                    for (int i=10;i<13;i++){
                        for (int j=i+1;j<14;j++){
                            List<String> list = new ArrayList<>();
                            list.add(splitArray[i]);
                            list.add(splitArray[j]);
                            list.sort(String::compareTo);
                            if (map.containsKey(list)){
                                map.put(list,map.get(list)+1);
                            }
                            else map.put(list,1);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return map;

    }

    public List<String> getTopMovies(int top_k, String by){
        String line;
        String[] splitArray;
        IdentityHashMap<String, Integer> map1 = new IdentityHashMap<>();
        IdentityHashMap<String, String> map2 = new IdentityHashMap<>();

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(dataset_path), StandardCharsets.UTF_8))) {
            bufferedReader.readLine();
            while ((line = bufferedReader.readLine()) != null) {
                splitArray = line.trim().split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)",-1);

                String title = splitArray[1].replace("\"","");
                if (by=="runtime"){
                    String[] time = splitArray[4].split(" ");
                    int runtime = Integer.parseInt(time[0]);
                    map1.put(title,runtime);
                }
                else if (by=="overview"){
                    String overview=null;
                    if (splitArray[7].startsWith("\"") && splitArray[7].endsWith("\"")){
                        overview = splitArray[7].substring(1,splitArray[7].length()-1);
                    }
                    else overview = splitArray[7];
                    map2.put(title,overview);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (by=="runtime"){
            List<Map.Entry<String, Integer>> list = new LinkedList(map1.entrySet());
            Collections.sort(list, (o1, o2) -> {
                if (o2.getValue().equals(o1.getValue())){
                    return o1.getKey().compareTo(o2.getKey());
                }
                return o2.getValue().compareTo(o1.getValue());
            });

            List<String> result =new ArrayList<>();
            for (int i=0;i<top_k;i++){
                result.add(list.get(i).getKey());
            }
            return result;
        }
        else {
            List<Map.Entry<String, String>> list = new LinkedList(map2.entrySet());
            Collections.sort(list, (o1, o2) -> {
                if (o2.getValue().length()==o1.getValue().length()){
                    return o1.getKey().compareTo(o2.getKey());
                }
                return (o2.getValue().length()<(o1.getValue().length()) ? -1 : 1);
            });

            List<String> result =new ArrayList<>();
            for (int i=0;i<top_k;i++){
                result.add(list.get(i).getKey());
            }
            return result;
        }

    }

    public List<String> getTopStars(int top_k, String by){
        String line;
        String[] splitArray;
        Map<String, Double> ratingMap1 = new HashMap<>();
        Map<String, Integer> timesMap1 = new HashMap<>();
        Map<String, Double> ratingMap2 = new HashMap<>();
        Map<String, Integer> timesMap2 = new HashMap<>();

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(dataset_path), StandardCharsets.UTF_8))) {
            bufferedReader.readLine();
            while ((line = bufferedReader.readLine()) != null) {
                splitArray = line.trim().split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)",-1);

                if (by=="rating"){
                    float rating = Float.parseFloat(splitArray[6]);
                    for (int i=10;i<=13;i++){
                        String name = splitArray[i];
                        if (ratingMap1.containsKey(name)){
                            ratingMap1.put(name,ratingMap1.get(name)+rating);
                            timesMap1.put(name,timesMap1.get(name)+1);
                        }
                        else {
                            ratingMap1.put(name, Double.valueOf(rating));
                            timesMap1.put(name,1);
                        }
                    }
                }
                else if (by=="gross"){
                    if (splitArray[15].equals("")){
                        continue;
                    }
                    double gross = Double.parseDouble(splitArray[15].replace(",","").replace("\"",""));
                    for (int i=10;i<=13;i++){
                        String name = splitArray[i];
                        if (ratingMap2.containsKey(name)){
                            ratingMap2.put(name,ratingMap2.get(name)+gross);
                            timesMap2.put(name,timesMap2.get(name)+1);
                        }
                        else {
                            ratingMap2.put(name,gross);
                            timesMap2.put(name,1);
                        }
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (by=="rating"){
            Map<String, Double> map = new HashMap<>();
            for (Map.Entry<String, Double> entry : ratingMap1.entrySet()) {
                map.put(entry.getKey(),(entry.getValue()*2)/(timesMap1.get(entry.getKey())*2));

            }
            List<Map.Entry<String, Double>> list = new LinkedList(map.entrySet());
            Collections.sort(list, (o1, o2) -> {
                if (o2.getValue().equals(o1.getValue())){
                    return o1.getKey().compareTo(o2.getKey());
                }
                return o2.getValue().compareTo(o1.getValue());
            });

            List<String> result =new ArrayList<>();
            for (int i=0;i<top_k;i++){
                result.add(list.get(i).getKey());
            }
            return result;
        }
        else {
            Map<String, Double> map = new HashMap<>();
            for (Map.Entry<String, Double> entry : ratingMap2.entrySet()) {
                map.put(entry.getKey(),entry.getValue()/timesMap2.get(entry.getKey()));

            }
            List<Map.Entry<String, Double>> list = new LinkedList(map.entrySet());
            Collections.sort(list, (o1, o2) -> {
                if (o2.getValue().equals(o1.getValue())){
                    return o1.getKey().compareTo(o2.getKey());
                }
                return o2.getValue().compareTo(o1.getValue());
            });

            List<String> result =new ArrayList<>();
            for (int i=0;i<top_k;i++){
                result.add(list.get(i).getKey());
            }
            return result;
        }


    }

    public List<String> searchMovies(String genre, float min_rating, int max_runtime){
        String line;
        String[] splitArray;
        List<String> list = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(dataset_path), StandardCharsets.UTF_8))) {
            bufferedReader.readLine();
            while ((line = bufferedReader.readLine()) != null) {
                splitArray = line.trim().split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)",-1);

                String title = splitArray[1].replace("\"","");

                float rating = Float.parseFloat(splitArray[6]);

                String[] time = splitArray[4].split(" ");
                int runtime = Integer.parseInt(time[0]);

                String[] splitGenre;
                splitGenre = splitArray[5].trim().replace("\"","").replace(" ","").split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)",-1);
                for (int i=0;i<splitGenre.length;i++){
                    if (splitGenre[i].equals(genre) && rating>=min_rating &&runtime<=max_runtime){
                        list.add(title);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Collections.sort(list);
        return list;
    }

}