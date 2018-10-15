#!/bin/sh

# Before run the script you must register
# twurl authorize --consumer-key xxxxxxx --consumer-secret yyyyyyyyyyyy

now=$(date +"%m_%d_%Y")
./downloadTweets.sh Distrito%20Centro%20Madrid "output/tweetsCentro.$now"
./downloadTweets.sh Arganzuela%20Madrid "output/tweetsArganzuela.$now"
./downloadTweets.sh Barrio%20Retiro%20Madrid "output/tweetsRetiro.$now"
./downloadTweets.sh Barrio%20Salamanca%20Madrid "output/tweetsSalamanca.$now"
./downloadTweets.sh Chamartín%20Madrid "output/tweetsChamartín.$now"
./downloadTweets.sh Tetuán%20Madrid "output/tweetsTetuán.$now"
./downloadTweets.sh Chamberí%20Madrid "output/tweetsChamberí.$now"
./downloadTweets.sh Fuencarral%20Madrid "output/tweetsFuencarral.$now"
./downloadTweets.sh Moncloa%20Madrid "output/tweetsMoncloa.$now"
./downloadTweets.sh Latina%20Madrid "output/tweetsLatina.$now"
./downloadTweets.sh Carabanchel%20Madrid "output/tweetsCarabanchel.$now"
./downloadTweets.sh Usera%20Madrid "output/tweetsUsera.$now"
./downloadTweets.sh Puente%20Vallecas%20Madrid "output/tweetsPuenteVallecas.$now"
./downloadTweets.sh Moratalaz%20Madrid "output/tweetsMoratalaz.$now"
./downloadTweets.sh Ciudad%20Lineal%20Madrid "output/tweetsCiudadLineal.$now"
./downloadTweets.sh Hortaleza%20Madrid "output/tweetsHortaleza.$now"
./downloadTweets.sh Villaverde%20Madrid "output/tweetsVillaverde.$now"
./downloadTweets.sh Villa%20Vallecas%20Madrid "output/tweetsVillaVallecas.$now"
./downloadTweets.sh Vicálvaro%20Madrid "output/tweetsVicálvaro.$now"
./downloadTweets.sh Canillejas%20Madrid "output/tweetsCanillejas.$now"
./downloadTweets.sh Barajas%20Madrid "output/tweetsBarajas.$now"

