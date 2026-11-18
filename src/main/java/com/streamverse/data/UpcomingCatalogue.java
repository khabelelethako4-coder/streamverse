package com.streamverse.data;

import com.streamverse.model.Upcoming;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

/**
 * Films that are not out yet (release dates gathered in November 2026 from public release calendars; studios move
 * dates, so treat them as "planned"). Trailers are not stored: TrailerResolver finds an embeddable one on YouTube when needed.
 * To add a film, add one up(...) line.
 */
public final class UpcomingCatalogue {
    private UpcomingCatalogue() { }

    private static Upcoming up(String slug, String title, int y, int m, int d, String genre, String text) {
        return new Upcoming(slug, title, LocalDate.of(y, m, d), genre, text);
    }

    /** Sorted by release date. */
    public static List<Upcoming> all() {
        return List.of(
            up("hunger-games-sunrise", "The Hunger Games: Sunrise on the Reaping", 2026, 11, 20, "Sci-Fi Thriller",
               "Twenty-four years before the first story, young Haymitch Abernathy is chosen for the 50th Hunger Games."),
            up("violent-night-2", "Violent Night 2", 2026, 12, 4, "Action Comedy",
               "The festive action-comedy returns for another very bloody Christmas."),
            up("jumanji-3", "Jumanji 3", 2026, 12, 11, "Adventure Comedy",
               "Dwayne Johnson, Jack Black, Karen Gillan and Awkwafina are pulled back into the game."),
            up("dune-part-three", "Dune: Part Three", 2026, 12, 18, "Sci-Fi",
               "Denis Villeneuve concludes the trilogy, adapted from Frank Herbert's Dune Messiah."),
            up("avengers-doomsday", "Avengers: Doomsday", 2026, 12, 18, "Superhero",
               "Earth's mightiest heroes assemble again as Doctor Doom's reign of terror begins."),
            up("ray-gunn", "Ray Gunn", 2026, 12, 18, "Animation Sci-Fi",
               "Brad Bird's retro-futuristic animated sci-fi adventure."),
            up("ice-age-boiling-point", "Ice Age: Boiling Point", 2027, 2, 5, "Animation",
               "The prehistoric herd returns for a brand-new adventure."),
            up("sonic-4", "Sonic the Hedgehog 4", 2027, 3, 19, "Family Adventure",
               "The blue blur speeds back onto the big screen."),
            up("godzilla-kong-supernova", "Godzilla x Kong: Supernova", 2027, 3, 26, "Action Sci-Fi",
               "The two Titans clash and team up once more."),
            up("legend-of-zelda", "The Legend of Zelda", 2027, 5, 7, "Fantasy",
               "Nintendo's fantasy saga makes its live-action debut."),
            up("star-wars-starfighter", "Star Wars: Starfighter", 2027, 5, 28, "Sci-Fi",
               "A new standalone Star Wars adventure starring Ryan Gosling."),
            up("httyd-2-live-action", "How to Train Your Dragon 2 (live-action)", 2027, 6, 11, "Fantasy Family",
               "The live-action retelling of the dragon saga continues."),
            up("spider-verse-3", "Spider-Man: Beyond the Spider-Verse", 2027, 6, 18, "Animation Superhero",
               "Miles Morales starts out as a fugitive, pursued by every other Spider-Person across the multiverse."),
            up("shrek-5", "Shrek 5", 2027, 6, 30, "Animation Comedy",
               "The ogre is back - the plot is still a secret."),
            up("man-of-tomorrow", "Man of Tomorrow", 2027, 7, 9, "Superhero",
               "Superman and Lex Luthor form an uneasy alliance to face Brainiac."),
            up("quiet-place-3", "A Quiet Place Part III", 2027, 7, 30, "Horror",
               "The silence-or-death horror series continues."),
            up("batman-part-2", "The Batman Part II", 2027, 10, 1, "Crime Superhero",
               "Matt Reeves returns to Gotham with Robert Pattinson."),
            up("frozen-3", "Frozen III", 2027, 11, 24, "Animation Musical",
               "More adventures in Arendelle."),
            up("avengers-secret-wars", "Avengers: Secret Wars", 2027, 12, 17, "Superhero",
               "The second half of the two-part Avengers finale."),
            up("lotr-hunt-for-gollum", "The Lord of the Rings: The Hunt for Gollum", 2027, 12, 17, "Fantasy",
               "Andy Serkis directs as Middle-earth returns to cinemas.")
        ).stream().sorted(Comparator.comparing(Upcoming::releaseDate)).toList();
    }
}
