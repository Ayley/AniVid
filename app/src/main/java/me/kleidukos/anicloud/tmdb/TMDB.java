package me.kleidukos.anicloud.tmdb;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.kleidukos.anicloud.tmdb.models.Episode;
import me.kleidukos.anicloud.tmdb.models.Result;
import me.kleidukos.anicloud.tmdb.models.Search;
import me.kleidukos.anicloud.tmdb.models.Season;
import me.kleidukos.anicloud.ui.videoplayer.StreamVideoPlayer;
import me.kleidukos.anicloud.util.JsoupBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class TMDB {

    private static final String key = "009e8284b6f97c638e0face5f5400c1b";

    private static ObjectMapper mapper = new ObjectMapper();

    public static List<Episode> getEpisodes(String name, String altName, String year, int seasonId) {
        String a = new String(name.getBytes(StandardCharsets.UTF_8));
        String b = new String(altName.getBytes(StandardCharsets.UTF_8));

        Result rs = getStreamResult(a, b, year);

        if (rs == null) {
            return null;
        }

        int id = rs.getId();

        String url = "https://api.themoviedb.org/3/tv/" + id + "/season/" + seasonId + "?api_key=" + key + "&language=de";

        String result = getJson(url);

        try {
            return mapper.readValue(result, Season.class).getEpisodes();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Result getStreamResult(String name, String altName, String year) {
        Result result = getTVSearch(name, altName, year);

        if (result != null) {
            return result;
        }

        result = getMovieSearch(name, altName, year);

        return result;
    }

    private static Result getTVSearch(String name, String altName, String year) {
        String url = null;
        try {
            url = "https://api.themoviedb.org/3/search/tv?api_key=" + key + "&query=" + URLEncoder.encode(name, StandardCharsets.UTF_8.name()) + "&language=de";
            Log.d("TMDB", url);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String result = getJson(url);

        if(result == null || result.isEmpty()){
            return null;
        }

        Search search;

        try {
            search = mapper.readValue(result, Search.class);

            List<Result> results = search.getResults().stream().filter(it -> (it.getName().contains(name) || it.getName().contains(altName)) && it.getFirst_air_date().contains(year)).collect(Collectors.toList());

            if (results.size() == 0) {
                if(search.getResults().size() > 0) {
                    return search.getResults().get(0);
                }else {
                    return null;
                }
            }

            return results.get(0);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Result getMovieSearch(String name, String altName, String year) {
        String url = null;
        try {
            url = "https://api.themoviedb.org/3/search/movie?api_key=" + key + "&query=" + URLEncoder.encode(name, StandardCharsets.UTF_8.name()) + "&language=de";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String result = getJson(url);

        if(result == null || result.isEmpty()){
            return null;
        }

        Search search;

        try {
            search = mapper.readValue(result, Search.class);

            List<Result> results = search.getResults().stream().filter(it -> (it.getName().contains(name) || it.getName().equalsIgnoreCase(altName)) && it.getFirst_air_date().contains(year)).collect(Collectors.toList());

            if (results.size() == 0) {
                if(search.getResults().size() > 0) {
                    return search.getResults().get(0);
                }else {
                    return null;
                }
            }
            return results.get(0);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getJson(String urlString) {
        String result = new String(JsoupBuilder.Companion.getDocument(urlString, null).body().text().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        return result;

    }
}
