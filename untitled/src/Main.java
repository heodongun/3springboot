import java.util.*;

public class Main {
    static int bb=0;
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String a=sc.nextLine();
        int sum=0;
       loop(a,a.length()-1);
       System.out.println(bb);
    }
    static void loop(String a,int b){
        if(b<1){
            return;
        }
        else{
            System.out.println(a.charAt(b));
            b=a.charAt(b);
            loop(a,b-1);
        }
    }
}
