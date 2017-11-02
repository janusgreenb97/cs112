package structures;

import java.util.ArrayList;

/**
 * This class implements a compressed trie. Each node of the tree is a CompressedTrieNode, with fields for
 * indexes, first child and sibling.
 * 
 * @author Sesh Venugopal
 *
 */
public class Trie {
	
	/**
	 * Words indexed by this trie.
	 */
	ArrayList<String> words;
	
	/**
	 * Root node of this trie.
	 */
	TrieNode root;
	
	/**
	 * Initializes a compressed trie with words to be indexed, and root node set to
	 * null fields.
	 * 
	 * @param words
	 */
	public Trie() {
		root = new TrieNode(null, null, null);
		words = new ArrayList<String>();
		
	}
	
	/**
	 * Inserts a word into this trie. Converts to lower case before adding.
	 * The word is first added to the words array list, then inserted into the trie.
	 * 
	 * @param word Word to be inserted.
	 */
	public void insertWord(String word) {
		int newWordIndex = words.size();
		words.add(word.toLowerCase());
		TrieNode wordNode = new TrieNode(new Indexes(newWordIndex,(short)0,(short)(word.length()-1)),null,null); 
		
		if(newWordIndex==0){
			//no need to compare
			//System.out.println("The first insert node is "+words.get(newWordIndex));
			root.firstChild = wordNode;
			return;
		}
		else{
			//words.size()>1
			//need to compare
			TrieNode ptr = root.firstChild;
			TrieNode parent = root;
			
			while(ptr!=null){
				String ptrws = words.get(ptr.substr.wordIndex).substring(ptr.substr.startIndex, ptr.substr.endIndex+1);
				String ws = words.get(newWordIndex).substring(wordNode.substr.startIndex, wordNode.substr.endIndex+1);
				//System.out.print("the ptr substring is "+ptrws+"|");
				//System.out.print("the ws substring is "+ws+"|");
				int commonChar = findCommon(ptrws,ws);
				//System.out.print("the find common number is  "+ commonChar + " |");
				//System.out.println(ptr.substr.endIndex-ptr.substr.startIndex+ "  .");
				
				//not found
				if(commonChar==-1){
					parent = ptr;
					ptr = ptr.sibling;
					continue;
				}
				
				//found, all include
				//ptr must points to an internal node
				
				else if (commonChar==ptr.substr.endIndex-ptr.substr.startIndex){
					//System.out.println("all include");
					//cut the wordNode with the internal index
					wordNode.substr.startIndex = (short) (ptr.substr.endIndex+1);//out of bound?
					parent = ptr;
					ptr = ptr.firstChild;
					continue;
				}
				
				//found, not all include
				//common string < ptr
				else {
					//update the ptr node, make it into the latest internal node
					//its sibling do not change
					int ptrEndIndex = ptr.substr.endIndex;
					ptr.substr.endIndex = (short) (ptr.substr.startIndex+commonChar);
					//update the wordNode
					wordNode.substr.startIndex = (short)(ptr.substr.endIndex+1);
					
					//for internal node
					if(ptr.firstChild!=null){
						//save the children of previous internal node into a temp
						TrieNode tempNode = ptr.firstChild;
						
						//create a new internal node, connect it with the previous one
						ptr.firstChild = new TrieNode(new Indexes((short)ptr.substr.wordIndex, (short)(ptr.substr.endIndex+1),(short)ptrEndIndex),tempNode,wordNode);
						}
					//for leaf node
					else{
						ptr.firstChild = new TrieNode(new Indexes((short)ptr.substr.wordIndex,(short)(ptr.substr.endIndex+1),(short)ptrEndIndex),null,wordNode);
					}
					return;
				}
			}
			parent.sibling = wordNode;
			return;
		}
	}
	
