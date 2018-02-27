package apps;
import java.io.*;
import java.util.*;
public class prac {
	public static void isNumber(String a){
		try{
			int n = Integer.parseInt(a);
			System.out.println(":-)It is a number: "+n);
			//notice, an integer n<2^31-1 && n>-2^31
		} catch(NumberFormatException e){
			System.out.println(":-(It is not a number: "+a);
		}
	}
	private static int paraMatch(char left,int i, String s){
    	char right=' ';
    	int priority = 1;
    	int index = -1;
    	if(left=='[')
    		right=']';
    	if(left=='(')
    		right=')';
    	for(int j=i+1; j<s.length(); j++){
    		System.out.print("j is: "+ j);
    		System.out.println(" priority is: "+ priority);
    		if(s.charAt(j)==left){
    			System.out.println("found a left");	
    			priority++;
    		}
    		if(s.charAt(j)==right){
    			priority--;
    			System.out.println("found a right");
    			if(priority==0){
    				//found
    				index = j;
    				break;
    			}
    			//else priority--;//continue search
    		}
    	}
    	return index;	
    }
	
	public static void main(String[] args){
		
		//read a input string
		/*BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String a;
		try {
			a = br.readLine();
		} catch (IOException e1) {
			System.out.println("finish");
			return;
		}
		//remove tabs and blanks
		System.out.println("-->"+a+"<--");
		a = a.replaceAll(" ","");
		a = a.replaceAll("\\t","");
		System.out.println("-->"+a+"<--");
		
		//check how to use String.contains
		
		//practice how to use StringTokenizer
		final String delims = " \t*+-/()[]";
		StringTokenizer st = new StringTokenizer(a,delims);
		while(st.hasMoreTokens()){
			String b = st.nextToken();
			isNumber(b);			
		}
		
		//add a char to the end of a string
		String s1 = "abc";
		s1 = "front"+s1+'d';
		System.out.println("the updated s1 is: "+s1);
		//ArrayList
		ArrayList<String> a1 = new ArrayList<String>();
		a1.add("first");
		a1.add("second");
		System.out.println(a1.get(0).equals("first"));
		*/
		//indexAt
		/*String s2 = "(a + A[a*2-b])";
		s2 = s2.replaceAll(" ","");
		s2 = s2.replaceAll("\\t","");
		System.out.println(paraMatch('[',4,s2));
		*/
		//replace all
		String s3 = "abababab";
		s3 = s3.replaceAll("a", "b");
		System.out.println(s3);
		
	}
		
}
