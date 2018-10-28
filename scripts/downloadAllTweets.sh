#!/bin/bash

# Before run the script you must register
# twurl authorize --consumer-key xxxxxxx --consumer-secret yyyyyyyyyyyy

source properties.sh

now=$(date +"%d_%m_%Y")
filename=tweets.$now
base_dir=$scriptdata/output
files=( "centro" "Arganzuela" "Retiro" "Salamanca" "Chamartin" "Tetuan" "Chamberi" "Fuencarral" "Moncloa" "Latina" "Carabanchel" "Usera" "PuenteVallecas" "Moratalaz" "CiudadLineal" "Hortaleza" "Villaverde" "VillaVallecas" "Vicalvaro" "Canillejas" "Barajas" ) 

for i in "${files[@]}"
do
dir=$base_dir/$i
if [ ! -d $dir ]; then
    mkdir $dir
fi
done

./downloadTweets.sh Distrito%20Centro%20Madrid "$base_dir/centro/$filename"
./downloadTweets.sh Arganzuela%20Madrid "$base_dir/Arganzuela/$filename"
./downloadTweets.sh Barrio%20Retiro%20Madrid "$base_dir/Retiro/$filename"
./downloadTweets.sh Barrio%20Salamanca%20Madrid "$base_dir/Salamanca/$filename"
./downloadTweets.sh Chamartin%20Madrid "$base_dir/Chamartin/$filename"
./downloadTweets.sh Tetuan%20Madrid "$base_dir/Tetuan/$filename"
./downloadTweets.sh Chamberi%20Madrid "$base_dir/Chamberi/$filename"
./downloadTweets.sh Fuencarral%20Madrid "$base_dir/Fuencarral/$filename"
./downloadTweets.sh Moncloa%20Madrid "$base_dir/Moncloa/$filename"
./downloadTweets.sh Latina%20Madrid "$base_dir/Latina/$filename"
./downloadTweets.sh Carabanchel%20Madrid "$base_dir/Carabanchel/$filename"
./downloadTweets.sh Usera%20Madrid "$base_dir/Usera/$filename"
./downloadTweets.sh Puente%20Vallecas%20Madrid "$base_dir/PuenteVallecas/$filename"
./downloadTweets.sh Moratalaz%20Madrid "$base_dir/Moratalaz/$filename"
./downloadTweets.sh Ciudad%20Lineal%20Madrid "$base_dir/CiudadLineal/$filename"
./downloadTweets.sh Hortaleza%20Madrid "$base_dir/Hortaleza/$filename"
./downloadTweets.sh Villaverde%20Madrid "$base_dir/Villaverde/$filename"
./downloadTweets.sh Villa%20Vallecas%20Madrid "$base_dir/VillaVallecas/$filename"
./downloadTweets.sh Vicalvaro%20Madrid "$base_dir/Vicalvaro/$filename"
./downloadTweets.sh Canillejas%20Madrid "$base_dir/Canillejas/$filename"
./downloadTweets.sh Barajas%20Madrid "$base_dir/Barajas/$filename"

