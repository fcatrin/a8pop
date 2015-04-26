package fcatrin.pop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Huffman {
	int nodeId = 0;
	class Node {
		int id;
		Node left;
		Node right;
		int value;
		int freq;
		public String bits;
		
		public boolean isLeaf() {
			return left == null && right == null;
		}
		
		@Override
		public String toString() {
			if (isLeaf()) {
				String format = "leaf {id:%d, v:%d, f:%d}";
				return String.format(format, id, value, freq);
			} else {
				String format = "node {id:%d, f:%d, l:%d, r:%d}";
				return String.format(format, id, freq, left.id, right.id);
			}
		}
	}
	
	List<Node> q1 = new ArrayList<Node>();
	List<Node> q2 = new ArrayList<Node>();
	
	Map<Integer, Node> nodes = new HashMap<Integer, Node>();
	
	Map<String, Integer> tableDecompress = new HashMap<String, Integer>();
	Map<Integer, String> tableCompress = new HashMap<Integer, String>();

	public Huffman(int[] values) {
		Map<Integer, Integer> codes = new HashMap<Integer, Integer>();
		for(int value: values) {
			Integer n = codes.get(value);
			if (n == null) n = 0;
			n++;
			codes.put(value, n);
		}
		buildTree(codes);
	}
	
	public Huffman(Map<Integer, Integer> codes) {
		buildTree(codes);
	}
	
	private void buildTree(Map<Integer, Integer> codes) {
		
		for(Entry<Integer, Integer> code : codes.entrySet()) {
			Node node = new Node();
			node.value = code.getKey();
			node.freq = code.getValue();
			q1.add(node);
		}
		
		Collections.sort(q1, new Comparator<Node>() {
			@Override
			public int compare(Node o1, Node o2) {
				if (o1.freq == o2.freq) {
					return o1.value == o2.value?0:(o1.value<o2.value?-1:1);
				} else return o1.freq<o2.freq?-1:1;
			}
		});
		for(Node node : q1) {
			node.id = nodeId++;
			nodes.put(node.id, node);
		}
		System.out.println("Huffman sorted");
		System.out.println(q1);
		
		while (true) {
			int f1 = Integer.MAX_VALUE;
			int f2 = Integer.MAX_VALUE;
			int f3 = Integer.MAX_VALUE;
			boolean hasPair = false;
			if (q1.size()>1) {
				f1 = q1.get(0).freq + q1.get(1).freq;
				hasPair = true;
			}
			if (q2.size()>1) {
				f2 = q2.get(0).freq + q2.get(1).freq;
				hasPair = true;
			}
			if (q1.size()>0 && q2.size()>0) {
				f3 = q1.get(0).freq + q2.get(0).freq;
				hasPair = true;
			}
			if (!hasPair) break;
			
			System.out.println(String.format("f1:%d, f2:%d, f3:%d  q1.size:%d, q2.size:%d", f1, f2, f3, q1.size(), q2.size()));
			
			Node n1, n2;
			if (f1 <= f2 && f1 <= f3) {
				n1 = dequeue(q1);
				n2 = dequeue(q1);
			} else if (f3<=f2) {
				n1 = dequeue(q1);
				n2 = dequeue(q2);
			} else {
				n1 = dequeue(q2);
				n2 = dequeue(q2);
			}
			System.out.println(String.format("n1:%s, n2:%s", n1.toString(), n2.toString()));
			// put leaf to the left
			if (!n1.isLeaf()) {
				Node tmp = n1;
				n1 = n2;
				n2 = tmp;
			}
			Node intermediate = new Node();
			intermediate.id = nodeId++;
			intermediate.left = n1;
			intermediate.right = n2;
			intermediate.freq = n1.freq + n2.freq;
			enqueue(q2, intermediate);
			nodes.put(intermediate.id, intermediate);
			System.out.println("q1 " + q1);
			System.out.println("q2 " + q2);
		}
		System.out.println("Huffman Tree");
		Node root = dequeue(q2);
		dump(root);
		
		buildCodes(root, "");
		int id = 0;
		do {
			Node node = nodes.get(id++);
			if (node == null) break;
			if (node.isLeaf()) {
				System.out.println(node + ", bits " + node.bits);
			}
		} while (true);
	}

	private void dump(Node root) {
		System.out.println(root);
		if (root.left!=null) dump(root.left);
		if (root.right!=null) dump(root.right);
	}
	
	private void buildCodes(Node node, String bits) {
		if (node.isLeaf()) {
			node.bits = bits;
			tableCompress.put(node.value, bits);
			tableDecompress.put(node.bits, node.value);
		} else {
			buildCodes(node.left, bits + "0");
			buildCodes(node.right, bits + "1");
		}
	}

	public String compress(int values[]) {
		StringBuffer compressed = new StringBuffer();
		for(int value : values) {
			compressed.append(tableCompress.get(value));
		}
		return compressed.toString();
	}
	
	private void enqueue(List<Node> q, Node node) {
		q.add(node);
	}

	private Node dequeue(List<Node> q) {
		if (q.size()==0) return null;
		Node node = q.get(0);
		q.remove(0);
		return node;
	}
	
	/*
	 * format:
	 * 8 bits: symbol
	 * 4 bits: huffman code size (n)
	 * n bits: huffman code
	 */
	
	public String dumpTree() {
		StringBuffer bitTree = new StringBuffer();
		int id = 0;
		do {
			Node node = nodes.get(id++);
			if (node == null) break;
			if (node.isLeaf()) {
				String value = Utils.bits2string(node.value, 8);
				String bits = node.bits;
				int size = bits.length();
				String bitSize = Utils.bits2string(size, 3);
				
				bitTree.append(value).append(bitSize).append(bits);
			}
		} while (true);
		return bitTree.toString();
	}
}