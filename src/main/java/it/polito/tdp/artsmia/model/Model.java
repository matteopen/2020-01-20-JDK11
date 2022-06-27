package it.polito.tdp.artsmia.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.artsmia.db.ArtsmiaDAO;

public class Model {
	
	private ArtsmiaDAO dao;
	private Graph<Artist, DefaultWeightedEdge> grafo;
	private Map<Integer, Artist> idMap;
	private List<Artist> best;
	
	
	public Model() {
		
		this.dao = new ArtsmiaDAO();
		this.idMap = new HashMap<Integer, Artist>();
		
	}
	
	public void creaGrafo(String role) {
		
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		List<Artist> artisti = this.dao.roleArtists(role, this.idMap);
		
		Graphs.addAllVertices(this.grafo, artisti);
		
		List<Arco> archi = this.dao.getArchi(role, this.idMap);
		
		for(Arco arco : archi) {
			if(!this.grafo.containsEdge(this.grafo.getEdge(arco.getA1(), arco.getA2()))) {
				Graphs.addEdge(this.grafo, arco.getA1(), arco.getA2(), arco.getPeso());
			}
		}
		
		

		System.out.println("Grafo creato!"+"\n");
		System.out.println("#Vertici: "+this.grafo.vertexSet().size()+"\n");
		System.out.println("#Archi: "+this.grafo.edgeSet().size()+"\n");
	}
	
	public int nVertices() {
		return this.grafo.vertexSet().size();
	}
	
	public int nEdges() {
		return this.grafo.edgeSet().size();
	}
	
	public List<String> getRoles(){
		return this.dao.getRoles();
	}
	
	public String connessi(String role) {
		
		String result = "";
		List<Arco> archi = this.dao.getArchi(role, this.idMap);
		
		Collections.sort(archi);
		
		for(Arco a : archi) {
			result += a.getA1()+" - "+a.getA2()+" Mostre in comune: "+a.getPeso()+"\n";
		}
		
		return result;
	}
	
	public boolean isCorrect(Integer id) {
		Artist artist = null;
		artist=this.idMap.get(id);
		if(artist==null) {
			return false;
		}
		else {
			return true;
		}
	}
	
	public String cerca(Integer id) {
		
		String result = "Percorso: \n";
		this.best = new ArrayList<>();
		Artist partenza = this.idMap.get(id);
		List<Artist> parziale = new ArrayList<>();
		parziale.add(partenza);
		ricorsione(parziale);
		for(Artist a : this.best) {
			result += a +"\n";
		}
		
		return result;
		
	}

	private void ricorsione(List<Artist> parziale) {
		
		//	COND. DI TERMINAZIONE
		if(parziale.size()!=1) {
			if(!this.isPresente(parziale)) {
				if(parziale.size()>this.best.size()) {
					this.best = new ArrayList<>(parziale);
					return;
				}
			}
		}
		for(Artist target : Graphs.neighborListOf(this.grafo, parziale.get(parziale.size()-1))) {
			//parziale.add(target);
			if(parziale.size()==2) {
				parziale.add(target);
				ricorsione(parziale);
				parziale.remove(parziale.size()-1);
			}
			else {
				if(this.grafo.getEdgeWeight(this.grafo.getEdge(parziale.get(parziale.size()-1), target))==this.grafo.getEdgeWeight(this.grafo.getEdge(parziale.get(parziale.size()-2), parziale.get(parziale.size()-1)))) {
					parziale.add(target);
					ricorsione(parziale);
					parziale.remove(parziale.size()-1);
				}
			}
			
		}
		
	}
	
	public boolean isPresente(List<Artist> parziale) {
		
		boolean presente = false;
		for(Artist t : Graphs.neighborListOf(this.grafo, parziale.get(parziale.size()-1))) {
			if(!parziale.contains(t)) {
				if(this.grafo.getEdgeWeight(this.grafo.getEdge(parziale.get(parziale.size()-1), t))==this.grafo.getEdgeWeight(this.grafo.getEdge(parziale.get(parziale.size()-1), parziale.get(parziale.size()-2)))) {
					presente = true;
				}
			}
		}
		return presente;
	}

}
