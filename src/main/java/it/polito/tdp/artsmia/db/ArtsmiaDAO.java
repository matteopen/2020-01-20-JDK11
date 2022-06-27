package it.polito.tdp.artsmia.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.artsmia.model.Arco;
import it.polito.tdp.artsmia.model.ArtObject;
import it.polito.tdp.artsmia.model.Artist;
import it.polito.tdp.artsmia.model.Exhibition;

public class ArtsmiaDAO {

	public List<ArtObject> listObjects() {
		
		String sql = "SELECT * from objects";
		List<ArtObject> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				ArtObject artObj = new ArtObject(res.getInt("object_id"), res.getString("classification"), res.getString("continent"), 
						res.getString("country"), res.getInt("curator_approved"), res.getString("dated"), res.getString("department"), 
						res.getString("medium"), res.getString("nationality"), res.getString("object_name"), res.getInt("restricted"), 
						res.getString("rights_type"), res.getString("role"), res.getString("room"), res.getString("style"), res.getString("title"));
				
				result.add(artObj);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Exhibition> listExhibitions() {
		
		String sql = "SELECT * from exhibitions";
		List<Exhibition> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Exhibition exObj = new Exhibition(res.getInt("exhibition_id"), res.getString("exhibition_department"), res.getString("exhibition_title"), 
						res.getInt("begin"), res.getInt("end"));
				
				result.add(exObj);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<String> getRoles() {
		
		String sql = "SELECT DISTINCT a.role "
				+ "FROM authorship a "
				+ "ORDER BY a.role ASC ";
		List<String> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				String role = res.getString("role");
				
				result.add(role);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public List<Artist> roleArtists(String role,Map<Integer, Artist> idMap ) {
		
		String sql = "SELECT DISTINCT ar.artist_id, ar.name, a.role "
				+ "FROM authorship a, artists ar "
				+ "WHERE a.artist_id=ar.artist_id "
				+ "AND a.role=? ";
		List<Artist> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, role);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Artist artist = new Artist(res.getInt("artist_id"), res.getString("name"), res.getString("role"));
				
				idMap.put(artist.getId(), artist);
				result.add(artist);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
public List<Arco> getArchi(String role, Map<Integer, Artist> idMap) {
		
		String sql = "SELECT a.artist_id AS id1, a2.artist_id AS id2, COUNT(e.exhibition_id) AS peso "
				+ "FROM authorship a, exhibition_objects e, exhibition_objects e2, authorship a2  "
				+ "WHERE a.role=a2.role "
				+ "AND a.role=? "
				+ "AND a2.role=? "
				+ "AND e.exhibition_id=e2.exhibition_id "
				+ "AND e.object_id=a.object_id "
				+ "AND e2.object_id=a2.object_id "
				+ "AND a.artist_id!=a2.artist_id "
				+ "GROUP BY a.artist_id, a2.artist_id ";
		List<Arco> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, role);
			st.setString(2, role);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Artist a1 = idMap.get(res.getInt("id1"));
				Artist a2 = idMap.get(res.getInt("id2"));
				if(a1!=a2) {
					int peso = res.getInt("peso");
				
					Arco arco = new Arco(a1,a2,peso);
				
					result.add(arco);
				}
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
