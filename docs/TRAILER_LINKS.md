# Trailer links, video codes and picture URLs

Every movie in StreamVerse is stored as **one YouTube video code** (the 11 characters after `watch?v=`). Nothing else is saved:
the app builds all three links below from that code (see `model/Movie.java`).

| Link | Pattern |
|---|---|
| Trailer (watch page) | `https://www.youtube.com/watch?v=<CODE>` |
| Trailer played inside the app | `https://www.youtube.com/embed/<CODE>` (loaded by `player/LocalPlayerServer` + `web/player.html`) |
| Thumbnail on the cards (480x360) | `https://img.youtube.com/vi/<CODE>/hqdefault.jpg` |
| Large picture on the hero banner (1280x720) | `https://img.youtube.com/vi/<CODE>/maxresdefault.jpg` |

```java
// model/Movie.java
public String thumbnailUrl() { return "https://img.youtube.com/vi/" + youtubeId + "/hqdefault.jpg"; }
public String backdropUrl()  { return "https://img.youtube.com/vi/" + youtubeId + "/maxresdefault.jpg"; }
public String watchUrl()     { return "https://www.youtube.com/watch?v=" + youtubeId; }
```

Other pictures used by the app (stored inside the project, not on YouTube): `src/main/resources/images/stage.jpg` (page background).

## All 165 movies

