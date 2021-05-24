import java.io.*; 
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;


public class A4_2019CS10341{
	public static void main(String args[]) throws Exception{
		String nodesCSV;
		String edgesCSV;
		String functionName;
		try{
			nodesCSV = args[0];
			edgesCSV = args[1];
			functionName = args[2];
		}
		catch(Exception e) {
			throw new Exception("Invalid syntax, please run: java A4_2019CS10341 <nodes.csv> <edges.csv> <functionName>");
		}

		
		HashMap<String, Integer> labelToIndex = new HashMap<String, Integer>();
		HashMap<Integer, String> indexToLabel = new HashMap<Integer, String>();

		UnDirectedGraph G = new UnDirectedGraph();
		
        try{
        	BufferedReader br = new BufferedReader(new FileReader(nodesCSV)); 
	        br.readLine(); 
	        String line = "";
	        int i = 0;
	        
	        while((line = br.readLine())!=null){
	            String elements[] = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
	            String label = elements[1].charAt(0)=='"'?elements[1].substring(1, elements[1].length()-1):elements[1];

	            labelToIndex.put( label , i );
	            indexToLabel.put( i, label );

	            G.addVertex(i, label);
	        
	            i++;
	        }
        }
        catch(Exception e) {
			throw new Exception("File not found: " + nodesCSV);
		}
		try{
        	BufferedReader br_edges = new BufferedReader(new FileReader(edgesCSV));
	        br_edges.readLine(); 
	        String line = "";
	        
	        while((line = br_edges.readLine())!=null){
	            String elements[] = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
	            String source = elements[0].charAt(0)=='"'?elements[0].substring(1, elements[0].length()-1):elements[0];
	            String target = elements[1].charAt(0)=='"'?elements[1].substring(1, elements[1].length()-1):elements[1];
	            int weight = Integer.parseInt(elements[2]); 

	            if (labelToIndex.containsKey(source) && labelToIndex.containsKey(target)){
	            	int sourceIndex = labelToIndex.get(source);
	            	int targetIndex = labelToIndex.get(target);
	            	G.addEdge(sourceIndex, targetIndex, weight);
	            }
	            else{
	            	throw new Exception("The edge: -" + source + "- to -" + target + "- has a missing vertex");
	            }
	        }
        }
        catch(Exception e) {
			throw new Exception("File not found: " + edgesCSV);
		}

		switch (functionName){
			case "average":
				G.average();
				break;
			case "rank":
				G.rank();
				break;
			case "independent_storylines_dfs":
				G.independent_storylines_dfs();
				break;
			default:
				;
		}


	}
}


class UnDirectedGraph{
	public int V, E;
	private ArrayList<Node> vertices;
	private int[] visited;

	UnDirectedGraph(){
		this.V = 0;
		this.E = 0;
		this.vertices = new ArrayList<Node>();
	}



	public void addVertex(int index, String label){
		Node n = new Node(index, label);
		this.vertices.add(n);
		this.V += 1;
	}

	public void addEdge(int sourceIndex, int targetIndex, int weight){
		Node source = vertices.get(sourceIndex);
		Node target = vertices.get(targetIndex);
		source.co_occurrence+=weight;
		target.co_occurrence+=weight;
		Edge e1 = new Edge(weight, targetIndex);
		source.edges.add(e1);
		Edge e2 = new Edge(weight, sourceIndex);
		target.edges.add(e2);
		this.E+=1;
	}





	public void average(){
		float V_f = (float) this.V;
		float E_f = (float) this.E;
		String average;
		if(this.V == 0){
			average = "0.00";
		}
		else{
			average = String.format("%.2f", 2 * E_f / V_f);
		}
		System.out.println(average);
	}

	private void merge(int[] sortArr, int[] arr, int low, int mid, int high){

        int[] temp_sort = new int[high - low + 1];
        int[] temp_arr = new int[high - low + 1];
        for(int i = low; i<=high; i++){
        	temp_sort[i - low] = sortArr[i];
        	temp_arr[i - low] = arr[i];
        }

        int i = 0;
        int j = mid + 1 - low;
        int k = low;
        while(k<=high){
        	if(j>high - low){
        		sortArr[k] = temp_sort[i];
        		arr[k] = temp_arr[i];
        		i+=1;
        	}
        	else if (i>mid - low){
        		sortArr[k] = temp_sort[j];
        		arr[k] = temp_arr[j];
        		j+=1;
        	}
        	else if(temp_sort[i]<temp_sort[j]){
        		sortArr[k] = temp_sort[j];
        		arr[k] = temp_arr[j];
        		j+=1;
        	}
        	else if (temp_sort[i]>temp_sort[j]){
        		sortArr[k] = temp_sort[i];
        		arr[k] = temp_arr[i];
        		i+=1;
        	}
        	else if(temp_sort[i]==temp_sort[j]){
        		String i_label = this.vertices.get(temp_arr[i]).label;
        		String j_label = this.vertices.get(temp_arr[j]).label;
        		if(i_label.compareTo(j_label) < 0){
        			sortArr[k] = temp_sort[j];
	        		arr[k] = temp_arr[j];
	        		j+=1;
        		}
        		else{
        			sortArr[k] = temp_sort[i];
	        		arr[k] = temp_arr[i];
	        		i+=1;
        		}
        	}
        	k+=1;
        }
    }

