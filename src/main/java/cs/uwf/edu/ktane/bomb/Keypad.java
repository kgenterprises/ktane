package cs.uwf.edu.ktane.bomb;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

/**
 * Created by Daniel on 3/31/2017.
 */
@Setter
@Getter
public class Keypad extends Module {

    private final ArrayList<String> column1 = new ArrayList<>();
    private final ArrayList<String> column2 = new ArrayList<>();
    private final ArrayList<String> column3 = new ArrayList<>();
    private final ArrayList<String> column4 = new ArrayList<>();
    private final ArrayList<String> column5 = new ArrayList<>();
    private final ArrayList<String> column6 = new ArrayList<>();
    private final ArrayList<String> allSymbols = new ArrayList<>();

    private String sym1;
    private String sym2;
    private String sym3;
    private String sym4;

    public Keypad(){
        super("keypad");

        setColumn1();
        setColumn2();
        setColumn3();
        setColumn4();
        setColumn5();
        setColumn6();
        setAllSymbols();

        setSym1("none");
        setSym2("none");
        setSym3("none");
        setSym4("none");
    }

    public Keypad(String aSym1, String aSym2, String aSym3, String aSym4 ){
        super("keypad");

        setColumn1();
        setColumn2();
        setColumn3();
        setColumn4();
        setColumn5();
        setColumn6();
        setAllSymbols();

        setSym1(aSym1);
        setSym2(aSym2);
        setSym3(aSym3);
        setSym4(aSym4);
    }

    public void solve(){
        //make sure all of the symbols entered are valid
        if(allSymbols.contains(getSym1()) && allSymbols.contains(getSym2())
                && allSymbols.contains(getSym3()) && allSymbols.contains(getSym4())){

            //find the column then output the order
            System.out.println(getOrderOfSymbols(findColumn(), getSym1(), getSym2(), getSym3(), getSym4()));
        }
        else{
            System.out.println("You did not enter valid symbols." +
                    "\nBe sure to follow the instruction in the readme file\n");
        }
    }

    /*method to find the column containing all of the symbols
    the method will not be reached if all symbols are not valid
     */
    public ArrayList<String> findColumn(){
        if(column1.contains(getSym1()) && column1.contains(getSym2())
                && column1.contains(getSym3()) && column1.contains(getSym4())){

            return column1;
        }
        else if (column2.contains(getSym1()) && column2.contains(getSym2())
                && column2.contains(getSym3()) && column2.contains(getSym4())) {

            return column2;
        }
        else if (column3.contains(getSym1()) && column3.contains(getSym2())
                && column3.contains(getSym3()) && column3.contains(getSym4())) {

            return column3;
        }
        else if (column4.contains(getSym1()) && column4.contains(getSym2())
                && column4.contains(getSym3()) && column4.contains(getSym4())) {

            return column4;
        }
        else if (column5.contains(getSym1()) && column5.contains(getSym2())
                && column5.contains(getSym3()) && column5.contains(getSym4())) {

            return column5;
        }
        else{
            return column6;
        }
    }

    //Method to get the order of the symbols after the correct column has been found
    public String getOrderOfSymbols(ArrayList<String> column, String oneSym, String twoSym, String threeSym, String
            fourSym){

        String theOutput = "Click the keypads in this order.\n";

        for(int i = 0; i < column.size(); i++){
            if(column.get(i).equalsIgnoreCase(oneSym)){
                theOutput += (oneSym + "\n");
            }
            else if(column.get(i).equalsIgnoreCase(twoSym)){
                theOutput += (twoSym + "\n");
            }
            else if(column.get(i).equalsIgnoreCase(threeSym)){
                theOutput += (threeSym + "\n");
            }
            else if(column.get(i).equalsIgnoreCase(fourSym)){
                theOutput += (fourSym + "\n");
            }
        }

        return theOutput;
    }

    public void setColumn1(){
        column1.add("tennis");
        column1.add("at");
        column1.add("lambda");
        column1.add("lightning");
        column1.add("cat");
        column1.add("htail");
        column1.add("lc");
    }

    public void setColumn2(){
        column2.add("edot");
        column2.add("tennis");
        column2.add("lc");
        column2.add("cq");
        column2.add("wstar");
        column2.add("htail");
        column2.add("question");
    }

    public void setColumn3(){
        column3.add("copy");
        column3.add("butt");
        column3.add("cq");
        column3.add("xi");
        column3.add("r");
        column3.add("lambda");
        column3.add("wstar");
    }

    public void setColumn4(){
        column4.add("6");
        column4.add("paragraph");
        column4.add("tb");
        column4.add("cat");
        column4.add("xi");
        column4.add("question");
        column4.add("smile");
    }

    public void setColumn5(){
        column5.add("psy");
        column5.add("smile");
        column5.add("tb");
        column5.add("rc");
        column5.add("paragraph");
        column5.add("snake");
        column5.add("bstar");
    }

    public void setColumn6(){
        column6.add("6");
        column6.add("edot");
        column6.add("h");
        column6.add("ae");
        column6.add("psy");
        column6.add("n");
        column6.add("omega");
    }

    public void setAllSymbols(){
        allSymbols.add("tennis");
        allSymbols.add("at");
        allSymbols.add("lambda");
        allSymbols.add("lightning");
        allSymbols.add("cat");
        allSymbols.add("htail");
        allSymbols.add("lc");
        allSymbols.add("edot");
        allSymbols.add("cq");
        allSymbols.add("wstar");
        allSymbols.add("question");
        allSymbols.add("copy");
        allSymbols.add("butt");
        allSymbols.add("xi");
        allSymbols.add("r");
        allSymbols.add("6");
        allSymbols.add("paragraph");
        allSymbols.add("tb");
        allSymbols.add("smile");
        allSymbols.add("psy");
        allSymbols.add("rc");
        allSymbols.add("snake");
        allSymbols.add("bstar");
        allSymbols.add("h");
        allSymbols.add("ae");
        allSymbols.add("n");
        allSymbols.add("omega");
    }
}