| # | Movie | Year | Code | Trailer | Thumbnail |
|---|---|---|---|---|---|
| 1 | Mad Max: Fury Road | 2015 | `hEJnMQG9ev8` | [watch](https://www.youtube.com/watch?v=hEJnMQG9ev8) | [picture](https://img.youtube.com/vi/hEJnMQG9ev8/hqdefault.jpg) |
| 2 | John Wick | 2014 | `2AUmvWm5ZDQ` | [watch](https://www.youtube.com/watch?v=2AUmvWm5ZDQ) | [picture](https://img.youtube.com/vi/2AUmvWm5ZDQ/hqdefault.jpg) |
| 3 | Gladiator | 2000 | `owK1qxDselE` | [watch](https://www.youtube.com/watch?v=owK1qxDselE) | [picture](https://img.youtube.com/vi/owK1qxDselE/hqdefault.jpg) |
| 4 | Top Gun: Maverick | 2022 | `giXco2jaZ_4` | [watch](https://www.youtube.com/watch?v=giXco2jaZ_4) | [picture](https://img.youtube.com/vi/giXco2jaZ_4/hqdefault.jpg) |
| 5 | Mission: Impossible - Fallout | 2018 | `wb49-oV0F78` | [watch](https://www.youtube.com/watch?v=wb49-oV0F78) | [picture](https://img.youtube.com/vi/wb49-oV0F78/hqdefault.jpg) |
| 6 | Mission: Impossible - Dead Reckoning | 2023 | `avz06PDqDbM` | [watch](https://www.youtube.com/watch?v=avz06PDqDbM) | [picture](https://img.youtube.com/vi/avz06PDqDbM/hqdefault.jpg) |
| 7 | Skyfall | 2012 | `6kw1UVovByw` | [watch](https://www.youtube.com/watch?v=6kw1UVovByw) | [picture](https://img.youtube.com/vi/6kw1UVovByw/hqdefault.jpg) |
| 8 | Casino Royale | 2006 | `36mnx8dBbGE` | [watch](https://www.youtube.com/watch?v=36mnx8dBbGE) | [picture](https://img.youtube.com/vi/36mnx8dBbGE/hqdefault.jpg) |
| 9 | Terminator 2: Judgment Day | 1991 | `CRRlbK5w8AE` | [watch](https://www.youtube.com/watch?v=CRRlbK5w8AE) | [picture](https://img.youtube.com/vi/CRRlbK5w8AE/hqdefault.jpg) |
| 10 | Edge of Tomorrow | 2014 | `vw61gCe2oqI` | [watch](https://www.youtube.com/watch?v=vw61gCe2oqI) | [picture](https://img.youtube.com/vi/vw61gCe2oqI/hqdefault.jpg) |
| 11 | Baby Driver | 2017 | `z2z857RSfhk` | [watch](https://www.youtube.com/watch?v=z2z857RSfhk) | [picture](https://img.youtube.com/vi/z2z857RSfhk/hqdefault.jpg) |
| 12 | Die Hard | 1988 | `2TQ-pOvI6Xo` | [watch](https://www.youtube.com/watch?v=2TQ-pOvI6Xo) | [picture](https://img.youtube.com/vi/2TQ-pOvI6Xo/hqdefault.jpg) |
| 13 | Inception | 2010 | `YoHD9XEInc0` | [watch](https://www.youtube.com/watch?v=YoHD9XEInc0) | [picture](https://img.youtube.com/vi/YoHD9XEInc0/hqdefault.jpg) |
| 14 | Interstellar | 2014 | `zSWdZVtXT7E` | [watch](https://www.youtube.com/watch?v=zSWdZVtXT7E) | [picture](https://img.youtube.com/vi/zSWdZVtXT7E/hqdefault.jpg) |
| 15 | The Matrix | 1999 | `vKQi3bBA1y8` | [watch](https://www.youtube.com/watch?v=vKQi3bBA1y8) | [picture](https://img.youtube.com/vi/vKQi3bBA1y8/hqdefault.jpg) |
| 16 | Blade Runner 2049 | 2017 | `gCcx85zbxz4` | [watch](https://www.youtube.com/watch?v=gCcx85zbxz4) | [picture](https://img.youtube.com/vi/gCcx85zbxz4/hqdefault.jpg) |
| 17 | Dune | 2021 | `n9xhJrPXop4` | [watch](https://www.youtube.com/watch?v=n9xhJrPXop4) | [picture](https://img.youtube.com/vi/n9xhJrPXop4/hqdefault.jpg) |
| 18 | Dune: Part Two | 2024 | `Way9Dexny3w` | [watch](https://www.youtube.com/watch?v=Way9Dexny3w) | [picture](https://img.youtube.com/vi/Way9Dexny3w/hqdefault.jpg) |
| 19 | Arrival | 2016 | `tFMo3UJ4B4g` | [watch](https://www.youtube.com/watch?v=tFMo3UJ4B4g) | [picture](https://img.youtube.com/vi/tFMo3UJ4B4g/hqdefault.jpg) |
| 20 | The Martian | 2015 | `ej3ioOneTy8` | [watch](https://www.youtube.com/watch?v=ej3ioOneTy8) | [picture](https://img.youtube.com/vi/ej3ioOneTy8/hqdefault.jpg) |
| 21 | Ex Machina | 2014 | `EoQuVnKhxaM` | [watch](https://www.youtube.com/watch?v=EoQuVnKhxaM) | [picture](https://img.youtube.com/vi/EoQuVnKhxaM/hqdefault.jpg) |
| 22 | Tenet | 2020 | `LdOM0x0XDMo` | [watch](https://www.youtube.com/watch?v=LdOM0x0XDMo) | [picture](https://img.youtube.com/vi/LdOM0x0XDMo/hqdefault.jpg) |
| 23 | Back to the Future | 1985 | `qvsgGtivCgs` | [watch](https://www.youtube.com/watch?v=qvsgGtivCgs) | [picture](https://img.youtube.com/vi/qvsgGtivCgs/hqdefault.jpg) |
| 24 | Star Wars: The Force Awakens | 2015 | `sGbxmsDFVnE` | [watch](https://www.youtube.com/watch?v=sGbxmsDFVnE) | [picture](https://img.youtube.com/vi/sGbxmsDFVnE/hqdefault.jpg) |
| 25 | The Dark Knight | 2008 | `EXeTwQWrcwY` | [watch](https://www.youtube.com/watch?v=EXeTwQWrcwY) | [picture](https://img.youtube.com/vi/EXeTwQWrcwY/hqdefault.jpg) |
| 26 | The Dark Knight Rises | 2012 | `GokKUqLcvD8` | [watch](https://www.youtube.com/watch?v=GokKUqLcvD8) | [picture](https://img.youtube.com/vi/GokKUqLcvD8/hqdefault.jpg) |
| 27 | The Avengers | 2012 | `eOrNdBpGMv8` | [watch](https://www.youtube.com/watch?v=eOrNdBpGMv8) | [picture](https://img.youtube.com/vi/eOrNdBpGMv8/hqdefault.jpg) |
| 28 | Avengers: Endgame | 2019 | `TcMBFSGVi1c` | [watch](https://www.youtube.com/watch?v=TcMBFSGVi1c) | [picture](https://img.youtube.com/vi/TcMBFSGVi1c/hqdefault.jpg) |
| 29 | Black Panther | 2018 | `xjDjIWPwcPU` | [watch](https://www.youtube.com/watch?v=xjDjIWPwcPU) | [picture](https://img.youtube.com/vi/xjDjIWPwcPU/hqdefault.jpg) |
| 30 | Iron Man | 2008 | `8ugaeA-nMTc` | [watch](https://www.youtube.com/watch?v=8ugaeA-nMTc) | [picture](https://img.youtube.com/vi/8ugaeA-nMTc/hqdefault.jpg) |
| 31 | Guardians of the Galaxy | 2014 | `d96cjJhvlMA` | [watch](https://www.youtube.com/watch?v=d96cjJhvlMA) | [picture](https://img.youtube.com/vi/d96cjJhvlMA/hqdefault.jpg) |
| 32 | Logan | 2017 | `Div0iP65aZo` | [watch](https://www.youtube.com/watch?v=Div0iP65aZo) | [picture](https://img.youtube.com/vi/Div0iP65aZo/hqdefault.jpg) |
| 33 | Spider-Man: No Way Home | 2021 | `JfVOs4VSpmA` | [watch](https://www.youtube.com/watch?v=JfVOs4VSpmA) | [picture](https://img.youtube.com/vi/JfVOs4VSpmA/hqdefault.jpg) |
| 34 | Deadpool | 2016 | `ONHBaC-pfsk` | [watch](https://www.youtube.com/watch?v=ONHBaC-pfsk) | [picture](https://img.youtube.com/vi/ONHBaC-pfsk/hqdefault.jpg) |
| 35 | Wonder Woman | 2017 | `1Q8fG0TtVAY` | [watch](https://www.youtube.com/watch?v=1Q8fG0TtVAY) | [picture](https://img.youtube.com/vi/1Q8fG0TtVAY/hqdefault.jpg) |
| 36 | Captain America: Civil War | 2016 | `dKrVegVI0Us` | [watch](https://www.youtube.com/watch?v=dKrVegVI0Us) | [picture](https://img.youtube.com/vi/dKrVegVI0Us/hqdefault.jpg) |
| 37 | Toy Story | 1995 | `v-PjgYDrg70` | [watch](https://www.youtube.com/watch?v=v-PjgYDrg70) | [picture](https://img.youtube.com/vi/v-PjgYDrg70/hqdefault.jpg) |
| 38 | Finding Nemo | 2003 | `wZdpNglLbt8` | [watch](https://www.youtube.com/watch?v=wZdpNglLbt8) | [picture](https://img.youtube.com/vi/wZdpNglLbt8/hqdefault.jpg) |
| 39 | The Lion King | 1994 | `4sj1MT05lAA` | [watch](https://www.youtube.com/watch?v=4sj1MT05lAA) | [picture](https://img.youtube.com/vi/4sj1MT05lAA/hqdefault.jpg) |
| 40 | Shrek | 2001 | `CwXOrWvPBPk` | [watch](https://www.youtube.com/watch?v=CwXOrWvPBPk) | [picture](https://img.youtube.com/vi/CwXOrWvPBPk/hqdefault.jpg) |
| 41 | Frozen | 2013 | `TbQm5doF_Uc` | [watch](https://www.youtube.com/watch?v=TbQm5doF_Uc) | [picture](https://img.youtube.com/vi/TbQm5doF_Uc/hqdefault.jpg) |
| 42 | Coco | 2017 | `Rvr68u6k5sI` | [watch](https://www.youtube.com/watch?v=Rvr68u6k5sI) | [picture](https://img.youtube.com/vi/Rvr68u6k5sI/hqdefault.jpg) |
| 43 | Up | 2009 | `ORFWdXl_zJ4` | [watch](https://www.youtube.com/watch?v=ORFWdXl_zJ4) | [picture](https://img.youtube.com/vi/ORFWdXl_zJ4/hqdefault.jpg) |
| 44 | Spider-Man: Into the Spider-Verse | 2018 | `g4Hbz2jLxvQ` | [watch](https://www.youtube.com/watch?v=g4Hbz2jLxvQ) | [picture](https://img.youtube.com/vi/g4Hbz2jLxvQ/hqdefault.jpg) |
| 45 | Inside Out | 2015 | `yRUAzGQ3nSY` | [watch](https://www.youtube.com/watch?v=yRUAzGQ3nSY) | [picture](https://img.youtube.com/vi/yRUAzGQ3nSY/hqdefault.jpg) |
| 46 | Moana | 2016 | `LKFuXETZUsI` | [watch](https://www.youtube.com/watch?v=LKFuXETZUsI) | [picture](https://img.youtube.com/vi/LKFuXETZUsI/hqdefault.jpg) |
| 47 | Zootopia | 2016 | `jWM0ct-OLsM` | [watch](https://www.youtube.com/watch?v=jWM0ct-OLsM) | [picture](https://img.youtube.com/vi/jWM0ct-OLsM/hqdefault.jpg) |
| 48 | The Incredibles | 2004 | `-UaGUdNJdRQ` | [watch](https://www.youtube.com/watch?v=-UaGUdNJdRQ) | [picture](https://img.youtube.com/vi/-UaGUdNJdRQ/hqdefault.jpg) |
| 49 | Get Out | 2017 | `DzfpyUB60YY` | [watch](https://www.youtube.com/watch?v=DzfpyUB60YY) | [picture](https://img.youtube.com/vi/DzfpyUB60YY/hqdefault.jpg) |
| 50 | The Conjuring | 2013 | `k10ETZ41q5o` | [watch](https://www.youtube.com/watch?v=k10ETZ41q5o) | [picture](https://img.youtube.com/vi/k10ETZ41q5o/hqdefault.jpg) |
| 51 | A Quiet Place | 2018 | `WR7cc5t7tv8` | [watch](https://www.youtube.com/watch?v=WR7cc5t7tv8) | [picture](https://img.youtube.com/vi/WR7cc5t7tv8/hqdefault.jpg) |
| 52 | Hereditary | 2018 | `V6wWKNij_1M` | [watch](https://www.youtube.com/watch?v=V6wWKNij_1M) | [picture](https://img.youtube.com/vi/V6wWKNij_1M/hqdefault.jpg) |
| 53 | It | 2017 | `xKJmEC5ieOk` | [watch](https://www.youtube.com/watch?v=xKJmEC5ieOk) | [picture](https://img.youtube.com/vi/xKJmEC5ieOk/hqdefault.jpg) |
| 54 | Insidious | 2010 | `zuZnRUcoWos` | [watch](https://www.youtube.com/watch?v=zuZnRUcoWos) | [picture](https://img.youtube.com/vi/zuZnRUcoWos/hqdefault.jpg) |
| 55 | The Shining | 1980 | `S014oGZiSdI` | [watch](https://www.youtube.com/watch?v=S014oGZiSdI) | [picture](https://img.youtube.com/vi/S014oGZiSdI/hqdefault.jpg) |
| 56 | Midsommar | 2019 | `1Vnghdsjmd0` | [watch](https://www.youtube.com/watch?v=1Vnghdsjmd0) | [picture](https://img.youtube.com/vi/1Vnghdsjmd0/hqdefault.jpg) |
| 57 | The Exorcist | 1973 | `YDGw1MTEe9k` | [watch](https://www.youtube.com/watch?v=YDGw1MTEe9k) | [picture](https://img.youtube.com/vi/YDGw1MTEe9k/hqdefault.jpg) |
| 58 | The Babadook | 2014 | `k5WQZzDRVtw` | [watch](https://www.youtube.com/watch?v=k5WQZzDRVtw) | [picture](https://img.youtube.com/vi/k5WQZzDRVtw/hqdefault.jpg) |
| 59 | Smile | 2022 | `BcDK7lkzzsU` | [watch](https://www.youtube.com/watch?v=BcDK7lkzzsU) | [picture](https://img.youtube.com/vi/BcDK7lkzzsU/hqdefault.jpg) |
| 60 | Nope | 2022 | `In8fuzj3gck` | [watch](https://www.youtube.com/watch?v=In8fuzj3gck) | [picture](https://img.youtube.com/vi/In8fuzj3gck/hqdefault.jpg) |
| 61 | Titanic | 1997 | `kVrqfYjkTdQ` | [watch](https://www.youtube.com/watch?v=kVrqfYjkTdQ) | [picture](https://img.youtube.com/vi/kVrqfYjkTdQ/hqdefault.jpg) |
| 62 | La La Land | 2016 | `0pdqf4P9MB8` | [watch](https://www.youtube.com/watch?v=0pdqf4P9MB8) | [picture](https://img.youtube.com/vi/0pdqf4P9MB8/hqdefault.jpg) |
| 63 | The Notebook | 2004 | `yDJIcYE32NU` | [watch](https://www.youtube.com/watch?v=yDJIcYE32NU) | [picture](https://img.youtube.com/vi/yDJIcYE32NU/hqdefault.jpg) |
| 64 | Forrest Gump | 1994 | `bLvqoHBptjg` | [watch](https://www.youtube.com/watch?v=bLvqoHBptjg) | [picture](https://img.youtube.com/vi/bLvqoHBptjg/hqdefault.jpg) |
| 65 | The Shawshank Redemption | 1994 | `NmzuHjWmXOc` | [watch](https://www.youtube.com/watch?v=NmzuHjWmXOc) | [picture](https://img.youtube.com/vi/NmzuHjWmXOc/hqdefault.jpg) |
| 66 | Parasite | 2019 | `5xH0HfJHsaY` | [watch](https://www.youtube.com/watch?v=5xH0HfJHsaY) | [picture](https://img.youtube.com/vi/5xH0HfJHsaY/hqdefault.jpg) |
| 67 | Whiplash | 2014 | `7d_jQycdQGo` | [watch](https://www.youtube.com/watch?v=7d_jQycdQGo) | [picture](https://img.youtube.com/vi/7d_jQycdQGo/hqdefault.jpg) |
| 68 | Joker | 2019 | `zAGVQLHvwOY` | [watch](https://www.youtube.com/watch?v=zAGVQLHvwOY) | [picture](https://img.youtube.com/vi/zAGVQLHvwOY/hqdefault.jpg) |
| 69 | Fight Club | 1999 | `SUXWAaZUaOQ` | [watch](https://www.youtube.com/watch?v=SUXWAaZUaOQ) | [picture](https://img.youtube.com/vi/SUXWAaZUaOQ/hqdefault.jpg) |
| 70 | The Godfather | 1972 | `UaVTIH8mujA` | [watch](https://www.youtube.com/watch?v=UaVTIH8mujA) | [picture](https://img.youtube.com/vi/UaVTIH8mujA/hqdefault.jpg) |
| 71 | Good Will Hunting | 1997 | `PaZVjZEFkRs` | [watch](https://www.youtube.com/watch?v=PaZVjZEFkRs) | [picture](https://img.youtube.com/vi/PaZVjZEFkRs/hqdefault.jpg) |
| 72 | The Green Mile | 1999 | `Ki4haFrqSrw` | [watch](https://www.youtube.com/watch?v=Ki4haFrqSrw) | [picture](https://img.youtube.com/vi/Ki4haFrqSrw/hqdefault.jpg) |
| 73 | The Hangover | 2009 | `tcdUhdOlz9M` | [watch](https://www.youtube.com/watch?v=tcdUhdOlz9M) | [picture](https://img.youtube.com/vi/tcdUhdOlz9M/hqdefault.jpg) |
| 74 | Superbad | 2007 | `4eaZ_48ZYog` | [watch](https://www.youtube.com/watch?v=4eaZ_48ZYog) | [picture](https://img.youtube.com/vi/4eaZ_48ZYog/hqdefault.jpg) |
| 75 | Dumb and Dumber | 1994 | `l13yPhimE3o` | [watch](https://www.youtube.com/watch?v=l13yPhimE3o) | [picture](https://img.youtube.com/vi/l13yPhimE3o/hqdefault.jpg) |
| 76 | Home Alone | 1990 | `jEDaVHmw7r4` | [watch](https://www.youtube.com/watch?v=jEDaVHmw7r4) | [picture](https://img.youtube.com/vi/jEDaVHmw7r4/hqdefault.jpg) |
| 77 | Mean Girls | 2004 | `oDU84nmSDZY` | [watch](https://www.youtube.com/watch?v=oDU84nmSDZY) | [picture](https://img.youtube.com/vi/oDU84nmSDZY/hqdefault.jpg) |
| 78 | Jumanji: Welcome to the Jungle | 2017 | `rBxcF-r9Ibs` | [watch](https://www.youtube.com/watch?v=rBxcF-r9Ibs) | [picture](https://img.youtube.com/vi/rBxcF-r9Ibs/hqdefault.jpg) |
| 79 | The Grand Budapest Hotel | 2014 | `1Fg5iWmQjwk` | [watch](https://www.youtube.com/watch?v=1Fg5iWmQjwk) | [picture](https://img.youtube.com/vi/1Fg5iWmQjwk/hqdefault.jpg) |
| 80 | Barbie | 2023 | `pBk4NYhWNMM` | [watch](https://www.youtube.com/watch?v=pBk4NYhWNMM) | [picture](https://img.youtube.com/vi/pBk4NYhWNMM/hqdefault.jpg) |
| 81 | Free Guy | 2021 | `X2m-08cOAbc` | [watch](https://www.youtube.com/watch?v=X2m-08cOAbc) | [picture](https://img.youtube.com/vi/X2m-08cOAbc/hqdefault.jpg) |
| 82 | Everything Everywhere All at Once | 2022 | `wxN1T1uxQ2g` | [watch](https://www.youtube.com/watch?v=wxN1T1uxQ2g) | [picture](https://img.youtube.com/vi/wxN1T1uxQ2g/hqdefault.jpg) |
| 83 | Jojo Rabbit | 2019 | `tL4McUzXfFI` | [watch](https://www.youtube.com/watch?v=tL4McUzXfFI) | [picture](https://img.youtube.com/vi/tL4McUzXfFI/hqdefault.jpg) |
| 84 | Zombieland | 2009 | `8m9EVP8X7N8` | [watch](https://www.youtube.com/watch?v=8m9EVP8X7N8) | [picture](https://img.youtube.com/vi/8m9EVP8X7N8/hqdefault.jpg) |
| 85 | Se7en | 1995 | `znmZoVkCjpI` | [watch](https://www.youtube.com/watch?v=znmZoVkCjpI) | [picture](https://img.youtube.com/vi/znmZoVkCjpI/hqdefault.jpg) |
| 86 | The Silence of the Lambs | 1991 | `RuX2MQeb8UM` | [watch](https://www.youtube.com/watch?v=RuX2MQeb8UM) | [picture](https://img.youtube.com/vi/RuX2MQeb8UM/hqdefault.jpg) |
| 87 | Gone Girl | 2014 | `2-_-1nJf8Vg` | [watch](https://www.youtube.com/watch?v=2-_-1nJf8Vg) | [picture](https://img.youtube.com/vi/2-_-1nJf8Vg/hqdefault.jpg) |
| 88 | Prisoners | 2013 | `bpXfcTF6iVk` | [watch](https://www.youtube.com/watch?v=bpXfcTF6iVk) | [picture](https://img.youtube.com/vi/bpXfcTF6iVk/hqdefault.jpg) |
| 89 | Shutter Island | 2010 | `5iaYLCiq5RM` | [watch](https://www.youtube.com/watch?v=5iaYLCiq5RM) | [picture](https://img.youtube.com/vi/5iaYLCiq5RM/hqdefault.jpg) |
| 90 | Pulp Fiction | 1994 | `s7EdQ4FqbhY` | [watch](https://www.youtube.com/watch?v=s7EdQ4FqbhY) | [picture](https://img.youtube.com/vi/s7EdQ4FqbhY/hqdefault.jpg) |
| 91 | The Departed | 2006 | `auYbpnEwBBg` | [watch](https://www.youtube.com/watch?v=auYbpnEwBBg) | [picture](https://img.youtube.com/vi/auYbpnEwBBg/hqdefault.jpg) |
| 92 | Zodiac | 2007 | `yNncHPl1UXg` | [watch](https://www.youtube.com/watch?v=yNncHPl1UXg) | [picture](https://img.youtube.com/vi/yNncHPl1UXg/hqdefault.jpg) |
| 93 | The Usual Suspects | 1995 | `oiXdPolca5w` | [watch](https://www.youtube.com/watch?v=oiXdPolca5w) | [picture](https://img.youtube.com/vi/oiXdPolca5w/hqdefault.jpg) |
| 94 | Knives Out | 2019 | `qGqiHJTsRkQ` | [watch](https://www.youtube.com/watch?v=qGqiHJTsRkQ) | [picture](https://img.youtube.com/vi/qGqiHJTsRkQ/hqdefault.jpg) |
| 95 | No Country for Old Men | 2007 | `38A__WetWVs` | [watch](https://www.youtube.com/watch?v=38A__WetWVs) | [picture](https://img.youtube.com/vi/38A__WetWVs/hqdefault.jpg) |
| 96 | Goodfellas | 1990 | `2ilzidi_J8Q` | [watch](https://www.youtube.com/watch?v=2ilzidi_J8Q) | [picture](https://img.youtube.com/vi/2ilzidi_J8Q/hqdefault.jpg) |
| 97 | The Lord of the Rings: The Fellowship of the Ring | 2001 | `V75dMMIW2B4` | [watch](https://www.youtube.com/watch?v=V75dMMIW2B4) | [picture](https://img.youtube.com/vi/V75dMMIW2B4/hqdefault.jpg) |
| 98 | The Lord of the Rings: The Two Towers | 2002 | `LbfMDwc4azU` | [watch](https://www.youtube.com/watch?v=LbfMDwc4azU) | [picture](https://img.youtube.com/vi/LbfMDwc4azU/hqdefault.jpg) |
| 99 | The Lord of the Rings: The Return of the King | 2003 | `r5X-hFf6Bwo` | [watch](https://www.youtube.com/watch?v=r5X-hFf6Bwo) | [picture](https://img.youtube.com/vi/r5X-hFf6Bwo/hqdefault.jpg) |
| 100 | The Hobbit: An Unexpected Journey | 2012 | `SDnYMbYB-nU` | [watch](https://www.youtube.com/watch?v=SDnYMbYB-nU) | [picture](https://img.youtube.com/vi/SDnYMbYB-nU/hqdefault.jpg) |
| 101 | Harry Potter and the Sorcerer's Stone | 2001 | `VyHV0BRtdxo` | [watch](https://www.youtube.com/watch?v=VyHV0BRtdxo) | [picture](https://img.youtube.com/vi/VyHV0BRtdxo/hqdefault.jpg) |
| 102 | Pirates of the Caribbean: The Curse of the Black Pearl | 2003 | `naQr0uTrH_s` | [watch](https://www.youtube.com/watch?v=naQr0uTrH_s) | [picture](https://img.youtube.com/vi/naQr0uTrH_s/hqdefault.jpg) |
| 103 | Jurassic Park | 1993 | `lc0UehYemQA` | [watch](https://www.youtube.com/watch?v=lc0UehYemQA) | [picture](https://img.youtube.com/vi/lc0UehYemQA/hqdefault.jpg) |
| 104 | Raiders of the Lost Ark | 1981 | `XkkzKHCx154` | [watch](https://www.youtube.com/watch?v=XkkzKHCx154) | [picture](https://img.youtube.com/vi/XkkzKHCx154/hqdefault.jpg) |
| 105 | Avatar | 2009 | `5PSNL1qE6VY` | [watch](https://www.youtube.com/watch?v=5PSNL1qE6VY) | [picture](https://img.youtube.com/vi/5PSNL1qE6VY/hqdefault.jpg) |
| 106 | Aladdin (2019) | 2019 | `foyufD52aog` | [watch](https://www.youtube.com/watch?v=foyufD52aog) | [picture](https://img.youtube.com/vi/foyufD52aog/hqdefault.jpg) |
| 107 | The Lion King (2019) | 2019 | `7TavVZMewpY` | [watch](https://www.youtube.com/watch?v=7TavVZMewpY) | [picture](https://img.youtube.com/vi/7TavVZMewpY/hqdefault.jpg) |
| 108 | Doctor Strange | 2016 | `HSzx-zryEgM` | [watch](https://www.youtube.com/watch?v=HSzx-zryEgM) | [picture](https://img.youtube.com/vi/HSzx-zryEgM/hqdefault.jpg) |
| 109 | Saving Private Ryan | 1998 | `9CiW_DgxCnQ` | [watch](https://www.youtube.com/watch?v=9CiW_DgxCnQ) | [picture](https://img.youtube.com/vi/9CiW_DgxCnQ/hqdefault.jpg) |
| 110 | 1917 | 2019 | `YqNYrYUiMfg` | [watch](https://www.youtube.com/watch?v=YqNYrYUiMfg) | [picture](https://img.youtube.com/vi/YqNYrYUiMfg/hqdefault.jpg) |
| 111 | Dunkirk | 2017 | `F-eMt3SrfFU` | [watch](https://www.youtube.com/watch?v=F-eMt3SrfFU) | [picture](https://img.youtube.com/vi/F-eMt3SrfFU/hqdefault.jpg) |
| 112 | Hacksaw Ridge | 2016 | `s2-1hz1juBI` | [watch](https://www.youtube.com/watch?v=s2-1hz1juBI) | [picture](https://img.youtube.com/vi/s2-1hz1juBI/hqdefault.jpg) |
| 113 | 300 | 2006 | `UrIbxk7idYA` | [watch](https://www.youtube.com/watch?v=UrIbxk7idYA) | [picture](https://img.youtube.com/vi/UrIbxk7idYA/hqdefault.jpg) |
| 114 | Fury | 2014 | `q6EoRBvdVPQ` | [watch](https://www.youtube.com/watch?v=q6EoRBvdVPQ) | [picture](https://img.youtube.com/vi/q6EoRBvdVPQ/hqdefault.jpg) |
| 115 | Schindler's List | 1993 | `gG22XNhtnoY` | [watch](https://www.youtube.com/watch?v=gG22XNhtnoY) | [picture](https://img.youtube.com/vi/gG22XNhtnoY/hqdefault.jpg) |
| 116 | The Pianist | 2002 | `BFwGqLa_oAo` | [watch](https://www.youtube.com/watch?v=BFwGqLa_oAo) | [picture](https://img.youtube.com/vi/BFwGqLa_oAo/hqdefault.jpg) |
| 117 | Apocalypse Now | 1979 | `FTjG-Aux_yQ` | [watch](https://www.youtube.com/watch?v=FTjG-Aux_yQ) | [picture](https://img.youtube.com/vi/FTjG-Aux_yQ/hqdefault.jpg) |
| 118 | Braveheart | 1995 | `1cnvXdkhWz4` | [watch](https://www.youtube.com/watch?v=1cnvXdkhWz4) | [picture](https://img.youtube.com/vi/1cnvXdkhWz4/hqdefault.jpg) |
| 119 | Inglourious Basterds | 2009 | `KnrRy6kSFF0` | [watch](https://www.youtube.com/watch?v=KnrRy6kSFF0) | [picture](https://img.youtube.com/vi/KnrRy6kSFF0/hqdefault.jpg) |
| 120 | Oppenheimer | 2023 | `uYPbbksJxIg` | [watch](https://www.youtube.com/watch?v=uYPbbksJxIg) | [picture](https://img.youtube.com/vi/uYPbbksJxIg/hqdefault.jpg) |
| 121 | Kill Bill: Vol. 1 | 2003 | `7kSuas6mRpk` | [watch](https://www.youtube.com/watch?v=7kSuas6mRpk) | [picture](https://img.youtube.com/vi/7kSuas6mRpk/hqdefault.jpg) |
| 122 | Kingsman: The Secret Service | 2014 | `kl8F-8tR8to` | [watch](https://www.youtube.com/watch?v=kl8F-8tR8to) | [picture](https://img.youtube.com/vi/kl8F-8tR8to/hqdefault.jpg) |
| 123 | Gravity | 2013 | `OiTiKOy59o4` | [watch](https://www.youtube.com/watch?v=OiTiKOy59o4) | [picture](https://img.youtube.com/vi/OiTiKOy59o4/hqdefault.jpg) |
| 124 | Alien | 1979 | `LjLamj-b0I8` | [watch](https://www.youtube.com/watch?v=LjLamj-b0I8) | [picture](https://img.youtube.com/vi/LjLamj-b0I8/hqdefault.jpg) |
| 125 | The Batman | 2022 | `mqqft2x_Aa4` | [watch](https://www.youtube.com/watch?v=mqqft2x_Aa4) | [picture](https://img.youtube.com/vi/mqqft2x_Aa4/hqdefault.jpg) |
| 126 | Man of Steel | 2013 | `T6DJcgm3wNY` | [watch](https://www.youtube.com/watch?v=T6DJcgm3wNY) | [picture](https://img.youtube.com/vi/T6DJcgm3wNY/hqdefault.jpg) |
| 127 | Batman Begins | 2005 | `neY2xVmOfUM` | [watch](https://www.youtube.com/watch?v=neY2xVmOfUM) | [picture](https://img.youtube.com/vi/neY2xVmOfUM/hqdefault.jpg) |
| 128 | Spirited Away | 2001 | `ByXuk9QqQkk` | [watch](https://www.youtube.com/watch?v=ByXuk9QqQkk) | [picture](https://img.youtube.com/vi/ByXuk9QqQkk/hqdefault.jpg) |
| 129 | Kung Fu Panda | 2008 | `PXi3Mv6KMzY` | [watch](https://www.youtube.com/watch?v=PXi3Mv6KMzY) | [picture](https://img.youtube.com/vi/PXi3Mv6KMzY/hqdefault.jpg) |
| 130 | Ratatouille | 2007 | `NgsQ8mVkN8w` | [watch](https://www.youtube.com/watch?v=NgsQ8mVkN8w) | [picture](https://img.youtube.com/vi/NgsQ8mVkN8w/hqdefault.jpg) |
| 131 | WALL-E | 2008 | `CZ1CATNbXg0` | [watch](https://www.youtube.com/watch?v=CZ1CATNbXg0) | [picture](https://img.youtube.com/vi/CZ1CATNbXg0/hqdefault.jpg) |
| 132 | Jaws | 1975 | `U1fu_sA7XhE` | [watch](https://www.youtube.com/watch?v=U1fu_sA7XhE) | [picture](https://img.youtube.com/vi/U1fu_sA7XhE/hqdefault.jpg) |
| 133 | Annabelle | 2014 | `paFgQNPGlsg` | [watch](https://www.youtube.com/watch?v=paFgQNPGlsg) | [picture](https://img.youtube.com/vi/paFgQNPGlsg/hqdefault.jpg) |
| 134 | The Nun | 2018 | `pzD9zGcUNrw` | [watch](https://www.youtube.com/watch?v=pzD9zGcUNrw) | [picture](https://img.youtube.com/vi/pzD9zGcUNrw/hqdefault.jpg) |
| 135 | The Prestige | 2006 | `o4gHCmTQDVI` | [watch](https://www.youtube.com/watch?v=o4gHCmTQDVI) | [picture](https://img.youtube.com/vi/o4gHCmTQDVI/hqdefault.jpg) |
| 136 | 12 Years a Slave | 2013 | `z02Ie8wKKRg` | [watch](https://www.youtube.com/watch?v=z02Ie8wKKRg) | [picture](https://img.youtube.com/vi/z02Ie8wKKRg/hqdefault.jpg) |
| 137 | Moonlight | 2016 | `9NJj12tJzqc` | [watch](https://www.youtube.com/watch?v=9NJj12tJzqc) | [picture](https://img.youtube.com/vi/9NJj12tJzqc/hqdefault.jpg) |
| 138 | Birdman | 2014 | `uJfLoE6hanc` | [watch](https://www.youtube.com/watch?v=uJfLoE6hanc) | [picture](https://img.youtube.com/vi/uJfLoE6hanc/hqdefault.jpg) |
| 139 | The Pursuit of Happyness | 2006 | `89Kq8SDyvfg` | [watch](https://www.youtube.com/watch?v=89Kq8SDyvfg) | [picture](https://img.youtube.com/vi/89Kq8SDyvfg/hqdefault.jpg) |
| 140 | A Beautiful Mind | 2001 | `aS_d0Ayjw4o` | [watch](https://www.youtube.com/watch?v=aS_d0Ayjw4o) | [picture](https://img.youtube.com/vi/aS_d0Ayjw4o/hqdefault.jpg) |
| 141 | Dead Poets Society | 1989 | `LjHORRHXtyI` | [watch](https://www.youtube.com/watch?v=LjHORRHXtyI) | [picture](https://img.youtube.com/vi/LjHORRHXtyI/hqdefault.jpg) |
| 142 | The Social Network | 2010 | `lB95KLmpLR4` | [watch](https://www.youtube.com/watch?v=lB95KLmpLR4) | [picture](https://img.youtube.com/vi/lB95KLmpLR4/hqdefault.jpg) |
| 143 | The Fault in Our Stars | 2014 | `9ItBvH5J6ss` | [watch](https://www.youtube.com/watch?v=9ItBvH5J6ss) | [picture](https://img.youtube.com/vi/9ItBvH5J6ss/hqdefault.jpg) |
| 144 | A Star Is Born | 2018 | `nSbzyEJ8X9E` | [watch](https://www.youtube.com/watch?v=nSbzyEJ8X9E) | [picture](https://img.youtube.com/vi/nSbzyEJ8X9E/hqdefault.jpg) |
| 145 | About Time | 2013 | `T7A810duHvw` | [watch](https://www.youtube.com/watch?v=T7A810duHvw) | [picture](https://img.youtube.com/vi/T7A810duHvw/hqdefault.jpg) |
| 146 | 500 Days of Summer | 2009 | `PsD0NpFSADM` | [watch](https://www.youtube.com/watch?v=PsD0NpFSADM) | [picture](https://img.youtube.com/vi/PsD0NpFSADM/hqdefault.jpg) |
| 147 | Call Me by Your Name | 2017 | `Z9AYPxH5NTM` | [watch](https://www.youtube.com/watch?v=Z9AYPxH5NTM) | [picture](https://img.youtube.com/vi/Z9AYPxH5NTM/hqdefault.jpg) |
| 148 | Past Lives | 2023 | `kA244xewjcI` | [watch](https://www.youtube.com/watch?v=kA244xewjcI) | [picture](https://img.youtube.com/vi/kA244xewjcI/hqdefault.jpg) |
| 149 | Casablanca | 1942 | `BkL9l7qovsE` | [watch](https://www.youtube.com/watch?v=BkL9l7qovsE) | [picture](https://img.youtube.com/vi/BkL9l7qovsE/hqdefault.jpg) |
| 150 | Eternal Sunshine of the Spotless Mind | 2004 | `07-QBnEkgXU` | [watch](https://www.youtube.com/watch?v=07-QBnEkgXU) | [picture](https://img.youtube.com/vi/07-QBnEkgXU/hqdefault.jpg) |
| 151 | Her | 2013 | `WzV6mXIOVl4` | [watch](https://www.youtube.com/watch?v=WzV6mXIOVl4) | [picture](https://img.youtube.com/vi/WzV6mXIOVl4/hqdefault.jpg) |
| 152 | The Shape of Water | 2017 | `XFYWazblaUA` | [watch](https://www.youtube.com/watch?v=XFYWazblaUA) | [picture](https://img.youtube.com/vi/XFYWazblaUA/hqdefault.jpg) |
| 153 | Little Women | 2019 | `AST2-4db4ic` | [watch](https://www.youtube.com/watch?v=AST2-4db4ic) | [picture](https://img.youtube.com/vi/AST2-4db4ic/hqdefault.jpg) |
| 154 | Pride & Prejudice | 2005 | `1dYv5u6w_kA` | [watch](https://www.youtube.com/watch?v=1dYv5u6w_kA) | [picture](https://img.youtube.com/vi/1dYv5u6w_kA/hqdefault.jpg) |
| 155 | Beauty and the Beast (2017) | 2017 | `e3Czma1ou1M` | [watch](https://www.youtube.com/watch?v=e3Czma1ou1M) | [picture](https://img.youtube.com/vi/e3Czma1ou1M/hqdefault.jpg) |
| 156 | Bohemian Rhapsody | 2018 | `mYfJxlgR2jw` | [watch](https://www.youtube.com/watch?v=mYfJxlgR2jw) | [picture](https://img.youtube.com/vi/mYfJxlgR2jw/hqdefault.jpg) |
| 157 | The Greatest Showman | 2017 | `AXCTMLTA0wQ` | [watch](https://www.youtube.com/watch?v=AXCTMLTA0wQ) | [picture](https://img.youtube.com/vi/AXCTMLTA0wQ/hqdefault.jpg) |
| 158 | Encanto | 2021 | `CaimKeDcudo` | [watch](https://www.youtube.com/watch?v=CaimKeDcudo) | [picture](https://img.youtube.com/vi/CaimKeDcudo/hqdefault.jpg) |
| 159 | Soul | 2020 | `xOsLIiBStEs` | [watch](https://www.youtube.com/watch?v=xOsLIiBStEs) | [picture](https://img.youtube.com/vi/xOsLIiBStEs/hqdefault.jpg) |
| 160 | The Revenant | 2015 | `LoebZZ8K5N0` | [watch](https://www.youtube.com/watch?v=LoebZZ8K5N0) | [picture](https://img.youtube.com/vi/LoebZZ8K5N0/hqdefault.jpg) |
| 161 | The Imitation Game | 2014 | `S5CjKEFb-sM` | [watch](https://www.youtube.com/watch?v=S5CjKEFb-sM) | [picture](https://img.youtube.com/vi/S5CjKEFb-sM/hqdefault.jpg) |
| 162 | American Sniper | 2014 | `99k3u0m2Zmo` | [watch](https://www.youtube.com/watch?v=99k3u0m2Zmo) | [picture](https://img.youtube.com/vi/99k3u0m2Zmo/hqdefault.jpg) |
| 163 | The Wolf of Wall Street | 2013 | `iszwuX1AK6A` | [watch](https://www.youtube.com/watch?v=iszwuX1AK6A) | [picture](https://img.youtube.com/vi/iszwuX1AK6A/hqdefault.jpg) |
| 164 | Ford v Ferrari | 2019 | `zyYgDtY2AMY` | [watch](https://www.youtube.com/watch?v=zyYgDtY2AMY) | [picture](https://img.youtube.com/vi/zyYgDtY2AMY/hqdefault.jpg) |
| 165 | Green Book | 2018 | `QkZxoko_HC0` | [watch](https://www.youtube.com/watch?v=QkZxoko_HC0) | [picture](https://img.youtube.com/vi/QkZxoko_HC0/hqdefault.jpg) |

> The codes were written from memory, so some are wrong. **The app repairs dead ones by itself** (`net/TrailerResolver`): it finds a working official trailer, shows its picture and remembers the fix in `~/.streamverse/trailer_fixes.txt`. The footer button **Repair trailers** checks all of them at once. Also available from a terminal: Run `python tools/verify_trailers.py` to check them and `python tools/fix_trailers.py` to repair any broken one.