    private void MergeSort(int[] sortArr, int[] arr, int low, int high){
        if(low>=high){
        	return;
        }
        int mid = (low + high)/2;
        MergeSort(sortArr, arr, low, mid);
        MergeSort(sortArr, arr, mid + 1, high);
        merge(sortArr, arr,low,mid,high);
    }

	public void rank(){
		int[] co_occurrences = new int[this.V];
		int[] indices = new int[this.V];
		for (int i = 0; i<this.V; i++){
			co_occurrences[i] = (vertices.get(i).co_occurrence);
			indices[i] = i;
		}
		this.MergeSort(co_occurrences, indices, 0, this.V-1);


		for (int i = 0; i<this.V; i++){
			int index = indices[i];
			System.out.print(vertices.get(index).label);
			if(i<this.V - 1){
				System.out.print(",");
			}
		}
	}



	private int dfs(Node s, ArrayList<Node> component) {
		this.visited[s.index] = 1;
		component.add(s);
		int count = 1;
		for (int i = 0; i < s.edges.size(); i++ ) {
			Node v = this.vertices.get(s.edges.get(i).target);
			if(this.visited[v.index] == 0) {
				count += this.dfs(v, component);
			}
		}	
		return count;
	}
	public void independent_storylines_dfs() {
		this.visited = new int[this.V];
		for(int i = 0; i<this.V; i++){
			this.visited[i] = 0;
		}

		ArrayList<ArrayList<Node>> components = new ArrayList<ArrayList<Node>>();
		ArrayList<Integer> sizes = new ArrayList<Integer>();
		int count = 0;
		for(int i = 0; i<this.V; i++){
			if(this.visited[i]==0){
				ArrayList<Node> component = new ArrayList<Node>();
				Node s = this.vertices.get(i);
				int size = this.dfs(s, component);
				components.add(component);
				sizes.add(size);
				count+=1;
			}
		}
		int sizesInt[] = new int[sizes.size()];
		int componentsLexiMax[] = new int[components.size()];
		HashMap<Integer, Integer> lexiMaxToIndex = new HashMap<Integer, Integer>();
		
		for(int i = 0; i<sizes.size(); i++){
			sizesInt[i] = sizes.get(i);
			int index = components.get(i).get(0).index;
			String s = components.get(i).get(0).label;
			for(int j = 0; j<components.get(i).size(); j++){
				if(s.compareTo(components.get(i).get(j).label) < 0){
					s = components.get(i).get(j).label;
					index = components.get(i).get(j).index;
				}
			}
			componentsLexiMax[i] = index;
			lexiMaxToIndex.put(index, i);
		}

		this.MergeSort(sizesInt, componentsLexiMax, 0, components.size()-1);

		ArrayList<ArrayList<Node>> temp = new ArrayList<ArrayList<Node>>();
		for(int i = 0; i<components.size(); i++){
			temp.add(components.get(lexiMaxToIndex.get(componentsLexiMax[i])));
		}
		components = temp;

		for(int i = 0; i<components.size(); i++){
			int indices[] = new int[components.get(i).size()];
			int ones[] = new int[components.get(i).size()];
			for(int j = 0; j<components.get(i).size(); j++){
				indices[j] = components.get(i).get(j).index;
				ones[j] = 1;
			}
			this.MergeSort(ones, indices, 0, components.get(i).size()-1);
			for(int j = 0; j<components.get(i).size(); j++){
				System.out.print(this.vertices.get(indices[j]).label);
				if(j<components.get(i).size() - 1){
					System.out.print(",");
				}
			}
			System.out.print("\n");
		}
	}
}

class Node{
	public int index;
	public String label;
	public ArrayList<Edge> edges;
	public int co_occurrence;
	Node(int in, String l){
		this.index = in;
		this.label = l;
		this.edges = new ArrayList<Edge>();
		this.co_occurrence = 0;
	}
	Node(int in, String l, ArrayList<Edge> e){
		this.index = in;
		this.label = l;
		this.edges = e;
		this.co_occurrence = 0;

	}
	
}

class Edge{
	public int weight;
	public int target;
	Edge(int w, int t){
		this.weight = w;
		this.target = t;
	}
}