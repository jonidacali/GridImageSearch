GridImageSearch
===============
Summary: 

Libraries used to complete this project:


Stories
* User can enter a search query that will display a grid of image results from the Google Image API.
* User can click on "settings" which allows selection of advanced search options to filter results
* User can configure advanced search filters such as:
        Size (small, medium, large, extra-large)
        Color filter (black, blue, brown, gray, green, etc...)
        Type (faces, photo, clip art, line art)
        Site (espn.com)
* Subsequent searches will have any filters applied to the search results
* User can tap on any image in results to see the image full-screen
* User can scroll down “infinitely” to continue loading more image results (up to 8 pages)
* Use the ActionBar SearchView or custom layout as the query box instead of an EditText
* User can share an image to their friends or email it to themselves (this only takes me to mms sharing - didn't manage to figure out how to show the list of other ways a user can potentially share an image)
* Robust error handling, check if internet is available, handle error cases, network failures (did some elementary checks - haven't added error messages)



Future improvements

* Replace Filter Settings Activity with a lightweight modal overlay (I spent quiet some time on this and was surprised I couldn't finish it since I had already done it once for the todo app)
* Reorganize code (I could have introduced a few methods - ie. for making the acynch call - in order to reuse code segments)
* Improve the user interface and experiment with image assets and/or styling and coloring
* Use the StaggeredGridView to display improve the grid of image results
* User can zoom or pan images displayed in full-screen detail view
