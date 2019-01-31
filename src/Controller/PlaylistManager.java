package Controller;


import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.nio.file.*;
import java.util.LinkedList;
import java.util.Map;


public class PlaylistManager {

	private static ArrayList<Playlist> playlistArrayList = new ArrayList<>();

	private static HashMap<String, Track> trackMap = new HashMap<>();
	private static LinkedList<newPlaylist> openPlaylists = new LinkedList<>();
	private static SimpleObjectProperty<ArrayList<Playlist>> playlistsChange = new SimpleObjectProperty<>();

	/**
	 * Gibt Playlist mit dem Namen nameOfPlaylist zurück
	 * @param nameOfPlaylist
	 * @return
	 */
	public static Playlist getPlaylist(String nameOfPlaylist){
		int temp = playlistArrayList.indexOf(nameOfPlaylist);
		return playlistArrayList.get(temp);
	}

	/**
	 * Speichert Playlist mit Namen
	 * @param playlist
	 * @param name
	 * @throws IOException
	 */
	
	public static void savePlaylist(ArrayList<String> playlist, String name) throws IOException {



		try (BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/"+ System.getProperty("user.name") +"/Music/" + name + ".m3u"))){
			
			for (String i : playlist) {
				
				writer.write(i);
				writer.newLine();
				
			}
			System.out.println("speichern erfolgreich");
			
		}catch(IOException e) {
			e.printStackTrace();
		}
		
	}

	public static void savePlaylist(Playlist playlist, String name) throws IOException{

		try (BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/"+ System.getProperty("user.name") +"/Music/" + name + ".m3u"))){

			for (Track i : playlist.getTracks()) {

				writer.write(i.getPath());
				writer.newLine();

			}
			System.out.println("speichern erfolgreich");

		}catch(IOException e) {
			e.printStackTrace();
		}

	}

	/** Überprüft Tracks auf Duplikate und gibt sie zurückt
	 *
	 * @return ArrayList aller Tracks als String
	 */
	
	public static ArrayList<String> getAllTracks() {
		
		ArrayList <String> allMp3s = new ArrayList <String> ();

		String username = System.getProperty("user.name");
		
		allMp3s.addAll(searchForMp3("/Users/"+ username +"/Music"));
		
		HashMap<String, String>songs = new HashMap<String, String>();
		
		for (String i : allMp3s) {
			String name;
			String path[];
			path = i.split("/");
			
			name = path[path.length - 1];
			path = name.split("\\.");
			name = path[0];
			
			if (!songs.containsKey(name)) {
				songs.put(name, i);
			}
		}

		allMp3s = new ArrayList <String>(songs.values());
		
		return allMp3s;
	}

	/**
	 * Hilfsmethode sucht Tracks im Angegebenen Pfad
	 *
	 * @param path
	 * @return Liste von gefundendenen MP3 als Pfadstrings
	 */
	private static ArrayList<String> searchForMp3(String path) {

		File  files[];
		ArrayList<String> mp3s = new ArrayList<String>();

		File dir = new File (path);
			files = dir.listFiles();

			if (files != null) {
				for (File i: files) {

					if (i.isDirectory() && !i.getAbsolutePath().endsWith("Library")) {
						mp3s.addAll(searchForMp3(i.getAbsolutePath()));
					}
					else if (i.toString().endsWith("mp3")) {
						mp3s.add(i.getAbsolutePath());
					}

				}
			}

		return mp3s;

	}

	/** Sammelt alle Playlisten im Musikordner und überprüft sie auf Duplikate
	 *
	 * Wenn keine Playlist gefunden wurde, dann sucht die Methode nach allen MP3 im Musikordner und erstellt
	 * daraus eine Playlist.
	 *
	 * @return ArrayList aller Playlisten als Playlistobjekte
	 */
	public static ArrayList<Playlist> getAllPlaylists(){
		ArrayList <String> allM3us = new ArrayList <> ();

		String username = System.getProperty("user.name");

		allM3us.addAll(searchForM3u("/Users/"+ username +"/Music"));

		HashMap<String, Playlist>playlists = new HashMap<>();


		for (String absolutePath : allM3us) {
			String name;
			String path[];
			path = absolutePath.split("/");

			name = path[path.length - 1];
			path = name.split("\\.");
			name = path[0];

			if (!playlists.containsKey(name)) {
				playlists.put(name, new Playlist(absolutePath, name));
			}
		}
		playlistsChange.set(playlistArrayList);
		playlistArrayList.addAll(playlists.values());

		if(playlistArrayList.isEmpty()){
			try {
				savePlaylist(PlaylistManager.getAllTracks(), "default");
			} catch (IOException e) {
				e.printStackTrace();

			}
			getAllPlaylists();
		}

		collectExistingTracks();
		createPlaylist(new float [] {0.0f,0.4f,0.5f,0.0f,0.0f,0.0f,0.0f,0.4f,60.0f},"suggestTest");
		createSuggestionPlaylist(SuggestionParams.VALENCE, SuggestionParams.BPM);

		return playlistArrayList;
	}