	/**
	 * Given a string prefix, returns its "completion list", i.e. all the words in the trie
	 * that start with this prefix. For instance, if the tree had the words bear, bull, stock, and bell,
	 * the completion list for prefix "b" would be bear, bull, and bell; for prefix "be" would be
	 * bear and bell; and for prefix "bell" would be bell. (The last example shows that a prefix can be
	 * an entire word.) The order of returned words DOES NOT MATTER. So, if the list contains bear and
	 * bell, the returned list can be either [bear,bell] or [bell,bear]
	 * 
	 * @param prefix Prefix to be completed with words in trie
	 * @return List of all words in tree that start with the prefix, order of words in list does not matter.
	 *         If there is no word in the tree that has this prefix, null is returned.
	 */
	public ArrayList<String> completionList(String prefix) {
	root= root.firstChild;
	
	TrieNode temp= new TrieNode(new Indexes((short)0,(short)0,(short)0),null,null);
	TrieNode hold= new TrieNode(new Indexes((short)0,(short)0,(short)0),null,null);
	
	while(root.firstChild!=null){
		if(root.sibling==null){//直接往下找－有没有firstchild
			if((words.get(root.substr.wordIndex)).substring(root.substr.startIndex,root.substr.endIndex+1).equals
			(prefix.substring(root.substr.startIndex,prefix.length()))){
				if(root.substr.endIndex+1==prefix.length()){
					hold=root.firstChild;
					}
				}
			temp=root;

	}else{

	for(TrieNode ptr3=root; ptr3.sibling!=null; ptr3=ptr3.sibling){//横向查prefix

	if((words.get(ptr3.substr.wordIndex)).substring(ptr3.substr.startIndex,ptr3.substr.endIndex+1).equals(prefix.substring(ptr3.substr.startIndex,ptr3.substr.endIndex+1))){

	//找对应prefix

	if(ptr3.substr.endIndex+1==prefix.length()){
		hold=ptr3.firstChild;
		}

	temp=ptr3;
	}
	}

	root=temp.firstChild;}
	}
	ArrayList<String> print= new ArrayList<String>();
	while(hold.firstChild!=null){
		print.add(words.get(hold.substr.wordIndex));
	}
	return print;
	}
	
	
	
	
	
	public ArrayList<String> MecompletionList(String prefix) {
		ArrayList<String> list = new ArrayList<String>();
		traverse(root.firstChild,prefix,list,false);
		if(list.size()==0)
			return null;
		else
			return list;
	}
	
	
	public void print() {
		print(root, 1, words);
	}
	
	private static void print(TrieNode root, int indent, ArrayList<String> words) {
		if (root == null) {
			return;
		}
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		
		if (root.substr != null) {
			System.out.println("      " + words.get(root.substr.wordIndex));
		}
		
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		System.out.println("(" + root.substr + ")");
		
		for (TrieNode ptr=root.firstChild; ptr != null; ptr=ptr.sibling) {
			for (int i=0; i < indent-1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			//System.out.print("     |");
			print(ptr, indent+1, words);
		}
	}
	//helper method: recursive traverse
	private void traverse(TrieNode root,String prefix, ArrayList<String> list,boolean printAll){
		//base case
		if(root==null)
			return;
		//ns is the internal part
		String ns = words.get(root.substr.wordIndex).substring(root.substr.startIndex, root.substr.endIndex+1);
		//String ts = words.get(root.substr.wordIndex).substring(0, root.substr.endIndex+1);
		
		if(printAll)
			prefix = ns;
		
		int comCharNum = findCommon(ns,prefix);
		
		//no commonChar
		if(comCharNum == -1){
			//go to the sibling
			//System.out.println(ns+" is wrong with " + prefix+ " going to the siblings");
			traverse(root.sibling,prefix,list,false);
		//prefix equals to ns or prefix equals front part of ns
		}else if(comCharNum >= prefix.length()-1){
			//print all children
			if(root.firstChild!=null){
				//System.out.println("print all of " + ts + " , going to the first child of " + ts);
				traverse(root.firstChild,prefix,list,true);
				//if(root.firstChild.sibling!=null){
					//System.out.println(" goint to the siblings of  "+ns);
					//traverse(root.firstChild.sibling,prefix,list,true);
					//}
				}
			if(printAll&root.sibling!=null){
				traverse(root.sibling,prefix,list,true);
			}
			
			if(root.firstChild==null)
				list.add(words.get(root.substr.wordIndex));
		}
		//ns equals to the front part of the prefix
		else{
			//go to ns's children, continue comparing
			//System.out.println(ts + " is not enough, so going to the first child of "+ns);
			traverse(root.firstChild,prefix.substring(comCharNum+1),list,false);
		}
	}
	
	//helper method: find common string index (from 0 to the last)
	private static int findCommon(String s1, String s2){
		int commonIndex = -1; //no common chars
		for(int i = 0; i<Math.min(s1.length(),s2.length());i++){
			if(s1.charAt(i)==s2.charAt(i))
				commonIndex++;
			else
				break;
		}
		return commonIndex;
	}
 }
