package com.streamverse.data;

import com.streamverse.model.Category;
import com.streamverse.model.Movie;
import com.streamverse.model.Upcoming;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Built-in catalogue: 16 genres x 16 movies. Every movie is declared ONCE with reg(movie(...)) and then listed by title
 * in as many genres as it fits (e.g. Inception is both Sci-Fi and Mind-Benders), exactly like a real streaming service.
 *
 * To change a trailer, replace the last argument of its movie(...) call (the YouTube video id = the part after "watch?v="
 * in the trailer URL). Run tools/verify_trailers.py afterwards, or tools/fix_trailers.py to repair broken ids automatically.
 */
public class SampleMovieRepository implements MovieRepository {

    private final Map<String, Movie> catalogue = new LinkedHashMap<>();
    private final List<Category> categories = new ArrayList<>();
    private final List<Movie> featured = new ArrayList<>();

    public SampleMovieRepository() {
        // ------------------------------------------------------------------ all movies (declared once)
        reg(movie("Mad Max: Fury Road", 2015, "R", "Action",
            "A relentless desert chase as a road warrior and a fierce rebel flee a tyrant's war machine.", "hEJnMQG9ev8"));
        reg(movie("John Wick", 2014, "R", "Action",
            "A retired hitman is dragged back into the underworld after a senseless act of violence.", "2AUmvWm5ZDQ"));
        reg(movie("Gladiator", 2000, "R", "Action",
            "A betrayed Roman general becomes a gladiator and fights his way toward revenge in the arena.", "owK1qxDselE"));
        reg(movie("Top Gun: Maverick", 2022, "PG-13", "Action",
            "Decades on, a legendary pilot returns to train a new generation for an impossible mission.", "giXco2jaZ_4"));
        reg(movie("Mission: Impossible - Fallout", 2018, "PG-13", "Action",
            "Ethan Hunt races to stop a nuclear catastrophe after a mission goes disastrously wrong.", "wb49-oV0F78"));
        reg(movie("Mission: Impossible - Dead Reckoning", 2023, "PG-13", "Action",
            "Ethan Hunt faces an unstoppable new weapon and a ghost from his past.", "avz06PDqDbM"));
        reg(movie("Skyfall", 2012, "PG-13", "Action",
            "Bond's loyalty is tested when MI6 is attacked by a vengeful enemy from M's past.", "6kw1UVovByw"));
        reg(movie("Casino Royale", 2006, "PG-13", "Action",
            "A newly minted 007 must bankrupt a terror financier at a high-stakes poker table.", "36mnx8dBbGE"));
        reg(movie("Terminator 2: Judgment Day", 1991, "R", "Action",
            "A reprogrammed machine protects a boy from a deadlier, shape-shifting killer.", "CRRlbK5w8AE"));
        reg(movie("Edge of Tomorrow", 2014, "PG-13", "Action",
            "A soldier doomed to relive the same battle learns to fight back against an alien invasion.", "vw61gCe2oqI"));
        reg(movie("Baby Driver", 2017, "R", "Action",
            "A talented getaway driver tries to leave crime behind, with music in his ears.", "z2z857RSfhk"));
        reg(movie("Die Hard", 1988, "R", "Action",
            "One off-duty cop, one skyscraper, and a gang of thieves who picked the wrong Christmas party.", "2TQ-pOvI6Xo"));
        reg(movie("Inception", 2010, "PG-13", "Sci-Fi",
            "A thief who steals secrets from dreams is offered one last job: planting an idea instead.", "YoHD9XEInc0"));
        reg(movie("Interstellar", 2014, "PG-13", "Sci-Fi",
            "With Earth failing, explorers travel through a wormhole in search of a new home for humanity.", "zSWdZVtXT7E"));
        reg(movie("The Matrix", 1999, "R", "Sci-Fi",
            "A hacker discovers that reality is a simulation and joins a rebellion against its machine masters.", "vKQi3bBA1y8"));
        reg(movie("Blade Runner 2049", 2017, "R", "Sci-Fi",
            "A new blade runner uncovers a long-buried secret that could unravel what is left of society.", "gCcx85zbxz4"));
        reg(movie("Dune", 2021, "PG-13", "Sci-Fi",
            "A gifted young heir is thrust into a deadly struggle over the most valuable planet in the universe.", "n9xhJrPXop4"));
        reg(movie("Dune: Part Two", 2024, "PG-13", "Sci-Fi",
            "Paul Atreides unites with the Fremen to take revenge on those who destroyed his family.", "Way9Dexny3w"));
        reg(movie("Arrival", 2016, "PG-13", "Sci-Fi",
            "When mysterious spacecraft touch down worldwide, a linguist races to talk to the visitors.", "tFMo3UJ4B4g"));
        reg(movie("The Martian", 2015, "PG-13", "Sci-Fi",
            "Stranded alone on Mars, an astronaut must science his way to survival until rescue can arrive.", "ej3ioOneTy8"));
        reg(movie("Ex Machina", 2014, "R", "Sci-Fi",
            "A programmer is invited to test whether a reclusive founder's humanoid AI is truly conscious.", "EoQuVnKhxaM"));
        reg(movie("Tenet", 2020, "PG-13", "Sci-Fi",
            "A secret agent manipulates the flow of time to prevent a war that could end the world.", "LdOM0x0XDMo"));
        reg(movie("Back to the Future", 1985, "PG", "Sci-Fi",
            "A teenager is sent thirty years into the past and must make sure his parents fall in love.", "qvsgGtivCgs"));
        reg(movie("Star Wars: The Force Awakens", 2015, "PG-13", "Sci-Fi",
            "A scavenger, a defector and a smuggler race to find a missing Jedi as a new dark power rises.", "sGbxmsDFVnE"));
        reg(movie("The Dark Knight", 2008, "PG-13", "Superhero",
            "Batman faces the Joker, a chaotic mastermind who pushes Gotham and its heroes to the breaking point.", "EXeTwQWrcwY"));
        reg(movie("The Dark Knight Rises", 2012, "PG-13", "Superhero",
            "Eight years later, a masked mercenary forces Batman out of retirement to save Gotham.", "GokKUqLcvD8"));
        reg(movie("The Avengers", 2012, "PG-13", "Superhero",
            "Earth's mightiest heroes must learn to fight as a team to stop an alien invasion.", "eOrNdBpGMv8"));
        reg(movie("Avengers: Endgame", 2019, "PG-13", "Superhero",
            "Earth's heroes assemble one last time to undo the damage of a universe-shattering defeat.", "TcMBFSGVi1c"));
        reg(movie("Black Panther", 2018, "PG-13", "Superhero",
            "A young king returns to Wakanda and must defend its future against a rival claimant to the throne.", "xjDjIWPwcPU"));
        reg(movie("Iron Man", 2008, "PG-13", "Superhero",
            "A billionaire inventor builds a high-tech suit of armor to escape captivity and fight for justice.", "8ugaeA-nMTc"));
        reg(movie("Guardians of the Galaxy", 2014, "PG-13", "Superhero",
            "A band of misfit outlaws joins forces to protect a powerful orb from a fanatical villain.", "d96cjJhvlMA"));
        reg(movie("Logan", 2017, "R", "Superhero",
            "A weary Wolverine protects a young mutant on a final journey across the border.", "Div0iP65aZo"));
        reg(movie("Spider-Man: No Way Home", 2021, "PG-13", "Superhero",
            "When his identity is revealed, Peter Parker asks for help and the multiverse cracks open.", "JfVOs4VSpmA"));
        reg(movie("Deadpool", 2016, "R", "Superhero",
            "A wisecracking mercenary hunts the man who gave him accelerated healing and a ruined face.", "ONHBaC-pfsk"));
        reg(movie("Wonder Woman", 2017, "PG-13", "Superhero",
            "An Amazon princess leaves paradise to end the war to end all wars.", "1Q8fG0TtVAY"));
        reg(movie("Captain America: Civil War", 2016, "PG-13", "Superhero",
            "The Avengers split into two sides when government oversight divides the team.", "dKrVegVI0Us"));
        reg(movie("Toy Story", 1995, "G", "Animation",
            "When a new space-ranger toy arrives, a loyal cowboy doll must learn to share his owner's heart.", "v-PjgYDrg70"));
        reg(movie("Finding Nemo", 2003, "G", "Animation",
            "An anxious clownfish crosses the ocean with a forgetful friend to find his lost son.", "wZdpNglLbt8"));
        reg(movie("The Lion King", 1994, "G", "Animation",
            "A young lion cub must reclaim his place after tragedy forces him from the Pride Lands.", "4sj1MT05lAA"));
        reg(movie("Shrek", 2001, "PG", "Animation",
            "A grumpy ogre sets out to reclaim his swamp and ends up on a fairy-tale rescue mission.", "CwXOrWvPBPk"));
        reg(movie("Frozen", 2013, "PG", "Animation",
            "Two royal sisters, one with icy powers, journey to save their kingdom from eternal winter.", "TbQm5doF_Uc"));
        reg(movie("Coco", 2017, "PG", "Animation",
            "A music-loving boy crosses into the Land of the Dead to uncover his family's secrets.", "Rvr68u6k5sI"));
        reg(movie("Up", 2009, "PG", "Animation",
            "An elderly widower ties balloons to his house and sets off for South America, with a stowaway scout.", "ORFWdXl_zJ4"));
        reg(movie("Spider-Man: Into the Spider-Verse", 2018, "PG", "Animation",
            "Teen Miles Morales becomes Spider-Man and meets heroes from other dimensions.", "g4Hbz2jLxvQ"));
        reg(movie("Inside Out", 2015, "PG", "Animation",
            "Inside a girl's mind, her emotions scramble to guide her through a big move.", "yRUAzGQ3nSY"));
        reg(movie("Moana", 2016, "PG", "Animation",
            "A fearless teen sails across the ocean with a demigod to save her island.", "LKFuXETZUsI"));
        reg(movie("Zootopia", 2016, "PG", "Animation",
            "A rookie bunny cop and a sly fox team up to crack a city-wide conspiracy.", "jWM0ct-OLsM"));
        reg(movie("The Incredibles", 2004, "PG", "Animation",
            "A family of undercover superheroes is pulled back into action.", "-UaGUdNJdRQ"));
        reg(movie("Get Out", 2017, "R", "Horror",
            "A weekend at his girlfriend's family estate turns sinister for a young photographer.", "DzfpyUB60YY"));
        reg(movie("The Conjuring", 2013, "R", "Horror",
            "Paranormal investigators help a family terrorized by a dark presence in their farmhouse.", "k10ETZ41q5o"));
        reg(movie("A Quiet Place", 2018, "PG-13", "Horror",
            "A family must live in silence to survive creatures that hunt by sound.", "WR7cc5t7tv8"));
        reg(movie("Hereditary", 2018, "R", "Horror",
            "After the family matriarch dies, a grieving household begins to unearth disturbing secrets.", "V6wWKNij_1M"));
        reg(movie("It", 2017, "R", "Horror",
            "A group of outcast kids band together to face a shape-shifting evil that preys on their town.", "xKJmEC5ieOk"));
        reg(movie("Insidious", 2010, "PG-13", "Horror",
            "A family tries to save their comatose son from a realm of restless spirits.", "zuZnRUcoWos"));
        reg(movie("The Shining", 1980, "R", "Horror",
            "A winter caretaker's family is snowed in at an isolated hotel as madness creeps in.", "S014oGZiSdI"));
        reg(movie("Midsommar", 2019, "R", "Horror",
            "A grieving couple's trip to a Swedish festival becomes a nightmare in broad daylight.", "1Vnghdsjmd0"));
        reg(movie("The Exorcist", 1973, "R", "Horror",
            "A mother seeks help from two priests when her daughter's behaviour turns terrifying.", "YDGw1MTEe9k"));
        reg(movie("The Babadook", 2014, "NR", "Horror",
            "A widowed mother and her son are haunted by a sinister storybook creature.", "k5WQZzDRVtw"));
        reg(movie("Smile", 2022, "R", "Horror",
            "After a patient's horrifying death, a therapist is haunted by a smile she cannot escape.", "BcDK7lkzzsU"));
        reg(movie("Nope", 2022, "R", "Horror",
            "Siblings on a California ranch uncover something terrifying in the sky.", "In8fuzj3gck"));
        reg(movie("Titanic", 1997, "PG-13", "Drama",
            "A young artist and a first-class passenger fall in love aboard the doomed ocean liner.", "kVrqfYjkTdQ"));
        reg(movie("La La Land", 2016, "PG-13", "Drama",
            "An aspiring actress and a jazz pianist chase their dreams, and each other, in Los Angeles.", "0pdqf4P9MB8"));
        reg(movie("The Notebook", 2004, "PG-13", "Drama",
            "A summer romance across class lines is told through decades of love and memory.", "yDJIcYE32NU"));
        reg(movie("Forrest Gump", 1994, "PG-13", "Drama",
            "A kind-hearted man unwittingly witnesses and shapes decades of American history.", "bLvqoHBptjg"));
        reg(movie("The Shawshank Redemption", 1994, "R", "Drama",
            "Two prisoners forge an enduring friendship and find hope inside Shawshank prison.", "NmzuHjWmXOc"));
        reg(movie("Parasite", 2019, "R", "Drama",
            "A struggling family schemes its way into the lives of a wealthy household, with unexpected consequences.", "5xH0HfJHsaY"));
        reg(movie("Whiplash", 2014, "R", "Drama",
            "A promising young drummer is pushed to the edge by an uncompromising conservatory teacher.", "7d_jQycdQGo"));
        reg(movie("Joker", 2019, "R", "Drama",
            "A struggling comedian's descent into madness sparks a movement in a decaying Gotham.", "zAGVQLHvwOY"));
        reg(movie("Fight Club", 1999, "R", "Drama",
            "An insomniac office worker and a soap salesman start an underground club that spirals out of control.", "SUXWAaZUaOQ"));
        reg(movie("The Godfather", 1972, "R", "Drama",
            "The aging patriarch of a crime dynasty hands control to his reluctant youngest son.", "UaVTIH8mujA"));
        reg(movie("Good Will Hunting", 1997, "R", "Drama",
            "A janitor with a gift for mathematics is pushed to face his past by a therapist.", "PaZVjZEFkRs"));
        reg(movie("The Green Mile", 1999, "R", "Drama",
            "A death-row guard discovers that one inmate has a miraculous gift.", "Ki4haFrqSrw"));
        reg(movie("The Hangover", 2009, "R", "Comedy",
            "Three friends wake up from a bachelor party with no memory and a missing groom.", "tcdUhdOlz9M"));
        reg(movie("Superbad", 2007, "R", "Comedy",
            "Two high school friends try to make one wild night before graduation count.", "4eaZ_48ZYog"));
        reg(movie("Dumb and Dumber", 1994, "PG-13", "Comedy",
            "Two lovably clueless pals drive across the country to return a briefcase.", "l13yPhimE3o"));
        reg(movie("Home Alone", 1990, "PG", "Comedy",
            "A boy left behind at Christmas defends his house against two bumbling burglars.", "jEDaVHmw7r4"));
        reg(movie("Mean Girls", 2004, "PG-13", "Comedy",
            "A homeschooled newcomer navigates the brutal social order of high school.", "oDU84nmSDZY"));
        reg(movie("Jumanji: Welcome to the Jungle", 2017, "PG-13", "Comedy",
            "Four teens are sucked into a video game as avatars with very different strengths.", "rBxcF-r9Ibs"));
        reg(movie("The Grand Budapest Hotel", 2014, "R", "Comedy",
            "A legendary concierge and his lobby boy get tangled in a theft and a family fortune feud.", "1Fg5iWmQjwk"));
        reg(movie("Barbie", 2023, "PG-13", "Comedy",
            "Barbie leaves Barbieland and discovers what it means to be human.", "pBk4NYhWNMM"));
        reg(movie("Free Guy", 2021, "PG-13", "Comedy",
            "A bank teller realises he is a background character in a video game.", "X2m-08cOAbc"));
        reg(movie("Everything Everywhere All at Once", 2022, "R", "Comedy",
            "A laundromat owner is thrust into a multiverse adventure to save everything.", "wxN1T1uxQ2g"));
        reg(movie("Jojo Rabbit", 2019, "PG-13", "Comedy",
            "A lonely German boy discovers his mother is hiding a Jewish girl and questions his beliefs.", "tL4McUzXfFI"));
        reg(movie("Zombieland", 2009, "R", "Comedy",
            "Survivors follow a set of rules to stay alive in a world overrun by zombies.", "8m9EVP8X7N8"));
        reg(movie("Se7en", 1995, "R", "Thriller",
            "Two detectives hunt a killer who uses the seven deadly sins as his guide.", "znmZoVkCjpI"));
        reg(movie("The Silence of the Lambs", 1991, "R", "Thriller",
            "A young FBI trainee seeks help from an imprisoned cannibal to catch another killer.", "RuX2MQeb8UM"));
        reg(movie("Gone Girl", 2014, "R", "Thriller",
            "On his anniversary, a man becomes the prime suspect in his wife's disappearance.", "2-_-1nJf8Vg"));
        reg(movie("Prisoners", 2013, "R", "Thriller",
            "When two girls vanish, a desperate father takes justice into his own hands.", "bpXfcTF6iVk"));
        reg(movie("Shutter Island", 2010, "R", "Thriller",
            "A U.S. marshal investigates a patient's disappearance from a remote psychiatric hospital.", "5iaYLCiq5RM"));
        reg(movie("Pulp Fiction", 1994, "R", "Thriller",
            "Interwoven stories of hitmen, a boxer and a gangster's wife in Los Angeles.", "s7EdQ4FqbhY"));
        reg(movie("The Departed", 2006, "R", "Thriller",
            "An undercover cop and a mole in the police race to expose each other.", "auYbpnEwBBg"));
        reg(movie("Zodiac", 2007, "R", "Thriller",
            "A cartoonist and two cops become obsessed with identifying a notorious serial killer.", "yNncHPl1UXg"));
        reg(movie("The Usual Suspects", 1995, "R", "Thriller",
            "Five criminals in a police line-up are tied to a mysterious mastermind.", "oiXdPolca5w"));
        reg(movie("Knives Out", 2019, "PG-13", "Thriller",
            "A detective investigates the death of a famous mystery novelist.", "qGqiHJTsRkQ"));
        reg(movie("No Country for Old Men", 2007, "R", "Thriller",
            "A hunter who finds a drug deal's cash is pursued by an unstoppable killer.", "38A__WetWVs"));
        reg(movie("Goodfellas", 1990, "R", "Thriller",
            "The rise and fall of a young man in the New York mob.", "2ilzidi_J8Q"));
        reg(movie("The Lord of the Rings: The Fellowship of the Ring", 2001, "PG-13", "Fantasy",
            "A hobbit sets out with a fellowship to destroy a ring of ultimate evil.", "V75dMMIW2B4"));
        reg(movie("The Lord of the Rings: The Two Towers", 2002, "PG-13", "Fantasy",
            "The fellowship is divided as war closes in on Middle-earth.", "LbfMDwc4azU"));
        reg(movie("The Lord of the Rings: The Return of the King", 2003, "PG-13", "Fantasy",
            "The final battle for Middle-earth begins as Frodo nears Mount Doom.", "r5X-hFf6Bwo"));
        reg(movie("The Hobbit: An Unexpected Journey", 2012, "PG-13", "Fantasy",
            "A reluctant hobbit joins thirteen dwarves to reclaim their homeland from a dragon.", "SDnYMbYB-nU"));
        reg(movie("Harry Potter and the Sorcerer's Stone", 2001, "PG", "Fantasy",
            "An orphan discovers he is a wizard and starts at a school of magic.", "VyHV0BRtdxo"));
        reg(movie("Pirates of the Caribbean: The Curse of the Black Pearl", 2003, "PG-13", "Fantasy",
            "A blacksmith and an eccentric pirate chase a cursed crew to save a governor's daughter.", "naQr0uTrH_s"));
        reg(movie("Jurassic Park", 1993, "PG-13", "Fantasy",
            "A theme park's cloned dinosaurs break loose and the guests become the prey.", "lc0UehYemQA"));
        reg(movie("Raiders of the Lost Ark", 1981, "PG", "Fantasy",
            "Archaeologist Indiana Jones races the Nazis to find the Ark of the Covenant.", "XkkzKHCx154"));
        reg(movie("Avatar", 2009, "PG-13", "Fantasy",
            "A paralysed marine is sent to a distant moon and torn between two worlds.", "5PSNL1qE6VY"));
        reg(movie("Aladdin (2019)", 2019, "PG", "Fantasy",
            "A street thief, a genie and a princess collide in a tale of three wishes.", "foyufD52aog"));
        reg(movie("The Lion King (2019)", 2019, "PG", "Fantasy",
            "A young lion prince flees his kingdom and must return to claim his destiny.", "7TavVZMewpY"));
        reg(movie("Doctor Strange", 2016, "PG-13", "Fantasy",
            "A brilliant surgeon discovers hidden realms of magic after a career-ending crash.", "HSzx-zryEgM"));
        reg(movie("Saving Private Ryan", 1998, "R", "War",
            "Eight soldiers go behind enemy lines to bring one paratrooper home.", "9CiW_DgxCnQ"));
        reg(movie("1917", 2019, "R", "War",
            "Two soldiers race across enemy territory to deliver a message that could save 1,600 men.", "YqNYrYUiMfg"));
        reg(movie("Dunkirk", 2017, "PG-13", "War",
            "Soldiers, sailors and civilians fight to evacuate a beach under siege.", "F-eMt3SrfFU"));
        reg(movie("Hacksaw Ridge", 2016, "R", "War",
            "A medic who refuses to carry a weapon saves dozens in a brutal battle.", "s2-1hz1juBI"));
        reg(movie("300", 2006, "R", "War",
            "King Leonidas and 300 Spartans stand against a vast Persian army.", "UrIbxk7idYA"));
        reg(movie("Fury", 2014, "R", "War",
            "A war-weary tank commander and his crew face an overwhelming enemy in 1945.", "q6EoRBvdVPQ"));
        reg(movie("Schindler's List", 1993, "R", "War",
            "A businessman saves over a thousand Jewish refugees during the Holocaust.", "gG22XNhtnoY"));
        reg(movie("The Pianist", 2002, "R", "War",
            "A Polish pianist struggles to survive the destruction of the Warsaw ghetto.", "BFwGqLa_oAo"));
        reg(movie("Apocalypse Now", 1979, "R", "War",
            "A captain travels upriver to end the command of a rogue colonel.", "FTjG-Aux_yQ"));
        reg(movie("Braveheart", 1995, "R", "War",
            "William Wallace leads the Scots in a rebellion against English rule.", "1cnvXdkhWz4"));
        reg(movie("Inglourious Basterds", 2009, "R", "War",
            "Jewish-American soldiers and a cinema owner plot to take down the Third Reich.", "KnrRy6kSFF0"));
        reg(movie("Oppenheimer", 2023, "R", "War",
            "The story of the physicist who led the creation of the atomic bomb.", "uYPbbksJxIg"));
        reg(movie("Kill Bill: Vol. 1", 2003, "R", "Action",
            "A betrayed assassin wakes from a coma and sets out on a blood-soaked path to revenge.", "7kSuas6mRpk"));
        reg(movie("Kingsman: The Secret Service", 2014, "R", "Action",
            "A street-smart kid is recruited by a secret spy agency and trained as a gentleman agent.", "kl8F-8tR8to"));
        reg(movie("Gravity", 2013, "PG-13", "Sci-Fi",
            "Two astronauts struggle to survive after debris destroys their shuttle in orbit.", "OiTiKOy59o4"));
        reg(movie("Alien", 1979, "R", "Horror",
            "The crew of a space freighter discovers that something deadly has come aboard.", "LjLamj-b0I8"));
        reg(movie("The Batman", 2022, "PG-13", "Superhero",
            "In his second year as Gotham's vigilante, Batman hunts a killer who targets the city's elite.", "mqqft2x_Aa4"));
        reg(movie("Man of Steel", 2013, "PG-13", "Superhero",
            "A young man with extraordinary powers must choose between his homeland and his adopted Earth.", "T6DJcgm3wNY"));
        reg(movie("Batman Begins", 2005, "PG-13", "Superhero",
            "Bruce Wayne trains with a secret order and returns to Gotham as the Dark Knight.", "neY2xVmOfUM"));
        reg(movie("Spirited Away", 2001, "PG", "Animation",
            "A girl wanders into a spirit world and must work at a bathhouse to free her parents.", "ByXuk9QqQkk"));
        reg(movie("Kung Fu Panda", 2008, "PG", "Animation",
            "A clumsy, noodle-loving panda is chosen as the legendary Dragon Warrior.", "PXi3Mv6KMzY"));
        reg(movie("Ratatouille", 2007, "G", "Animation",
            "A rat with a gift for cooking teams up with a young kitchen worker in a Paris restaurant.", "NgsQ8mVkN8w"));
        reg(movie("WALL-E", 2008, "G", "Animation",
            "A lonely waste-collecting robot follows a probe to space and changes humanity's future.", "CZ1CATNbXg0"));
        reg(movie("Jaws", 1975, "PG", "Horror",
            "A police chief, a marine scientist and a fisherman hunt a great white terrorising a beach town.", "U1fu_sA7XhE"));
        reg(movie("Annabelle", 2014, "R", "Horror",
            "A couple is terrorised by a vintage doll that attracts a demonic presence.", "paFgQNPGlsg"));
        reg(movie("The Nun", 2018, "R", "Horror",
            "A priest and a novice investigate a death at a remote abbey in Romania.", "pzD9zGcUNrw"));
        reg(movie("The Prestige", 2006, "PG-13", "Thriller",
            "Two rival magicians push their obsession to dangerous extremes to create the perfect illusion.", "o4gHCmTQDVI"));
        reg(movie("12 Years a Slave", 2013, "R", "Drama",
            "A free Black man is kidnapped and sold into slavery in the pre-Civil War South.", "z02Ie8wKKRg"));
        reg(movie("Moonlight", 2016, "R", "Drama",
            "A young man grows up in Miami, searching for identity across three chapters of his life.", "9NJj12tJzqc"));
        reg(movie("Birdman", 2014, "R", "Drama",
            "A washed-up actor famous for a superhero role tries to prove himself on Broadway.", "uJfLoE6hanc"));
        reg(movie("The Pursuit of Happyness", 2006, "PG-13", "Drama",
            "A struggling salesman fights homelessness to give his young son a better life.", "89Kq8SDyvfg"));
        reg(movie("A Beautiful Mind", 2001, "PG-13", "Drama",
            "A brilliant mathematician battles schizophrenia while changing economics forever.", "aS_d0Ayjw4o"));
        reg(movie("Dead Poets Society", 1989, "PG", "Drama",
            "An unconventional English teacher inspires students at a strict boys' school to seize the day.", "LjHORRHXtyI"));
        reg(movie("The Social Network", 2010, "PG-13", "Drama",
            "The founding of Facebook and the friendships and lawsuits that followed.", "lB95KLmpLR4"));
        reg(movie("The Fault in Our Stars", 2014, "PG-13", "Romance",
            "Two teenagers with cancer fall in love and travel to Amsterdam.", "9ItBvH5J6ss"));
        reg(movie("A Star Is Born", 2018, "R", "Romance",
            "A seasoned musician discovers a young singer and watches her rise as his own star fades.", "nSbzyEJ8X9E"));
        reg(movie("About Time", 2013, "R", "Romance",
            "A young man discovers he can travel in time and uses it to find love.", "T7A810duHvw"));
        reg(movie("500 Days of Summer", 2009, "PG-13", "Romance",
            "A greeting-card writer looks back on the 500 days of his relationship with Summer.", "PsD0NpFSADM"));
        reg(movie("Call Me by Your Name", 2017, "R", "Romance",
            "A summer romance blossoms between a teenager and a visiting scholar in 1980s Italy.", "Z9AYPxH5NTM"));
        reg(movie("Past Lives", 2023, "PG-13", "Romance",
            "Two childhood friends reunite decades later and wonder about the lives they might have had.", "kA244xewjcI"));
        reg(movie("Casablanca", 1942, "PG", "Romance",
            "A cynical nightclub owner must choose between love and virtue in wartime Morocco.", "BkL9l7qovsE"));
        reg(movie("Eternal Sunshine of the Spotless Mind", 2004, "R", "Romance",
            "After a painful breakup, a couple undergoes a procedure to erase each other from memory.", "07-QBnEkgXU"));
        reg(movie("Her", 2013, "R", "Romance",
            "A lonely writer develops an unlikely relationship with an intelligent operating system.", "WzV6mXIOVl4"));
        reg(movie("The Shape of Water", 2017, "R", "Romance",
            "A mute cleaner forms a bond with a mysterious amphibious creature held in a secret lab.", "XFYWazblaUA"));
        reg(movie("Little Women", 2019, "PG", "Romance",
            "Four sisters come of age in post-Civil War New England, each chasing her own dream.", "AST2-4db4ic"));
        reg(movie("Pride & Prejudice", 2005, "PG", "Romance",
            "Elizabeth Bennet's sharp wit meets the proud Mr. Darcy in Regency England.", "1dYv5u6w_kA"));
        reg(movie("Beauty and the Beast (2017)", 2017, "PG", "Romance",
            "A bookish young woman befriends a cursed prince and his enchanted household.", "e3Czma1ou1M"));
        reg(movie("Bohemian Rhapsody", 2018, "PG-13", "Musical",
            "The rise of Queen and the larger-than-life story of frontman Freddie Mercury.", "mYfJxlgR2jw"));
        reg(movie("The Greatest Showman", 2017, "PG", "Musical",
            "A visionary dreamer builds a circus that turns outcasts into stars.", "AXCTMLTA0wQ"));
        reg(movie("Encanto", 2021, "PG", "Animation",
            "A girl in a magical Colombian family is the only one without a gift - until the house is in danger.", "CaimKeDcudo"));
        reg(movie("Soul", 2020, "PG", "Animation",
            "A jazz pianist's soul is separated from his body just before his big break.", "xOsLIiBStEs"));
        reg(movie("The Revenant", 2015, "R", "Drama",
            "A frontiersman fights to survive and take revenge after being left for dead.", "LoebZZ8K5N0"));
        reg(movie("The Imitation Game", 2014, "PG-13", "War",
            "Alan Turing leads a team of codebreakers racing to crack the Enigma machine.", "S5CjKEFb-sM"));
        reg(movie("American Sniper", 2014, "R", "War",
            "A Navy SEAL sniper becomes a legend on the battlefield while struggling at home.", "99k3u0m2Zmo"));
        reg(movie("The Wolf of Wall Street", 2013, "R", "Drama",
            "The rise and fall of a stockbroker whose greed and excess know no limits.", "iszwuX1AK6A"));
        reg(movie("Ford v Ferrari", 2019, "PG-13", "Drama",
            "A designer and a driver take on Ferrari at the 24 Hours of Le Mans.", "zyYgDtY2AMY"));
        reg(movie("Green Book", 2018, "PG-13", "Drama",
            "A bouncer drives a world-class pianist through the Deep South on a 1962 concert tour.", "QkZxoko_HC0"));

        // ------------------------------------------------------------------ genres (16 titles each)
        genre("Action & Adventure", "Explosive stunts and edge-of-your-seat chases",
            "Mad Max: Fury Road", "John Wick", "Gladiator", "Top Gun: Maverick", "Mission: Impossible - Fallout", "Mission: Impossible - Dead Reckoning", "Skyfall", "Casino Royale", "Terminator 2: Judgment Day", "Edge of Tomorrow", "Baby Driver", "Die Hard", "Raiders of the Lost Ark", "Pirates of the Caribbean: The Curse of the Black Pearl", "Kill Bill: Vol. 1", "Kingsman: The Secret Service");
        genre("Sci-Fi", "Other worlds, bold ideas, big screens",
            "Inception", "Interstellar", "The Matrix", "Blade Runner 2049", "Dune", "Dune: Part Two", "Arrival", "The Martian", "Ex Machina", "Tenet", "Back to the Future", "Star Wars: The Force Awakens", "Gravity", "Avatar", "Terminator 2: Judgment Day", "Edge of Tomorrow");
        genre("Superheroes", "Capes, cowls and cinematic universes",
            "The Dark Knight", "The Dark Knight Rises", "The Avengers", "Avengers: Endgame", "Black Panther", "Iron Man", "Guardians of the Galaxy", "Logan", "Spider-Man: No Way Home", "Deadpool", "Wonder Woman", "Captain America: Civil War", "The Batman", "Man of Steel", "Batman Begins", "Spider-Man: Into the Spider-Verse");
        genre("Animation", "Stories for every age, drawn to life",
            "Toy Story", "Finding Nemo", "The Lion King", "Shrek", "Frozen", "Coco", "Up", "Spider-Man: Into the Spider-Verse", "Inside Out", "Moana", "Zootopia", "The Incredibles", "Spirited Away", "Kung Fu Panda", "Ratatouille", "WALL-E");
        genre("Family & Kids", "Movie night for the whole family",
            "Toy Story", "Finding Nemo", "The Lion King", "Shrek", "Frozen", "Coco", "Up", "Inside Out", "Moana", "Encanto", "Soul", "Home Alone", "Jumanji: Welcome to the Jungle", "Harry Potter and the Sorcerer's Stone", "Aladdin (2019)", "Kung Fu Panda");
        genre("Horror", "Watch with the lights on",
            "Get Out", "The Conjuring", "A Quiet Place", "Hereditary", "It", "Insidious", "The Shining", "Midsommar", "The Exorcist", "The Babadook", "Smile", "Nope", "Alien", "Jaws", "Annabelle", "The Nun");
        genre("Drama", "Stories that stay with you",
            "Forrest Gump", "The Shawshank Redemption", "Parasite", "Whiplash", "Joker", "Fight Club", "The Godfather", "Good Will Hunting", "The Green Mile", "12 Years a Slave", "Moonlight", "Birdman", "The Pursuit of Happyness", "A Beautiful Mind", "Dead Poets Society", "The Social Network");
        genre("Romance", "Love stories, old and new",
            "Titanic", "La La Land", "The Notebook", "The Fault in Our Stars", "A Star Is Born", "About Time", "500 Days of Summer", "Call Me by Your Name", "Past Lives", "Casablanca", "Eternal Sunshine of the Spotless Mind", "Her", "The Shape of Water", "Little Women", "Pride & Prejudice", "Beauty and the Beast (2017)");
        genre("Comedy", "Laugh-out-loud picks for any mood",
            "The Hangover", "Superbad", "Dumb and Dumber", "Home Alone", "Mean Girls", "Jumanji: Welcome to the Jungle", "The Grand Budapest Hotel", "Barbie", "Free Guy", "Everything Everywhere All at Once", "Jojo Rabbit", "Zombieland", "Deadpool", "Knives Out", "Shrek", "Back to the Future");
        genre("Crime & Thriller", "Twists, heists and unreliable narrators",
            "Se7en", "The Silence of the Lambs", "Gone Girl", "Prisoners", "Shutter Island", "Pulp Fiction", "The Departed", "Zodiac", "The Usual Suspects", "Knives Out", "No Country for Old Men", "Goodfellas", "The Prestige", "The Dark Knight", "Joker", "Parasite");
        genre("Mind-Benders", "Films that make you question everything",
            "Inception", "The Matrix", "Tenet", "Shutter Island", "Fight Club", "The Prestige", "Arrival", "Eternal Sunshine of the Spotless Mind", "Everything Everywhere All at Once", "Ex Machina", "Blade Runner 2049", "Interstellar", "Joker", "Gone Girl", "Get Out", "Edge of Tomorrow");
        genre("Fantasy & Epic Adventure", "Magic, myth and far-off lands",
            "The Lord of the Rings: The Fellowship of the Ring", "The Lord of the Rings: The Two Towers", "The Lord of the Rings: The Return of the King", "The Hobbit: An Unexpected Journey", "Harry Potter and the Sorcerer's Stone", "Pirates of the Caribbean: The Curse of the Black Pearl", "Jurassic Park", "Raiders of the Lost Ark", "Avatar", "Aladdin (2019)", "The Lion King (2019)", "Doctor Strange", "Spirited Away", "The Shape of Water", "Coco", "Dune");
        genre("War & History", "True stories and battles that shaped the world",
            "Saving Private Ryan", "1917", "Dunkirk", "Hacksaw Ridge", "300", "Fury", "Schindler's List", "The Pianist", "Apocalypse Now", "Braveheart", "Inglourious Basterds", "Oppenheimer", "Gladiator", "Jojo Rabbit", "The Imitation Game", "American Sniper");
        genre("Musicals & Music", "Songs you will be humming all week",
            "La La Land", "Whiplash", "A Star Is Born", "Bohemian Rhapsody", "Baby Driver", "Coco", "Frozen", "Moana", "Encanto", "The Lion King", "The Lion King (2019)", "Aladdin (2019)", "Beauty and the Beast (2017)", "The Greatest Showman", "Soul", "Spider-Man: Into the Spider-Verse");
        genre("Survival & Disaster", "When everything goes wrong",
            "Gravity", "The Martian", "Interstellar", "Titanic", "The Revenant", "A Quiet Place", "Jurassic Park", "Dunkirk", "1917", "Mad Max: Fury Road", "Hacksaw Ridge", "Edge of Tomorrow", "Alien", "Jaws", "Saving Private Ryan", "Fury");
        genre("True Stories", "Based on real people and real events",
            "Oppenheimer", "The Social Network", "A Beautiful Mind", "The Pursuit of Happyness", "Bohemian Rhapsody", "Schindler's List", "12 Years a Slave", "The Imitation Game", "American Sniper", "Dunkirk", "Hacksaw Ridge", "The Wolf of Wall Street", "Ford v Ferrari", "Green Book", "Goodfellas", "The Pianist");

        for (String t : List.of("The Dark Knight", "Interstellar", "Dune: Part Two", "Spider-Man: Into the Spider-Verse", "Top Gun: Maverick", "Coco", "Avengers: Endgame", "Inception")) {
            featured.add(catalogue.get(t));
        }
    }

    private void reg(Movie m) { catalogue.put(m.title(), m); }

    /** Builds a genre row from titles that were registered above (fails fast on a typo). */
    private void genre(String name, String tagline, String... titles) {
        List<Movie> movies = new ArrayList<>();
        for (String t : titles) {
            Movie m = catalogue.get(t);
            if (m == null) throw new IllegalStateException("Unknown movie in genre " + name + ": " + t);
            movies.add(m);
        }
        categories.add(new Category(name, tagline, movies));
    }

    private static Movie movie(String title, int year, String rating, String genre, String desc, String id) {
        return new Movie(title, year, rating, genre, desc, id);
    }

    @Override public List<Category> getCategories() { return List.copyOf(categories); }

    @Override public List<Movie> getFeatured() { return List.copyOf(featured); }

    @Override public List<Upcoming> getUpcoming() { return UpcomingCatalogue.all(); }

    @Override public List<Movie> getAllMovies() { return List.copyOf(catalogue.values()); }

    @Override
    public Movie nextInGenre(Movie current) {
        for (Category c : categories) {
            int i = c.movies().indexOf(current);
            if (i >= 0) return c.movies().get((i + 1) % c.movies().size());
        }
        return null;
    }
}
