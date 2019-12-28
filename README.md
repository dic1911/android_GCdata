# GCdata for Android

[Download](https://030.cdpa.nsysu.edu.tw/gcdata/latest.apk)

### Main purpose

1. A proper Groove Coaster data viewer for Android (Native >>>>>> WebView)
2. Port some tools I wrote in node.js rewritten in Java
3. To learn Android app development maybe lmao

### Features

1. Ranking
    1. Top 1000 total score
    2. Top 1000 total score (area)
    3. Monthly ranking
2. MyPage
    1. Basic stats
    2. Score viewer
    3. Score backup
	4. Monthly stats

### To-Do (also known bugs)

1. Complete event stats
2. Handle network exceptions
3. Performance improvement
4. Support multiple NESiCA entry

### Changelog (since v0.3.6)
**v0.6.0**
1. Migrate to AndroidX
2. Bug fix

**v0.5.2**
1. Minor code cleanup
2. Partial fix for Android Q(sometimes navigation menu doesn't work, relaunch when this happens)

**v0.5.1**
1. Added timestamp display in score page
2. Updated URL in app

**v0.5.0**
1. Announcement string on main page
2. bug fixes

**v0.4.4**
1. Fix score display when rank is null

**v0.4.3**
1. Fixed double home/main activity caused when applying language option
2. Potential fix for event page
3. some strings update

**v0.4.2**
1. Fixed Monthly stats when the ranking data is not updated
2. Fixed Chinese locale (code needs to be cleaned tho)
3. Added permission request for score backup function

**v0.4.1**
1. Fixed crash when player open Monthly stat page without playing any of the songs
2. Add Traditional Chinese & Japanese translation

**v0.4.0**
1. Added monthly stats
2. Minor bug fix

**v0.3.8**
1. Event stats fixed
2. Fetch data on activity start

**v0.3.7**
1. Minor improvement in public ranking page
2. Introduce initial update checking feature in app
