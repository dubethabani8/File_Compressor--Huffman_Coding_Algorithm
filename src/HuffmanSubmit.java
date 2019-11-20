import java.util.HashMap;
import java.util.PriorityQueue;

// Import any package as required


public class HuffmanSubmit implements Huffman {
	HashMap<Character, String> charBinaries = new HashMap<Character, String>();
	
	private class Node implements Comparable<Node>{
		int freq;
		char c;
		Node left;
		Node right;
		Node parent;
		String binary;
		
		public Node(char c, int freq) {
			this.c = c;
			this.freq = freq;
			this.left = null;
			this.right = null;
			if(this.left != null)this.left.parent = this;
			if(this.right != null)this.right.parent = this;
			binary = "";
		}
		public Node(int freq, Node left, Node right) {
			this.freq = freq;
			this.left = left;
			this.right = right;
			this.left.parent = this;
			this.right.parent = this;
			binary = "";
		}
		@Override
		public int compareTo(Node other) {
			if (this.freq < other.freq) return -1;
			else if (this.freq > other.freq) return 1;
			else return 0;
		}
		
		public void BuildBinaries() {
			if(left != null) {
				if(parent != null)
				left.binary = left.parent.binary + "0";
				left.BuildBinaries();

			}
			
			if(right != null) {
				if(parent != null)
				right.binary = right.parent.binary + "1";
				right.BuildBinaries();

			}if(left == null && right == null) {
				
				charBinaries.put(c, binary);
			}
		}
	}
  
	// Feel free to add more methods and variables as required. 
 
	public void encode(String inputFile, String outputFile, String freqFile){

		BinaryIn in = new BinaryIn(inputFile);
		
		HashMap<Character, Integer> freqs = new HashMap<Character, Integer>();
		while(!in.isEmpty()) {
			char c = in.readChar();
			
			
			if (freqs.containsKey(c)) { 
				int val = freqs.get(c) + 1;
				freqs.replace(c, val);
				}
			else freqs.put(c, 1);
		}
		//Create nodes for each char and store them in priority queue with ascending frequencies
		PriorityQueue<Node> nodes = new PriorityQueue<Node>();
		for(Character c: freqs.keySet()) {
			nodes.add(new Node(c, freqs.get(c)));
		}
		
		//Build tree
		while(nodes.size() > 1) {
			Node left = nodes.poll();
			Node right = nodes.poll();
			Node node = new Node((left.freq+right.freq), left, right);
			nodes.add(node);
		}
		Node treeTop = nodes.poll();
		//assign binary values to values accordingly and store in hashmap
		charBinaries = new HashMap<Character, String>();
		treeTop.left.binary = "0";
		treeTop.right.binary = "1";
		treeTop.BuildBinaries();
		
		
		in = new BinaryIn(inputFile);
		BinaryOut out = new BinaryOut(outputFile);
		while(!in.isEmpty()) {
			char c = in.readChar();
			String binary = charBinaries.get(c);
			for(int  i=0; i < binary.length(); i++) {
				if(binary.charAt(i) == '1') {
					out.write(true);
				}
				else {
					out.write(false);
				}
				}
		}
		out.flush();
		in = new BinaryIn(outputFile);
		//Write to freqFile
		out = new BinaryOut(freqFile);
		for(Character c: freqs.keySet()) {
			int i = c;
			String bin = Integer.toBinaryString(i);
			String binary = String.format("%08d", Integer.parseInt(bin));
			out.write(binary + ':' + freqs.get(c) + '\n');
		}
		out.flush();

		
   }

   public void decode(String inputFile, String outputFile, String freqFile){
	   HashMap<Character, Integer> freqs = new HashMap<Character, Integer>();
	   BinaryIn in = new BinaryIn(freqFile);
	   boolean afterColon = false;
	   while(!in.isEmpty()) {
		   char c = in.readChar();
		   String encode = "";
		   String frequency = "";
		   
		   if(!afterColon && !in.isEmpty()) {
			   while(c != ':') {
				encode += c;
				if(!in.isEmpty()) {
					
					c = in.readChar();
				}
			   }
			   afterColon = true;
			   if(!in.isEmpty())
				   c = in.readChar();
			
		   }
		   if(!in.isEmpty() && afterColon) {
			   while(c != '\n') {
				
				 frequency += c;
				 c = in.readChar();
				 
			   }
			   afterColon = false;
			
			  // if(!in.isEmpty())
				  // c = in.readChar();
		   }
		  
		   //freqs.put(encode, Integer.parseInt(frequency));
		   //convert binary string to the char accordingly
		   int parseInt = Integer.parseInt(encode, 2);
		   char ch = (char)parseInt;
		   freqs.put(ch, Integer.parseInt(frequency));
	   }
	   
	   //Start decode!
	 //Create nodes for each char and store them in priority queue with ascending frequencies
	 		PriorityQueue<Node> nodes = new PriorityQueue<Node>();
	 		for(Character c: freqs.keySet()) {
	 			nodes.add(new Node(c, freqs.get(c)));
	 		}
	 		
	 		//Build tree
	 		while(nodes.size() > 1) {
	 			Node left = nodes.poll();
	 			
	 			Node right = nodes.poll();
	 			
	 			Node node = new Node((left.freq+right.freq), left, right);
	 			
	 			nodes.add(node);
	 		}
	 		Node treeTop = nodes.poll();
	 		//assign binary values to values accordingly and store in hashmap
	 		charBinaries = new HashMap<Character, String>();
	 		treeTop.left.binary = "0";
	 		treeTop.right.binary = "1";
	 		treeTop.BuildBinaries();
	 		//Swap keys and values in Charbinaries
	 		HashMap<String, Character> binaryChars = new HashMap<String, Character>();
	 		for(Character c: charBinaries.keySet()) {
	 			binaryChars.put(charBinaries.get(c), c);
	 		}
	 		int totalBits = 0;
	 		for(Character c: freqs.keySet()) {
	 			totalBits += (freqs.get(c) * charBinaries.get(c).length());
	 		}
	 		
	 		//read in file and start decoding it
	 		in = new BinaryIn(inputFile);
	 		BinaryOut out = new BinaryOut(outputFile);
	 		String binary = "";
	 		for(int i=0; i<totalBits; i++) {
	 			boolean b = in.readBoolean();
	 			if(b) binary += "1";
	 			else binary += "0";
	 			if(binaryChars.containsKey(binary)) {
	 				char c = binaryChars.get(binary);
	 				out.write(c);
	 				binary = "";
	 				out.flush();
	 			}
	 			
	 		}
	 		
	 		
   }




   public static void main(String[] args) {
      Huffman  huffman = new HuffmanSubmit();
		//huffman.encode("ur.jpg", "ur.enc", "freq.txt");
		huffman.encode("ur.jpg", "ur_enc", "freq.txt");
		huffman.decode("ur_enc", "ur_dec", "freq.txt");

		//huffman.decode("ur.enc", "ur_dec.jpg", "freq.txt");
		// After decoding, both ur.jpg and ur_dec.jpg should be the same. 
		// On linux and mac, you can use `diff' command to check if they are the same. 
   }

}
