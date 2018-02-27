package apps;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

import structures.Stack;

public class Expression {

	/**
	 * Expression to be evaluated
	 */
	String expr;                
    
	/**
	 * Scalar symbols in the expression 
	 */
	ArrayList<ScalarSymbol> scalars;   
	
	/**
	 * Array symbols in the expression
	 */
	ArrayList<ArraySymbol> arrays;
    
    /**
     * String containing all delimiters (characters other than variables and constants), 
     * to be used with StringTokenizer
     */
    public static final String delims = " \t*+-/()[]";
    
    /**
     * Initializes this Expression object with an input expression. Sets all other
     * fields to null.
     * 
     * @param expr Expression
     */
    public Expression(String expr) {
        this.expr = expr;
    }

    /**
     * Populates the scalars and arrays lists with symbols for scalar and array
     * variables in the expression. For every variable, a SINGLE symbol is created and stored,
     * even if it appears more than once in the expression.
     * At this time, values for all variables are set to
     * zero - they will be loaded from a file in the loadSymbolValues method.
     */
    public void buildSymbols() {
    	//create ArrayList
    	arrays = new ArrayList<ArraySymbol>();
    	scalars = new ArrayList<ScalarSymbol>();
    	//remove spaces and tabs
    	expr = expr.replaceAll(" ","");
		expr = expr.replaceAll("\\t","");
		String tempExpr = expr;
		//get tokens
		StringTokenizer st = new StringTokenizer(tempExpr,delims);
		String n = null;
		while(st.hasMoreTokens()){
			n = st.nextToken();
			boolean isArray = false;
		try{
			Integer.parseInt(n);
		}catch(NumberFormatException e){
			//a scaler or an array
			try{
			if(tempExpr.charAt(tempExpr.indexOf(n)+n.length())=='['){
				isArray = true;
			}
			else
				isArray = false;
			}catch(IndexOutOfBoundsException be){
				isArray = false;
			}
			//System.out.println(n);
			if(isArray){
				//System.out.println("is array");
				boolean duplicate = false;
				for(ArraySymbol as:arrays){
					if(as.name.equals(n))
						duplicate = true;
				}
				if(!duplicate){
					arrays.add(new ArraySymbol(n));
					tempExpr = tempExpr.substring(0,tempExpr.indexOf(n))+tempExpr.substring(tempExpr.indexOf(n)+n.length(),tempExpr.length());
					}
				}
			else{
				//System.out.println("is scalar");
				boolean duplicate = false;
				for(ScalarSymbol as:scalars){
					if(as.name.equals(n))
						duplicate = true;
					}
				if(!duplicate){
					scalars.add(new ScalarSymbol(n));
					tempExpr = tempExpr.substring(0,tempExpr.indexOf(n))+tempExpr.substring(tempExpr.indexOf(n)+n.length(),tempExpr.length());
					}
				}
			}
		}
		//printArrays();
		//printScalars();
		if(scalars.isEmpty()&&arrays.isEmpty()){
			//System.out.println("oh, no var");
			scalars = null;
			arrays = null;
		}	
	}
    
