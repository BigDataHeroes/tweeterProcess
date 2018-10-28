#!/bin/sh

# 1 -> search string
# 2 -> output file

twurl "/1.1/search/tweets.json?q=$1&count=100&src=typd">"file.tmp"

python processtweets.py "file.tmp" $2
