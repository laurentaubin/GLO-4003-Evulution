#!/bin/bash

heroku git:remote -a ev-ul-tion

git push -f heroku main
