#!/bin/bash

BRANCH=`git branch --show-current`

heroku git:remote -a ev-ul-tion

git push heroku ${BRANCH}:main
