# Virtual Sales Gong

An Android application which makes it very easy to implement a sales gong, ie. a sound is played when a new deal is won in the company.

Download the app from Play market: [Google Play](https://play.google.com/store/apps/details?id=zyzzyx.salesgong)

* Very easy to take into use
* Serverless
* Works with several CRMs, instructions for Pipedrive provided below
* Easy to customize the "gong" sound

## How to make the gong work with Pipedrive

In addition to Pipedrive, you need [Zapier](https://www.zapier.com) and [Pusher](https://www.pusher.com) accounts. Both are very easy to set up and free plans should be suitable for most uses. Zapier is used to inform Pusher whenever a deal is won in Pipedrive. The Android app in turn gets information of this new won deal via Pusher.

### 1) Create a new filter in Pipedrive to show won deals

![Filter setup](https://www.dropbox.com/s/3bfumiz6hp02gnp/pipedrive_filter.png?raw=1)

### 2) Create a new Channels App in Pusher:

* Create new app
* Name of your app: Sales gong (or whatever you like)
* Select cluster: EU
* Don't select "Create apps for multiple environments?"
* Ignore "Choose your tech stack."
* "Tell us about your app" - "Sales gong"
* Click "Create My App"
* Go to "App Keys" tab to view the __key__, which you will need later

### 3) Create a new Zap in Zapier:

* Trigger app = Pipedrive
* Click "show less common options"
* Select "Deal Matching Filter - Triggers when a deal matches a Pipedrive filter."
* Connect to your Pipedrive account
* Select the filter you created in step 1
* Action app = Pusher
* "Publish Pusher Event"
* Connect to your Pusher app
* Channels = sales-gong
* Event Name = sales-event
* Event Data: if you want to use custom gong sound, you have to store the mp3 file into a public server and give its url here. Type mp3-url to the first field (key) and URL to the second field (value). If you omit the URL then the default cheering sound is played. If you don't want to use custom sound, and don't want Zapier to send the whole deal payload to the Sales gong app, input anything to those fields (e.g. foo / bar).

### 4) Configure Android App

* Click menu->Settings
* Select "Pusher API key" and input the Pusher key
* Click back button so that you get to the main screen

All set up!

## Tips

* You can have as many Sales Gongs as you like in your offices
* If you have several gongs and want some of them play a different sound: create new Zap with different sound and Pusher channel for some of the gongs
* You can also have the gong play when you lose a deal. Just create a new filter for lost deals in Pipedrive, a new zap which points to this filter and mp3-url is a link to the lost sound. Use the same pusher channel you used for won deal.
* Gong should directly work with any CRM which is supported by Zapier
* You might also come up with other ways to use the gong, in case you want to play a sound in a remote location

__If you intend to use the gong with a bluetooth speaker, please note that most of them switch off automatically after a certain time of inactivity. One of the few that does not do this is Anker SoundCore.__