    /**
     * Loads values for symbols in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     */
    public void loadSymbolValues(Scanner sc) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String sym = st.nextToken();
            ScalarSymbol ssymbol = new ScalarSymbol(sym);
            ArraySymbol asymbol = new ArraySymbol(sym);
            int ssi = scalars.indexOf(ssymbol);
            int asi = arrays.indexOf(asymbol);
            if (ssi == -1 && asi == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                scalars.get(ssi).value = num;
            } else { // array symbol
            	asymbol = arrays.get(asi);
            	asymbol.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    String tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    asymbol.values[index] = val;              
                }
            }
        }
    }
    
    
    /**
     * Evaluates the expression, using RECURSION to evaluate subexpressions and to evaluate array 
     * subscript expressions.
     * 
     * @return Result of evaluation
     */
    public float evaluate() {
    	//in case the their buildSymbol method do not delete the space
    	expr = expr.replaceAll(" ","");
		expr = expr.replaceAll("\\t","");
    	//manipulate expr String, replace all scalars with their values
    	/*if(scalars!=null||arrays!=null){
    	for(int i = 0; i<scalars.size(); i++){
    		
    		//System.out.println(scalars.get(i).name);
    		expr = expr.replaceAll(scalars.get(i).name,scalars.get(i).value+"");
    		System.out.println("the string after fill value is: " + expr);
    	}
    	}*/
    	return evaluate(expr);
    }
    //recursive method
    private float evaluate(String s){
    	//using ptr, create buffers to store char, buffer bn for digits, buffer na for arrays
    	//**using ba also to buffer scalar value
    	String bn = "";
    	String ba = "";
    	//create two stacks used in basic operation, ns for operands, os for operator
    	Stack<Float> ns = new Stack<Float>(); 
    	Stack<Character> os = new Stack<Character>();
    	float result = 0;
    	int i = 0;
    	while(i<s.length()){
    		//System.out.println("the ba now is: "+ ba);
    		char ca = s.charAt(i);
    		if(ca=='['){
    			int index = (int)evaluate(s.substring(i+1,paraMatch(ca,i,s)));
    			//System.out.println(index);
    			ns.push((float)searchArrays(ba).values[index]);
    			//empty array name buffer
    			ba = "";
    			i = paraMatch(ca,i,s)+1;
    		}
    		else if(ca=='('){
    			//System.out.println("the paraMatch output is: "+paraMatch(ca,i,s));
    			//System.out.println("the i is: "+i);
    			//System.out.println(s.substring(i+1,paraMatch(ca,i,s)));
    			float tempv = evaluate(s.substring(i+1,paraMatch(ca,i,s)));
    			ns.push(tempv);
    			i = paraMatch(ca,i,s)+1;
    		}
    		else if(ca>='0'&&ca<='9'){
    			bn = bn + ca;
    			i++;
    		}
    		else if((ca>='a'&&ca<='z')||(ca>='A'&&ca<='Z')){
    			ba = ba + ca;
    			i++;
    		}
    		else if(ca=='+'||ca=='-'||ca=='*'||ca=='/'){
    			//push the value of current number buffer into ns
    			if(!bn.isEmpty()){
    				//push number
    				ns.push((float)Integer.parseInt(bn));
    				//empty buffer
    				bn = "";
    				}
    			if(!ba.isEmpty()){
    	    		ns.push(getScalarValue(ba));
    	    		ba = "";
    	    	}
    			while(true){
    				if(os.isEmpty()||isHigher(ca,os.peek())){
    					os.push(ca);
    					break;
    					}
    				else{
    					if(ns.size()<2){
    						ns.push(0-ns.pop());
    					}else{
    						//check size
        					//do operation
        					//push result
    						char op = os.pop();
    						//LIFO
    						float n2 = ns.pop();
    						float n1 = ns.pop();
    						ns.push(operation(n1,n2,op));
    						}
    					continue;
    					}
    				}
    			i++;
    		}
    	}
    	if(!bn.isEmpty()){
    		ns.push((float)Integer.parseInt(bn));
    		//System.out.println("push one!");
    	}
    	if(!ba.isEmpty()){
    		ns.push(getScalarValue(ba));
    	}
    	//check conditions of stack
    	while(true){
    		if(ns.size()==1&&os.isEmpty()){
    			result = ns.pop();
    			break;
    		}
    		else if(ns.size()==1&&os.size()==1){
    			result = 0-ns.pop();
    			break;
    		}
    		else{
    			//ns.size()>1&&os.size==ns.size()-1
    			char op = os.pop();
    			float n2 = ns.pop();
    			float n1 = ns.pop();
    			ns.push(operation(n1,n2,op));
    			continue;
    		}
    	}
    	return result;
    }
    /**
     * Utility method, prints the symbols in the scalars list
     */
    public void printScalars() {
        for (ScalarSymbol ss: scalars) {
            System.out.println(ss);
        }
    }
    
    /**
     * Utility method, prints the symbols in the arrays list
     */
    public void printArrays() {
    		for (ArraySymbol as: arrays) {
    			System.out.println(as);
    		}
    }
    
    //helper
    //give a name, return the ArraySymbol
    private ArraySymbol searchArrays(String target){
    	int index = -1;
    	for(int i = 0; i<arrays.size(); i++){
    		if(target.equals(arrays.get(i).name)){
    			index = i;
    			break;
    			}
    		}
    		return arrays.get(index);
    		}
    private float getScalarValue(String target){
    	int result = 0;
    	for(int i = 0; i<scalars.size();i++){
    		if(target.equals(scalars.get(i).name)){
    			result = scalars.get(i).value;
    			break;
    		}
    	}
    	return result;
    }
    private float operation(float n1, float n2, char op){
    	float result = 0;
    	if(op=='+')
    		result = n1+n2;
    	if(op=='-')
    		result = n1-n2;
    	if(op=='*')
    		result = n1*n2;
    	if(op=='/')
    		result = n1/n2;
    	return result;
    	}
    private boolean isHigher(char x, char peek){
    	if((x=='*'||x=='/')&&(peek=='+'||peek=='-')){
    		return true;
    	} else
    		return false;
    }
    private int paraMatch(char left,int i, String s){
    	char right=' ';
    	int priority = 1;
    	int index = -1;
    	if(left=='[')
    		right=']';
    	if(left=='(')
    		right=')';
    	for(int j=i+1; j<s.length(); j++){
    		if(s.charAt(j)==left){	
    			priority++;
    		}
    		if(s.charAt(j)==right){
    			priority--;
    			if(priority==0){
    				//found
    				index = j;
    				break;
    			}//continue search
    		}
    	}
    	return index;	
    }
	
}