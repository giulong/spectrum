#!/bin/bash
set -e

npm i -g appium
appium driver install uiautomator2
appium &>/dev/null &