	/**
	 * Hilfsmethode sucht Playlisten im Angegebenen Pfad
	 *
	 * @param path
	 * @return Liste von gefundendenen Playlisten als Pfadstrings
	 */
    private static ArrayList<String> searchForM3u(String path) {

        File  files[];
        ArrayList<String> m3us = new ArrayList<String>();

        File dir = new File (path);
        files = dir.listFiles();

        if (files != null) {
            for (File i: files) {

                if (i.isDirectory() && !i.getAbsolutePath().endsWith("Library")) {
                    m3us.addAll(searchForM3u(i.getAbsolutePath()));
                }
                else if (i.toString().endsWith(".m3u")) {
                    m3us.add(i.getAbsolutePath());
                }

            }
        }

        return m3us;

    }

	/**
	 * Gibt Liste der Playlisten zurück
	 * @return
	 */
	public static ArrayList<Playlist> getPlaylistArrayList() {
		return playlistArrayList;
	}

	private static void collectExistingTracks() {

		LinkedList<Track> temp = new LinkedList<>();
		for (Playlist playlist:
			 playlistArrayList) {
			temp = playlist.getTracks();
			for (Track tempTrack :
					temp) {
				if(tempTrack.getSpotId() != null) {
					trackMap.put(tempTrack.getSpotId(), tempTrack);
				}
			}
		}
	}

	private static void createPlaylist(float[] array, String name){
		openPlaylists.addLast(new newPlaylist());
		openPlaylists.getLast().setName(name);
		openPlaylists.getLast().setAcousticness(array[0]);
		openPlaylists.getLast().setDanceability(array[1]);
		openPlaylists.getLast().setEnergy(array[2]);
		openPlaylists.getLast().setInstrumentalness(array[3]);
		openPlaylists.getLast().setLiveness(array[4]);
		openPlaylists.getLast().setLoudness(array[5]);
		openPlaylists.getLast().setSpeechiness(array[6]);
		openPlaylists.getLast().setValence(array[7]);
		openPlaylists.getLast().setTempo(array[8]);
	}

	private static void createSuggestionPlaylist(SuggestionParams... param){
			HashMap<String,Track> suggestedTracklist = (HashMap)trackMap.clone();


		for (SuggestionParams actParam:
			 param) {
			switch (actParam){
				case VALENCE:
					for (Map.Entry<String, Track> entry : trackMap.entrySet()){

						if(entry.getValue().getSpotId().equals(null)){
							continue;
						}

						if(openPlaylists.getLast().getValence() > entry.getValue().getValence()){
							suggestedTracklist.remove(entry.getKey());
						}
					} break;
				case BPM:
					for (Map.Entry<String, Track> entry : trackMap.entrySet()){

						if(entry.getValue().getSpotId().equals(null)){
							continue;
						} else if(openPlaylists.getLast().getTempo() > entry.getValue().getBPM()){
							suggestedTracklist.remove(entry.getKey());
						}
					} break;
				case ACOUSTICNESS:
					for (Map.Entry<String, Track> entry : trackMap.entrySet()){

						if(entry.getValue().getSpotId().equals(null)){
							continue;
						} else if(openPlaylists.getLast().getAcousticness() > entry.getValue().getAcousticness()){
							suggestedTracklist.remove(entry.getKey());
						}
					} break;
				case DANCEABILITY:
					for (Map.Entry<String, Track> entry : trackMap.entrySet()){

						if(entry.getValue().getSpotId().equals(null)){
							continue;
						} else if(openPlaylists.getLast().getDanceability() > entry.getValue().getDanceability()){
							suggestedTracklist.remove(entry.getKey());
						}
					} break;
				case ENERGY:
					for (Map.Entry<String, Track> entry : trackMap.entrySet()){
						if(entry.getValue().getSpotId().equals(null)){
							continue;
						} else if(openPlaylists.getLast().getEnergy() > entry.getValue().getEnergy()){
							suggestedTracklist.remove(entry.getKey());
						}
					} break;
				case INSTRUMENTALNESS:
					for (Map.Entry<String, Track> entry : trackMap.entrySet()){
						if(entry.getValue().getSpotId().equals(null)){
							continue;
						} else if(openPlaylists.getLast().getInstrumentalness() > entry.getValue().getInstrumentalness()){
							suggestedTracklist.remove(entry.getKey());
						}
					} break;
				case LIVENESS:
					for (Map.Entry<String, Track> entry : trackMap.entrySet()){
						if(entry.getValue().getSpotId().equals(null)){
							continue;
						} else if(openPlaylists.getLast().getLiveness() > entry.getValue().getLiveness()){
							suggestedTracklist.remove(entry.getKey());
						}
					} break;
				case LOUDNESS:
					for (Map.Entry<String, Track> entry : trackMap.entrySet()){
						if(entry.getValue().getSpotId().equals(null)){
							continue;
						} else if(openPlaylists.getLast().getLoudness() > entry.getValue().getLoudness()){
							suggestedTracklist.remove(entry.getKey());
						}
					} break;
				default: continue;

			}

		}

		assert !suggestedTracklist.isEmpty();


		playlistArrayList.add(new Playlist(openPlaylists.getLast().getName(), suggestedTracklist ));
		/*try {
			savePlaylist(playlistArrayList.get(playlistArrayList.size()-1), openPlaylists.getLast().getName());
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		openPlaylists.removeLast();

	}

	public static void addTrack(Track track, Playlist playlist){
		playlist.addTrack(track);
	}

	public static SimpleObjectProperty<ArrayList<Playlist>> allPlaylistProperty(){
		return playlistsChange;
	}
}
