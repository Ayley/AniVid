package me.kleidukos.anicloud.scraping;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import me.kleidukos.anicloud.models.anicloud.Genre;
import me.kleidukos.anicloud.models.anicloud.SimpleStream;
import me.kleidukos.anicloud.room.series.RoomDisplayStream;
import me.kleidukos.anicloud.ui.main.MainActivity;
import me.kleidukos.anicloud.util.JsoupBuilder;

public class GenreScraper {

    public static List<RoomDisplayStream> getGenre(Genre genre){

        //todo scrap genre
        return null;
    }

}
