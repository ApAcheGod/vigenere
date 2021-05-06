import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class vigener extends JDialog {

    private JPanel contentPane;
    private JButton buttonDecrypt;
    private JButton buttonEncrypt;
    private JButton buttonHack;
    private JButton buttonClear;
    private JTextArea textArea3;
    private JTextArea textArea1;
    private JTextArea textArea2;

    private static String key = "";
    public static SortedMap<Character, Double> russianFrequency = new TreeMap<>();
    final static String russianAlphabet = "абвгдежзийклмнопрстуфхцчшщъыьэюя";
    final static char[] russian = russianAlphabet.toCharArray();
    static String preparedText = "";

    public vigener() {
        setContentPane(contentPane);
        setModal(true);

    buttonEncrypt.addActionListener(e -> OnEncrypt());
    buttonDecrypt.addActionListener(e -> OnDecrypt());
    buttonClear.addActionListener(e -> OnClear());
    buttonHack.addActionListener(e -> OnHack());

        contentPane.registerKeyboardAction(e -> {
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public static void checkKey() throws keyException {

        Pattern taboo = Pattern.compile("[a-z0-9\\p{Punct} №]");
        Matcher matcher = taboo.matcher(key);

        if (matcher.find()){
            throw new keyException();
        }
    }

    public void getKey(){
        key =  textArea3.getText().toLowerCase(Locale.ROOT);
    }

    public ArrayList<Character> makeKeyString(){
        ArrayList<Character> keyString = new ArrayList<>();
        for(int i = 0; i < preparedText.length(); i++){
            keyString.add(i, key.toCharArray()[i % key.toCharArray().length]);
        }
        return keyString;
    }

    public static String prepareText(String inputText){
        return inputText.toLowerCase(Locale.ROOT)
                .replace("ё", "е")
                .replaceAll("[^а-я]", "");

    }

    public static int gcd(int a, int b) {
        if (a == 0)
            return b;
        return gcd(b % a, a);
    }

    public static void initFrequency(SortedMap<Character, Double> map){
        map.put('а', 0.062);
        map.put('б', 0.014);
        map.put('в', 0.038);
        map.put('г', 0.013);
        map.put('д', 0.025);
        map.put('е', 0.072);
        map.put('ж', 0.007);
        map.put('з', 0.016);
        map.put('и', 0.062);
        map.put('й', 0.010);
        map.put('к', 0.028);
        map.put('л', 0.035);
        map.put('м', 0.026);
        map.put('н', 0.053);
        map.put('о', 0.090);
        map.put('п', 0.023);
        map.put('р', 0.040);
        map.put('с', 0.045);
        map.put('т', 0.053);
        map.put('у', 0.021);
        map.put('ф', 0.002);
        map.put('х', 0.009);
        map.put('ц', 0.003);
        map.put('ч', 0.012);
        map.put('ш', 0.006);
        map.put('щ', 0.003);
        map.put('ъ', 0.014);
        map.put('ы', 0.016);
        map.put('ь', 0.014);
        map.put('э', 0.003);
        map.put('ю', 0.006);
        map.put('я', 0.018);
    }

    public static SortedMap<Character, Double> textFrequency(String inputText){
        SortedMap<Character, Double> textFrequency = new TreeMap<>();
        String cleanText =  prepareText(inputText);
        char[] text = cleanText.toCharArray();
        for (char c : text) {
            double countOfChar = 0;
            for (char value : text) {
                if (c == value) {
                    countOfChar++;
                }
            }
            textFrequency.put(c, (countOfChar / text.length));
        }

        for(int i = 0; i < russianAlphabet.length(); i++){
            if(!textFrequency.containsKey(russian[i])){
                textFrequency.put(russian[i], 0.0);
            }
        }

        return textFrequency;

    }

    public static HashMap<String, ArrayList<Integer>> repeatedSubstrings(String inputText){
        int minCountOfRepeatedSubstrings = 3;
        int maxLengthOfSubstring = 5;
        int minLengthOfSubstring = 3;
        HashMap<String, ArrayList<Integer>> OCC = new HashMap<>();

        for(int i = 0; i < inputText.length(); i++){
            for(int j = minLengthOfSubstring; j <= maxLengthOfSubstring && ((i - j) >=  0) ; j++){
                String subString = inputText.substring(i - j, i);
                if(OCC.containsKey(subString)){
                    OCC.get(subString).add(i - j);
                }else{
                    OCC.put(subString, new ArrayList<>(Collections.singletonList(i - j)));
                }
            }
        }

        HashMap<String, ArrayList<Integer>> indexesOfSubStrings = new HashMap<>();

        OCC.forEach((k, v) -> {
            if (v.size() >= minCountOfRepeatedSubstrings){
                indexesOfSubStrings.put(k, v);
            }
        });

        HashMap<String, ArrayList<Integer>> rangesBetweenSubString = new HashMap<>();

        for(Map.Entry<String, ArrayList<Integer>> pair: indexesOfSubStrings.entrySet()){
            ArrayList<Integer> values = new ArrayList<>();

            for(int i = 0; i < pair.getValue().size(); i++){
                if(i == 0){
                    values.add(pair.getValue().get(i));
                }else{
                    values.add(pair.getValue().get(i) - pair.getValue().get(i - 1));
                }
            }
            rangesBetweenSubString.put(pair.getKey(), values);
        }
        return rangesBetweenSubString;
    }

    public static ArrayList<String> separate(String inputText, int step){
        ArrayList<String> separatedText = new ArrayList<>();

        for(int i = 0; i <= step; i++){
            StringBuilder sb = new StringBuilder();

            for(int j = i; j < inputText.length(); j += step + 1){
                sb.append(inputText.charAt(j));
            }
            separatedText.add(sb.toString());
        }
        return  separatedText;
    }

    public static String makeKey(String inputText, int keyLength){
        StringBuilder key = new StringBuilder();
        ArrayList<String> separatedText = separate(inputText, keyLength - 1);
        initFrequency(russianFrequency);
        separatedText.forEach( n -> key.append(russian[findKey(russianFrequency, textFrequency(n))]));

        return key.toString();

    }

    public static int findKey(SortedMap<Character, Double> russianFrequency, Map<Character, Double> textFrequency){
        double minD = Double.MAX_VALUE;
        double dSum = 0;
        int minIter = 0;

        initFrequency(russianFrequency);

        ArrayList<Double> russianValues = new ArrayList<>(russianFrequency.values());
        ArrayList<Double> textValues = new ArrayList<>(textFrequency.values());

        for(int j = 0; j < russianValues.size(); j++){
            for(int i = 0; i < textValues.size(); i++){
                double d = Math.pow(russianValues.get(i) - textValues.get((i + j) % russianFrequency.size()), 2);
                dSum += d;
            }

            if(dSum < minD){
                minD = dSum;
                minIter = j;
            }
            dSum = 0;
        }
        return minIter;
    }

    public void OnEncrypt(){
        preparedText = prepareText(textArea1.getText());
        try{
            getKey();
            checkKey();
            textArea2.setText(encrypt());
        }catch (keyException e){
            JOptionPane.showMessageDialog(null, "Ключ может быть только из русских букв!");
            textArea2.setText("");
            textArea3.setText("");
            key = "";
        }
    }

    public void OnDecrypt(){
        preparedText = prepareText(textArea1.getText());
        try{
            getKey();
            checkKey();
            textArea2.setText(decrypt());
        }catch (keyException e){
            JOptionPane.showMessageDialog(null, "Ключ может быть только из русских букв!");
            textArea2.setText("");
            textArea3.setText("");
            key = "";
        }
    }

    public void OnClear(){
        textArea1.setText("");
        textArea2.setText("");
        textArea3.setText("");
    }

    public void OnHack(){
        preparedText = prepareText(textArea1.getText());
        key = hack();
        textArea3.setText(key);
        OnDecrypt();

    }

    public String encrypt(){
        ArrayList<Character> result = new ArrayList<>();
        for(int i = 0; i < preparedText.length(); i++){
            result.add(
                    russian[(russianAlphabet.indexOf(preparedText.charAt(i)) +
                                    russianAlphabet.indexOf(makeKeyString().get(i)))
                            % russianAlphabet.length()]);
        }

        StringBuilder outputText = new StringBuilder();
        for(int i = 0; i < result.size(); i++){
            if(i != 0 && i % 5 == 0){
                outputText.append(" ");
            }
            outputText.append(result.get(i));
        }
        return outputText.toString();
    }

    public String decrypt(){
        String inputText = preparedText;
        ArrayList<Character> result = new ArrayList<>();
        for(int i = 0; i < inputText.length(); i++){
            result.add(
                    russian[(russianAlphabet.indexOf(inputText.charAt(i)) +
                                    russianAlphabet.length() - russianAlphabet.indexOf(makeKeyString().get(i)))
                                    % russianAlphabet.length()]);
        }

        StringBuilder outputText = new StringBuilder();
        for(int i = 0; i < result.size(); i++){
            if(i != 0 && i % 5 == 0){
                outputText.append(" ");
            }
            outputText.append(result.get(i));
        }
        return outputText.toString();
    }

    public static String hack(){
        String inputText = preparedText;
        HashMap<String, ArrayList<Integer>> repeatedSubStrings = repeatedSubstrings(inputText);
        HashMap<Integer, Integer> gcds = new HashMap<>();

        repeatedSubStrings.forEach((k, v) -> {
            int gcd = 0;
            for(int i = 0; i < v.size()-1; i++){
                if(gcd > 0){
                    gcd = gcd(v.get(i+1), gcd);
                }else{
                    gcd = gcd(v.get(i+1), v.get(i + 2));
                }
                if(gcd > 1){
                    if (gcds.containsKey(gcd)){
                        gcds.put(gcd, gcds.get(gcd) + 1);
                    }else{
                        gcds.put(gcd, 1);
                    }
                }
            }
        });

        int keyLength = Collections.max(gcds.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
        return makeKey(inputText, keyLength);
    }

    public static void main(String[] args) {
        vigener dialog = new vigener();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}